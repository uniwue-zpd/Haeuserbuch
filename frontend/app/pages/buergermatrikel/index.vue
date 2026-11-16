<script setup lang="ts">
import { h, onMounted, reactive, ref, resolveComponent, watch } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { Column } from "@tanstack/vue-table";
import { debounce } from "~/utils/debounce";

const citizenship_store = useCitizenshipStore();
const UButton = resolveComponent("UButton");

type CitizenshipTableRow = CitizenshipDTO & {
  /** Original spelling (first altName), falls back to the normalized full name */
  personName: string | null;
};

const showNormalized = ref(false);

const rows = ref<CitizenshipTableRow[]>([]);
const loading = ref(false);
const page = ref(0);
const rowsPerPage = ref(10);
const rowsPerPageOptions = [5, 10, 25, 50, 100];
const totalRecords = ref(0);
const sorting = ref<{ id: string; desc: boolean }[]>([]);

const YEAR_MIN = 1405;
const YEAR_MAX = 1613;

const filterValues = reactive({
  yearnaturalization: undefined as number | undefined,
  yearRange: [YEAR_MIN, YEAR_MAX] as [number, number],
  refnumber: "",
  signature: "",
  personName: "",
  personJob: "",
  personOrigin: "",
  guarantorName: "",
  guarantorJob: "",
  guarantorOrigin: "",
});

const resetFilters = () => {
  Object.assign(filterValues, {
    yearnaturalization: undefined,
    yearRange: [YEAR_MIN, YEAR_MAX],
    refnumber: "",
    signature: "",
    personName: "",
    personJob: "",
    personOrigin: "",
    guarantorName: "",
    guarantorJob: "",
    guarantorOrigin: "",
  });
};

const sortableHeader = (label: string) =>
  ({ column }: { column: Column<CitizenshipTableRow, unknown> }) =>
    h(
      UButton,
      {
        color: "neutral",
        variant: "ghost",
        label,
        icon:
          column.getIsSorted() === "asc"
            ? "i-lucide-arrow-up-narrow-wide"
            : column.getIsSorted() === "desc"
              ? "i-lucide-arrow-down-wide-narrow"
              : "i-lucide-arrow-up-down",
        class: "-mx-2.5",
        onClick: () =>
          column.toggleSorting(column.getIsSorted() === "asc"),
      },
    );

const columns: TableColumn<CitizenshipTableRow>[] = [
  {
    id: "actions",
    header: "",
    enableSorting: false,
    enableGlobalFilter: false,
    meta: { class: { th: "w-12", td: "w-12" } },
    cell: ({ row }) =>
      h(UButton, {
        to: `/buergermatrikel/${row.original.id}`,
        color: "neutral",
        variant: "ghost",
        icon: "i-lucide-arrow-up-right",
        "aria-label": `Matrikel ${row.original.refNumber} öffnen`,
        title: "Matrikel öffnen",
      }),
  },
  {
    accessorKey: "yearNaturalization",
    header: sortableHeader("Jahr")
  },
  {
    id: "naturalizedPerson",
    header: "Eingebürgerte Person",
    meta: { class: { th: "border-b border-default" } },
    columns: [
      {
        id: "person",
        accessorFn: (row) => row.personName,
        header: "Name",
      },
      {
        id: "personOrigin",
        header: "Herkunft",
        enableSorting: false,
      },
      {
        id: "personJob",
        header: "Beruf",
        enableSorting: false,
      },
    ],
  },
  {
    id: "guarantors",
    header: "Bürgen",
    enableSorting: false,
  },
  {
    accessorKey: "signature",
    header: sortableHeader("Signatur"),
    meta: { class: { td: "whitespace-nowrap" } },
  },
  {
    accessorKey: "refNumber",
    header: sortableHeader("Meyer-Erlach-Referenz"),
  }
];

/** Normalized full name, shown whenever the toggle is on */
const normalizedName = (row: CitizenshipTableRow): string | null =>
  showNormalized.value ? row.person?.fullName ?? null : null;

const loadData = async () => {
  loading.value = true;
  try {
    const sortField = sorting.value[0]?.id;
    const sort = sorting.value[0]
      ? `${sortField},${sorting.value[0].desc ? "desc" : "asc"}`
      : undefined;
    const res = await citizenship_store.fetchCitizenships({
      page: page.value,
      size: rowsPerPage.value,
      sort,
      yearnaturalization: filterValues.yearnaturalization || undefined,
      "year-from": filterValues.yearRange[0] > YEAR_MIN ? filterValues.yearRange[0] : undefined,
      "year-to": filterValues.yearRange[1] < YEAR_MAX ? filterValues.yearRange[1] : undefined,
      refnumber: filterValues.refnumber || undefined,
      signature: filterValues.signature || undefined,
      naturalizedperson: filterValues.personName || undefined,
      "naturalizedperson-job": filterValues.personJob || undefined,
      "naturalizedperson-origin": filterValues.personOrigin || undefined,
      guarantor: filterValues.guarantorName || undefined,
      "guarantor-job": filterValues.guarantorJob || undefined,
      "guarantor-origin": filterValues.guarantorOrigin || undefined,
    });
    rows.value = res.content.map((citizenship: CitizenshipDTO) => ({
      ...citizenship,
      personName: citizenship.person?.altNames?.[0] ?? citizenship.person?.fullName ?? null,
    }));
    totalRecords.value = res.totalElements;
  } finally {
    loading.value = false;
  }
};

