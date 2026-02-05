<script setup lang="ts">
import {ref} from "vue";
import {FilterMatchMode} from "@primevue/core";
import {title_shortener} from "~/utils/helpers";

const citizenship_store = useCitizenshipStore();

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  refNumber: { value: null, matchMode: FilterMatchMode.CONTAINS },
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
            :global-filter-fields="['signature', 'primarySource.title', 'refNumber', 'date']"
            filter-display="row"
            paginator :rows="10" stripedRows
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
          <Column field="refNumber" header="Nummer (Mayer-Erlach)" :sortable="true">
            <template #body="slotProps">
              <NuxtLink
                  :to="`/citizenships/${ slotProps.data.id }`"
                  class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                  prefetch
              >
                {{ slotProps.data.refNumber }}
              </NuxtLink>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText
                  v-model="filterModel.value"
                  type="text" @input="filterCallback()"
                  placeholder="Durchsuchen"
              />
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
          <Column field="date" header="Datum" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.date" class="text-nowrap">
                {{ slotProps.data.date }}
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
