<script setup lang="ts">
import maplibregl, {
  type CircleLayerSpecification,
  type FillExtrusionLayerSpecification,
  type LineLayerSpecification,
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import "@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css";
import { computed, onMounted } from "vue";
import { initMap } from "~/service/map_init";
import { isBuildingFeature, type BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";
import BuildingSkeleton from "~/components/UI/skeletons/BuildingSkeleton.vue";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import FetchError from "~/components/UI/FetchError.vue";

const route = useRoute();
const buildingId = Number(route.params.id);
const buildingStore = useBuildingStore();
const tileStore = useTileStore();

const {
  data: buildingItem,
  error: hasError,
  pending: isLoading,
} = await useAsyncData(`building-${buildingId}`, () =>
  buildingStore.getBuilding(buildingId),
);
const {
  data: associatedPeople,
  error: associatedPeopleError,
  pending: associatedPeoplePending,
  refresh: refreshAssociatedPeople,
} = await useAsyncData(
  `associated-people-building-${buildingId}`,
  () =>
    $fetch<PersonPreviewDTO[]>("/api/persons/filter", {
      query: { "associated-building-id": buildingId },
    }),
  { default: () => [] },
);

const building = computed<BuildingFeature | null>(() => {
  const feature = buildingItem.value;
  return feature && isBuildingFeature(feature) ? feature : null;
});
const sources = computed(() => tileStore.sources);
const layers = computed(() => tileStore.layers);

const bounds = computed(() => {
  const geometry = building.value?.geometry;
  if (!geometry) return null;
  const result = new maplibregl.LngLatBounds();
  const extend = (coordinates: unknown): void => {
    if (
      Array.isArray(coordinates) &&
      coordinates.length === 2 &&
      typeof coordinates[0] === "number" &&
      typeof coordinates[1] === "number"
    ) {
      result.extend(coordinates as [number, number]);
      return;
    }
    if (Array.isArray(coordinates)) coordinates.forEach(extend);
  };
  extend(geometry.coordinates);
  return result;
});

const layer = computed(() => {
  const geometryType = building.value?.geometry?.type;
  switch (geometryType) {
    case "Point":
      return {
        id: "building-point",
        type: "circle",
        source: "building",
        paint: {
          "circle-radius": 9,
          "circle-color": "#e66101",
          "circle-opacity": 0.9,
          "circle-stroke-width": 2,
          "circle-stroke-color": "#ffffff",
        },
      } as CircleLayerSpecification;
    case "LineString":
      return {
        id: "building-line",
        type: "line",
        source: "building",
        paint: { "line-width": 4, "line-color": "#e66101" },
      } as LineLayerSpecification;
    case "Polygon":
    case "MultiPolygon":
      return {
        id: "building-fill",
        type: "fill-extrusion",
        source: "building",
        paint: {
          "fill-extrusion-color": "#e66101",
          "fill-extrusion-opacity": 0.85,
          "fill-extrusion-height": 10,
        },
      } as FillExtrusionLayerSpecification;
    default:
      return null;
  }
});

let map: maplibregl.Map | null = null;

useHead(() => ({
  title: building.value?.properties.districtPropertyNumber
    ? `${building.value.properties.districtPropertyNumber} - Gebäudeverzeichnis`
    : "Gebäude - Gebäudeverzeichnis",
}));

onMounted(async () => {
  await nextTick();
  if (
    !document.getElementById("building-detail-map") ||
    !building.value?.geometry ||
    !layer.value
  )
    return;

  map = initMap(
    "building-detail-map",
    DEFAULT_MAP_CENTER,
    14,
    sources.value as Record<string, RasterSourceSpecification>,
    layers.value as RasterLayerSpecification[],
  );
  map.on("load", () => {
    if (!map || !building.value || !layer.value) return;
    map.addSource("building", {
      type: "geojson",
      data: building.value as GeoJSON.Feature,
    });
    map.addLayer(layer.value);
    if (bounds.value) map.fitBounds(bounds.value, { padding: 20, maxZoom: 18 });
  });
});

onBeforeUnmount(() => {
  map?.remove();
  map = null;
});
</script>

<template>
  <BuildingSkeleton v-if="isLoading" />
  <FetchError v-else-if="hasError" :error="hasError" />
  <div
    v-else-if="building"
    class="flex flex-col gap-4 rounded-lg border-2 border-gray-300 p-4 shadow-lg"
  >
    <div class="flex flex-row items-start justify-between gap-3">
      <h1 class="text-3xl font-bold">
        {{
          building.properties.districtPropertyNumber ||
          building.properties.object ||
          "Gebäude ohne Bezeichnung"
        }}
      </h1>
      <TaskBar :id="buildingId" entity_type="buildings" />
    </div>

    <div
      v-if="building.geometry"
      id="building-detail-map"
      class="h-[300px] w-full rounded-md shadow-md md:h-[500px]"
      aria-label="Karte mit Position des Gebäudes"
    />
    <div
      v-else
      class="flex h-[250px] flex-col items-center justify-center gap-4 rounded-md bg-amber-100 p-3"
    >
      <Icon
        name="material-symbols-location-off-outline"
        class="text-5xl text-amber-900"
      />
      <p class="text-center text-lg font-medium">
        Für dieses Gebäude sind bisher keine Geodaten hinterlegt
      </p>
    </div>

    <BuildingsDetails
      :building="building"
      :associated-people="associatedPeople"
      :associated-people-pending="associatedPeoplePending"
      :associated-people-error="Boolean(associatedPeopleError)"
      @retry-relationships="refreshAssociatedPeople"
    />
  </div>
</template>
