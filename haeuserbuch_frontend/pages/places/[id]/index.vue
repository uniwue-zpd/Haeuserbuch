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
const { data: placeItem, error: hasError, pending: isLoading } = await useAsyncData(`place-${ placeId }`, () => placeStore.getPlace(placeId));
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
    <div class="flex flex-col gap-4 p-4 rounded-md shadow-md border border-gray-200">
      <div class="flex flex-row justify-between">
        <h1 class="text-3xl montserrat-headline text-black font-bold">{{ properties?.realName }}</h1>
        <TaskBar :id="placeId" entity_type="places"/>
      </div>
      <div class="flex flex-col gap-2">
        <div id="map" v-show="geometry" class="h-[500px] w-full rounded-md border border-gray-200 shadow-md"/>
        <div v-if="properties && properties.altNames.length > 0" class="flex flex-col gap-2">
          <h2 class="text-xl montserrat-headline text-black font-bold">Namensvarianten</h2>
          <ul class="list-inside list-disc">
            <li v-for="name in properties?.altNames">
              {{ name }}
            </li>
          </ul>
        </div>
        <div v-if="associatedPeople && associatedPeople.length > 0" class="flex flex-col gap-2">
          <h2 class="text-xl montserrat-headline text-black font-bold">Möglicher Herkunftsort von</h2>
          <div class="flex flex-wrap gap-3.5">
            <span v-for="person in associatedPeople" :key="person.id as number">
              <NuxtLink
                  :to="`/persons/${ person.id }`"
                  class="p-1.5 bg-[#F1F2F2] rounded-md shadow-sm hover:shadow-md font-medium roboto-plain"
              >
                {{ person.fullName }}
              </NuxtLink>
            </span>
          </div>
        </div>
      </div>
      <div class="flex flex-col gap-2 p-4 rounded-md shadow-md bg-gray-100 border border-gray-200">
        <Panel header="Notizen" toggleable v-show="properties?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ properties?.generalNotes }}</p>
        </Panel>
        <div class="flex flex-col">
          <div v-if="properties?.createdDate" class="flex flex-row space-x-2 text-base text-black roboto-plain">
            <p class="font-bold">Erstellt am:</p>
            <p>{{ new Date(properties?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="properties?.lastModifiedDate" class="flex flex-row space-x-2 text-base text-black roboto-plain">
            <p class="font-bold">Stand:</p>
            <p>{{ new Date(properties?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
