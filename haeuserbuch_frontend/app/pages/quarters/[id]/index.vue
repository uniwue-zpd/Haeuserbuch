<script setup lang="ts">
import QuarterSkeleton from "~/components/UI/skeletons/QuarterSkeleton.vue";

const route = useRoute();
const quarterStore = useQuarterStore();
const buildingStore = useBuildingStore();

const quarterId = Number(route.params.id);

const { data: quarterItem, pending: quarterPending } = useAsyncData(
  `quarter-${quarterId}`,
  () => quarterStore.fetchQuarterById(quarterId),
);

const { data: relatedBuildings, pending: buildingsPending } = useAsyncData(
  `quarter-buildings-${quarterId}`,
  () => buildingStore.filterBuildings({ quarterId }),
  { default: () => [] },
);

const isLoading = computed(
  () => quarterPending.value || buildingsPending.value,
);

useHead(() => ({
  title: quarterItem.value
    ? `${quarterItem.value.name} - Verzeichnis der Viertel`
    : "Nicht gefunden",
}));
</script>

<template>
  <QuarterSkeleton v-if="isLoading" />
  <div v-else class="flex min-h-full flex-col gap-4">
    <Card>
      <template #title>
        <h1 class="text-3xl font-bold text-black">{{ quarterItem?.name }}</h1>
      </template>
      <template #content>
        <div class="flex flex-col gap-2">
          <div v-show="quarterItem?.description">
            <div class="text-xs font-bold">Beschreibung</div>
            <div>{{ quarterItem?.description }}</div>
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
                    :to="`/katasterplan/${data.id}`"
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
        <Panel header="Notizen" toggleable v-show="quarterItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black">{{ quarterItem?.generalNotes }}</p>
        </Panel>
      </template>
    </Card>
    <UIContentMetadata
      :created-date="quarterItem?.createdDate"
      :last-modified-date="quarterItem?.lastModifiedDate"
    />
  </div>
</template>
