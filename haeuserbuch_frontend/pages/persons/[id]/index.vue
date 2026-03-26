<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import { initMap } from "~/service/map_init";
import maplibregl, {type RasterLayerSpecification, type RasterSourceSpecification} from "maplibre-gl";
import { DEFAULT_MAP_CENTER } from "~/utils/constant_values";

const person_store = usePersonStore();
const place_store = usePlaceStore();
const tile_store = useTileStore();

// Display map
const place_features = computed(() => {
  const p = person_item.value;
  if (!p || !p.origin?.places) {
    return null;
  }
  const place_ids = p.origin.places.map(pl => pl.id);
  return {
    type: 'FeatureCollection',
    features: place_store.places?.features.filter((f) => f.id != null && place_ids.includes(f.id)) || []
  }
});

let map: maplibregl.Map | null = null;

const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

const route = useRoute();
const person_id = Number(route.params.id);
const person_item = computed(() => person_store.current_person);
const person_origin = computed(() => person_item.value?.origin);
const person_occupation = computed(() => person_item.value?.occupation);
const originCertainty = ref<Record<string, { label: string; color: string }>>({
  IDENTIFIED: { label: 'Identifiziert', color: 'bg-green-600' },
  AMBIGUOUS: { label: 'Mehrdeutig', color: 'bg-yellow-300' },
  UNKNOWN: { label: 'Unbekannt', color: 'bg-red-600' }
});

onMounted(async () => {
  await person_store.fetchPersonById(person_id);
  map = initMap(
      'origin_map',
      DEFAULT_MAP_CENTER,
      4,
      0,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!place_features.value) return;
    map!.addSource('place', {
      type: 'geojson',
      // @ts-ignore
      data: place_features.value,
    });
    map!.addLayer({
      id: 'place',
      type: 'circle',
      source: 'place',
      paint: {
        'circle-radius': 7,
        'circle-color': '#3254a8',
        'circle-opacity': 0.9,
      },
    });
  });
});

useHead(() => ({
  title: person_item.value ? `${person_item.value.fullName} - Personenverzeichnis` : 'Nicht gefunden',
}));
</script>

<template>
  <div class="flex flex-col gap-4 rounded-md shadow-md p-4">
    <div class="flex flex-row justify-between">
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ person_item?.fullName }}</h1>
      <TaskBar :id="person_id" entity_type="persons"/>
    </div>
    <div class="p-4 rounded-md shadow-md bg-gray-100">
      <table class="text-black roboto-plain w-full table-auto">
        <tbody class="divide-y divide-gray-200">
        <tr v-show="person_item?.firstName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Vorname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.firstName }}</td>
        </tr>
        <tr v-show="person_item?.lastName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Nachname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.lastName }}</td>
        </tr>
        <tr v-show="person_item?.altNames && person_item?.altNames.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Namensvarianten</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <ul class="list-disc list-inside">
              <li v-for="name in person_item?.altNames" :key="name">{{ name }}</li>
            </ul>
          </td>
        </tr>
        <tr v-show="person_item?.sex">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Geschlecht</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.sex }}</td>
        </tr>
        <tr v-if="person_occupation?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Berufliche Situation</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-col gap-1.5 rounded-md shadow-md p-2 bg-gray-200">
              <div class="flex flex-row space-x-3">
                <span class="font-bold">Eingetragener Beruf:</span>
                <span>{{ person_occupation.originalText }}</span>
              </div>
              <div v-if="person_occupation.occupationCategory" class="flex flex-row space-x-3 items-center">
                <span class="font-bold">Standardisierte Berufskategorie:</span>
                <NuxtLink
                    :to="`/occupations/${ person_occupation.occupationCategory.id }`"
                    class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
                >
                  {{ person_occupation.occupationCategory.name }}
                </NuxtLink>
              </div>
            </div>
          </td>
        </tr>
        <tr v-show="person_item?.associatedBuilding">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bezug zum Gebäude</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <NuxtLink
                :to="`/buildings/${ person_item?.associatedBuilding?.id }`"
                class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
            >
              {{ person_item?.associatedBuilding?.districtHouseNumber }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-show="person_item?.isCitizen">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bürger</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <i class="pi pi-check" style="color: green"/>
          </td>
        </tr>
        <tr v-show="person_item?.confession">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Religion</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.confession }}</td>
        </tr>
        <tr v-show="person_origin?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Herkunft</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-col gap-1.5 rounded-md shadow-md p-2 bg-gray-200">
              <div v-if="person_origin?.originalText" class="flex flex-row space-x-3">
                <span class="font-bold">Eingetragener Ort:</span>
                <span>{{ person_origin.originalText }}</span>
              </div>
              <div v-if="person_origin?.places && person_origin?.places.length > 0" class="flex flex-row space-x-3">
                <span class="font-bold">Möglicherweise:</span>
                <div class="flex flex-wrap gap-2">
                  <div v-for="place in person_origin.places">
                    <NuxtLink
                        :to="`/places/${ place.id }`"
                        class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
                    >
                      {{ place.realName }}
                    </NuxtLink>
                  </div>
                </div>
              </div>
              <div v-if="person_origin?.certainty" class="flex flex-row space-x-3 items-center">
                <span class="font-bold">Grad der Lokalisierbarkeit:</span>
                <div :class="`h-[19px] w-[19px] rounded-full shadow-md border border-black ${ originCertainty[person_origin.certainty].color }`"></div>
              </div>
              <div class="rounded-md w-full h-[200px]" id="origin_map"/>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="flex flex-col gap-2 p-4 rounded-md shadow-md bg-gray-100">
      <Panel header="Notizen" toggleable v-show="person_item?.generalNotes">
        <template #header>
          <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
        </template>
        <p class="text-sm text-black roboto-plain">{{ person_item?.generalNotes }}</p>
      </Panel>
      <div class="flex flex-col">
        <div v-if="person_item?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Erstellt am:</p>
          <p>{{ new Date(person_item?.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="person_item?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Stand:</p>
          <p>{{ new Date(person_item?.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
