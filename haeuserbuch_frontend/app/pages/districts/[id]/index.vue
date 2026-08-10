<script setup lang="ts">
import DistrictSkeleton from "~/components/UI/skeletons/DistrictSkeleton.vue";

const route = useRoute();
const districtStore = useDistrictStore();
const buildingStore = useBuildingStore();

const districtId = Number(route.params.id);

const { data: districtItem, pending: districtPending } = useAsyncData(
  `district-${districtId}`,
  () => districtStore.fetchDistrictById(districtId),
);

const { data: relatedBuildings, pending: buildingsPending } = useAsyncData(
  `district-buildings-${districtId}`,
  () => buildingStore.filterBuildings({ districtId }),
  { default: () => [] },
);

const isLoading = computed(
  () => districtPending.value || buildingsPending.value,
);

useHead(() => ({
  title: districtItem.value
    ? `${districtItem.value.name || "Unbekannt"} - Distriktverzeichnis`
    : "Nicht gefunden",
}));
</script>

<template>
  <DistrictSkeleton v-if="isLoading" />
  <div v-else class="flex min-h-full flex-col gap-4">
    <Card>
      <template #title>
        <h1 class="text-3xl font-bold text-black">
          Distrikt {{ districtItem?.name }}
        </h1>
      </template>
      <template #content>
        <div class="flex flex-col gap-2">
          <div v-show="districtItem?.description">
            <div class="flex flex-col gap-2">
              <div class="text-xs font-bold">Beschreibung</div>
              <div>{{ districtItem?.description }}</div>
            </div>
          </div>
          <Divider />
          <div v-show="relatedBuildings.length > 0" class="flex flex-col gap-2">
            <h2 class="text-lg font-bold text-black">Zugeordnete Gebäude</h2>
            <DataTable :value="relatedBuildings" paginator :rows="10" stripedRows>
              <Column
                field="districtPropertyNumber"
                header="Bezeichnung"
                :sortable="true"
              >
                <template #body="{ data }">
                  <NuxtLink
                    :to="`/buildings/${data.id}`"
                    class="font-bold text-black"
                  >
                    {{ data.districtPropertyNumber }}
                  </NuxtLink>
                </template>
              </Column>
            </DataTable>
          </div>
        </div>
      </template>
      <template #footer>
        <Panel header="Notizen" toggleable v-show="districtItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black">{{ districtItem?.generalNotes }}</p>
        </Panel>
      </template>
    </Card>
    <UIContentMetadata
      :created-date="districtItem?.createdDate"
      :last-modified-date="districtItem?.lastModifiedDate"
    />
  </div>
</template>
