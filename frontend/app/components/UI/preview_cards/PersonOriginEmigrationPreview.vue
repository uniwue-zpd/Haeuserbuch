<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { initMap } from "~/service/map_init";
import maplibregl, {
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";

const props = defineProps<{
  personId: number;
  personOrigin: PersonOrigin | null;
  personEmigration: PersonEmigration | null;
}>();

const placeStore = usePlaceStore();
const tile_store = useTileStore();

const sources = computed(() => tile_store.baseSources);
const layers = computed(() => tile_store.baseLayers);

const markerColors = { origin: "#3254a8", emigration: "#d9480f" };

const originCertainty = ref<Record<string, { label: string; color: string }>>({
  IDENTIFIED: { label: "Identifiziert", color: "bg-green-400" },
  AMBIGUOUS: { label: "Mehrdeutig", color: "bg-yellow-400" },
  UNKNOWN: { label: "Unbekannt", color: "bg-red-400" },
});

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

const originGeometries = computed(
  () =>
    (places.value?.features ?? [])
      .map((feature) => feature.geometry)
      .filter(Boolean) as Point[],
);
const emigrationGeometry = computed(
  () => placeFeature.value?.geometry as Point | null,
);

const hasOriginGeoData = computed(() => originGeometries.value.length > 0);
const hasEmigrationGeoData = computed(() => Boolean(emigrationGeometry.value));
const hasGeoData = computed(
  () => hasOriginGeoData.value || hasEmigrationGeoData.value,
);

const coordinates = computed(() =>
  [
    ...originGeometries.value,
    ...(emigrationGeometry.value ? [emigrationGeometry.value] : []),
  ].map(
    (geometry) =>
      [geometry.coordinates[0], geometry.coordinates[1]] as [number, number],
  ),
);

let map: maplibregl.Map | null = null;

function addPointLayer(id: string, data: unknown, color: string) {
  map!.addSource(id, {
    type: "geojson",
    // @ts-ignore
    data,
  });
  map!.addLayer({
    id,
    type: "circle",
    source: id,
    paint: {
      "circle-radius": 7,
      "circle-color": color,
      "circle-opacity": 0.9,
      "circle-stroke-width": 1,
      "circle-stroke-color": "#ffffff",
    },
  });
}

onMounted(() => {
  if (!hasGeoData.value) return;
  map = initMap(
    `person-${props.personId}_origin_emigration_map`,
    coordinates.value[0]!,
    4,
    sources.value as Record<string, RasterSourceSpecification>,
    // @ts-ignore
    layers.value as RasterLayerSpecification[],
  );
  map.on("load", () => {
    if (hasOriginGeoData.value) {
      addPointLayer("origin", places.value, markerColors.origin);
    }
    if (hasEmigrationGeoData.value) {
      addPointLayer("emigration", placeFeature.value, markerColors.emigration);
    }
    if (coordinates.value.length > 1) {
      const bounds = new maplibregl.LngLatBounds();
      coordinates.value.forEach((coordinate) => bounds.extend(coordinate));
      map!.fitBounds(bounds, { padding: 40, maxZoom: 8, animate: false });
    }
  });
});

onUnmounted(() => {
  map?.remove();
});
</script>

<template>
  <div
    class="person-origin-emigration-card flex min-w-0 flex-col gap-5 rounded-md border border-gray-300 bg-default p-5 shadow-md"
  >
    <div class="flex flex-col gap-2">
      <h2 class="text-xl font-semibold leading-tight text-highlighted">Herkunft und Wegzug</h2>
      <p class="text-sm leading-normal text-muted">
        Hier sind die Herkunfts- und Wegzugsorte der Person angezeigt. Herkunft:
        woher die Person nach Würzburg kam. Es können mehrere mögliche Orte
        eingetragen sein; die Lokalisierbarkeit gibt an, wie sicher sich der
        Herkunftsort bestimmen lässt. Wegzug: wohin die Person von Würzburg
        ausgewandert ist. Hierfür ist nur ein Ort eingetragen.
      </p>
    </div>
    <section v-if="personOrigin" class="flex flex-col gap-3 border-t border-muted pt-3">
      <h3 class="flex items-center gap-2 text-lg font-semibold leading-tight text-highlighted">
        <span
          class="size-3 rounded-full"
          :style="{ backgroundColor: markerColors.origin }"
        />
        Herkunft
      </h3>
      <div v-if="personOrigin.originalText" class="flex flex-col gap-2 py-1">
        <span class="text-sm font-bold leading-tight text-muted">
          Eingetragener Ort
        </span>
        <span class="text-base italic">
          {{ personOrigin.originalText }}
        </span>
      </div>
      <div v-if="personOrigin.places.length > 0" class="flex flex-col gap-2 py-1">
        <span class="text-sm font-bold leading-tight text-muted">
          Möglicherweise
        </span>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
            v-for="place in personOrigin.places"
            :key="place.id"
            :to="`/orte/${place.id}`"
            class="p-1.5 border-2 border-gray-300 rounded-md shadow-sm hover:shadow-md font-semibold"
          >
            {{ place.realName }}
          </NuxtLink>
        </div>
      </div>
      <div v-if="personOrigin.certainty" class="flex flex-col gap-2 py-1">
        <span class="text-sm font-bold leading-tight text-muted">
          Lokalisierbarkeit
        </span>
        <div>
          <span
            :class="`border border-gray-400 p-1.5 rounded-md text-xs font-bold uppercase tracking-wide shadow-sm ${originCertainty[personOrigin.certainty]?.color}`"
          >
            {{ originCertainty[personOrigin.certainty]?.label }}
          </span>
        </div>
      </div>
    </section>
    <section v-if="personEmigration" class="flex flex-col gap-3 border-t border-muted pt-3">
      <h3 class="flex items-center gap-2 text-lg font-semibold leading-tight text-highlighted">
        <span
          class="size-3 rounded-full"
          :style="{ backgroundColor: markerColors.emigration }"
        />
        Wegzug
      </h3>
      <div v-if="personEmigration.originalText" class="flex flex-col gap-2 py-1">
        <span class="text-sm font-bold leading-tight text-muted">
          Eingetragener Ort
        </span>
        <span class="text-base italic">
          {{ personEmigration.originalText }}
        </span>
      </div>
      <div v-if="personEmigration.place" class="flex flex-col gap-2 py-1">
        <span class="text-sm font-bold leading-tight text-muted">
          Wegzugsort
        </span>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
            :to="`/orte/${personEmigration.place.id}`"
            class="p-1.5 border-2 border-gray-300 rounded-md shadow-sm hover:shadow-md font-semibold"
          >
            {{ personEmigration.place.realName }}
          </NuxtLink>
        </div>
      </div>
    </section>
    <div v-if="hasGeoData" class="flex flex-col gap-2 border-t border-muted pt-3">
      <div class="flex flex-wrap gap-4 text-sm font-medium text-muted">
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
      </div>
      <div
        class="h-56 w-full overflow-hidden rounded-md border-0 shadow-none"
        :id="`person-${props.personId}_origin_emigration_map`"
      />
    </div>
  </div>
</template>
