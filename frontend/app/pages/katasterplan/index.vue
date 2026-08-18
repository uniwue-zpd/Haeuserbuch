<script setup lang="ts">
import { isBuildingFeature, type BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";

definePageMeta({ layout: "map" });

const route = useRoute();
const router = useRouter();
const buildingStore = useBuildingStore();

const {
  data: buildings,
  error: buildingsError,
  pending: buildingsPending,
  refresh: refreshBuildings,
} = useAsyncData(
  "buildings-feature-collection",
  () => buildingStore.getBuildings(),
  { lazy: true },
);

const search = ref("");
const debouncedSearch = ref("");
const district = ref("");
const quarter = ref("");
const objectType = ref("");
const georeferenced = ref("");
const notice = ref<string | null>(null);
const isDesktop = ref(false);
const viewportHeight = ref(800);
const sheetState = ref<"collapsed" | "half" | "full">("half");
const draggedSheetHeight = ref<number | null>(null);

const collator = new Intl.Collator("de-DE", {
  numeric: true,
  sensitivity: "base",
});
let searchTimer: ReturnType<typeof setTimeout> | null = null;
let desktopMedia: MediaQueryList | null = null;
let noticeTimer: ReturnType<typeof setTimeout> | null = null;
let dragStartY = 0;
let dragStartHeight = 0;
let dragMoved = false;

const buildingFeatures = computed<BuildingFeature[]>(() =>
  (buildings.value?.features ?? []).filter(isBuildingFeature),
);
const buildingById = computed(
  () => new Map(buildingFeatures.value.map((feature) => [feature.id, feature])),
);
const districtOptions = computed(() =>
  Array.from(
    new Set(
      buildingFeatures.value
        .map((feature) => feature.properties.district?.name)
        .filter(Boolean) as string[],
    ),
  ).sort(collator.compare),
);
const quarterOptions = computed(() =>
  Array.from(
    new Set(
      buildingFeatures.value
        .map((feature) => feature.properties.quarter?.name)
        .filter(Boolean) as string[],
    ),
  ).sort(collator.compare),
);
const objectTypeOptions = computed(() =>
  Array.from(
    new Set(
      buildingFeatures.value
        .map((feature) => feature.properties.object)
        .filter(Boolean) as string[],
    ),
  ).sort(collator.compare),
);

function normalize(value: string | number | null | undefined) {
  return String(value ?? "")
    .normalize("NFD")
    .replace(/\p{Diacritic}/gu, "")
    .toLocaleLowerCase("de-DE")
    .trim();
}

const searchIndex = computed(
  () =>
    new Map(
      buildingFeatures.value.map((feature) => {
        const properties = feature.properties;
        const addresses = properties.addresses.flatMap((address) => [
          address.street?.name,
          address.houseNumber,
        ]);
        const values = [
          properties.districtPropertyNumber,
          properties.propertyNumber,
          ...properties.names.map((name) => name.name),
          properties.object,
          properties.partType,
          properties.district?.name,
          properties.quarter?.name,
          ...addresses,
        ];
        return [feature.id, normalize(values.filter(Boolean).join(" "))];
      }),
    ),
);

function designation(feature: BuildingFeature) {
  return (
    feature.properties.districtPropertyNumber ||
    feature.properties.names.find((name) => name.name)?.name ||
    feature.properties.object ||
    `Gebäude ${feature.id}`
  );
}

const filteredBuildings = computed(() => {
  const query = normalize(debouncedSearch.value);
  return buildingFeatures.value
    .filter((feature) => {
      if (query && !searchIndex.value.get(feature.id)?.includes(query))
        return false;
      if (
        district.value &&
        feature.properties.district?.name !== district.value
      )
        return false;
      if (quarter.value && feature.properties.quarter?.name !== quarter.value)
        return false;
      if (objectType.value && feature.properties.object !== objectType.value)
        return false;
      if (georeferenced.value === "yes" && !feature.geometry) return false;
      if (georeferenced.value === "no" && feature.geometry) return false;
      return true;
    })
    .sort((left, right) => {
      const leftNamed = Boolean(left.properties.districtPropertyNumber);
      const rightNamed = Boolean(right.properties.districtPropertyNumber);
      if (leftNamed !== rightNamed) return leftNamed ? -1 : 1;
      return (
        collator.compare(designation(left), designation(right)) ||
        left.id - right.id
      );
    });
});
const filteredBuildingIds = computed(() =>
  filteredBuildings.value.map((feature) => feature.id),
);

function parseBuildingId(value: typeof route.query.building) {
  if (typeof value !== "string" || !/^\d+$/.test(value)) return null;
  const id = Number(value);
  return Number.isSafeInteger(id) ? id : null;
}

const initialBuildingId = parseBuildingId(route.query.building);
const selectedId = ref<number | null>(
  initialBuildingId && buildingById.value.has(initialBuildingId)
    ? initialBuildingId
    : null,
);
const selectedBuilding = computed(() =>
  selectedId.value ? (buildingById.value.get(selectedId.value) ?? null) : null,
);
const selectedIndex = computed(() =>
  selectedId.value
    ? filteredBuildings.value.findIndex(
        (feature) => feature.id === selectedId.value,
      )
    : -1,
);
const hasPrevious = computed(() => selectedIndex.value > 0);
const hasNext = computed(
  () =>
    selectedIndex.value >= 0 &&
    selectedIndex.value < filteredBuildings.value.length - 1,
);

const {
  data: associatedPeople,
  error: associatedPeopleError,
  pending: associatedPeoplePending,
  refresh: refreshAssociatedPeople,
} = useAsyncData(
  "building-explorer-associated-people",
  () =>
    selectedId.value
      ? $fetch<PersonPreviewDTO[]>("/api/persons/filter", {
          query: { "associated-building-id": selectedId.value },
        })
      : Promise.resolve([]),
  { default: () => [], lazy: true, watch: [selectedId] },
);

const sheetSnapHeights = computed(() => ({
  collapsed: 72,
  half: Math.round(viewportHeight.value * 0.45),
  full: Math.round(viewportHeight.value * 0.88),
}));
const sheetHeight = computed(
  () => draggedSheetHeight.value ?? sheetSnapHeights.value[sheetState.value],
);
const mapBottomPadding = computed(() =>
  isDesktop.value ? 0 : sheetHeight.value,
);

useHead(() => ({ title: "Gebäudezuordnung und Beschreibung auf der Grundlage des Urkatasterplans von 1832" }));

function updateQueryBuilding(id: number | null) {
  if (!import.meta.client) return;
  const url = new URL(window.location.href);
  const currentLocation = `${url.pathname}${url.search}${url.hash}`;
  if (id === null) url.searchParams.delete("building");
  else url.searchParams.set("building", String(id));
  const nextLocation = `${url.pathname}${url.search}${url.hash}`;
  if (nextLocation === currentLocation) return;

  // Selection is local UI state. Pushing through the history adapter preserves
  // deep links and Back/Forward without running a full Nuxt navigation per click.
  router.options.history.push(nextLocation);
}

function selectBuilding(id: number) {
  if (!buildingById.value.has(id)) return;
  selectedId.value = id;
  sheetState.value = "full";
  updateQueryBuilding(id);
  resetExplorerScroll();
}

function deselectBuilding() {
  selectedId.value = null;
  sheetState.value = "half";
  updateQueryBuilding(null);
  resetExplorerScroll();
}

function resetExplorerScroll() {
  if (!import.meta.client || isDesktop.value) return;
  requestAnimationFrame(() => window.scrollTo({ top: 0, behavior: "auto" }));
  window.setTimeout(() => window.scrollTo({ top: 0, behavior: "auto" }), 340);
}

function selectPrevious() {
  if (!hasPrevious.value) return;
  selectBuilding(filteredBuildings.value[selectedIndex.value - 1]!.id);
}

function selectNext() {
  if (!hasNext.value) return;
  selectBuilding(filteredBuildings.value[selectedIndex.value + 1]!.id);
}

function clearFilters() {
  search.value = "";
  debouncedSearch.value = "";
  district.value = "";
  quarter.value = "";
  objectType.value = "";
  georeferenced.value = "";
}

function showNotice(message: string) {
  notice.value = message;
  if (noticeTimer) clearTimeout(noticeTimer);
  noticeTimer = setTimeout(() => {
    notice.value = null;
  }, 5000);
}

function cycleSheet() {
  if (dragMoved) {
    dragMoved = false;
    return;
  }
  sheetState.value =
    sheetState.value === "collapsed"
      ? "half"
      : sheetState.value === "half"
        ? "full"
        : "collapsed";
}

function startSheetDrag(event: PointerEvent) {
  if (isDesktop.value) return;
  dragStartY = event.clientY;
  dragStartHeight = sheetHeight.value;
  dragMoved = false;
  window.addEventListener("pointermove", moveSheetDrag);
  window.addEventListener("pointerup", endSheetDrag, { once: true });
}

function moveSheetDrag(event: PointerEvent) {
  const movement = dragStartY - event.clientY;
  if (Math.abs(movement) > 6) dragMoved = true;
  draggedSheetHeight.value = Math.min(
    sheetSnapHeights.value.full,
    Math.max(sheetSnapHeights.value.collapsed, dragStartHeight + movement),
  );
}

function endSheetDrag() {
  window.removeEventListener("pointermove", moveSheetDrag);
  const current = draggedSheetHeight.value ?? dragStartHeight;
  const snaps = Object.entries(sheetSnapHeights.value) as Array<
    ["collapsed" | "half" | "full", number]
  >;
  sheetState.value = snaps.reduce((closest, candidate) =>
    Math.abs(candidate[1] - current) < Math.abs(closest[1] - current)
      ? candidate
      : closest,
  )[0];
  draggedSheetHeight.value = null;
}

function updateViewport() {
  viewportHeight.value = window.innerHeight;
  isDesktop.value = Boolean(desktopMedia?.matches);
}

function onKeydown(event: KeyboardEvent) {
  if (event.key === "Escape" && selectedId.value) deselectBuilding();
}

watch(search, (value) => {
  if (searchTimer) clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    debouncedSearch.value = value;
  }, 180);
});

