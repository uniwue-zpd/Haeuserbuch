<script setup lang="ts">
import FetchError from "~/components/UI/FetchError.vue";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";

const props = defineProps<{
  register: "places" | "occupations";
  type: "persons" | "citizenships";
}>();

type Entry = PersonPreviewDTO | CitizenshipPreviewDTO;

interface RegisterConfig {
  fetch: (params: FilterRegister) => Promise<Page<RegisterEntry<Entry>>>;
  key: string;
  placeholder: string;
  emptyText: string;
  personsTitle: string;
  link?: { to: (id: number) => string; label: string };
}

const placeApi = usePlaces();
const jobApi = useJobs();

const configs: Record<typeof props.register, RegisterConfig> = {
  places: {
    fetch: placeApi.getPlaceRegister<Entry>,
    key: "place",
    placeholder: "Nach Ortsnamen filtern",
    emptyText: "Keine Orte gefunden",
    personsTitle: "Herkunfts- oder Wegzugsort von",
    link: { to: (id) => `/orte/${id}`, label: "Zur Ortsseite" },
  },
  occupations: {
    fetch: jobApi.getJobRegister<Entry>,
    key: "job",
    placeholder: "Nach Berufen filtern",
    emptyText: "Keine Berufe gefunden",
    personsTitle: "Personen mit diesem Beruf",
  },
};
const config = configs[props.register];

const page = ref(0);
const rowsPerPage = ref(10);
const rowsPerPageOptions = [10, 25, 50];

const filterValues = reactive({
  name: "",
});

const resetFilters = () => {
  Object.assign(filterValues, {
    name: "",
  });
  rowsPerPage.value = 10;
  page.value = 0;
  debouncedLoadData();
};

const { data: register, pending: loading, error, refresh: loadData } = await useAsyncData(
  `${config.key}-register-${props.type}`,
  () => config.fetch({
    type: props.type,
    name: filterValues.name.trim() || undefined,
    page: page.value,
    size: rowsPerPage.value,
  }),
);

const groups = computed(() => register.value?.content ?? []);
const totalRecords = computed(() => register.value?.totalElements ?? 0);

const debouncedLoadData = debounce(() => {
  loadData();
}, 400);

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
</script>

<template>
  <FetchError v-if="error" :error="error" />
  <div v-else class="flex flex-col gap-4">
    <div class="flex items-center justify-between gap-2">
      <UInput
          v-model="filterValues.name"
          icon="i-lucide-search"
          :placeholder="config.placeholder"
          class="w-full sm:max-w-sm"
      />
      <UButton color="neutral" variant="ghost" size="sm" icon="i-lucide-rotate-ccw" label="Zurücksetzen" @click="resetFilters" />
    </div>
    <UniversalSkeleton v-if="loading && !register" />
    <ul
      v-else
      class="divide-y divide-muted rounded-md border border-default bg-default shadow-md transition-opacity"
      :class="{ 'opacity-60': loading }"
    >
      <li v-for="group in groups" :key="group.id" class="flex items-start gap-1 p-2">
        <UCollapsible class="min-w-0 flex-1">
          <UButton
            icon="i-lucide-chevron-right"
            color="neutral"
            variant="ghost"
            block
            class="group justify-start"
            :ui="{ leadingIcon: 'transition-transform duration-200 group-data-[state=open]:rotate-90' }"
          >
            <span class="truncate font-medium">{{ group.name || `Eintrag mit ID ${group.id}` }}</span>
            <UBadge :label="group.entries.length" color="neutral" variant="subtle" size="sm" class="ml-auto" />
          </UButton>
          <template #content>
            <div class="px-3 pb-2 pt-2">
              <UIEntityList
                :type="type"
                :title="type === 'persons' ? config.personsTitle : 'Erwähnt in den Einträgen'"
                :entries="group.entries as PersonPreviewDTO[] | CitizenshipPreviewDTO[]"
              />
            </div>
          </template>
        </UCollapsible>
        <UTooltip v-if="config.link" :text="config.link.label">
          <UButton
            :to="config.link.to(group.id)"
            icon="i-lucide-arrow-up-right"
            color="neutral"
            variant="ghost"
            :aria-label="`${config.link.label}: ${group.name ?? group.id}`"
          />
        </UTooltip>
      </li>
      <li v-if="groups.length === 0" class="px-4 py-6 text-center text-muted">
        {{ config.emptyText }}
      </li>
    </ul>
    <!-- Navigation controls -->
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
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
  </div>
</template>
