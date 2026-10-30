<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { initMap } from "~/service/map_init";
import maplibregl, {
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";

type MapView = "all" | "building" | "origin" | "emigration";

const props = defineProps<{
  personId: number;
  personOrigin: PersonOrigin | null;
  personEmigration: PersonEmigration | null;
  associatedBuilding: BuildingDTO | null;
}>();

const placeStore = usePlaceStore();
const buildingStore = useBuildingStore();
const tile_store = useTileStore();

const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

const markerColors = {
  origin: "#3254a8",
  emigration: "#d9480f",
  building: "#06b6d4",
};
const BUILDING_POLYGON_MIN_ZOOM = 15.5;

const { data: places } = await useAsyncData(
  `person-${props.personId}-origin-places`,
  async () => {
    if (!props.personOrigin?.places.length) {
      return {
        type: "FeatureCollection",
        features: [],
      };
    }
    const features = await Promise.all(
      props.personOrigin.places.map((place) => placeStore.getPlace(place.id)),
    );
    return {
      type: "FeatureCollection",
      features,
    };
  },
);

const { data: placeFeature } = await useAsyncData(
  `person-${props.personId}-emigration-place`,
  async () => {
    if (!props.personEmigration?.place?.id) return null;
    return placeStore.getPlace(props.personEmigration.place.id);
  },
);

const { data: buildingFeature } = await useAsyncData(
  `person-${props.personId}-associated-building`,
  async () => {
    if (!props.associatedBuilding?.id) return null;
    return buildingStore.getBuilding(props.associatedBuilding.id);
  },
);

const originGeometries = computed(
  () =>
    (places.value?.features ?? [])
      .map((feature) => feature.geometry)
      .filter(Boolean) as Point[],
);
const emigrationGeometry = computed(
  () => placeFeature.value?.geometry as Point | null,
);
const buildingGeometry = computed(() => buildingFeature.value?.geometry ?? null);

const placeGeometries = computed(() => [
  ...originGeometries.value,
  ...(emigrationGeometry.value ? [emigrationGeometry.value] : []),
]);

const hasOriginGeoData = computed(() => originGeometries.value.length > 0);
const hasEmigrationGeoData = computed(() => Boolean(emigrationGeometry.value));
const hasPlaceGeoData = computed(() => placeGeometries.value.length > 0);
const hasBuildingGeoData = computed(() => Boolean(buildingGeometry.value));
const hasGeoData = computed(
  () => hasPlaceGeoData.value || hasBuildingGeoData.value,
);

const activeView = ref<MapView>("all");
const viewOptions = computed(() => [
  { value: "all" as const, label: "Alle", disabled: false },
  { value: "building" as const, label: "Gebäude", disabled: !hasBuildingGeoData.value },
  { value: "origin" as const, label: "Herkunft", disabled: !hasOriginGeoData.value },
  { value: "emigration" as const, label: "Wegzug", disabled: !hasEmigrationGeoData.value },
]);

let map: maplibregl.Map | null = null;

function getBounds(geometries: GeometryType[]) {
  const bounds = new maplibregl.LngLatBounds();
  const extend = (coordinates: unknown): void => {
    if (
      Array.isArray(coordinates) &&
      coordinates.length === 2 &&
      typeof coordinates[0] === "number" &&
      typeof coordinates[1] === "number"
    ) {
      bounds.extend(coordinates as [number, number]);
      return;
    }
    if (Array.isArray(coordinates)) coordinates.forEach(extend);
  };
  geometries.forEach((geometry) => extend(geometry.coordinates));
  return bounds;
}

function getViewGeometries(view: MapView): GeometryType[] {
  const building = buildingGeometry.value ? [buildingGeometry.value] : [];
  if (view === "building") return building;
  if (view === "origin") return originGeometries.value;
  if (view === "emigration") return emigrationGeometry.value ? [emigrationGeometry.value] : [];
  return [...placeGeometries.value, ...building];
}

function selectView(view: MapView, duration = 600) {
  activeView.value = view;
  if (!map) return;
  const geometries = getViewGeometries(view);
  const bounds = getBounds(geometries);
  if (view === "all" && geometries.length === 1) {
    map.easeTo({ center: bounds.getCenter(), zoom: 4, duration });
    return;
  }
  map.fitBounds(bounds, {
    padding: 40,
    maxZoom: view === "building" ? 18 : 8,
    duration,
  });
}

function openBuilding() {
  navigateTo(`/katasterplan/${props.associatedBuilding!.id}`);
}

function showPointerOnHover(layer: string) {
  map!.on("mouseenter", layer, () => {
    map!.getCanvas().style.cursor = "pointer";
  });
  map!.on("mouseleave", layer, () => {
    map!.getCanvas().style.cursor = "";
  });
}

function addPlaceLayer(id: string, data: unknown, color: string) {
  addPointLayer(id, data, color);
  showPointerOnHover(id);
  map!.on("click", id, (event) => {
    navigateTo(`/orte/${event.features![0]!.id}`);
  });
}

function addPointLayer(id: string, data: unknown, color: string, maxzoom = 24) {
  map!.addSource(id, {
    type: "geojson",
    // @ts-ignore
    data,
  });
  map!.addLayer({
    id,
    type: "circle",
    source: id,
    maxzoom,
    paint: {
      "circle-radius": 7,
      "circle-color": color,
      "circle-opacity": 0.9,
      "circle-stroke-width": 1,
      "circle-stroke-color": "#ffffff",
    },
  });
}

function addBuildingLayers() {
  map!.addSource("building", {
    type: "geojson",
    // @ts-ignore
    data: buildingFeature.value,
  });
  map!.addLayer({
    id: "building-fill",
    type: "fill",
    source: "building",
    minzoom: BUILDING_POLYGON_MIN_ZOOM,
    paint: { "fill-color": markerColors.building, "fill-opacity": 0.65 },
  });
  map!.addLayer({
    id: "building-outline",
    type: "line",
    source: "building",
    minzoom: BUILDING_POLYGON_MIN_ZOOM,
    paint: { "line-color": "#22d3ee", "line-width": 3 },
  });
  // A single marker keeps the building findable while its polygon is too small to see.
  const isPoint = buildingGeometry.value!.type === "Point";
  addPointLayer(
    "building-marker",
    {
      type: "Point",
      coordinates: getBounds([buildingGeometry.value!]).getCenter().toArray(),
    },
    markerColors.building,
    isPoint ? 24 : BUILDING_POLYGON_MIN_ZOOM,
  );

  ["building-fill", "building-outline", "building-marker"].forEach(showPointerOnHover);
  map!.on("click", "building-fill", openBuilding);
  map!.on("click", "building-outline", openBuilding);
  map!.on("click", "building-marker", () => {
    if (isPoint && activeView.value === "building") openBuilding();
    else selectView("building");
  });
}

onMounted(() => {
  if (!hasGeoData.value) return;
  map = initMap(
    `person-${props.personId}_map`,
    getBounds(getViewGeometries("all").slice(0, 1)).getCenter().toArray() as [number, number],
    4,
    sources.value as Record<string, RasterSourceSpecification>,
    // @ts-ignore
    layers.value as RasterLayerSpecification[],
    { constrainToRasterBounds: false },
  );
  map.on("load", () => {
    if (hasBuildingGeoData.value) addBuildingLayers();
    if (hasOriginGeoData.value) {
      addPlaceLayer("origin", places.value, markerColors.origin);
    }
    if (hasEmigrationGeoData.value) {
      addPlaceLayer("emigration", placeFeature.value, markerColors.emigration);
    }
    if (getViewGeometries("all").length > 1) selectView("all", 0);
  });
});

onUnmounted(() => {
  map?.remove();
});
</script>

<template>
  <section
    v-if="hasGeoData"
    class="person-map flex min-w-0 flex-col gap-5 rounded-md border border-default bg-default p-5 shadow-md"
  >
    <div class="flex items-center gap-1">
      <h2 class="text-xl font-semibold leading-tight text-highlighted">Räumliche Bezüge</h2>
      <UPopover
        mode="click"
        :content="{ align: 'start', side: 'bottom', sideOffset: 8 }"
      >
        <UButton
          color="neutral"
          variant="ghost"
          size="xs"
          icon="i-lucide-info"
          aria-label="Hinweise zur Karte"
        />
        <template #content>
          <p class="max-w-sm p-4 text-sm leading-normal">
            Hier sind die Orte und Gebäude angezeigt, zu denen die Person
            einen Bezug hat. Herkunft: woher die Person nach Würzburg kam. Es
            können mehrere mögliche Orte eingetragen sein; die
            Lokalisierbarkeit gibt an, wie sicher sich der Herkunftsort
            bestimmen lässt. Wegzug: wohin die Person von Würzburg
            ausgewandert ist. Hierfür ist nur ein Ort eingetragen. Gebäude:
            das Haus in Würzburg, zu dem die Person einen Bezug hat. Über die
            Auswahl an der Karte lässt sich die Ansicht auf das Gebäude oder
            die Orte zoomen.
          </p>
        </template>
      </UPopover>
    </div>
    <div class="flex flex-col gap-3 border-t border-muted pt-3">
      <UFieldGroup size="xs" class="self-start">
        <UButton
          v-for="option in viewOptions"
          :key="option.value"
          :label="option.label"
          :disabled="option.disabled"
          :variant="activeView === option.value ? 'solid' : 'outline'"
          :aria-pressed="activeView === option.value"
          color="neutral"
          @click="selectView(option.value)"
        />
      </UFieldGroup>
      <div
        class="h-96 w-full overflow-hidden rounded-md lg:h-124"
        :id="`person-${props.personId}_map`"
      />
      <div class="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm font-medium text-muted">
        <span class="font-semibold">Legende</span>
        <span v-if="hasOriginGeoData" class="flex items-center gap-2">
          <span
            class="size-3 rounded-full"
            :style="{ backgroundColor: markerColors.origin }"
          />
          Herkunft
        </span>
        <span v-if="hasEmigrationGeoData" class="flex items-center gap-2">
          <span
            class="size-3 rounded-full"
            :style="{ backgroundColor: markerColors.emigration }"
          />
          Wegzug
        </span>
        <span v-if="hasBuildingGeoData" class="flex items-center gap-2">
          <span
            class="size-3 rounded-full"
            :style="{ backgroundColor: markerColors.building }"
          />
          Gebäude
        </span>
      </div>
    </div>
  </section>
</template>