const debouncedLoadData = debounce(() => {
  loadData();
}, 500);

const onPageChange = (nextPage: number) => {
  page.value = nextPage - 1;
  loadData();
};

const onRowsPerPageChange = () => {
  page.value = 0;
  loadData();
};

watch(
  filterValues,
  () => {
    page.value = 0;
    debouncedLoadData();
  },
  { deep: true },
);

watch(
  sorting,
  () => {
    page.value = 0;
    loadData();
  },
  { deep: true },
);

onMounted(() => {
  loadData();
});

useHead(() => ({
  title: "Bürgermatrikel - Verzeichnis",
}));
</script>

<template>
  <div class="citizenship-overview flex min-h-full flex-col gap-8">
    <div class="flex flex-col gap-4">
      <header class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div class="flex flex-col gap-2">
          <h1 class="text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">Bürgermatrikel</h1>
          <h1 class="text-xl sm:text-2xl">Verzeichnis der von 1405 bis 1613 aufgenommenen Würzburger Bürger</h1>
        </div>
      </header>
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-end">
        <div class="flex flex-wrap justify-end gap-2">
          <UModal
            title="Beschreibung der Bürgermatrikel"
            scrollable
            :ui="{ content: 'max-w-7xl', body: 'p-5 sm:p-8' }"
          >
            <UButton
              color="neutral"
              variant="outline"
              icon="i-lucide-book-open"
              label="Beschreibung"
            />

            <template #body>
              <CitizenshipsDescriptionContent />
            </template>
          </UModal>
          <UPopover
            mode="click"
            :content="{ align: 'end', side: 'bottom', sideOffset: 8 }"
          >
            <UButton
              color="neutral"
              variant="outline"
              icon="i-lucide-info"
              label="Hinweise"
            />

            <template #content>
              <ul class="max-w-sm list-inside list-disc space-y-2 p-4 text-sm">
                <li>
                  Beim Klicken auf den Pfeil am Anfang einer Zeile öffnet sich
                  die Seite der jeweiligen Matrikel mit zusätzlichen Informationen
                </li>
                <li>
                  Über die Sortierknöpfe in den Spaltenüberschriften können die
                  Werte alphabetisch sortiert werden
                </li>
                <li>
                  Name, Beruf und Herkunft der eingebürgerten Person werden in
                  der ursprünglichen Schreibung angezeigt
                </li>
                <li>
                  Über „Normalisierte Formen einblenden“ werden zusätzlich die
                  normalisierte Namensform, die Berufskategorie und
                  identifizierte Herkunftsorte (mit Lokalisierbarkeit) angezeigt
                </li>
                <li>
                  Die Textfilter für Name, Beruf und Herkunft durchsuchen
                  sowohl die ursprünglichen als auch die normalisierten
                  Schreibungen – für die eingebürgerte Person und für die Bürgen
                </li>
              </ul>
            </template>
          </UPopover>
          <UButton to="/buergermatrikel/fulltextsearch" color="neutral" variant="outline" icon="i-lucide-search" label="Volltextsuche" />
        </div>
      </div>

      <div class="grid gap-4 lg:grid-cols-[minmax(16rem,18rem)_minmax(0,1fr)]">
        <aside class="overview-filter-panel h-fit">
          <div class="border-b border-muted p-5">
            <div class="flex items-center justify-between">
              <h2 class="text-xl font-semibold text-highlighted">Filter</h2>
              <UButton color="neutral" variant="ghost" size="sm" icon="i-lucide-rotate-ccw" label="Zurücksetzen" @click="resetFilters" />
            </div>
            <p class="mt-2 text-xs text-muted">
              Die Textfilter decken sowohl die ursprünglichen als auch die normalisierten Schreibungen ab.
            </p>
          </div>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Jahr
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput
              v-model.number="filterValues.yearnaturalization"
              type="number"
              class="mt-3 w-full"
              placeholder="Suchen..."
            />
            <USlider
              v-model="filterValues.yearRange"
              :min="YEAR_MIN"
              :max="YEAR_MAX"
              :step="1"
              class="mt-5"
            />
            <div class="mt-3 flex items-center gap-2">
              <UInputNumber
                v-model="filterValues.yearRange[0]"
                :min="YEAR_MIN"
                :max="filterValues.yearRange[1]"
                :format-options="{ useGrouping: false }"
                class="w-full"
              />
              <span class="text-muted">–</span>
              <UInputNumber
                v-model="filterValues.yearRange[1]"
                :min="filterValues.yearRange[0]"
                :max="YEAR_MAX"
                :format-options="{ useGrouping: false }"
                class="w-full"
              />
            </div>
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Eingebürgerte Person
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <div class="mt-3 flex flex-col gap-3">
              <UFormField label="Name" size="sm">
                <UInput v-model="filterValues.personName" class="w-full" placeholder="Suchen..." />
              </UFormField>
              <UFormField label="Beruf" size="sm">
                <UInput v-model="filterValues.personJob" class="w-full" placeholder="Suchen..." />
              </UFormField>
              <UFormField label="Herkunft" size="sm">
                <UInput v-model="filterValues.personOrigin" class="w-full" placeholder="Suchen..." />
              </UFormField>
            </div>
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Bürgen
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <div class="mt-3 flex flex-col gap-3">
              <UFormField label="Name" size="sm">
                <UInput v-model="filterValues.guarantorName" class="w-full" placeholder="Suchen..." />
              </UFormField>
              <UFormField label="Beruf" size="sm">
                <UInput v-model="filterValues.guarantorJob" class="w-full" placeholder="Suchen..." />
              </UFormField>
              <UFormField label="Herkunft" size="sm">
                <UInput v-model="filterValues.guarantorOrigin" class="w-full" placeholder="Suchen..." />
              </UFormField>
            </div>
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Signatur
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.signature" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Meyer-Erlach-Referenz
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.refnumber" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
        </aside>
        <section class="overview-table-panel min-w-0 p-5">
          <div class="mb-3 flex flex-wrap items-center justify-between gap-3 border-b border-default pb-3">
            <p class="text-sm text-muted">Einträge insgesamt: {{ totalRecords }}</p>
            <USwitch
              v-model="showNormalized"
              label="Normalisierte Formen einblenden"
              description="z. B. Name, Berufskategorie, Herkunftsort"
            />
          </div>
          <UTable
            :data="rows"
            :columns="columns"
            :loading="loading"
            v-model:sorting="sorting"
            :sorting-options="{ manualSorting: true }"
            :ui="{ td: 'text-default' }"
            class="w-full"
          >
            <template #yearNaturalization-cell="{ row }">
              <span v-if="row.original.yearNaturalization">{{ row.original.yearNaturalization }}</span>
              <span v-else class="italic text-muted">unbekannt</span>
            </template>
            <template #person-cell="{ row }">
              <div v-if="row.original.person" class="flex flex-col">
                <NuxtLink
                    :to="`/personen/${row.original.person.id}`"
                    class="whitespace-nowrap"
                    prefetch
                >
                  {{ row.original.personName }}
                </NuxtLink>
                <span v-if="normalizedName(row.original)" class="text-xs text-muted" title="Normalisierte Form">
                  {{ normalizedName(row.original) }}
                </span>
              </div>
              <span v-else class="italic text-muted">unbekannt</span>
            </template>
            <template #personJob-cell="{ row }">
              <PersonsJobValue :job="row.original.person?.job" :show-normalized="showNormalized">
                <template #empty><span class="text-dimmed" title="keine Angabe">–</span></template>
              </PersonsJobValue>
            </template>
            <template #personOrigin-cell="{ row }">
              <PersonsOriginValue :origin="row.original.person?.origin" :show-normalized="showNormalized">
                <template #empty><span class="text-dimmed" title="keine Angabe">–</span></template>
              </PersonsOriginValue>
            </template>
            <template #guarantors-cell="{ row }">
              <span v-if="row.original.guarantors?.length">
                <template v-for="(guarantor, guarantorIndex) in row.original.guarantors" :key="guarantor.id">
                  <NuxtLink :to="`/personen/${guarantor.id}`" class="whitespace-nowrap">{{ guarantor.fullName }}</NuxtLink><span v-if="guarantorIndex < row.original.guarantors.length - 1">, </span>
                </template>
              </span>
              <span v-else class="italic text-muted">keine</span>
            </template>
          </UTable>
          <div
            class="flex flex-col gap-4 border-t border-default pt-4 sm:flex-row sm:items-center sm:justify-between"
          >
            <USelect
              v-model="rowsPerPage"
              :items="rowsPerPageOptions"
              class="w-24"
              @update:model-value="onRowsPerPageChange"
            />
            <UPagination
              :page="page + 1"
              :items-per-page="rowsPerPage"
              :total="totalRecords"
              @update:page="onPageChange"
            />
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
