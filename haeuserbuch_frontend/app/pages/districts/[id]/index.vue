<script setup lang="ts">
import DistrictSkeleton from "~/components/UI/skeletons/DistrictSkeleton.vue";

const route = useRoute()
const districtStore = useDistrictStore()
const buildingStore = useBuildingStore()

const districtId = Number(route.params.id)

const { data: districtItem, pending: districtPending } = useAsyncData(
    `district-${districtId}`,
    () => districtStore.fetchDistrictById(districtId)
);

const { data: relatedBuildings, pending: buildingsPending } = useAsyncData(
    `district-buildings-${districtId}`,
    () => buildingStore.filterBuildings({ districtId }),
    { default: () => [] }
);

const isLoading = computed(() => districtPending.value || buildingsPending.value);

useHead(() => ({
  title: districtItem.value
      ? `${districtItem.value.name || 'Unbekannt'} - Distriktverzeichnis`
      : 'Nicht gefunden'
}));
</script>

<template>
  <DistrictSkeleton v-if="isLoading"/>
  <Card v-else>
    <template #title>
      <h1 class="text-3xl montserrat-headline font-bold text-black">Distrikt {{ districtItem?.name }}</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <div v-show="districtItem?.description">
          <div class="flex flex-col gap-2">
            <div class="text-xs roboto-plain font-bold">Beschreibung</div>
            <div>{{ districtItem?.description }}</div>
          </div>
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
        <Panel header="Notizen" toggleable v-show="districtItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ districtItem?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="districtItem?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(districtItem?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="districtItem?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(districtItem?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
