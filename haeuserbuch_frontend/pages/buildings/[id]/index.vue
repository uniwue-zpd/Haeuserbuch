<script setup lang="ts">
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, ref, onMounted } from 'vue';
import type { Feature, Polygon } from "~/utils/GeoJsonTypes";
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';
import { initMap } from "~/service/map_init";
import BuildingSkeleton from "~/components/UI/skeletons/BuildingSkeleton.vue";

const route = useRoute();
const building_id = Number(route.params.id);

const loading = ref(true);

const building_store = useBuildingStore();
const tile_store = useTileStore();
const building_item = ref<Feature | null>(null);
const building_item_properties = computed(() => building_item.value?.properties as BuildingProperties ?? null);
const building_item_geometry = computed(() => building_item.value?.geometry as Polygon ?? null);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

let map: maplibregl.Map | null = null;
const center = ref<[number, number] | null>(null);

useHead(() => ({
  title: building_item.value ? `${building_item_properties.value?.districtHouseNumber} - Gebäudeverzeichnis` : 'Nicht gefunden',
}));

onMounted(async () => {
  try {
    await building_store.fetchBuildingById(building_id);
    building_item.value = building_store.current_building;
  } finally {
    loading.value = false;
  }
  console.log(building_item.value);
  await nextTick();
  if (!document.getElementById('map')) return;
  center.value = [
    (building_item.value?.geometry as Polygon).coordinates[0][0][0],
    (building_item.value?.geometry as Polygon).coordinates[0][0][1]
  ];
  map = initMap(
      'map',
      center.value ? center.value : DEFAULT_MAP_CENTER,
      14,
      70,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!building_item.value) return;
    map!.addSource('building', {
      type: 'geojson',
      // @ts-ignore
      data: building_item.value as Feature,
    });
    map!.addLayer({
      'id': 'building',
      'type': 'fill-extrusion',
      'source': 'building',
      'layout': {},
      'paint': {
        'fill-extrusion-color': 'rgba(0,255,4,0.8)',
        'fill-extrusion-opacity': 0.8,
        'fill-extrusion-height': 10
      }
    });
    map!.flyTo({
      center: center.value ? center.value : DEFAULT_MAP_CENTER,
      zoom: 17,
      speed: 0.2
    })
  });
});

onBeforeUnmount(() => {
  if (map) {
    map.remove();
    map = null;
  }
});
</script>

<template>
  <BuildingSkeleton v-if="loading"/>
  <div v-else class="flex flex-col gap-4 p-4 rounded-md shadow-md">
    <h1 v-if="building_item_properties?.districtHouseNumber" class="text-3xl montserrat-headline font-bold">
      {{ building_item_properties?.districtHouseNumber }}
    </h1>
    <div>
      <div v-if="building_item_geometry" class="h-[300px] md:h-[500px] w-full rounded-md shadow-md" id="map"/>
      <div v-else class="flex flex-col gap-4 items-center justify-center h-[250px] bg-yellow-200 rounded-md mx-auto p-2.5">
        <i class="pi pi-exclamation-circle text-5xl"/>
        <p class="roboto-plain text-center text-lg font-medium">Für dieses Gebäude sind bisher keine Geodaten hinterlegt</p>
      </div>
    </div>
    <div v-if="building_item_properties" class="flex flex-col p-4 bg-gray-100 rounded-md shadow-md roboto-plain divide-y divide-gray-300">
      <h2 class="text-2xl text-black font-semibold montserrat-headline pb-2">Metadaten</h2>
      <div v-if="building_item_properties.names.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Namen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="name in building_item_properties.names"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
            <div class="flex flex-row space-x-2">
              <span>{{ name.name }}</span>
              <NuxtLink
                  :to="`/sources/${name.source?.id}`"
                  class="text-blue-700 line-clamp-1"
                  :title="name.source?.title"
              >
                (Quelle)
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
      <div v-if="building_item_properties.addresses.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Adressen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="address in building_item_properties.addresses"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
            <div class="flex flex-row space-x-2">
              <NuxtLink
                  :to="`/streets/${address.street?.id}`"
                  class="text-blue-700"
              >{{ address.street?.name }}</NuxtLink>
              <span>{{ address.houseNumber }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="building_item_properties.houseNumber" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Hausnummer</p>
        <p>{{ building_item_properties.houseNumber }}</p>
      </div>
      <div v-if="building_item_properties.partType" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Bauteil</p>
        <p>{{ building_item_properties.partType }}</p>
      </div>
      <div v-if="building_item_properties.specialStatus" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Sonderstatus</p>
        <p>{{ building_item_properties.specialStatus }}</p>
      </div>
      <div v-if="building_item_properties.quarter" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Viertel</p>
        <div>
          <NuxtLink
              :to="`/quarters/${building_item_properties.quarter.id}`"
              class="text-blue-700 p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
            {{ building_item_properties.quarter.name }}
          </NuxtLink>
        </div>
      </div>
      <div v-if="building_item_properties.district" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Distrikt</p>
        <div>
          <NuxtLink
              :to="`/districts/${building_item_properties.district.id}`"
              class="text-blue-700 p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
            {{ building_item_properties.district.name }}
          </NuxtLink>
        </div>
      </div>
      <div v-if="building_item_properties.primarySources.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Primärquellen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="source in building_item_properties.primarySources"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md max-w-[30%]"
          >
              <NuxtLink
                  :to="`/sources/${source.id}`"
                  class="text-blue-700 line-clamp-1"
                  :title="source.title"
              >
                {{ source.title }}
              </NuxtLink>
          </div>
        </div>
      </div>
      <div v-if="building_item_properties.secondarySources.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Sekundärquellen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="source in building_item_properties.secondarySources"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md max-w-[30%]"
          >
            <NuxtLink
                :to="`/sources/${source.id}`"
                class="text-blue-700 line-clamp-1"
                :title="source.title"
            >
              {{ source.title }}
            </NuxtLink>
          </div>
        </div>
      </div>
      <div v-if="building_item_properties.generalNotes" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Notizen</p>
        <p>{{ building_item_properties.generalNotes }}</p>
      </div>
    </div>
    <div class="flex flex-col gap-2 p-4 bg-gray-100 rounded-md shadow-md">
      <div class="flex flex-col">
        <div v-if="building_item_properties?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Erstellt am:</p>
          <p>{{ new Date(building_item_properties.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="building_item_properties?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Stand:</p>
          <p>{{ new Date(building_item_properties.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
