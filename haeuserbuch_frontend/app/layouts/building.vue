<script setup lang="ts">
import { computed } from "vue";
import {
  isBuildingFeature,
  type BuildingFeature,
  type Feature,
  type FeatureCollection,
} from "~/utils/GeoJsonTypes";

const route = useRoute();
const buildingStore = useBuildingStore();

const { data: buildings } = await useAsyncData(
  "building-route-map-features",
  () => buildingStore.getBuildings(),
  {
    default: (): FeatureCollection => ({
      type: "FeatureCollection",
      features: [],
    }),
  },
);

const buildingFeatures = computed<BuildingFeature[]>(() =>
  (buildings.value?.features ?? []).filter(
    (feature): feature is BuildingFeature => isBuildingFeature(feature as Feature),
  ),
);
const buildingId = computed(() => {
  const value = route.params.id;
  if (typeof value !== "string" || !/^\d+$/.test(value)) return null;
  const id = Number(value);
  return Number.isSafeInteger(id) ? id : null;
});
const selectedBuilding = computed(() =>
  buildingFeatures.value.find((feature) => feature.id === buildingId.value) ?? null,
);
const orderedBuildings = computed(() => {
  const collator = new Intl.Collator("de-DE", { numeric: true, sensitivity: "base" });
  return [...buildingFeatures.value].sort((left, right) => {
    const leftNumber = left.properties.districtPropertyNumber?.trim() ?? "";
    const rightNumber = right.properties.districtPropertyNumber?.trim() ?? "";
    if (!leftNumber || !rightNumber) {
      if (!leftNumber && rightNumber) return 1;
      if (leftNumber && !rightNumber) return -1;
    }
    return collator.compare(leftNumber, rightNumber) || left.id - right.id;
  });
});
const currentBuildingIndex = computed(() =>
  orderedBuildings.value.findIndex((feature) => feature.id === buildingId.value),
);
const previousBuilding = computed(() =>
  currentBuildingIndex.value > 0
    ? (orderedBuildings.value[currentBuildingIndex.value - 1] ?? null)
    : null,
);
const nextBuilding = computed(() =>
  currentBuildingIndex.value >= 0 &&
  currentBuildingIndex.value < orderedBuildings.value.length - 1
    ? (orderedBuildings.value[currentBuildingIndex.value + 1] ?? null)
    : null,
);

function buildingLabel(feature: BuildingFeature) {
  return (
    feature.properties.districtPropertyNumber ||
    feature.properties.object ||
    `Gebäude ${feature.id}`
  );
}

function navigateToBuilding(feature: BuildingFeature | null) {
  if (feature) navigateTo(`/buildings/${feature.id}`);
}

function handleMapSelect(id: number) {
  if (id !== buildingId.value) navigateTo(`/buildings/${id}`);
}
</script>

<template>
  <AppShell main-class="mx-auto flex w-full grow max-w-[120rem] flex-col px-4 py-8 sm:px-6 lg:px-8 lg:py-10">
      <div class="building-page flex min-h-full flex-col gap-6">
      <header v-if="selectedBuilding" class="building-page__header">
        <h1 class="building-title">{{ buildingLabel(selectedBuilding) }}</h1>
        <div class="flex items-start gap-2">
          <div class="building-navigation" aria-label="Gebäude wechseln">
            <button
              type="button"
              class="building-navigation__button"
              :disabled="!previousBuilding"
              aria-label="Vorheriges Gebäude"
              @click="navigateToBuilding(previousBuilding)"
            >
              <Icon name="material-symbols-arrow-back-rounded" aria-hidden="true" />
            </button>
            <button
              type="button"
              class="building-navigation__button"
              :disabled="!nextBuilding"
              aria-label="Nächstes Gebäude"
              @click="navigateToBuilding(nextBuilding)"
            >
              <Icon name="material-symbols-arrow-forward-rounded" aria-hidden="true" />
            </button>
          </div>
          <UIPageActionsTaskBar :id="buildingId ?? 0" entity_type="buildings" />
        </div>
      </header>

      <div class="building-bento-grid">
        <section class="bento-card building-map-card">
          <ClientOnly>
            <BuildingsMap
              :features="buildingFeatures"
              :filtered-ids="buildingId ? [buildingId] : []"
              :selected-id="buildingId"
              borderless
              aria-label="Karte mit Position des Gebäudes"
              @select="handleMapSelect"
            />
            <template #fallback><div class="h-full w-full animate-pulse bg-slate-300" /></template>
          </ClientOnly>
        </section>
        <main class="building-route__content">
          <slot />
        </main>
      </div>
      <UIContentMetadata
        v-if="selectedBuilding"
        :created-date="selectedBuilding.properties.createdDate"
        :last-modified-date="selectedBuilding.properties.lastModifiedDate"
      />
    </div>
  </AppShell>
</template>

<style scoped>
.building-page__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1.5rem;
}

.building-title {
  color: var(--ui-text-highlighted);
  font-size: clamp(2.25rem, 5vw, 4.5rem);
  font-weight: 750;
  letter-spacing: -0.055em;
  line-height: 0.98;
}

.building-navigation {
  display: flex;
  gap: 0.35rem;
}

.building-navigation__button {
  display: grid;
  width: 2.75rem;
  height: 2.75rem;
  place-items: center;
  border: 1px solid var(--ui-border-accented);
  border-radius: 0.75rem;
  color: var(--ui-text-highlighted);
  transition: background-color 150ms ease, border-color 150ms ease, opacity 150ms ease;
}

.building-navigation__button:hover:not(:disabled),
.building-navigation__button:focus-visible {
  border-color: #d9a441;
  background: var(--ui-bg-elevated);
  outline: none;
}

.building-navigation__button:disabled {
  cursor: not-allowed;
  opacity: 0.35;
}

.building-bento-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 1rem;
}

.bento-card {
  display: flex;
  min-width: 0;
  flex-direction: column;
  border: 1px solid rgb(209 213 219);
  border-radius: 1rem;
  background: var(--ui-bg);
  box-shadow: 0 8px 24px rgb(15 23 42 / 0.06);
}

.building-map-card {
  height: 24rem;
  overflow: hidden;
  border: 0;
}

.building-route__content {
  display: contents;
  min-width: 0;
}

.building-route__content :deep(.building-details--bento) {
  display: contents;
}

.building-route__content :deep(.building-details--bento > div) {
  display: contents;
}

@media (min-width: 1024px) {
  .building-bento-grid {
    grid-template-columns: minmax(0, 1.5fr) minmax(22rem, 1fr);
    align-items: start;
  }

  .building-map-card {
    height: 31rem;
  }
}

@media (max-width: 767px) {
  .building-page__header {
    flex-direction: column;
  }
}
</style>
