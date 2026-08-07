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
      <header v-if="selectedBuilding" class="flex items-start justify-between gap-6 max-md:flex-col">
        <h1 class="text-[clamp(2.25rem,5vw,4.5rem)] font-[750] leading-[0.98] tracking-[-0.055em] text-highlighted">{{ buildingLabel(selectedBuilding) }}</h1>
        <div class="flex items-start gap-2">
            <div class="flex gap-[0.35rem]" aria-label="Gebäude wechseln">
            <button
              type="button"
              class="grid size-11 place-items-center rounded-xl border border-[var(--ui-border-accented)] text-highlighted transition-colors duration-150 hover:border-[#d9a441] hover:bg-elevated focus-visible:border-[#d9a441] focus-visible:bg-elevated focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-[0.35]"
              :disabled="!previousBuilding"
              aria-label="Vorheriges Gebäude"
              @click="navigateToBuilding(previousBuilding)"
            >
              <Icon name="material-symbols-arrow-back-rounded" aria-hidden="true" />
            </button>
            <button
              type="button"
              class="grid size-11 place-items-center rounded-xl border border-[var(--ui-border-accented)] text-highlighted transition-colors duration-150 hover:border-[#d9a441] hover:bg-elevated focus-visible:border-[#d9a441] focus-visible:bg-elevated focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-[0.35]"
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

      <div class="grid grid-cols-[minmax(0,1fr)] gap-4 lg:grid-cols-[minmax(0,1.5fr)_minmax(22rem,1fr)] lg:items-start">
        <section class="flex h-96 min-w-0 flex-col overflow-hidden rounded-2xl border-0 bg-default shadow-md lg:h-[31rem]">
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
        <main class="contents min-w-0">
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
