<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { title_shortener } from '~/utils/helpers';

const citizenship_store = useCitizenshipStore();

const rows = ref<CitizenshipDTO[]>([]);
const loading = ref(false);

const page = ref(0);
const rowsPerPage = ref(10);
const totalRecords = ref(0);
const sortField = ref<string | null>(null);
const sortOrder = ref<1 | -1 | null>(null);

const rowsPerPageOptions = [5, 10, 25, 50];

const loadData = async () => {
  loading.value = true;
  try {
    const sort = sortField.value && sortOrder.value
        ? `${sortField.value},${sortOrder.value === 1 ? 'asc' : 'desc'}`
        : undefined;
    const res = await citizenship_store.fetchCitizenships({
      page: page.value,
      size: rowsPerPage.value,
      sort
    });
    rows.value = res.content;
    totalRecords.value = res.totalElements;
  } finally {
    loading.value = false;
  }
}

const onPage = (event: any) => {
  page.value = event.page;
  rowsPerPage.value = event.rows;
  loadData();
}

const onSort = (event: any) => {
  sortField.value = event.sortField;
  sortOrder.value = event.sortOrder;
  loadData();
}

onMounted(() => {
  loadData();
});

useHead(() => ({
  title: 'Bürgermatrikel - Matrikelverzeichnis'
}));
</script>

<template>
  <Card>
    <template #title>
      <div class="flex flex-col gap-2">
        <h1 class="text-3xl font-bold text-black montserrat-headline">Bürgermatrikel</h1>
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
                <li>Beim Klicken auf die Nummer der jeweiligen Matrikel öffnet sich die Seite mit zusätzlichen Informationen</li>
                <li>Manche Felder besitzen einen Sortierknopf <i class="pi pi-sort-alt"/>, mit dem man die Werte alphabetisch sortieren kann</li>
              </ul>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>
        <DataTable
            :value="rows"
            paginator
            lazy
            :rows="rowsPerPage"
            :rowsPerPageOptions="rowsPerPageOptions"
            :totalRecords="totalRecords"
            :loading="loading"
            stripedRows
            @page="onPage($event)"
            @sort="onSort($event)"
            removableSort
        >
          <Column field="refNumber" header="Meyer-Erlach-Referenz" :sortable="true">
            <template #body="slotProps">
              <NuxtLink
                  :to="`/citizenships/${ slotProps.data.id }`"
                  class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                  prefetch
              >
                {{ slotProps.data.refNumber }}
              </NuxtLink>
            </template>
          </Column>
          <Column field="signature" header="Signatur" class="roboto-plain text-nowrap" :sortable="true"/>
          <Column field="person" header="Eingebürgerte Person" class="roboto-plain">
            <template #body="slotProps">
              <div v-if="slotProps.data.person">
                <NuxtLink
                    :to="`/persons/${ slotProps.data.person.id }`"
                    class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md text-nowrap"
                    prefetch
                >
                  {{ slotProps.data.person.firstName }} {{ slotProps.data.person.lastName }}
                </NuxtLink>
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
          </Column>
          <Column field="dateNaturalization" header="Einbürgerung" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.dateNaturalization" class="text-nowrap">
                {{ slotProps.data.dateNaturalization }}
              </div>
              <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </template>
          </Column>
          <Column field="primarySource.title" header="Primärquelle" class="roboto-plain" :sortable="true">
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
          </Column>
          <Column field="secondarySource" header="Sekundärquelle" class="roboto-plain" :sortable="true">
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
          </Column>
        </DataTable>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
