<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { title_shortener } from '~/utils/helpers';
import { FilterMatchMode } from "@primevue/core";

const citizenship_store = useCitizenshipStore();

const rows = ref<CitizenshipDTO[]>([]);
const loading = ref(false);
const page = ref(0);
const rowsPerPage = ref(10);
const rowsPerPageOptions = [5, 10, 25, 50, 100];
const totalRecords = ref(0);
const sortField = ref<string | null>(null);
const sortOrder = ref<1 | -1 | null>(null);

const filters = ref({
  refNumber: { value: null, matchMode: FilterMatchMode.CONTAINS },
  signature: { value: null, matchMode: FilterMatchMode.CONTAINS },
  'person.fullName': { value: null, matchMode: FilterMatchMode.CONTAINS },
  dateNaturalization: { value: null, matchMode: FilterMatchMode.CONTAINS },
  'primarySource.title': { value: null, matchMode: FilterMatchMode.CONTAINS },
  'secondarySource.title': { value: null, matchMode: FilterMatchMode.CONTAINS }
});

const loadData = async () => {
  loading.value = true;
  try {
    const sort = sortField.value && sortOrder.value
        ? `${sortField.value},${sortOrder.value === 1 ? 'asc' : 'desc'}`
        : undefined;
    const res = await citizenship_store.fetchCitizenships({
      page: page.value,
      size: rowsPerPage.value,
      sort,
      refnumber: filters.value.refNumber?.value || undefined,
      signature: filters.value.signature?.value || undefined,
      naturalizedperson: filters.value['person.fullName']?.value || undefined,
      datenaturalization: filters.value.dateNaturalization?.value || undefined,
      primarysource: filters.value['primarySource.title']?.value || undefined,
      secondarysource: filters.value['secondarySource.title']?.value || undefined
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
</script>

<template>
  <Card>
    <template #title>
      <div class="flex flex-col gap-2">
        <h1 class="text-3xl font-bold text-black montserrat-headline">
          Bürgermatrikel
        </h1>
        <p class="text-lg roboto-plain font-medium">
          Einträge insgesamt: {{ totalRecords }}
        </p>
      </div>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <NuxtLink
            to="citizenships/description"
            class="group flex w-fit items-center gap-3 rounded-lg border border-gray-200 bg-gray-100 p-4 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-300 hover:shadow-md"
        >
          <div class="flex h-10 w-10 items-center justify-center rounded-md bg-gray-200 transition-colors group-hover:bg-blue-100">
            <Icon name="material-symbols-book-5-outline-rounded" class="text-2xl text-gray-700"/>
          </div>
          <div class="flex flex-col">
            <h2 class="text-base font-bold montserrat-headline text-black">Beschreibung</h2>
            <p class="text-sm roboto-plain text-gray-600">Informationen zu Bürgermatrikeln</p>
          </div>
        </NuxtLink>
        <Accordion :value="null">
          <AccordionPanel value="0">
            <AccordionHeader>
              <h1 class="text-base font-bold text-black montserrat-headline">Hinweise</h1>
            </AccordionHeader>
            <AccordionContent>
              <ul class="list-disc list-inside outfit-headline text-sm">
                <li>Beim Klicken auf die Nummer der jeweiligen Matrikel öffnet sich die Seite mit zusätzlichen Informationen</li>
                <li>Manche Felder besitzen einen Sortierknopf <i class="pi pi-sort-alt"/>, mit dem man die Werte alphabetisch sortieren kann</li>
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
          <Column field="refNumber" header="Meyer-Erlach-Referenz" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <NuxtLink
                  :to="`/citizenships/${slotProps.data.id}`"
                  class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                  prefetch
              >
                {{ slotProps.data.refNumber }}
              </NuxtLink>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  placeholder="Suche Referenz"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="signature" header="Signatur" class="roboto-plain text-nowrap" :sortable="true" :showFilterMenu="false">
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  placeholder="Suche Signatur"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="person" filterField="person.fullName" header="Eingebürgerte Person" class="roboto-plain" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.person">
                <NuxtLink
                    :to="`/persons/${ slotProps.data.person.id }`"
                    class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md text-nowrap"
                    prefetch
                >
                  {{ slotProps.data.person.fullName }}
                </NuxtLink>
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  placeholder="Suche Person"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="dateNaturalization" header="Einbürgerung" class="roboto-plain" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.dateNaturalization" class="text-nowrap">
                {{ slotProps.data.dateNaturalization }}
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  placeholder="Suche Datum"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="primarySource" filterField="primarySource.title" header="Primärquelle" class="roboto-plain" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.primarySource">
                <NuxtLink
                    :to="`/sources/${ slotProps.data.primarySource.id }`"
                    class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md text-nowrap"
                >
                  {{ title_shortener(slotProps.data.primarySource.title) }}
                </NuxtLink>
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  placeholder="Suche Quelle"
                  @input="filterCallback()"
              />
            </template>
          </Column>
          <Column field="secondarySource" filterField="secondarySource.title" header="Sekundärquelle" class="roboto-plain" :sortable="true" :showFilterMenu="false">
            <template #body="slotProps">
              <div v-if="slotProps.data.secondarySource">
                <NuxtLink
                    :to="`/sources/${ slotProps.data.secondarySource.id }`"
                    class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md text-nowrap"
                >
                  {{ title_shortener(slotProps.data.secondarySource.title) }}
                </NuxtLink>
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  placeholder="Suche Quelle"
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
