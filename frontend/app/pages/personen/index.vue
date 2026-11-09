<script setup lang="ts">
import { h, onMounted, reactive, ref, resolveComponent, watch } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { Column } from "@tanstack/vue-table";
import { debounce } from "~/utils/debounce";

const personStore = usePersonStore();
const UButton = resolveComponent("UButton");

type PersonTableRow = PersonDTO & {
  matchedAltNames: string[];
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
  weapon: "",
  religion: "",
  origin: "",
  emigration: "",
  building: "",
});

const resetFilters = () => {
  Object.assign(filterValues, {
    fullName: "",
    sex: "",
    job: "",
    weapon: "",
    religion: "",
    origin: "",
    emigration: "",
    building: "",
  });
  citizenFilter.value = "all";
};

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
        to: `/personen/${row.original.id}`,
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
    accessorFn: (row) => row.job?.originalText,
    header: sortableHeader("Beruf"),
    enableGlobalFilter: false,
  },
  {
    id: "weapons",
    accessorFn: (row) => row.weapons?.map((w) => w.originalText).join(", "),
    header: "Bewaffnung",
    enableSorting: false,
    enableGlobalFilter: false,
  },
  {
    id: "religion",
    accessorFn: (row) => row.religion?.originalText,
    header: sortableHeader("Religion"),
    enableGlobalFilter: false,
  },
  {
    id: "origin",
    accessorFn: (row) => row.origin?.originalText,
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
    const nameQuery = filterValues.fullName.trim().toLowerCase();
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
      weapon: filterValues.weapon || undefined,
      religion: filterValues.religion || undefined,
      "place-of-origin": filterValues.origin || undefined,
      "place-of-emigration": filterValues.emigration || undefined,
      "associated-building": filterValues.building || undefined,
    });
    rows.value = res.content.map((person: PersonDTO) => ({
      ...person,
      matchedAltNames: nameQuery
        ? (person.altNames ?? []).filter(
            (altName: string) =>
              altName.toLowerCase().includes(nameQuery) &&
              altName.trim().toLowerCase() !== person.fullName?.trim().toLowerCase()
          )
        : [],
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
  title: "Personen",
}));
</script>

