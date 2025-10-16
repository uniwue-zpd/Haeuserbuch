<script setup lang="ts">
import {onMounted} from "vue";

const route = useRoute();
const street_store = useStreetStore();
const building_store = useBuildingStore();
const street_id = Number(route.params.id);
const street_item = computed(() => street_store.current_street);
const related_buildings = ref<BuildingDTO[]>([]);

onMounted(async () => {
  await street_store.fetchStreetById(street_id);
  related_buildings.value = await building_store.filterBuildingsByPropertyId("streetId", street_id);
});

useHead(() => ({
  title: street_item.value ? `${street_item.value.name} - Verzeichnis der Straßen` : 'Nicht gefunden',
}));
</script>

<template>
  <Card v-show="street_item">
    <template #title>
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ street_item?.name }}</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <div v-show="street_item?.description">
          <div class="flex flex-col gap-2">
            <div class="text-lg roboto-plain font-bold">Beschreibung</div>
            <div>{{ street_item?.description }}</div>
          </div>
        </div>
        <div v-show="street_item?.altNames.length > 0">
          <div class="flex flex-col gap-2">
            <h2 class="text-lg roboto-plain font-bold">Andere Namen</h2>
            <ul class="list-disc list-inside">
              <li v-for="(altName, index) in street_item?.altNames" :key="index" class="roboto-plain">
                {{ altName }}
              </li>
            </ul>
          </div>
        </div>
        <Divider/>
        <div v-show="related_buildings.length > 0" class="flex flex-col gap-2">
          <h2 class="text-lg montserrat-headline font-bold text-black">Zugeordnete Gebäude</h2>
          <DataTable :value="related_buildings" paginator :rows="10" stripedRows>
            <Column field="districtHouseNumber" header="Bezeichnung" :sortable="true">
              <template #body="{ data }">
                <NuxtLink
                    :to="`/buildings/${data.id}`"
                    class="roboto-plain font-bold text-black"
                >
                  {{ data.districtHouseNumber }}
                </NuxtLink>
              </template>
            </Column>
          </DataTable>
        </div>
      </div>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="street_item?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ street_item?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="street_item?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(street_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="street_item?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(street_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
