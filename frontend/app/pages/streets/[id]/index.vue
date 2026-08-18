<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import StreetSkeleton from "~/components/UI/skeletons/StreetSkeleton.vue";

const route = useRoute();
const street_store = useStreetStore();
const building_store = useBuildingStore();
const streetId = Number(route.params.id);

const { data: streetItem, pending: streetPending } = useAsyncData(
  `street-${streetId}`,
  () => street_store.fetchStreetById(streetId),
);

const { data: relatedBuildings, pending: relatedBuildingsPending } =
  useAsyncData(
    `street-${streetId}-related-buildings`,
    () => building_store.filterBuildings({ streetId: streetId }),
    { default: () => [] },
  );

const altNames = computed(() => streetItem.value?.altNames || null);

const isLoading = computed(
  () => streetPending.value || relatedBuildingsPending.value,
);

useHead(() => ({
  title: streetItem.value
    ? `${streetItem.value.name || "Unbekannt"} - Straßenverzeichnis`
    : "Nicht gefunden",
}));
</script>

<template>
  <StreetSkeleton v-if="isLoading" />
  <div v-else class="flex min-h-full flex-col gap-4">
    <Card>
      <template #title>
        <div class="flex flex-row justify-between">
          <h1 class="text-3xl font-bold text-highlighted">{{ streetItem?.name }}</h1>
          <TaskBar :id="streetId" entity_type="streets" />
        </div>
      </template>
      <template #content>
        <div class="flex flex-col gap-2">
          <div v-show="streetItem?.description">
            <div class="flex flex-col gap-2">
              <div class="text-lg font-bold">Beschreibung</div>
              <div>{{ streetItem?.description }}</div>
            </div>
          </div>
          <div v-show="altNames">
            <div class="flex flex-col gap-2">
              <h2 class="text-lg font-bold">Andere Namen</h2>
              <ul class="list-disc list-inside">
                <li v-for="(altName, index) in altNames" :key="index">
                  {{ altName }}
                </li>
              </ul>
            </div>
          </div>
          <Divider />
          <div v-show="relatedBuildings.length > 0" class="flex flex-col gap-2">
            <h2 class="text-lg font-bold text-highlighted">Zugeordnete Gebäude</h2>
            <DataTable
              :value="relatedBuildings"
              paginator
              :rows="10"
              stripedRows
              :loading="!relatedBuildings"
            >
              <Column
                field="districtPropertyNumber"
                header="Bezeichnung"
                :sortable="true"
              >
                <template #body="{ data }">
                  <NuxtLink
                    :to="`/katasterplan/${data.id}`"
                    class="font-bold text-highlighted"
                  >
                    {{ data.districtPropertyNumber }}
                  </NuxtLink>
                </template>
              </Column>
              <Column field="id" header="ID" :sortable="true" />
            </DataTable>
          </div>
        </div>
      </template>
      <template #footer>
        <Panel header="Notizen" toggleable v-show="streetItem?.generalNotes">
          <template #header>
            <p class="text-sm font-bold text-highlighted">Notizen</p>
          </template>
          <p class="text-sm text-highlighted">{{ streetItem?.generalNotes }}</p>
        </Panel>
      </template>
    </Card>
    <UIContentMetadata
      :created-date="streetItem?.createdDate"
      :last-modified-date="streetItem?.lastModifiedDate"
    />
  </div>
</template>
