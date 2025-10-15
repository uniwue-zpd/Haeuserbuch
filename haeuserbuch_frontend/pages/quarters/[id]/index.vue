<script setup lang="ts">
import type {BuildingDTO} from "~/utils/types";

const route = useRoute();
const quarter_store = useQuarterStore();
const building_store = useBuildingStore();
const quarter_id = Number(route.params.id);
const quarter_item = computed(() => quarter_store.current_quarter);
const related_buildings = ref<BuildingDTO[]>([]);

onMounted(async () => {
  await quarter_store.fetchQuarterById(quarter_id);
  related_buildings.value = await building_store.filterBuildingsByPropertyId("quarter", quarter_id);
});

useHead(() => ({
  title: quarter_item.value ? `${quarter_item.value.name} - Distriktverzeichnis` : 'Nicht gefunden',
}));
</script>

<template>
  <Card v-show="quarter_item">
    <template #title>
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ quarter_item?.name }}</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <div v-show="quarter_item?.description">
          <div class="text-xs roboto-plain font-bold">Beschreibung</div>
          <div>{{ quarter_item?.description }}</div>
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
        <Panel header="Notizen" toggleable v-show="quarter_item?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ quarter_item?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="quarter_item?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(quarter_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="quarter_item?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(quarter_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
