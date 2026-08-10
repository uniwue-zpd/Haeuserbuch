<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted } from "vue";
import maplibregl, {
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import { usePlaceStore } from "~/stores/PlaceStore";
import { DEFAULT_MAP_CENTER } from "~/utils/constant_values";
import type { Feature } from "~/utils/GeoJsonTypes";
import { initMap } from "~/service/map_init";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";
import FetchError from "~/components/UI/FetchError.vue";

const router = useRoute();
const placeId = Number(router.params.id);
const placeStore = usePlaceStore();
const person_store = usePersonStore();
const {
  data: placeItem,
  error: hasError,
  pending: isLoading,
} = await useAsyncData(`place-${placeId}-details`, () =>
  placeStore.getPlace(placeId),
);
const tile_store = useTileStore();
const geometry = computed(() => placeItem.value?.geometry as Point | null);
const properties = computed(
  () => placeItem.value?.properties as PlaceProperties | null,
);
const sources = computed(() => tile_store.baseSources);
const layers = computed(() => tile_store.baseLayers);

let map: maplibregl.Map | null = null;
const mapCenter = computed<[number, number]>(() =>
  geometry.value
    ? [geometry.value.coordinates[0], geometry.value.coordinates[1]]
    : DEFAULT_MAP_CENTER,
);
const { data: associatedPeople } = await useAsyncData(
  `place-${placeId}-associated_people`,
  () => person_store.filterPersons({ "place-of-origin-id": placeId }),
);

useHead(() => ({
  title: placeItem.value
    ? `${properties.value?.realName} - Orteverzeichnis`
    : "Nicht gefunden",
}));

onMounted(() => {
  if (hasError.value || !geometry.value) return;
  map = initMap(
    "map",
    mapCenter.value,
    5.5,
    sources.value as Record<string, RasterSourceSpecification>,
    // @ts-ignore
    layers.value as RasterLayerSpecification[],
    { fadeDuration: 0 },
  );
  map.on("load", () => {
    if (!placeItem.value) return;
    map!.addSource("place", {
      type: "geojson",
      // @ts-ignore
      data: placeItem.value as Feature,
    });
    map!.addLayer({
      id: "place",
      type: "circle",
      source: "place",
      paint: {
        "circle-radius": 8,
        "circle-color": "#3254a8",
        "circle-opacity": 0.8,
      },
    });
  });
});

onBeforeUnmount(() => {
  map?.remove();
  map = null;
});
</script>

<template>
  <UniversalSkeleton v-if="isLoading" />
  <FetchError v-else-if="hasError" :error="hasError" />
  <div v-else class="place-page flex min-h-full flex-col gap-8">
    <header class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <h1 class="mt-1 text-4xl font-bold tracking-tight text-highlighted">
          {{ properties?.realName }}
        </h1>
      </div>
      <TaskBar :id="placeId" entity_type="places" />
    </header>

    <div v-if="properties" class="grid grid-cols-1 auto-rows-auto gap-4 lg:auto-rows-auto lg:grid-cols-12">
      <section class="flex min-w-0 flex-col gap-5 overflow-hidden rounded-2xl border-0 bg-default p-0 shadow-md lg:col-span-8">
        <div
          v-if="geometry"
          id="map"
          class="h-96 w-full bg-elevated lg:h-124"
        />
        <div
          v-else
          class="flex min-h-80 flex-1 flex-col items-center justify-center gap-4 bg-warning/10 p-6 text-center"
        >
          <Icon name="material-symbols-location-off-outline" class="text-5xl text-warning" aria-hidden="true" />
          <p class="max-w-sm text-lg font-medium text-highlighted">
            Für diesen Ort sind bisher keine Geodaten hinterlegt
          </p>
        </div>
      </section>

      <section v-if="properties.altNames?.length" class="flex min-w-0 flex-col gap-5 self-start rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-4">
        <div class="flex items-start justify-between gap-4">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">Namensvarianten</h2>
        </div>
        <div class="flex flex-col">
          <div v-for="name in properties.altNames" :key="name" class="flex flex-col gap-2 border-t border-muted py-3 first:border-t-0 first:pt-0 last:pb-0">
            <p class="font-normal text-highlighted">{{ name }}</p>
          </div>
        </div>
      </section>

      <section
        v-if="properties.generalNotes"
        class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-8"
      >
        <div class="flex items-start justify-between gap-4">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">Notizen und Anmerkungen</h2>
        </div>
        <div class="flex flex-col">
          <div class="flex flex-col gap-2 border-t border-muted py-3 first:border-t-0 first:pt-0 last:pb-0">
             <p class="text-sm font-semibold text-muted">Notizen allgemein</p>
            <p class="whitespace-pre-wrap text-highlighted">{{ properties.generalNotes }}</p>
          </div>
        </div>
      </section>

       <section class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-7">
         <div class="flex items-start justify-between gap-4">
           <h2 class="text-xl font-semibold leading-tight text-highlighted">Beziehungen zu anderen Entitäten</h2>
        </div>
         <div class="flex flex-col">
           <div class="flex flex-col gap-2 border-t border-muted py-3 first:border-t-0 first:pt-0 last:pb-0">
              <p class="text-sm font-semibold text-muted">Möglicher Herkunftsort von</p>
            <div v-if="associatedPeople?.length" class="flex flex-wrap gap-2">
              <NuxtLink
                v-for="person in associatedPeople"
                :key="person.id ?? person.fullName ?? 'person'"
                :to="`/persons/${person.id}`"
                 class="inline-flex max-w-full rounded-lg border border-accented px-2 py-2 text-sm font-semibold leading-tight text-highlighted transition-colors duration-150 hover:border-accented hover:bg-elevated"
              >
                {{ person.fullName || `Person mit ID ${person.id}` }}
              </NuxtLink>
            </div>
            <p v-else class="text-sm text-muted">Bisher keine Relationen gefunden</p>
          </div>
        </div>
      </section>

    </div>
    <UIContentMetadata
      :created-date="properties?.createdDate"
      :last-modified-date="properties?.lastModifiedDate"
    />
  </div>
</template>
