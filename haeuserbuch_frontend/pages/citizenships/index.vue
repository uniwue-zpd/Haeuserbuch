<script setup lang="ts">
import {ref} from "vue";
import {FilterMatchMode} from "@primevue/core";

const citizenship_store = useCitizenshipStore();

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  number: { value: null, matchMode: FilterMatchMode.CONTAINS },
  'source.title': { value: null, matchMode: FilterMatchMode.CONTAINS },
  'place.realName': { value: null, matchMode: FilterMatchMode.CONTAINS },
});

useHead(() => ({
  title: 'Bürgermatrikel - Matrikelverzeichnis'
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black montserrat-headline">Bürgermatrikel</h1>
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
                <li>Anhand dieser Tabelle können Sie nach bestimmten Informationen suchen und verschiedene Filter <i class="pi pi-filter"/> aktivieren</li>
                <li>Manche Felder besitzen einen Sortierknopf <i class="pi pi-sort-alt"/>, mit dem man die Werte alphabetisch sortieren kann</li>
              </ul>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>
        <DataTable
            v-model:filters="filters"
            :value="citizenship_store.citizenships"
            :global-filter-fields="['signature', 'source.title', 'place.realName', 'number', 'date']"
            filter-display="row"
            paginator :rows="10"
        >
          <template #header>
            <div class="flex flex-row justify-end">
              <IconField>
                <InputIcon>
                  <i class="pi pi-search"/>
                </InputIcon>
                <InputText
                    v-model="filters['global'].value"
                    type="text"
                    placeholder="Schlagwortsuche"
                />
              </IconField>
            </div>
          </template>
          <Column field="number" header="Nummer" :sortable="true">
            <template #body="slotProps">
              <NuxtLink
                  :to="`/citizenships/${slotProps.data.id}`"
                  class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                  prefetch
              >
                {{ slotProps.data.number }}
              </NuxtLink>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  type="text" @input="filterCallback()"
                  placeholder="Nach Nummern suchen"
              />
            </template>
          </Column>
          <Column field="signature" header="Signatur" class="roboto-plain" :sortable="true" />
          <Column field="persons" header="Personen" class="roboto-plain">
            <template #body="slotProps">
              <div v-if="slotProps.data.persons.length > 0">
                <ul class="list-disc list-inside">
                  <li v-for="(person, index) in slotProps.data.persons" :key="index">
                    <NuxtLink :to="`/persons/${person.id}`">{{ person.fullName }}</NuxtLink>
                  </li>
                </ul>
              </div>
              <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
            </template>
          </Column>
          <Column field="place" header="Ort" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.place">
                <NuxtLink :to="`/places/${slotProps.data.place.id}`">
                  {{ slotProps.data.place.realName }}
                </NuxtLink>
              </div>
              <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
            </template>
          </Column>
          <Column field="date" header="Datum" class="roboto-plain" :sortable="true" />
          <Column field="source" header="Quelle" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.source">
                <NuxtLink :to="`/sources/${slotProps.data.source.id}`">
                  {{ slotProps.data.source.title }}
                </NuxtLink>
              </div>
              <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
            </template>
          </Column>
        </DataTable>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
