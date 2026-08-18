<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import SourceSkeleton from "~/components/UI/skeletons/SourceSkeleton.vue";

const route = useRoute();
const sourceId = Number(route.params.id);
const sourceStore = useSourceStore();

const { data: sourceItem, pending: sourcePending } = useAsyncData(
  `source-${sourceId}`,
  () => sourceStore.fetchSourceById(sourceId),
);

const authors = computed(() =>
  sourceItem.value?.authors?.length ? sourceItem.value.authors : null,
);
const links = computed(() =>
  sourceItem.value?.links?.length ? sourceItem.value.links : null,
);

const isLoading = computed(() => sourcePending.value);

useHead(() => ({
  title: sourceItem.value
    ? `${sourceItem.value.title || "Unbekannt"} - Quellenverzeichnis`
    : "Nicht gefunden",
}));
</script>

<template>
  <SourceSkeleton v-if="isLoading" />
  <div v-else class="flex min-h-full flex-col gap-4">
    <Card>
      <template #title>
        <div class="flex flex-col gap-2">
          <div class="flex flex-row justify-between">
            <h1 class="max-w-4/5 text-3xl font-bold text-highlighted">
              {{ sourceItem?.title }}
            </h1>
            <TaskBar :id="sourceId" entity_type="sources" />
          </div>
          <h2
            class="text-2xl font-semibold text-muted"
            v-show="sourceItem?.type"
          >
            {{ sourceItem?.type }}
          </h2>
        </div>
      </template>
      <template #content>
        <Divider />
        <div class="flex flex-col gap-4">
          <div v-show="sourceItem?.signature">
            <div class="flex flex-col gap-2 text-highlighted">
              <div class="text-lg font-bold">Signatur</div>
              <div>{{ sourceItem?.signature }}</div>
            </div>
          </div>
          <div v-show="sourceItem?.description">
            <div class="flex flex-col gap-2 text-highlighted">
              <div class="text-lg font-bold">Beschreibung</div>
              <div>{{ sourceItem?.description }}</div>
            </div>
          </div>
          <div v-show="authors">
            <div class="flex flex-col gap-2 text-highlighted">
              <div class="text-lg font-bold">Autoren</div>
              <ul class="list-disc list-inside">
                <li v-for="author in authors">{{ author }}</li>
              </ul>
            </div>
          </div>
          <div v-show="links">
            <div class="flex flex-col gap-2">
              <div class="text-lg font-bold text-highlighted">Weiterführende Links</div>
              <ul class="list-disc list-inside">
                <li v-for="link in sourceItem?.links">
                  <NuxtLink :to="link" target="_blank" class="text-primary hover:text-highlighted">{{
                    link
                  }}</NuxtLink>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </template>
      <template #footer>
        <Panel header="Notizen" toggleable v-show="sourceItem?.generalNotes">
          <template #header>
            <p class="text-sm font-bold text-highlighted">Notizen</p>
          </template>
          <p class="text-sm text-highlighted">{{ sourceItem?.generalNotes }}</p>
        </Panel>
      </template>
    </Card>
    <UIContentMetadata
      :created-date="sourceItem?.createdDate"
      :last-modified-date="sourceItem?.lastModifiedDate"
    />
  </div>
</template>
