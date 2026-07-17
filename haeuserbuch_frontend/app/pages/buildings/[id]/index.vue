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
const addressesCurrent = computed(() =>
    buildingItemProperties.value?.addresses.filter(address => String(address.fromDate ?? '') === '2025') ?? []
);
const addressesOld = computed(() =>
    buildingItemProperties.value?.addresses.filter(address => String(address.fromDate ?? '') !== '2025') ?? []
);

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
          'circle-radius': 9,
          'circle-color': '#E66101',
          'circle-opacity': 0.9,
          'circle-stroke-width': 2,
          'circle-stroke-color': '#ffffff',
          'circle-stroke-opacity': 0.9
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

useHead(() => {
  const districtPropertyNumber = buildingItemProperties.value?.districtPropertyNumber;
  return {
    title: districtPropertyNumber
        ? `${districtPropertyNumber} - Gebäudeverzeichnis`
        : 'Gebäude - Gebäudeverzeichnis',
  };
});

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
  <div v-else class="flex flex-col gap-4 p-4 rounded-lg shadow-lg border-2 border-gray-300">
    <div class="flex flex-row justify-between">
      <h1 v-if="buildingItemProperties?.districtPropertyNumber" class="text-3xl montserrat-headline font-bold">
        {{ buildingItemProperties?.districtPropertyNumber }}
      </h1>
      <h1 v-else class="text-3xl montserrat-headline font-bold">{{ buildingItemProperties?.object || 'Gebäude ohne Bezeichnung' }}</h1>
      <TaskBar :id="building_id" entity_type="buildings"/>
    </div>
    <div>
      <div v-if="buildingItemGeometry" class="h-[300px] md:h-[500px] w-full rounded-md shadow-md" id="map"/>
      <div v-else class="flex flex-col gap-4 items-center justify-center h-[250px] bg-yellow-200 rounded-md mx-auto p-2.5">
        <i class="pi pi-exclamation-circle text-5xl"/>
        <p class="roboto-plain text-center text-lg font-medium">Für dieses Gebäude sind bisher keine Geodaten hinterlegt</p>
      </div>
    </div>
    <div v-if="buildingItemProperties" class="flex flex-col gap-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300">
          <h2 class="text-2xl font-semibold montserrat-headline">Adressen und Flurstücke</h2>
          <div v-if="buildingItemProperties.propertyNumber" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Historische Besitznummer</p>
            <p class="font-semibold">{{ buildingItemProperties.propertyNumber }}</p>
          </div>
          <div v-if="buildingItemProperties.district" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Distrikt</p>
            <div>
              <NuxtLink
                  :to="`/districts/${buildingItemProperties.district.id}`"
                  class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
              >
                {{ buildingItemProperties.district.name }}
              </NuxtLink>
            </div>
          </div>
          <div v-if="buildingItemProperties.quarter" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Viertel</p>
            <div>
              <NuxtLink
                  :to="`/quarters/${buildingItemProperties.quarter.id}`"
                  class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
              >
                {{ buildingItemProperties.quarter.name }}
              </NuxtLink>
            </div>
          </div>
          <div v-if="addressesOld.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Adressen um 1869</p>
            <div class="flex flex-wrap gap-2">
              <NuxtLink
                  v-for="address in addressesOld"
                  :to="`/streets/${address.street?.id}`"
                  class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
              >
                {{ address.street?.name }} {{ address.houseNumber }}
              </NuxtLink>
            </div>
          </div>
          <div v-if="addressesCurrent.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Aktuelle Adressen</p>
            <div class="flex flex-wrap gap-2">
              <NuxtLink
                  v-for="address in addressesCurrent"
                  :to="`/streets/${address.street?.id}`"
                  class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
              >
                {{ address.street?.name }} {{ address.houseNumber }}
              </NuxtLink>
            </div>
          </div>
          <div v-if="buildingItemProperties.parcelNumber" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Flurstücksnummer</p>
            <p class="font-semibold">{{ buildingItemProperties.parcelNumber }}</p>
          </div>
          <div v-if="buildingItemProperties.parcelNumberCounter" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Flurstücksnummerzähler</p>
            <p class="font-semibold">{{ buildingItemProperties.parcelNumberCounter }}</p>
          </div>
        </div>
        <div class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300">
          <h2 class="text-2xl font-semibold montserrat-headline">Objektbeschreibung</h2>
          <div v-if="buildingItemProperties.year" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Jahr</p>
            <p class="font-semibold">{{ buildingItemProperties.year }}</p>
          </div>
          <div v-if="buildingItemProperties.names.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Gebäudenamen</p>
            <ul class="list-inside list-disc">
              <li
                  v-for="name in buildingItemProperties.names"
                  class="font-semibold"
              >
                {{ name.name }}
                <NuxtLink
                    v-if="name.source"
                    :to="`/sources/${name.source?.id}`"
                    class="text-blue-500 hover:text-blue-700"
                    title="Quelle"
                >
                  <Icon name="material-symbols-book-2-outline" class="text-base opacity-70"/>
                </NuxtLink>
              </li>
            </ul>
          </div>
          <div v-if="buildingItemProperties.object" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Objekt</p>
            <p class="font-semibold">{{ buildingItemProperties.object }}</p>
          </div>
          <div v-if="buildingItemProperties.partType" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
            <p class="text-sm text-gray-500 font-medium">Bauteil</p>
            <p class="font-semibold">{{ buildingItemProperties.partType }}</p>
          </div>
        </div>
      </div>
      <div
          v-if="buildingItemProperties.sources.length > 0 || buildingItemProperties.literature.length > 0"
          class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
      >
        <h2 class="text-2xl font-semibold montserrat-headline">Quellen- und Literaturangaben</h2>
        <div v-if="buildingItemProperties.sources.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Quellen</p>
          <div class="flex flex-wrap gap-2">
            <NuxtLink
                v-for="source in buildingItemProperties.sources"
                :to="`/sources/${ source.id }`"
                class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
            >
              {{ source.title ? title_shortener(source.title, 4) : 'Unbenannte Quelle' }}
            </NuxtLink>
          </div>
        </div>
        <div v-if="buildingItemProperties.literature.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Literatur</p>
          <div class="flex flex-wrap gap-2">
            <NuxtLink
                v-for="source in buildingItemProperties.literature"
                :to="`/sources/${ source.id }`"
                class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
            >
              {{ source.title ? title_shortener(source.title, 4) : 'Unbenannte Quelle' }}
            </NuxtLink>
          </div>
        </div>
      </div>
      <div
          v-if="buildingItemProperties.generalNotes"
          class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
      >
        <h2 class="text-2xl font-semibold montserrat-headline">Notizen und Anmerkungen</h2>
        <div v-if="buildingItemProperties.generalNotes" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Anmerkungen</p>
          <p class="text-justify">{{ buildingItemProperties.generalNotes }}</p>
        </div>
        <div v-if="buildingItemProperties.internalNotes" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Notizen</p>
          <p class="text-justify">{{ buildingItemProperties.internalNotes }}</p>
        </div>
      </div>
      <div class="roboto-plain flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300">
        <h2 class="text-2xl font-semibold montserrat-headline">Beziehungen zu anderen Entitäten</h2>
        <div v-if="associatedPeople && associatedPeople.length > 0" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Personen</p>
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
        <div v-if="buildingItemProperties.createdDate" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Erstellt am</p>
          <p class="text-justify">{{ new Date(buildingItemProperties.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="buildingItemProperties.lastModifiedDate" class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Zuletzt aktualisiert am</p>
          <p class="text-justify">{{ new Date(buildingItemProperties.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
        <!-- TO-DO: After Keycloak integration: createdBy & lastModifiedBy -->
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
