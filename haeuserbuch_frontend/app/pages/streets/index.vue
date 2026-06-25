<script setup lang="ts">
import {FilterMatchMode} from "@primevue/core";

const street_store = useStreetStore();

const { data: streets } = useAsyncData('streets', () => street_store.fetchStreets());

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  name: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

useHead(() => ({
  title: 'Straßen - Verzeichnis der Straßen'
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black montserrat-headline">Straßen</h1>
    </template>
    <template #content>
      <DataTable
          v-model:filters="filters"
          :value="streets"
          :loading="!streets"
          paginator
          :rows="10"
          :global-filter-fields="['name']"
          filter-display="row"
      >
        <Column field="name" header="Name" :sortable="true">
          <template #body="{ data }">
            <NuxtLink
              :to="`/streets/${data.id}`"
              class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
            >
              {{ data.name }}
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
        <Column field="altNames" header="Andere Namen">
          <template #body="{ data }">
            <div v-if="data.altNames.length > 0">
              <ul class="list-disc list-inside">
                <li v-for="(altName, index) in data.altNames" :key="index">{{ altName }}</li>
              </ul>
            </div>
            <div v-else>
              <span class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
            </div>
          </template>
        </Column>
      </DataTable>
    </template>
  </Card>
</template>

<style scoped>

</style>
