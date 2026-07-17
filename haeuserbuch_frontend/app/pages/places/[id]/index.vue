<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import maplibregl, {type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { usePlaceStore } from "~/stores/PlaceStore";
import { DEFAULT_MAP_CENTER } from "~/utils/constant_values";
import type { Feature } from "~/utils/GeoJsonTypes";
import {initMap} from "~/service/map_init";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";
import FetchError from "~/components/UI/FetchError.vue";

const router = useRoute();
const placeId = Number(router.params.id);
const placeStore = usePlaceStore();
const person_store = usePersonStore();
const { data: placeItem, error: hasError, pending: isLoading } = await useAsyncData(`place-${ placeId }-details`, () => placeStore.getPlace(placeId));
const tile_store = useTileStore();
const geometry = computed(() => placeItem.value?.geometry as Point | null);
const properties = computed(() => placeItem.value?.properties as PlaceProperties | null);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

let map: maplibregl.Map | null = null;
const center = ref<[number, number] | null>(null);
const { data: associatedPeople } = await useAsyncData(`place-${ placeId }-associated_people`, () => person_store.filterPersons({ 'place-of-origin-id': placeId }));

useHead(() => ({
  title: placeItem.value ? `${ properties.value?.realName } - Orteverzeichnis` : 'Nicht gefunden',
}));

onMounted(async () => {
  if (hasError.value) return;
  center.value = (geometry.value)
          ? [(geometry.value as Point).coordinates[0], (geometry.value as Point).coordinates[1]]
          : DEFAULT_MAP_CENTER;
  map = initMap(
      'map',
      center.value,
      14,
      70,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!placeItem.value) return;
    map!.addSource('place', {
      type: 'geojson',
      // @ts-ignore
      data: placeItem.value as Feature,
    });
    map!.addLayer({
      id: 'place',
      type: 'circle',
      source: 'place',
      paint: {
        'circle-radius': 8,
        'circle-color': '#3254a8',
        'circle-opacity': 0.8,
      },
    });
    map!.flyTo({
      center: center.value ? center.value : DEFAULT_MAP_CENTER,
      zoom: 17,
      speed: 0.2
    })
  });
});
</script>

<template>
  <UniversalSkeleton v-if="isLoading"/>
  <FetchError v-else-if="hasError" :error="hasError"/>
  <div v-else>
    <div class="flex flex-col gap-4 p-4 rounded-lg shadow-lg border-2 border-gray-300">
      <div class="flex flex-row justify-between">
        <h1 class="text-3xl montserrat-headline font-bold">{{ properties?.realName }}</h1>
        <TaskBar :id="placeId" entity_type="places"/>
      </div>
      <div>
        <div v-if="geometry" class="h-[300px] md:h-[500px] w-full rounded-md shadow-md" id="map"/>
        <div v-else class="flex flex-col gap-4 items-center justify-center h-[250px] bg-yellow-200 rounded-md mx-auto p-2.5">
          <i class="pi pi-exclamation-circle text-5xl"/>
          <p class="roboto-plain text-center text-lg font-medium">Für diesen Ort sind bisher keine Geodaten hinterlegt</p>
        </div>
      </div>
      <div v-if="properties" class="flex flex-col gap-4">
        <div class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300">
          <h2 class="text-2xl font-semibold montserrat-headline">Informationen zum Ort</h2>
          <div v-if="properties.altNames" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Namensvarianten</p>
            <ul class="list-inside list-disc">
              <li
                  v-for="name in properties.altNames"
                  class="font-semibold"
              >
                {{ name }}
              </li>
            </ul>
          </div>
        </div>
        <div
            v-if="properties.generalNotes"
            class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
        >
          <h2 class="text-2xl font-semibold montserrat-headline">Notizen und Anmerkungen</h2>
          <div v-if="properties.generalNotes" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Anmerkungen</p>
            <p class="text-justify">{{ properties.generalNotes }}</p>
          </div>
          <div v-if="properties.internalNotes" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Notizen</p>
            <p class="text-justify">{{ properties.internalNotes }}</p>
          </div>
        </div>
        <div class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300">
          <h2 class="text-2xl font-semibold montserrat-headline">Beziehungen zu anderen Entitäten</h2>
          <div v-if="associatedPeople && associatedPeople.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Möglicher Herkunftsort von</p>
            <div class="flex flex-wrap gap-2">
              <NuxtLink
                  v-for="person in associatedPeople"
                  :to="`/persons/${ person.id }`"
                  class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
              >
                {{ person.fullName || 'Person mit ID' + person.id }}
              </NuxtLink>
            </div>
          </div>
          <div v-else class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Bisher keine Relationen gefunden</p>
          </div>
        </div>
        <div class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300">
          <h2 class="text-2xl font-semibold montserrat-headline">Über den Eintrag</h2>
          <div v-if="properties.createdDate" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Erstellt am</p>
            <p class="text-justify">{{ new Date(properties.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="properties.lastModifiedDate" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Zuletzt aktualisiert am</p>
            <p class="text-justify">{{ new Date(properties.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
          <!-- TO-DO: After Keycloak integration: createdBy & lastModifiedBy -->
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