function validateBuildingQuery(value: typeof route.query.building) {
  if (buildingsPending.value) return;
  if (value === undefined) {
    selectedId.value = null;
    return;
  }
  const id = parseBuildingId(value);
  if (id && buildingById.value.has(id)) {
    selectedId.value = id;
    return;
  }
  selectedId.value = null;
  if (import.meta.client) {
    const url = new URL(window.location.href);
    url.searchParams.delete("building");
    router.options.history.replace(`${url.pathname}${url.search}${url.hash}`);
  }
  showNotice("Das angeforderte Gebäude konnte nicht gefunden werden.");
}

function syncSelectionFromLocation() {
  const value = new URL(window.location.href).searchParams.get("building");
  if (value === null) {
    selectedId.value = null;
    return;
  }
  validateBuildingQuery(value);
}

watch(() => route.query.building, validateBuildingQuery);

watch(buildingsPending, (pending) => {
  if (!pending) validateBuildingQuery(route.query.building);
});

watch(selectedId, (id) => {
  if (id && !isDesktop.value) sheetState.value = "full";
});

onMounted(() => {
  desktopMedia = window.matchMedia("(min-width: 1024px)");
  updateViewport();
  desktopMedia.addEventListener("change", updateViewport);
  window.addEventListener("resize", updateViewport);
  window.addEventListener("keydown", onKeydown);
  window.addEventListener("popstate", syncSelectionFromLocation);
  validateBuildingQuery(route.query.building);
});

