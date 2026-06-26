<script setup lang="ts">
import { onMounted, ref } from "vue";
import {FilterMatchMode} from "@primevue/core";

const personStore = usePersonStore();

const rows = ref<PersonDTO[]>([]);
const loading = ref(false);
const page = ref(0);
const rowsPerPage = ref(10);
const totalRecords = ref(0);
const sortField = ref<string | null>(null);
const sortOrder = ref<1 | -1 | null>(null);
const rowsPerPageOptions = [5, 10, 25, 50];

const defaultFilters = {
  fullName: { value: null, matchMode: FilterMatchMode.CONTAINS },
  sex: { value: null, matchMode: FilterMatchMode.CONTAINS },
  isCitizen: { value: undefined, matchMode: FilterMatchMode.EQUALS },
  'job.originalText': { value: null, matchMode: FilterMatchMode.CONTAINS },
  'religion.originalText': { value: null, matchMode: FilterMatchMode.CONTAINS },
  'origin.originalText': { value: null, matchMode: FilterMatchMode.CONTAINS },
};

const filters = ref({ ...defaultFilters });

const loadData = async () => {
  loading.value = true;
  try {
    const sort = sortField.value && sortOrder.value
        ? `${sortField.value},${sortOrder.value === 1 ? 'asc' : 'desc'}`
        : undefined;
    const res = await personStore.fetchPersons({
      page: page.value,
      size: rowsPerPage.value,
      sort,
      name: filters.value.fullName?.value || undefined,
      sex: filters.value.sex?.value || undefined,
      'is-citizen': filters.value.isCitizen?.value,
      job: filters.value['job.originalText']?.value || undefined,
      religion: filters.value['religion.originalText']?.value || undefined,
      'place-of-origin': filters.value['origin.originalText']?.value || undefined,
    });
    rows.value = res.content;
    totalRecords.value = res.totalElements;
  } finally {
    loading.value = false;
  }
};

const onPage = (event: any) => {
  page.value = event.page;
  rowsPerPage.value = event.rows;
  loadData();
};

const onSort = (event: any) => {
  sortField.value = event.sortField;
  sortOrder.value = event.sortOrder;
  loadData();
};

const onFilter = (event: any) => {
  filters.value = event.filters;
  page.value = 0;
  debouncedLoadData();
};

// Applies only if additional params are set
const debouncedLoadData = debounce(() => {
  loadData();
}, 1000);

onMounted(() => {
  loadData();
});

useHead(() => ({
  title: 'Personen - Personenverzeichnis',
}));
</script>

<template>
  <Card>
    <template #title>
      <div class="flex flex-col gap-2">
        <h1 class="text-3xl font-bold text-black montserrat-headline">Personen</h1>
        <p class="text-lg roboto-plain font-medium">Einträge insgesamt: {{ totalRecords }}</p>
      </div>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <Accordion :value="null">
          <AccordionPanel value="0">
            <AccordionHeader>
              <h1 class="text-base font-bold text-black montserrat-headline">Hinweise</h1>
            </AccordionHeader>
            <AccordionContent>
              <ul class="list-disc list-inside outfit-headline text-sm">
                <li>Beim Klicken auf den Namen der jeweiligen Person öffnet sich die Seite mit zusätzlichen Informationen</li>
                <li>Jedes Feld besitzt einen Sortierknopf <i class="pi pi-sort-alt"/>, mit dem man die Werte alphabetisch sortieren kann</li>
              </ul>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>
        <DataTable
            v-model:filters="filters"
            :value="rows"
            paginator
            lazy
            filterDisplay="row"
            :rows="rowsPerPage"
            :rowsPerPageOptions="rowsPerPageOptions"
            :totalRecords="totalRecords"
            :loading="loading"
            stripedRows
            @page="onPage"
            @sort="onSort"
            @filter="onFilter"
            removableSort
        >
          <Column field="fullName" header="Name" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <NuxtLink
                  :to="`/persons/${slotProps.data.id}`"
                  class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                  prefetch
              >
                {{ slotProps.data.fullName }}
              </NuxtLink>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column
              field="isCitizen" filterField="isCitizen"
              header="Bürger"
              class="roboto-plain"
              :showFilterMenu="false"
          >
            <template #body="slotProps">
              <div v-if="slotProps.data.isCitizen !== null">
                <i :class="[slotProps.data.isCitizen ? 'pi pi-check text-green-500' : 'pi pi-times text-red-500']"/>
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Select
                  v-model="filterModel.value"
                  @change="filterCallback()"
                  :options="[
                    { label: 'Bürger', value: true },
                    { label: 'kein Bürger', value: false }
                  ]"
                  placeholder="Bürgerstatus"
                  optionValue="value"
                  optionLabel="label"
                  style="min-width: 10rem"
                  showClear
              />
            </template>
          </Column>
          <Column
              field="sex" filterField="sex"
              header="Geschlecht"
              class="roboto-plain"
              :showFilterMenu="false" :sortable="true"
          >
            <template #body="slotProps">
              <div v-if="slotProps.data.sex">
                {{ slotProps.data.sex }}
              </div>
              <div v-else class="roboto-italic">unbekannt</div>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="job" header="Beruf" filterField="job.originalText" class="roboto-plain" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.job.originalText">{{ slotProps.data.job.originalText }}</div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="religion" filterField="religion.originalText" header="Religion" class="roboto-plain" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.religion.originalText">{{ slotProps.data.religion.originalText }}</div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="origin" header="Herkunft" filterField="origin.originalText" class="roboto-plain" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.origin.originalText">{{ slotProps.data.origin.originalText }}</div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  @input="filterCallback()"
              />
            </template>
          </Column>
        </DataTable>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
