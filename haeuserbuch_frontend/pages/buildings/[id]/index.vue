<script setup lang="ts">
import maplibregl, {
  type CircleLayerSpecification, type FillExtrusionLayerSpecification,
  type LineLayerSpecification,
  type RasterLayerSpecification,
  type RasterSourceSpecification
} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, onMounted } from 'vue';
import type { Feature } from "~/utils/GeoJsonTypes";
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';
import { initMap } from "~/service/map_init";
import BuildingSkeleton from "~/components/UI/skeletons/BuildingSkeleton.vue";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import { title_shortener } from "~/utils/helpers";
import FetchError from "~/components/UI/FetchError.vue";

const route = useRoute();
const building_id = Number(route.params.id);

const building_store = useBuildingStore();
const person_store = usePersonStore();
const tile_store = useTileStore();
const { data: buildingItem, error: hasError, pending: isLoading } = await useAsyncData(`building-${building_id}`, () => building_store.getBuilding(building_id));
const { data: associatedPeople } = await useAsyncData(`associated-people-building-${building_id}`, () => person_store.filterPersons({ "associated-building-id": building_id }));
const buildingItemProperties = computed(() => buildingItem.value?.properties as BuildingProperties ?? null);
const buildingItemGeometry = computed(() => buildingItem.value?.geometry ?? null);

// Get bounds
const bounds = computed(() => {
  const geometry = buildingItemGeometry.value;
  if (!geometry) return null;
  const bounds = new maplibregl.LngLatBounds();
  const extend = (coords: any): void => {
    if (Array.isArray(coords) && coords.length === 2 && typeof coords[0] === "number") {
      bounds.extend(coords as [number, number]);
      return;
    }
    coords.forEach(extend);
  };
  extend(geometry.coordinates);
  return bounds;
});

// Get layer design
const layer = computed(() => {
  if (!buildingItemGeometry.value) return null;
  const geometryType = buildingItemGeometry.value.type;
  switch (geometryType) {
    case "Point":
      return {
        id: "building-point",
        type: "circle",
        source: "building",
        paint: {
          "circle-radius": 6,
          "circle-color": "#d8cece"
        }
      } as CircleLayerSpecification;
    case "LineString":
      return {
        id: "building-line",
        type: "line",
        source: "building",
        paint: {
          "line-width": 4,
          "line-color": "#56e3bd"
        }
      } as LineLayerSpecification;
    case "Polygon":
    case "MultiPolygon":
      return {
        id: "building-fill",
        type: "fill-extrusion",
        source: "building",
        paint: {
          "fill-extrusion-color": "rgb(0,119,255)",
          "fill-extrusion-opacity": 0.8,
          "fill-extrusion-height": 10
        }
      } as FillExtrusionLayerSpecification;
    default:
      throw new Error(`Unsupported geometry type: ${ geometryType }`);
  }
})

// Get custom maps data
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

// Declare the map
let map: maplibregl.Map | null = null;

useHead(() => ({
  title: buildingItem.value ? `${ buildingItemProperties.value?.districtHouseNumber } - Gebäudeverzeichnis` : 'Nicht gefunden',
}));