onBeforeUnmount(() => {
  if (searchTimer) clearTimeout(searchTimer);
  if (noticeTimer) clearTimeout(noticeTimer);
  desktopMedia?.removeEventListener("change", updateViewport);
  window.removeEventListener("resize", updateViewport);
  window.removeEventListener("keydown", onKeydown);
  window.removeEventListener("popstate", syncSelectionFromLocation);
  window.removeEventListener("pointermove", moveSheetDrag);
  window.removeEventListener("pointerup", endSheetDrag);
});
</script>

<template>
  <section
    class="relative h-[calc(100svh-79px)] min-h-140 w-full overflow-hidden bg-elevated"
  >
    <div
      v-if="notice"
      class="absolute left-1/2 top-4 z-40 flex max-w-full -translate-x-1/2 items-center gap-2 rounded-xl bg-slate-800 px-4 py-3 text-sm font-semibold text-white shadow-xl"
      role="status"
    >
      <Icon
        name="material-symbols-info-outline-rounded"
        class="shrink-0 text-xl text-amber-500"
      />
      {{ notice }}
      <button
        type="button"
        class="ml-2"
        aria-label="Hinweis schließen"
        @click="notice = null"
      >
        <Icon name="material-symbols-close-rounded" class="text-xl" />
      </button>
    </div>

    <div
      v-if="buildingsError"
      class="flex h-full items-center justify-center bg-default p-6"
    >
      <div
        class="max-w-md rounded-2xl border border-error/30 bg-default p-7 text-center shadow-xl"
      >
        <Icon
          name="material-symbols-error-outline-rounded"
          class="text-5xl text-error"
        />
        <h1 class="mt-3 text-2xl font-bold text-highlighted">
          Gebäude konnten nicht geladen werden
        </h1>
        <p class="mt-2 text-sm text-muted">
          Bitte versuchen Sie es erneut.
        </p>
        <button
          type="button"
          class="mt-5 rounded-lg bg-inverted px-4 py-2 font-bold text-inverted"
          @click="refreshBuildings()"
        >
          Erneut versuchen
        </button>
      </div>
    </div>

    <div
      v-else
      class="grid h-full min-h-0 grid-cols-1 lg:grid-cols-[minmax(0,1fr)_560px]"
    >
      <div class="relative min-h-0 overflow-hidden">
        <ClientOnly>
          <BuildingsMap
            :features="buildingFeatures"
            :filtered-ids="filteredBuildingIds"
            :selected-id="selectedId"
            :selection-bottom-padding="mapBottomPadding"
            @select="selectBuilding"
          />
          <template #fallback
            ><div class="h-full w-full animate-pulse bg-slate-300"
          /></template>
        </ClientOnly>
      </div>

      <div
         class="absolute inset-x-0 bottom-0 z-30 h-[var(--sheet-height)] min-h-18 overflow-anchor-none overflow-hidden rounded-t-2xl border-t border-default bg-default shadow-xl transition-all duration-300 ease-out lg:static lg:inset-auto lg:z-auto lg:h-full lg:rounded-none lg:border-l lg:border-t lg:shadow-lg"
        :class="draggedSheetHeight !== null ? '!duration-0' : ''"
        :style="{ '--sheet-height': `${sheetHeight}px` }"
      >
        <button
          type="button"
          class="flex h-7 w-full touch-none cursor-ns-resize items-center justify-center bg-default focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-amber-500 lg:hidden"
          :aria-label="`Gebäudebereich ${sheetState === 'collapsed' ? 'öffnen' : sheetState === 'half' ? 'vergrößern' : 'einklappen'}`"
          @pointerdown="startSheetDrag"
          @click="cycleSheet"
        >
          <span
            class="h-1.5 w-12 rounded-full bg-slate-300"
            aria-hidden="true"
          />
        </button>
        <div class="h-full min-h-0 lg:h-full">
          <BuildingsSidebar
            :buildings="filteredBuildings"
            :buildings-pending="buildingsPending"
            :selected-building="selectedBuilding"
            :search="search"
            :district="district"
            :quarter="quarter"
            :object-type="objectType"
            :georeferenced="georeferenced"
            :district-options="districtOptions"
            :quarter-options="quarterOptions"
            :object-type-options="objectTypeOptions"
            :associated-people="associatedPeople"
            :associated-people-pending="associatedPeoplePending"
            :associated-people-error="Boolean(associatedPeopleError)"
            :has-previous="hasPrevious"
            :has-next="hasNext"
            @update:search="search = $event"
            @update:district="district = $event"
            @update:quarter="quarter = $event"
            @update:object-type="objectType = $event"
            @update:georeferenced="georeferenced = $event"
            @select="selectBuilding"
            @deselect="deselectBuilding"
            @previous="selectPrevious"
            @next="selectNext"
            @clear-filters="clearFilters"
            @retry-relationships="refreshAssociatedPeople"
          />
        </div>
      </div>
    </div>
  </section>
</template>
