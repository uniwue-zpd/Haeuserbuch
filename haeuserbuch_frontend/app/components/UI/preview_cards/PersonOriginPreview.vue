<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { initMap } from "~/service/map_init";
import { DEFAULT_MAP_CENTER } from "~/utils/constant_values";
import maplibregl, {
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";

const props = defineProps<{
  personId: number;
  personOrigin: PersonOrigin;
}>();

const placeStore = usePlaceStore();
const tile_store = useTileStore();

const sources = computed(() => tile_store.baseSources);
const layers = computed(() => tile_store.baseLayers);

const originCertainty = ref<Record<string, { label: string; color: string }>>({
  IDENTIFIED: { label: "Identifiziert", color: "bg-green-400" },
  AMBIGUOUS: { label: "Mehrdeutig", color: "bg-yellow-400" },
  UNKNOWN: { label: "Unbekannt", color: "bg-red-400" },
});

const { data: places } = await useAsyncData(
  `person-${props.personId}-origin-places`,
  async () => {
    if (props.personOrigin.places.length === 0) {
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

const hasGeoData = computed(() => {
  return places.value && places.value.features.length > 0;
});

let map: maplibregl.Map | null = null;

onMounted(() => {
  if (!hasGeoData.value) return;
  map = initMap(
    `person-${props.personId}_origin_map`,
    DEFAULT_MAP_CENTER,
    4,
    sources.value as Record<string, RasterSourceSpecification>,
    // @ts-ignore
    layers.value as RasterLayerSpecification[],
  );
  map.on("load", () => {
    if (!places.value) return;
    map!.addSource("place", {
      type: "geojson",
      // @ts-ignore
      data: places.value,
    });
    map!.addLayer({
      id: "place",
      type: "circle",
      source: "place",
      paint: {
        "circle-radius": 7,
        "circle-color": "#3254a8",
        "circle-opacity": 0.9,
      },
    });
  });
});

onUnmounted(() => {
  map?.remove();
});
</script>

<template>
  <div
    class="person-origin-card flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
  >
    <h2 class="text-2xl font-semibold">Herkunft</h2>
    <div v-if="personOrigin.originalText" class="flex flex-col gap-1">
      <span class="text-xs font-semibold uppercase tracking-wide text-gray-500">
        Eingetragener Ort
      </span>
      <span class="text-base italic">
        {{ personOrigin.originalText }}
      </span>
    </div>
    <div v-if="personOrigin.places.length > 0" class="flex flex-col gap-2">
      <span class="text-xs font-semibold uppercase tracking-wide text-gray-500">
        Möglicherweise
      </span>
      <div class="flex flex-wrap gap-2">
        <NuxtLink
          v-for="place in personOrigin.places"
          :key="place.id"
          :to="`/places/${place.id}`"
          class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
        >
          {{ place.realName }}
        </NuxtLink>
      </div>
    </div>
    <div v-if="personOrigin.certainty" class="flex flex-col gap-2">
      <span class="text-xs font-semibold uppercase tracking-wide text-gray-500">
        Lokalisierbarkeit
      </span>
      <div>
        <span
          :class="`border border-gray-400 p-1.5 rounded-md text-xs font-bold uppercase tracking-wide shadow-sm ${originCertainty[personOrigin.certainty].color}`"
        >
          {{ originCertainty[personOrigin.certainty].label }}
        </span>
      </div>
    </div>
    <div
      v-if="hasGeoData"
      class="h-[220px] w-full rounded-lg border border-gray-300 shadow-inner overflow-hidden"
      :id="`person-${props.personId}_origin_map`"
    />
  </div>
</template>

<style scoped></style>
