<script setup lang="ts">
import QuarterSkeleton from "~/components/UI/skeletons/QuarterSkeleton.vue";

const route = useRoute();
const quarterStore = useQuarterStore();
const buildingStore = useBuildingStore();

const quarterId = Number(route.params.id);

const { data: quarterItem, pending: quarterPending } = useAsyncData(
    `quarter-${quarterId}`,
    () => quarterStore.fetchQuarterById(quarterId)
);

const { data: relatedBuildings, pending: buildingsPending } = useAsyncData(
    `quarter-buildings-${quarterId}`,
    () => buildingStore.filterBuildings({ quarterId }),
    { default: () => [] }
);

const isLoading = computed(() => quarterPending.value || buildingsPending.value);

useHead(() => ({
  title: quarterItem.value
      ? `${quarterItem.value.name} - QuarterVerzeichnis`
      : 'Nicht gefunden',
}));
</script>

<template>
  <QuarterSkeleton v-if="isLoading"/>
  <Card v-else>
    <template #title>
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ quarterItem?.name }}</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <div v-show="quarterItem?.description">
          <div class="text-xs roboto-plain font-bold">Beschreibung</div>
          <div>{{ quarterItem?.description }}</div>
        </div>
        <Divider/>
        <div v-show="relatedBuildings.length > 0" class="flex flex-col gap-2">
          <h2 class="text-lg montserrat-headline font-bold text-black">Zugeordnete Gebäude</h2>
          <DataTable :value="relatedBuildings" paginator :rows="10" stripedRows>
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
        <Panel header="Notizen" toggleable v-show="quarterItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ quarterItem?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="quarterItem?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(quarterItem?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="quarterItem?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(quarterItem?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