onMounted(async () => {
  await nextTick();
  if (!document.getElementById('map')) return;
  map = initMap(
      'map',
      DEFAULT_MAP_CENTER,
      14,
      70,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!buildingItem.value) return;
    map!.addSource('building', {
      type: 'geojson',
      // @ts-ignore
      data: buildingItem.value as Feature,
    });
    map!.addLayer(layer.value!);
    map?.fitBounds(bounds.value!, { padding: 20, maxZoom: 18 });
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
  <BuildingSkeleton v-if="isLoading"/>
  <FetchError v-else-if="hasError" :error="hasError"/>
  <div v-else class="flex flex-col gap-4 p-4 rounded-md shadow-md">
    <div class="flex flex-row justify-between">
      <h1 v-if="buildingItemProperties?.districtHouseNumber" class="text-3xl montserrat-headline font-bold">
        {{ buildingItemProperties?.districtHouseNumber }}
      </h1>
      <TaskBar :id="building_id" entity_type="buildings"/>
    </div>
    <div>
      <div v-if="buildingItemGeometry" class="h-[300px] md:h-[500px] w-full rounded-md shadow-md" id="map"/>
      <div v-else class="flex flex-col gap-4 items-center justify-center h-[250px] bg-yellow-200 rounded-md mx-auto p-2.5">
        <i class="pi pi-exclamation-circle text-5xl"/>
        <p class="roboto-plain text-center text-lg font-medium">Für dieses Gebäude sind bisher keine Geodaten hinterlegt</p>
      </div>
    </div>
    <div v-if="buildingItemProperties" class="flex flex-col p-4 bg-gray-100 rounded-md shadow-md roboto-plain divide-y divide-gray-300">
      <h2 class="text-2xl text-black font-semibold montserrat-headline pb-2">Metadaten</h2>
      <div v-if="buildingItemProperties.year" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Jahr</p>
        <p>{{ buildingItemProperties.year }}</p>
      </div>
      <div v-if="buildingItemProperties.parcelNumber" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Flurstücksnummer</p>
        <p>{{ buildingItemProperties.parcelNumber }}</p>
      </div>
      <div v-if="buildingItemProperties.parcelNumberCounter" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Flurstücksnummerzähler</p>
        <p>{{ buildingItemProperties.parcelNumberCounter }}</p>
      </div>
      <div v-if="buildingItemProperties.names.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Namen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="name in buildingItemProperties.names"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
            <div class="flex flex-row space-x-2 font-medium">
              <span>{{ name.name }}</span>
              <NuxtLink
                  :to="`/sources/${name.source?.id}`"
                  class="text-blue-700 line-clamp-1"
                  :title="name.source?.title as string"
              >
                (Quelle)
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
      <div v-if="buildingItemProperties.addresses.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Adressen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="address in buildingItemProperties.addresses"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
            <div class="flex flex-row space-x-2 font-medium">
              <NuxtLink
                  :to="`/streets/${address.street?.id}`"
                  class="text-blue-700"
              >{{ address.street?.name }}</NuxtLink>
              <span>{{ address.houseNumber }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="buildingItemProperties.houseNumber" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Hausnummer</p>
        <p>{{ buildingItemProperties.houseNumber }}</p>
      </div>
      <div v-if="buildingItemProperties.partType" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Bauteil</p>
        <p>{{ buildingItemProperties.partType }}</p>
      </div>
      <div v-if="buildingItemProperties.specialStatus" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Sonderstatus</p>
        <p>{{ buildingItemProperties.specialStatus }}</p>
      </div>
      <div v-if="buildingItemProperties.quarter" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Viertel</p>
        <div>
          <NuxtLink
              :to="`/quarters/${buildingItemProperties.quarter.id}`"
              class="text-blue-700 p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md font-medium"
          >
            {{ buildingItemProperties.quarter.name }}
          </NuxtLink>
        </div>
      </div>
      <div v-if="buildingItemProperties.district" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Distrikt</p>
        <div>
          <NuxtLink
              :to="`/districts/${buildingItemProperties.district.id}`"
              class="text-blue-700 p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md font-medium"
          >
            {{ buildingItemProperties.district.name }}
          </NuxtLink>
        </div>
      </div>
      <div v-if="buildingItemProperties.primarySources.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Primärquellen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="source in buildingItemProperties.primarySources"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
          >
              <NuxtLink
                  :to="`/sources/${source.id}`"
                  class="text-blue-700 line-clamp-1 font-medium"
                  :title="source.title as string"
              >
                {{ source.title ? title_shortener(source.title, 4) : 'Unbenannte Quelle' }}
              </NuxtLink>
          </div>
        </div>
      </div>
      <div v-if="buildingItemProperties.secondarySources.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Sekundärquellen</p>
        <div class="flex flex-wrap gap-3.5">
          <div
              v-for="source in buildingItemProperties.secondarySources"
              class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md max-w-[30%]"
          >
            <NuxtLink
                :to="`/sources/${source.id}`"
                class="text-blue-700 line-clamp-1 font-medium"
                :title="source.title as string"
            >
              {{ source.title ? title_shortener(source.title, 4) : 'Unbenannte Quelle' }}
            </NuxtLink>
          </div>
        </div>
      </div>
      <div v-if="buildingItemProperties.generalNotes" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Notizen</p>
        <p>{{ buildingItemProperties.generalNotes }}</p>
      </div>
      <div v-if="associatedPeople && associatedPeople.length > 0" class="grid grid-cols-2 gap-2 p-2.5">
        <p class="font-bold">Assoziierte Personen</p>
        <div class="flex flex-wrap gap-3.5">
          <span
              v-for="person in associatedPeople"
              :key="person.id as number"
          >
            <NuxtLink
                :to="`/persons/${ person.id }`"
                class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md text-blue-700 line-clamp-1 font-medium"
            >
              {{ person.fullName }}
            </NuxtLink>
          </span>
        </div>
      </div>
    </div>
    <div class="flex flex-col gap-2 p-4 bg-gray-100 rounded-md shadow-md">
      <div class="flex flex-col">
        <div v-if="buildingItemProperties?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Erstellt am:</p>
          <p>{{ new Date(buildingItemProperties.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="buildingItemProperties?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Stand:</p>
          <p>{{ new Date(buildingItemProperties.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