<template>
  <div class="flex flex-col gap-4">
    <header class="flex items-start justify-between gap-4">
      <div class="flex flex-col gap-2">
        <h1 class="text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">Personen</h1>
        <h1 class="text-xl sm:text-2xl">Die aufgenommenen Würzburger Bürger und andere Personen in alphabetischer Anordnung</h1>
      </div>
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
    <div>
      <p>Einträge insgesamt: {{ totalRecords }}</p>
    </div>

    <div class="grid grid-cols-1 gap-4 lg:grid-cols-[minmax(16rem,18rem)_minmax(0,1fr)] lg:items-start">
      <aside class="overview-filter-panel">
        <div class="border-b border-muted p-5">
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold text-highlighted">Filter</h2>
            <UButton color="neutral" variant="ghost" size="sm" icon="i-lucide-rotate-ccw" label="Zurücksetzen" @click="resetFilters" />
          </div>
          <p class="mt-2 text-xs text-muted">
            Die Textfilter durchsuchen sowohl die ursprünglichen als auch die standardisierten Schreibungen.
          </p>
        </div>
        <div class="flex flex-col">
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Name
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.fullName" class="mt-3 w-full" placeholder="Suchen..." />          </details>
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
                Bewaffnung
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.weapon" class="mt-3 w-full" placeholder="Suchen..." />
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
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Herkunft
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.origin" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="border-b border-default p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Wegzugsort
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.emigration" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
          <details open class="p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Gebäude
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput v-model="filterValues.building" class="mt-3 w-full" placeholder="Suchen..." />
          </details>
        </div>
      </aside>
      <section class="overview-table-panel p-5">
          <UTable
            :data="rows"
            :columns="columns"
            :loading="loading"
            v-model:sorting="sorting"
            :sorting-options="{ manualSorting: true }"
            :ui="{ td: 'text-default' }"
            class="w-full"
          >
        <template #fullName-cell="{ row }">
          <div class="flex flex-col">
            <NuxtLink :to="`/personen/${row.original.id}`" prefetch>
              {{ row.original.fullName }}
            </NuxtLink>
            <span
              class="text-xs text-muted"
              v-if="row.original.matchedAltNames.length > 0"
            >
              orig. Schreibweise: {{ row.original.matchedAltNames.join(', ') }}
            </span>
          </div>
        </template>
        <template #isCitizen-cell="{ row }">
          <UIcon
            v-if="row.original.isCitizen !== null"
            :name="row.original.isCitizen ? 'i-lucide-check' : 'i-lucide-x'"
            :class="row.original.isCitizen ? 'text-green-500' : 'text-red-500'"
            :aria-label="row.original.isCitizen ? 'Bürger' : 'Kein Bürger'"
          />
          <span v-else class="italic text-muted">unbekannt</span>
        </template>
        <template #sex-cell="{ row }">
          <span v-if="row.original.sex">{{ row.original.sex }}</span>
          <span v-else class="italic text-muted">unbekannt</span>
        </template>
        <template #job-cell="{ row }">
          <div v-if="row.original.job?.jobCategory?.name || row.original.job?.originalText" class="flex flex-col items-start gap-1">
            <span v-if="row.original.job.originalText">{{ row.original.job.originalText }}</span>
            <UBadge
              v-if="row.original.job.jobCategory?.name && row.original.job.jobCategory.name.trim().toLowerCase() !== row.original.job.originalText?.trim().toLowerCase()"
              :label="row.original.job.jobCategory.name"
              color="neutral"
              variant="soft"
              size="sm"
              title="Normalisierte Form"
            />
          </div>
          <span v-else class="italic text-muted">unbekannt</span>
        </template>
        <template #weapons-cell="{ row }">
          <div v-if="row.original.weapons?.length" class="flex flex-col gap-2">
            <div v-for="weaponry in row.original.weapons" :key="weaponry.id ?? undefined" class="flex flex-col items-start gap-1">
              <span v-if="weaponry.originalText">{{ weaponry.originalText }}</span>
              <UBadge
                v-if="weaponry.weapon?.name && weaponry.weapon.name.trim().toLowerCase() !== weaponry.originalText?.trim().toLowerCase()"
                :label="weaponry.weapon.name"
                color="neutral"
                variant="soft"
                size="sm"
                title="Normalisierte Form"
              />
            </div>
          </div>
          <span v-else class="italic text-muted">unbekannt</span>
        </template>
        <template #religion-cell="{ row }">
          <div v-if="row.original.religion?.religionCategory?.name || row.original.religion?.originalText" class="flex flex-col items-start gap-1">
            <span v-if="row.original.religion.originalText">{{ row.original.religion.originalText }}</span>
            <UBadge
              v-if="row.original.religion.religionCategory?.name && row.original.religion.religionCategory.name.trim().toLowerCase() !== row.original.religion.originalText?.trim().toLowerCase()"
              :label="row.original.religion.religionCategory.name"
              color="neutral"
              variant="soft"
              size="sm"
              title="Normalisierte Form"
            />
          </div>
          <span v-else class="italic text-muted">unbekannt</span>
        </template>
        <template #origin-cell="{ row }">
          <div
            v-if="row.original.origin?.originalText || row.original.origin?.places?.length || row.original.origin?.certainty"
            class="flex flex-col"
          >
            <span v-if="row.original.origin.originalText">{{ row.original.origin.originalText }}</span>
            <div
              v-if="row.original.origin.certainty || row.original.origin.places.length"
              class="flex flex-wrap items-baseline gap-x-1 text-xs"
            >
              <span
                v-if="row.original.origin.certainty"
                :class="`size-2 shrink-0 self-center rounded-full border border-gray-400 ${ORIGIN_CERTAINTY[row.original.origin.certainty].color}`"
                :title="`Lokalisierbarkeit: ${ORIGIN_CERTAINTY[row.original.origin.certainty].label}`"
                :aria-label="`Lokalisierbarkeit: ${ORIGIN_CERTAINTY[row.original.origin.certainty].label}`"
              />
              <span v-if="row.original.origin.places.length">
                <template v-for="(place, index) in row.original.origin.places" :key="place.id">
                  <NuxtLink
                    :to="`/orte/${place.id}`"
                    :title="place.realName ?? undefined"
                    class="text-highlighted underline decoration-muted underline-offset-4 hover:decoration-current"
                  >
                    {{ place.realName?.split(",")[0] }}
                  </NuxtLink>
                  <span v-if="index < row.original.origin.places.length - 1" class="text-muted">, </span>
                </template>
              </span>
            </div>
          </div>
          <span v-else class="italic text-muted">unbekannt</span>
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
