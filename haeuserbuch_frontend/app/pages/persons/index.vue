<script setup lang="ts">
import { h, onMounted, reactive, ref, resolveComponent, watch } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { Column } from "@tanstack/vue-table";
import { debounce } from "~/utils/debounce";

const personStore = usePersonStore();
const UButton = resolveComponent("UButton");

type PersonTableRow = PersonDTO & {
  jobText: string | null;
  religionText: string | null;
  originText: string | null;
};

type CitizenFilter = "all" | "citizen" | "not-citizen";

const rows = ref<PersonTableRow[]>([]);
const loading = ref(false);
const page = ref(0);
const rowsPerPage = ref(10);
const totalRecords = ref(0);
const sorting = ref<{ id: string; desc: boolean }[]>([]);
const citizenFilter = ref<CitizenFilter>("all");
const rowsPerPageOptions = [5, 10, 25, 50];
const citizenOptions = [
  { label: "Alle", value: "all" },
  { label: "Bürger", value: "citizen" },
  { label: "Kein Bürger", value: "not-citizen" },
];

const filterValues = reactive({
  fullName: "",
  sex: "",
  job: "",
  religion: "",
  origin: "",
});

const sortableHeader = (label: string) =>
  ({ column }: { column: Column<PersonTableRow, unknown> }) =>
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

const columns: TableColumn<PersonTableRow>[] = [
  {
    id: "actions",
    header: "",
    enableSorting: false,
    enableGlobalFilter: false,
    meta: { class: { th: "w-12", td: "w-12" } },
    cell: ({ row }) =>
      h(UButton, {
        to: `/persons/${row.original.id}`,
        color: "neutral",
        variant: "ghost",
        icon: "i-lucide-arrow-up-right",
        "aria-label": `${row.original.fullName} öffnen`,
        title: "Person öffnen",
      }),
  },
  {
    accessorKey: "fullName",
    header: sortableHeader("Name"),
  },
  {
    accessorKey: "isCitizen",
    header: "Bürger",
    enableSorting: false,
    enableGlobalFilter: false,
  },
  {
    accessorKey: "sex",
    header: sortableHeader("Geschlecht"),
    enableGlobalFilter: false,
  },
  {
    id: "job",
    accessorFn: (row) => row.jobText,
    header: sortableHeader("Beruf"),
    enableGlobalFilter: false,
  },
  {
    id: "religion",
    accessorFn: (row) => row.religionText,
    header: sortableHeader("Religion"),
    enableGlobalFilter: false,
  },
  {
    id: "origin",
    accessorFn: (row) => row.originText,
    header: sortableHeader("Herkunft"),
    enableGlobalFilter: false,
  },
];

const loadData = async () => {
  loading.value = true;
  try {
    const sort = sorting.value[0]
      ? `${sorting.value[0].id},${sorting.value[0].desc ? "desc" : "asc"}`
      : undefined;
    const res = await personStore.fetchPersons({
      page: page.value,
      size: rowsPerPage.value,
      sort,
      name: filterValues.fullName || undefined,
      sex: filterValues.sex || undefined,
      "is-citizen":
        citizenFilter.value === "all"
          ? undefined
          : citizenFilter.value === "citizen",
      job: filterValues.job || undefined,
      religion: filterValues.religion || undefined,
      "place-of-origin": filterValues.origin || undefined,
    });
    rows.value = res.content.map((person) => ({
      ...person,
      jobText: person.job?.originalText ?? null,
      religionText: person.religion?.originalText ?? null,
      originText: person.origin?.originalText ?? null,
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
  [filterValues, citizenFilter],
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
  title: "Personen - Personenverzeichnis",
}));
</script>

<template>
  <div class="flex flex-col gap-4">
    <header class="flex items-start justify-between gap-4">
      <h1 class="text-[clamp(2.25rem,5vw,4.5rem)] font-[750] leading-[0.98] tracking-[-0.055em] text-highlighted">Personen</h1>
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
              Beim Klicken auf den Namen der jeweiligen Person öffnet sich
              die Seite mit zusätzlichen Informationen
            </li>
            <li>
              Über die Sortierknöpfe in den Spaltenüberschriften können die
              Werte alphabetisch sortiert werden
            </li>
          </ul>
        </template>
      </UPopover>
    </header>
    <div class="w-fit rounded-2xl border border-gray-300 bg-[var(--ui-bg)] p-[0.8rem_1rem] text-base font-[650] text-highlighted shadow-[0_8px_24px_rgb(15_23_42_/_0.06)]">
      <p>Einträge insgesamt: {{ totalRecords }}</p>
    </div>

    <div class="grid grid-cols-[minmax(0,1fr)] gap-4 lg:grid-cols-[minmax(16rem,18rem)_minmax(0,1fr)] lg:items-start">
      <aside class="min-w-0 overflow-hidden rounded-2xl border border-gray-300 bg-default p-0 shadow-md">
        <div class="border-b border-muted p-5">
          <h2 class="text-[1.2rem] font-[650] text-highlighted">Filter</h2>
        </div>
        <div class="flex flex-col">
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Name
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.fullName" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Bürgerstatus
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <USelect
              v-model="citizenFilter"
              :items="citizenOptions"
              value-key="value"
              class="mt-3 w-full"
            />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Geschlecht
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.sex" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Beruf
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.job" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Religion
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.religion" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Herkunft
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.origin" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
        </div>
      </aside>
      <section class="min-w-0 overflow-hidden rounded-2xl border border-gray-300 bg-default p-0 shadow-md">
          <UTable
            :data="rows"
            :columns="columns"
            :loading="loading"
            v-model:sorting="sorting"
            :sorting-options="{ manualSorting: true }"
            class="w-full"
          >
        <template #fullName-cell="{ row }">
          <NuxtLink :to="`/persons/${row.original.id}`" prefetch>
            {{ row.original.fullName }}
          </NuxtLink>
        </template>
        <template #isCitizen-cell="{ row }">
          <UIcon
            v-if="row.original.isCitizen !== null"
            :name="row.original.isCitizen ? 'i-lucide-check' : 'i-lucide-x'"
            :class="row.original.isCitizen ? 'text-green-500' : 'text-red-500'"
            :aria-label="row.original.isCitizen ? 'Bürger' : 'Kein Bürger'"
          />
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
        <template #sex-cell="{ row }">
          <span v-if="row.original.sex">{{ row.original.sex }}</span>
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
        <template #job-cell="{ row }">
          <span v-if="row.original.jobText">{{ row.original.jobText }}</span>
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
        <template #religion-cell="{ row }">
          <span v-if="row.original.religionText">{{ row.original.religionText }}</span>
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
        <template #origin-cell="{ row }">
          <span v-if="row.original.originText">{{ row.original.originText }}</span>
          <span v-else class="italic text-gray-500">unbekannt</span>
        </template>
          </UTable>

          <div class="flex flex-col gap-4 border-t border-default pt-4 sm:flex-row sm:items-center sm:justify-between">
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
</template>

<style scoped>
section :deep(table) {
  border: 0;
}

section > :deep(.flex.flex-col) {
  padding: 1.25rem;
}

</style>
