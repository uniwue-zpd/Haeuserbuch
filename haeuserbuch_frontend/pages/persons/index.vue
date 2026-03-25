<script setup lang="ts">
import { ref } from "vue";
import { FilterMatchMode } from "@primevue/core";

const person_store = usePersonStore();

const sex = ref([
  {type: 'männlich', value: 'männlich'},
  {type: 'weiblich', value: 'weiblich'},
  {type: 'unbekannt', value: null}
]);
const isCitizen = ref([
  {type: 'Ja', value: true},
  {type: 'Nein', value: false},
  {type: 'unbekannt', value: null}
]);

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  fullName: { value: null, matchMode: FilterMatchMode.CONTAINS },
  sex: { value: null, matchMode: FilterMatchMode.IN },
  isCitizen: { value: null, matchMode: FilterMatchMode.IN }
});

useHead(() => ({
  title: 'Personen - Personenverzeichnis',
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black montserrat-headline">Personen</h1>
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
                <li>Anhand dieser Tabelle können Sie nach bestimmten Personen suchen und verschiedene Filter <i class="pi pi-filter"/> aktivieren</li>
                <li>Jedes Feld besitzt einen Sortierknopf <i class="pi pi-sort-alt"/>, mit dem man die Werte alphabetisch sortieren kann</li>
              </ul>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>
        <DataTable
            v-model:filters="filters"
            :value="person_store.persons"
            :global-filter-fields="['fullName', 'sex', 'occupation', 'occupationCategory', 'confession', 'origin.realName']"
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
          <Column field="fullName" header="Name" :sortable="true">
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
                  type="text" @input="filterCallback()"
                  placeholder="Nach Namen suchen"
              />
            </template>
          </Column>
          <Column field="firstName" header="Vorname" class="roboto-plain" :sortable="true" />
          <Column field="lastName" header="Nachname" class="roboto-plain" :sortable="true" />
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
              <MultiSelect
                  v-model="filterModel.value"
                  @change="filterCallback()"
                  :options="sex"
                  optionLabel="type"
                  placeholder="Beliebige"
              >
                <template #option="slotProps">
                  <div>{{ slotProps.option.value }}</div>
                </template>
              </MultiSelect>
            </template>
          </Column>
          <Column field="occupation" header="Beruf" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.occupation.originalText">{{ slotProps.data.occupation.originalText }}</div>
              <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
            </template>
          </Column>
          <Column field="occupationCategory" header="Berufskategorie" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.occupationCategory">{{ slotProps.data.occupationCategory }}</div>
              <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
            </template>
          </Column>
          <Column
              field="isCitizen" filterField="isCitizen"
              header="Bürger"
              class="roboto-plain"
              :showFilterMenu="false" :sortable="true"
          >
            <template #body="slotProps">
              <div v-if="slotProps.data.isCitizen !== null">
                <i :class="[slotProps.data.isCitizen ? 'pi pi-check text-green-500' : 'pi pi-times text-red-500']"/>
              </div>
              <div v-else class="roboto-italic">unbekannt</div>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <MultiSelect
                  v-model="filterModel.value"
                  @change="filterCallback()"
                  :options="isCitizen" optionLabel="type" :option-value="option => option.value"
                  placeholder="Beliebige"
              >
                <template #option="slotProps">
                  <div>{{ slotProps.option.value }}</div>
                </template>
              </MultiSelect>
            </template>
          </Column>
          <Column field="confession" header="Religion" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.confession">{{ slotProps.data.confession }}</div>
              <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
            </template>
          </Column>
          <Column field="origin" header="Herkunft" class="roboto-plain" :sortable="true">
            <template #body="slotProps">
              <div v-if="slotProps.data.origin.originalText">
                <div>{{ slotProps.data.origin.originalText }}</div>
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
