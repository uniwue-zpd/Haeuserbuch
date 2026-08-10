<script setup lang="ts">
import { h, onMounted, reactive, ref, resolveComponent, watch } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { Column } from "@tanstack/vue-table";
import { title_shortener } from "~/utils/helpers";
import { debounce } from "~/utils/debounce";

const citizenship_store = useCitizenshipStore();
const UButton = resolveComponent("UButton");

type CitizenshipTableRow = CitizenshipDTO & {
  personName: string | null;
  primarySourceTitle: string | null;
  secondarySourceTitle: string | null;
};

const rows = ref<CitizenshipTableRow[]>([]);
const loading = ref(false);
const page = ref(0);
const rowsPerPage = ref(10);
const rowsPerPageOptions = [5, 10, 25, 50, 100];
const totalRecords = ref(0);
const sorting = ref<{ id: string; desc: boolean }[]>([]);

const filterValues = reactive({
  refNumber: "",
  signature: "",
  personName: "",
  primarySourceTitle: "",
  secondarySourceTitle: "",
});

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
    accessorKey: "refNumber",
    header: sortableHeader("Meyer-Erlach-Referenz"),
  },
  {
    accessorKey: "signature",
    header: sortableHeader("Signatur"),
    meta: { class: { td: "whitespace-nowrap" } },
  },
  {
    id: "person",
    accessorFn: (row) => row.personName,
    header: "Eingebürgerte Person",
    enableSorting: false,
  },
  {
    id: "primarySource",
    accessorFn: (row) => row.primarySourceTitle,
    header: sortableHeader("Primärquelle"),
  },
  {
    id: "secondarySource",
    accessorFn: (row) => row.secondarySourceTitle,
    header: sortableHeader("Sekundärquelle"),
  },
];

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
      refnumber: filterValues.refNumber || undefined,
      signature: filterValues.signature || undefined,
      naturalizedperson: filterValues.personName || undefined,
      primarysource: filterValues.primarySourceTitle || undefined,
      secondarysource: filterValues.secondarySourceTitle || undefined,
    });
    rows.value = res.content.map((citizenship: CitizenshipDTO) => ({
      ...citizenship,
      personName: citizenship.person?.fullName ?? null,
      primarySourceTitle: citizenship.primarySource?.title ?? null,
      secondarySourceTitle: citizenship.secondarySource?.title ?? null,
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
        <div>
          <h1 class="text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">
            Bürgermatrikel
          </h1>
        </div>
        <div class="flex flex-wrap gap-2 sm:justify-end">
          <UButton to="/buergermatrikel/fulltextsearch" color="neutral" variant="outline" icon="i-lucide-search" label="Volltextsuche" />
        </div>
      </header>
      <div
        class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"
      >
        <div>
          Einträge insgesamt: {{ totalRecords }}
        </div>
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
                  Beim Klicken auf die Nummer der jeweiligen Matrikel öffnet
                  sich die Seite mit zusätzlichen Informationen
                </li>
                <li>
                  Über die Sortierknöpfe in den Spaltenüberschriften können die
                  Werte alphabetisch sortiert werden
                </li>
              </ul>
            </template>
          </UPopover>
        </div>
      </div>

      <div class="grid gap-4 lg:grid-cols-[minmax(16rem,18rem)_minmax(0,1fr)]">
        <aside class="overview-filter-panel h-fit">
          <div class="border-b border-muted p-5">
            <h2 class="text-xl font-semibold text-highlighted">Filter</h2>
          </div>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Meyer-Erlach-Referenz
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.refNumber" class="mt-3 w-full" placeholder="Suchen..." />
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
                Eingebürgerte Person
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.personName" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Primärquelle
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.primarySourceTitle" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Sekundärquelle
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.secondarySourceTitle" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
        </aside>
        <section class="overview-table-panel min-w-0 p-5">
          <UTable
            :data="rows"
            :columns="columns"
            :loading="loading"
            v-model:sorting="sorting"
            :sorting-options="{ manualSorting: true }"
            :ui="{ td: 'text-default' }"
            class="w-full"
          >
        <template #refNumber-cell="{ row }">
          <NuxtLink :to="`/buergermatrikel/${row.original.id}`" prefetch>
            {{ row.original.refNumber }}
          </NuxtLink>
        </template>
        <template #person-cell="{ row }">
          <NuxtLink
            v-if="row.original.person"
            :to="`/personen/${row.original.person.id}`"
            class="whitespace-nowrap"
            prefetch
          >
            {{ row.original.personName }}
          </NuxtLink>
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
        <template #primarySource-cell="{ row }">
          <NuxtLink
            v-if="row.original.primarySource"
            :to="`/quellen/${row.original.primarySource.id}`"
            class="whitespace-nowrap"
          >
            {{ title_shortener(row.original.primarySourceTitle ?? "") }}
          </NuxtLink>
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
        <template #secondarySource-cell="{ row }">
          <NuxtLink
            v-if="row.original.secondarySource"
            :to="`/quellen/${row.original.secondarySource.id}`"
            class="whitespace-nowrap"
          >
            {{ title_shortener(row.original.secondarySourceTitle ?? "") }}
          </NuxtLink>
          <span v-else class="italic text-gray-500">unbekannt</span>
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
