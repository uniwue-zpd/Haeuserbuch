<script setup lang="ts">
import { useVirtualizer } from "@tanstack/vue-virtual";
import { computed, nextTick, ref, watch } from "vue";
import type { BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";

const props = withDefaults(
  defineProps<{
    buildings: BuildingFeature[];
    buildingsPending?: boolean;
    selectedBuilding?: BuildingFeature | null;
    search: string;
    district: string;
    quarter: string;
    objectType: string;
    georeferenced: string;
    districtOptions: string[];
    quarterOptions: string[];
    objectTypeOptions: string[];
    associatedPeople?: PersonPreviewDTO[] | null;
    associatedPeoplePending?: boolean;
    associatedPeopleError?: boolean;
    hasPrevious?: boolean;
    hasNext?: boolean;
  }>(),
  {
    buildingsPending: false,
    selectedBuilding: null,
    associatedPeople: () => [],
    associatedPeoplePending: false,
    associatedPeopleError: false,
    hasPrevious: false,
    hasNext: false,
  },
);

const emit = defineEmits<{
  "update:search": [value: string];
  "update:district": [value: string];
  "update:quarter": [value: string];
  "update:objectType": [value: string];
  "update:georeferenced": [value: string];
  select: [id: number];
  deselect: [];
  previous: [];
  next: [];
  clearFilters: [];
  retryRelationships: [];
}>();

const listViewport = ref<HTMLElement | null>(null);
const itemHeight = 112;
const allOptionValue = "__all__";

const searchModel = computed({
  get: () => props.search,
  set: (value) => emit("update:search", value),
});
const districtModel = computed({
  get: () => props.district || allOptionValue,
  set: (value) =>
    emit("update:district", value === allOptionValue ? "" : value),
});
const quarterModel = computed({
  get: () => props.quarter || allOptionValue,
  set: (value) => emit("update:quarter", value === allOptionValue ? "" : value),
});
const objectTypeModel = computed({
  get: () => props.objectType || allOptionValue,
  set: (value) =>
    emit("update:objectType", value === allOptionValue ? "" : value),
});
const georeferencedModel = computed({
  get: () => props.georeferenced || allOptionValue,
  set: (value) =>
    emit("update:georeferenced", value === allOptionValue ? "" : value),
});
const districtSelectItems = computed(() => [
  { label: "Alle Distrikte", value: allOptionValue },
  ...props.districtOptions.map((value) => ({ label: value, value })),
]);
const quarterSelectItems = computed(() => [
  { label: "Alle Viertel", value: allOptionValue },
  ...props.quarterOptions.map((value) => ({ label: value, value })),
]);
const objectTypeSelectItems = computed(() => [
  { label: "Alle Objekttypen", value: allOptionValue },
  ...props.objectTypeOptions.map((value) => ({ label: value, value })),
]);
const georeferencedSelectItems = [
  { label: "Alle Gebäude", value: allOptionValue },
  { label: "Kartiert", value: "yes" },
  { label: "Ohne Geodaten", value: "no" },
];

const buildingVirtualizerOptions = computed(() => ({
  count: props.buildings.length,
  getScrollElement: () => listViewport.value,
  estimateSize: () => itemHeight,
  getItemKey: (index: number) => props.buildings[index]?.id ?? index,
  overscan: 5,
}));
const buildingVirtualizer = useVirtualizer(buildingVirtualizerOptions);
const activeFilters = computed(() =>
  Boolean(
    props.search ||
      props.district ||
      props.quarter ||
      props.objectType ||
      props.georeferenced,
  ),
);

function buildingTitle(building: BuildingFeature) {
  return (
    building.properties.districtPropertyNumber ||
    building.properties.names.find((name) => name.name)?.name ||
    building.properties.object ||
    `Gebäude ${building.id}`
  );
}

function primaryAddress(building: BuildingFeature) {
  const current = building.properties.addresses.find(
    (address) => String(address.fromDate ?? "") === "2025",
  );
  const address = current ?? building.properties.addresses[0];
  return address
    ? `${address.street?.name ?? "Unbekannte Straße"} ${address.houseNumber ?? ""}`.trim()
    : null;
}

watch(
  () => props.buildings,
  async () => {
    await nextTick();
    buildingVirtualizer.value.scrollToOffset(0);
  },
);
</script>

<template>
  <aside
    class="flex h-full min-h-0 flex-col overflow-hidden bg-default text-default"
    aria-label="Gebäudeverzeichnis"
    :aria-busy="buildingsPending"
  >
    <template v-if="buildingsPending">
      <div
        class="animate-pulse border-b border-muted bg-default px-4 pb-4 pt-5"
      >
        <div class="h-7 w-44 rounded bg-elevated" />
        <div class="mt-4 h-10 w-full rounded bg-elevated" />
        <div class="mt-3 grid grid-cols-2 gap-2 sm:grid-cols-4">
          <div
            v-for="index in 3"
            :key="index"
            class="h-11 rounded bg-elevated"
          />
        </div>
      </div>
      <div
        class="border-b border-muted bg-default px-4 py-2.5 text-sm font-semibold text-muted"
      >
        Gebäude werden geladen
      </div>
      <div
        class="min-h-0 flex-1 overflow-hidden bg-default px-4 py-3"
        role="status"
        aria-live="polite"
      >
        <div
          v-for="index in 5"
          :key="index"
          class="h-28 border-b border-muted py-3"
        >
          <div class="h-5 w-3/5 rounded bg-elevated" />
          <div class="mt-3 h-4 w-2/5 rounded bg-elevated" />
          <div class="mt-2 h-3 w-4/5 rounded bg-elevated" />
        </div>
        <span class="sr-only">Gebäude werden geladen</span>
      </div>
    </template>

    <template v-else-if="selectedBuilding">
      <div class="z-10 border-b border-muted bg-default px-4 py-3">
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0">
            <p class="text-xs font-medium text-muted">Ausgewähltes Gebäude</p>
            <h1 class="mt-0.5 truncate text-xl font-bold text-highlighted">
              {{ buildingTitle(selectedBuilding) }}
            </h1>
          </div>
          <UButton
            color="neutral"
            variant="ghost"
            icon="i-material-symbols-close-rounded"
            aria-label="Auswahl schließen"
            title="Auswahl schließen"
            class="shrink-0"
            @click="emit('deselect')"
          />
        </div>

        <div class="mt-2 flex items-center gap-1">
          <UButton
            color="neutral"
            variant="ghost"
            icon="i-material-symbols-arrow-back-rounded"
            :disabled="!hasPrevious"
            aria-label="Vorheriges Gebäude"
            label="Zurück"
            @click="emit('previous')"
          />
          <UButton
            color="neutral"
            variant="ghost"
            trailing-icon="i-material-symbols-arrow-forward-rounded"
            :disabled="!hasNext"
            aria-label="Nächstes Gebäude"
            label="Weiter"
            @click="emit('next')"
          />
          <UButton
            :to="`/katasterplan/${selectedBuilding.id}`"
            color="neutral"
            variant="link"
            trailing-icon="i-material-symbols-open-in-new-rounded"
            label="Vollständige Seite"
            class="ml-auto"
          />
        </div>
      </div>

      <div class="min-h-0 flex-1 overflow-y-auto bg-default px-4">
        <UAlert
          v-if="!selectedBuilding.geometry"
          color="warning"
          variant="subtle"
          icon="i-material-symbols-location-off-outline"
          description="Für dieses Gebäude sind bisher keine Geodaten hinterlegt."
          class="mt-4"
        />
        <BuildingsDetails
          :building="selectedBuilding"
          :associated-people="associatedPeople"
          :associated-people-pending="associatedPeoplePending"
          :associated-people-error="associatedPeopleError"
          compact
          @retry-relationships="emit('retryRelationships')"
        />
      </div>
    </template>

    <template v-else>
      <div class="z-10 border-b border-muted bg-default px-4 pb-4 pt-5">
        <div class="flex items-end justify-between gap-3">
          <h1 class="mt-0.5 text-2xl font-bold text-highlighted">
            Gebäudezuordnung und Beschreibung auf der Grundlage des Urkatasterplans von 1832
          </h1>
          <UModal
            title="Die Würzburger Uraufnahme von 1832"
            scrollable
            :ui="{ content: 'max-w-7xl', body: 'p-5 sm:p-8' }"
          >
            <UButton
              color="neutral"
              variant="ghost"
              icon="i-lucide-info"
              label="Info"
              aria-label="Informationen zur Würzburger Uraufnahme"
              title="Informationen zur Würzburger Uraufnahme"
            />
            <template #body>
              <BuildingsDescriptionContent/>
            </template>
          </UModal>
        </div>

        <UInput
          v-model="searchModel"
          type="search"
          icon="i-material-symbols-search-rounded"
          size="lg"
          variant="outline"
          placeholder="Nummer, Name, Adresse …"
          aria-label="Gebäude durchsuchen"
          class="mt-4 w-full"
        />

        <div class="mt-3 grid grid-cols-3 gap-2">
          <UFormField
            label="Distrikt"
            :ui="{ label: 'text-xs font-semibold text-muted' }"
          >
            <USelect
              v-model="districtModel"
              :items="districtSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="Viertel"
            :ui="{ label: 'text-xs font-semibold text-muted' }"
          >
            <USelect
              v-model="quarterModel"
              :items="quarterSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="Objekttyp"
            :ui="{ label: 'text-xs font-semibold text-muted' }"
          >
            <USelect
              v-model="objectTypeModel"
              :items="objectTypeSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="Geodaten"
            :ui="{ label: 'text-xs font-semibold text-muted' }"
          >
            <USelect
              v-model="georeferencedModel"
              :items="georeferencedSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
        </div>

        <UButton
          v-if="activeFilters"
          color="neutral"
          variant="link"
          icon="i-material-symbols-filter-alt-off-outline-rounded"
          label="Filter zurücksetzen"
          class="mt-2 px-0"
          @click="emit('clearFilters')"
        />
      </div>

      <div
        class="border-b border-muted bg-default px-4 py-2.5 text-sm font-semibold text-muted"
        aria-live="polite"
      >
        Gebäude ({{ buildings.length }})
      </div>

      <div
        ref="listViewport"
        class="min-h-0 flex-1 overflow-y-auto overscroll-contain bg-default"
        role="listbox"
        aria-label="Gefilterte Gebäude"
      >
        <div
          v-if="buildings.length"
          class="relative w-full"
          :style="{ height: `${buildingVirtualizer.getTotalSize()}px` }"
        >
          <div
            class="absolute inset-x-0 top-0"
            :style="{
              transform: `translateY(${buildingVirtualizer.getVirtualItems()[0]?.start ?? 0}px)`,
            }"
          >
            <template
              v-for="virtualRow in buildingVirtualizer.getVirtualItems()"
              :key="virtualRow.key"
            >
              <div v-if="buildings[virtualRow.index]" class="h-28">
                <button
                  type="button"
                  role="option"
                  :aria-selected="false"
                    class="group flex h-full w-full flex-col justify-center border-b border-muted !p-4 text-left transition-colors hover:bg-elevated focus-visible:bg-elevated focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-gray-400"
                  @click="emit('select', buildings[virtualRow.index]!.id)"
                >
                  <div class="flex items-start justify-between gap-2">
                    <h2 class="line-clamp-1 font-bold text-highlighted">
                      {{ buildingTitle(buildings[virtualRow.index]!) }}
                    </h2>
                    <UBadge
                      :color="
                        buildings[virtualRow.index]!.geometry
                          ? 'success'
                          : 'neutral'
                      "
                      :icon="
                        buildings[virtualRow.index]!.geometry
                          ? 'i-material-symbols-location-on-outline-rounded'
                          : 'i-material-symbols-location-off-outline-rounded'
                      "
                      :label="
                        buildings[virtualRow.index]!.geometry
                          ? 'Kartiert'
                          : 'Ohne Geodaten'
                      "
                      variant="subtle"
                      size="sm"
                    />
                  </div>

                  <p
                    v-if="primaryAddress(buildings[virtualRow.index]!)"
                    class="mt-1.5 line-clamp-1 text-sm text-default"
                  >
                    {{ primaryAddress(buildings[virtualRow.index]!) }}
                  </p>
                  <p v-else class="mt-1.5 text-sm italic text-muted">
                    Keine Adresse hinterlegt
                  </p>

                  <p class="mt-1 line-clamp-1 pr-6 text-xs text-muted">
                    Distrikt
                    {{
                      buildings[virtualRow.index]!.properties.district?.name ??
                      "–"
                    }}
                    <template
                      v-if="
                        buildings[virtualRow.index]!.properties.quarter?.name
                      "
                    >
                      ·
                      {{
                        buildings[virtualRow.index]!.properties.quarter?.name
                      }}</template
                    >
                    <template
                      v-if="
                        buildings[virtualRow.index]!.properties.object ||
                        buildings[virtualRow.index]!.properties.partType
                      "
                    >
                      ·
                      {{
                        buildings[virtualRow.index]!.properties.object ||
                        buildings[virtualRow.index]!.properties.partType
                      }}
                    </template>
                  </p>
                </button>
              </div>
            </template>
          </div>
        </div>

        <div
          v-else
          class="flex h-full min-h-48 flex-col items-center justify-center px-8 text-center text-slate-600"
        >
          <UIcon
            name="i-material-symbols-search-off-rounded"
            class="text-5xl text-muted"
          />
          <h2 class="mt-3 text-lg font-bold text-highlighted">
            Keine Gebäude gefunden
          </h2>
          <p class="mt-1 text-sm">
            Ändern Sie die Suche oder setzen Sie die Filter zurück.
          </p>
          <UButton
            v-if="activeFilters"
            color="neutral"
            variant="outline"
            label="Filter zurücksetzen"
            class="mt-4"
            @click="emit('clearFilters')"
          />
        </div>
      </div>
    </template>
  </aside>
</template>
