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
  const p = personItem.value;
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
const personId = Number(route.params.id);
const person_origin = computed(() => personItem.value?.origin);
const originCertainty = ref<Record<string, { label: string; color: string }>>({
  IDENTIFIED: { label: 'Identifiziert', color: 'bg-green-600' },
  AMBIGUOUS: { label: 'Mehrdeutig', color: 'bg-yellow-300' },
  UNKNOWN: { label: 'Unbekannt', color: 'bg-red-600' }
});
const person_job = computed(() => personItem.value?.job);
const person_religion = computed(() => personItem.value?.religion);
const person_weapons = computed(() => personItem.value?.weapons);

const { data: personItem, status } = await useAsyncData(`person-${ personId }`, () => person_store.fetchPersonById(personId));

onMounted(async () => {
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
  title: personItem.value ? `${personItem.value.fullName} - Personenverzeichnis` : 'Nicht gefunden',
}));
</script>

<template>
  <div class="flex flex-col gap-4 rounded-md shadow-md p-4">
    <div class="flex flex-row justify-between">
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ personItem?.fullName }}</h1>
      <TaskBar :id="personId" entity_type="persons"/>
    </div>
    <div class="p-4 rounded-md shadow-md bg-gray-100">
      <table class="text-black roboto-plain w-full table-auto">
        <tbody class="divide-y divide-gray-200">
        <tr v-show="personItem?.firstName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Vorname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ personItem?.firstName }}</td>
        </tr>
        <tr v-show="personItem?.lastName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Nachname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ personItem?.lastName }}</td>
        </tr>
        <tr v-show="personItem?.altNames && personItem?.altNames.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Namensvarianten</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <ul class="list-disc list-inside">
              <li v-for="name in personItem?.altNames" :key="name">{{ name }}</li>
            </ul>
          </td>
        </tr>
        <tr v-show="personItem?.sex">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Geschlecht</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ personItem?.sex }}</td>
        </tr>
        <tr v-show="personItem?.isCitizen">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bürger</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <i class="pi pi-check" style="color: green"/>
          </td>
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
        <tr v-show="personItem?.associatedBuilding">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bezug zum Gebäude</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <NuxtLink
                :to="`/buildings/${ personItem?.associatedBuilding?.id }`"
                class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
            >
              {{ personItem?.associatedBuilding?.districtHouseNumber }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-if="person_job?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Berufliche Situation</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-col gap-1.5 rounded-md shadow-md p-2 bg-gray-200">
              <div class="flex flex-row space-x-3">
                <span class="font-bold">Eingetragener Beruf:</span>
                <span>{{ person_job.originalText }}</span>
              </div>
              <div v-if="person_job.jobCategory" class="flex flex-row space-x-3 items-center">
                <span class="font-bold">Standardisierte Berufskategorie:</span>
                <NuxtLink
                    :to="`/jobs/${ person_job.jobCategory.id }`"
                    class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
                >
                  {{ person_job.jobCategory.name }}
                </NuxtLink>
              </div>
            </div>
          </td>
        </tr>
        <tr v-if="person_religion?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Religiöse Zugehörigkeit</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-col gap-1.5 rounded-md shadow-md p-2 bg-gray-200">
              <div class="flex flex-row space-x-3">
                <span class="font-bold">Eingetragene Religion:</span>
                <span>{{ person_religion.originalText }}</span>
              </div>
              <div v-if="person_religion.religionCategory" class="flex flex-row space-x-3 items-center">
                <span class="font-bold">Standardisierte Religionskategorie:</span>
                <NuxtLink
                    :to="`/religions/${ person_religion.religionCategory.id }`"
                    class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
                >
                  {{ person_religion.religionCategory.name }}
                </NuxtLink>
              </div>
            </div>
          </td>
        </tr>
        <tr v-if="person_weapons && person_weapons.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bewaffnung</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-wrap gap-2">
              <div v-for="weapon_item in person_weapons">
                <NuxtLink
                  v-if="weapon_item.weapon"
                  :to="`/weapons/${ weapon_item.weapon.id }`"
                  class="flex flex-row space-x-2 p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg"
                >
                  <span class="font-light">{{ weapon_item.originalText }}</span>
                  <span class="font-medium">({{ weapon_item.weapon.name }})</span>
                </NuxtLink>
                <span v-else class="p-1.5 bg-gray-300 rounded-md shadow-md font-light">
                  {{ weapon_item.originalText }}
                </span>
              </div>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="flex flex-col gap-2 p-4 rounded-md shadow-md bg-gray-100">
      <Panel header="Notizen" toggleable v-show="personItem?.generalNotes">
        <template #header>
          <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
        </template>
        <p class="text-sm text-black roboto-plain">{{ personItem?.generalNotes }}</p>
      </Panel>
      <div class="flex flex-col">
        <div v-if="personItem?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Erstellt am:</p>
          <p>{{ new Date(personItem?.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="personItem?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Stand:</p>
          <p>{{ new Date(personItem?.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
