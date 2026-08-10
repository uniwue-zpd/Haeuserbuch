<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import StreetSkeleton from "~/components/UI/skeletons/StreetSkeleton.vue";

const route = useRoute();
const street_store = useStreetStore();
const building_store = useBuildingStore();
const streetId = Number(route.params.id);

const { data: streetItem, pending: streetPending } = useAsyncData(
    `street-${streetId}`,
    () => street_store.fetchStreetById(streetId)
);

const { data: relatedBuildings, pending: relatedBuildingsPending } = useAsyncData(
    `street-${streetId}-related-buildings`,
    () => building_store.filterBuildings({ streetId: streetId }),
    { default: () =>[] }
);

const altNames = computed(() => streetItem.value?.altNames || null);

const isLoading = computed(() => streetPending.value || relatedBuildingsPending.value);

useHead(() => ({
  title: streetItem.value
      ? `${streetItem.value.name || 'Unbekannt'} - Straßenverzeichnis`
      : 'Nicht gefunden'
}));
</script>

<template>
  <StreetSkeleton v-if="isLoading"/>
  <Card v-else>
    <template #title>
      <div class="flex flex-row justify-between">
        <h1 class="text-3xl montserrat-headline font-bold text-black">{{ streetItem?.name }}</h1>
        <TaskBar :id="streetId" entity_type="streets"/>
      </div>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <div v-show="streetItem?.description">
          <div class="flex flex-col gap-2">
            <div class="text-lg roboto-plain font-bold">Beschreibung</div>
            <div>{{ streetItem?.description }}</div>
          </div>
        </div>
        <div v-show="altNames">
          <div class="flex flex-col gap-2">
            <h2 class="text-lg roboto-plain font-bold">Andere Namen</h2>
            <ul class="list-disc list-inside">
              <li v-for="(altName, index) in altNames" :key="index" class="roboto-plain">
                {{ altName }}
              </li>
            </ul>
          </div>
        </div>
        <Divider/>
        <div v-show="relatedBuildings.length > 0" class="flex flex-col gap-2">
          <h2 class="text-lg montserrat-headline font-bold text-black">Zugeordnete Gebäude</h2>
          <DataTable :value="relatedBuildings" paginator :rows="10" stripedRows :loading="!relatedBuildings">
            <Column field="districtPropertyNumber" header="Bezeichnung" :sortable="true">
              <template #body="{ data }">
                <NuxtLink
                    :to="`/buildings/${data.id}`"
                    class="roboto-plain font-bold text-black"
                >
                  {{ data.districtPropertyNumber }}
                </NuxtLink>
              </template>
            </Column>
            <Column field="id" header="ID" :sortable="true"/>
          </DataTable>
        </div>
      </div>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="streetItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ streetItem?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="streetItem?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(streetItem?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="streetItem?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(streetItem?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
