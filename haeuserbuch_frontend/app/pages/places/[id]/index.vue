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
  <div v-else class="place-page">
    <header class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <p class="text-sm font-medium text-muted">Orteverzeichnis</p>
        <h1 class="mt-1 text-4xl font-bold tracking-tight text-highlighted">
          {{ properties?.realName }}
        </h1>
      </div>
      <TaskBar :id="placeId" entity_type="places" />
    </header>

    <div v-if="properties" class="place-bento-grid">
      <section class="bento-card place-map-card lg:col-span-8 lg:row-span-2">
        <div class="bento-card-heading">
          <div>
            <p class="bento-eyebrow">Räumliche Einordnung</p>
            <h2>Standort</h2>
          </div>
          <Icon name="material-symbols-public" class="text-2xl text-muted" aria-hidden="true" />
        </div>
        <div
          v-if="geometry"
          id="map"
          class="h-[320px] w-full overflow-hidden rounded-lg bg-elevated sm:h-[420px] lg:h-full lg:min-h-[520px]"
        />
        <div
          v-else
          class="flex min-h-[320px] flex-col items-center justify-center gap-4 rounded-lg bg-warning/10 p-6 text-center sm:min-h-[420px] lg:min-h-[520px]"
        >
          <Icon name="material-symbols-location-off-outline" class="text-5xl text-warning" aria-hidden="true" />
          <p class="max-w-sm text-lg font-medium text-highlighted">
            Für diesen Ort sind bisher keine Geodaten hinterlegt
          </p>
        </div>
      </section>

      <section class="bento-card lg:col-span-4">
        <div class="bento-card-heading">
          <h2>Informationen zum Ort</h2>
          <Icon name="material-symbols-info-outline" class="text-2xl text-muted" aria-hidden="true" />
        </div>
        <div class="detail-list">
          <div v-if="properties.type" class="detail-row">
            <p class="detail-label">Typ</p>
            <p class="font-semibold text-highlighted">{{ properties.type }}</p>
          </div>
          <div class="detail-row">
            <p class="detail-label">Georeferenziert</p>
            <p class="font-semibold text-highlighted">{{ geometry ? "Ja" : "Nein" }}</p>
          </div>
          <div v-if="properties.isUncertain !== null" class="detail-row">
            <p class="detail-label">Ortszuordnung</p>
            <p class="font-semibold text-highlighted">
              {{ properties.isUncertain ? "Unsicher" : "Gesichert" }}
            </p>
          </div>
        </div>
      </section>

      <section v-if="properties.altNames?.length" class="bento-card lg:col-span-4">
        <div class="bento-card-heading">
          <h2>Namensvarianten</h2>
          <Icon name="material-symbols-history" class="text-2xl text-muted" aria-hidden="true" />
        </div>
        <div class="detail-list">
          <div v-for="name in properties.altNames" :key="name" class="detail-row">
            <p class="font-semibold text-highlighted">{{ name }}</p>
          </div>
        </div>
      </section>

      <section
        v-if="properties.generalNotes || properties.internalNotes"
        class="bento-card lg:col-span-8"
      >
        <div class="bento-card-heading">
          <h2>Notizen und Anmerkungen</h2>
          <Icon name="material-symbols-sticky-note-2-outline" class="text-2xl text-muted" aria-hidden="true" />
        </div>
        <div class="detail-list">
          <div v-if="properties.generalNotes" class="detail-row">
            <p class="detail-label">Anmerkungen</p>
            <p class="whitespace-pre-wrap text-highlighted">{{ properties.generalNotes }}</p>
          </div>
          <div v-if="properties.internalNotes" class="detail-row">
            <p class="detail-label">Notizen</p>
            <p class="whitespace-pre-wrap text-highlighted">{{ properties.internalNotes }}</p>
          </div>
        </div>
      </section>

      <section class="bento-card lg:col-span-7">
        <div class="bento-card-heading">
          <h2>Beziehungen zu anderen Entitäten</h2>
          <Icon name="material-symbols-group-outline" class="text-2xl text-muted" aria-hidden="true" />
        </div>
        <div class="detail-list">
          <div class="detail-row">
            <p class="detail-label">Möglicher Herkunftsort von</p>
            <div v-if="associatedPeople?.length" class="flex flex-wrap gap-2">
              <NuxtLink
                v-for="person in associatedPeople"
                :key="person.id ?? person.fullName ?? 'person'"
                :to="`/persons/${person.id}`"
                class="detail-link"
              >
                {{ person.fullName || `Person mit ID ${person.id}` }}
              </NuxtLink>
            </div>
            <p v-else class="text-sm text-muted">Bisher keine Relationen gefunden</p>
          </div>
        </div>
      </section>

      <section class="bento-card lg:col-span-5">
        <div class="bento-card-heading">
          <h2>Über den Eintrag</h2>
          <Icon name="material-symbols-history-edu" class="text-2xl text-muted" aria-hidden="true" />
        </div>
        <div class="detail-list">
          <div v-if="properties.createdDate" class="detail-row">
            <p class="detail-label">Erstellt am</p>
            <p class="text-highlighted">{{ new Date(properties.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="properties.lastModifiedDate" class="detail-row">
            <p class="detail-label">Zuletzt aktualisiert am</p>
            <p class="text-highlighted">{{ new Date(properties.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.place-page {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.place-bento-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 1rem;
  grid-auto-rows: minmax(0, auto);
}

.bento-card {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 1.25rem;
  border: 1px solid rgb(209 213 219);
  border-radius: 1rem;
  background: var(--ui-bg);
  padding: 1.25rem;
  box-shadow: 0 8px 24px rgb(15 23 42 / 0.06);
}

.bento-card-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.bento-card-heading h2 {
  color: var(--ui-text-highlighted);
  font-size: 1.2rem;
  font-weight: 650;
  line-height: 1.25;
}

.bento-eyebrow {
  margin-bottom: 0.25rem;
  color: var(--ui-text-muted);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.detail-list {
  display: flex;
  flex-direction: column;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
  border-top: 1px solid var(--ui-border-muted);
  padding: 0.85rem 0;
}

.detail-row:first-child {
  border-top: 0;
  padding-top: 0;
}

.detail-row:last-child {
  padding-bottom: 0;
}

.detail-label {
  color: var(--ui-text-muted);
  font-size: 0.8125rem;
  font-weight: 600;
}

.detail-link {
  display: inline-flex;
  max-width: 100%;
  border: 1px solid var(--ui-border-accented);
  border-radius: 0.5rem;
  padding: 0.45rem 0.65rem;
  color: var(--ui-text-highlighted);
  font-size: 0.875rem;
  font-weight: 600;
  line-height: 1.25;
  transition: background-color 150ms ease, border-color 150ms ease;
}

.detail-link:hover {
  border-color: var(--ui-border-accented);
  background: var(--ui-bg-elevated);
}

@media (min-width: 1024px) {
  .place-bento-grid {
    grid-template-columns: repeat(12, minmax(0, 1fr));
    grid-auto-rows: minmax(10rem, auto);
  }
}
</style>
