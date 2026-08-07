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
  <Card v-else>
    <template #title>
      <div class="flex flex-col gap-2">
        <div class="flex flex-row justify-between">
          <h1 class="text-3xl font-bold text-black max-w-[80%]">
            {{ sourceItem?.title }}
          </h1>
          <TaskBar :id="sourceId" entity_type="sources" />
        </div>
        <h2
          class="text-2xl font-semibold text-gray-600"
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
          <div class="flex flex-col gap-2 text-black">
            <div class="text-lg font-bold">Signatur</div>
            <div>{{ sourceItem?.signature }}</div>
          </div>
        </div>
        <div v-show="sourceItem?.description">
          <div class="flex flex-col gap-2 text-black">
            <div class="text-lg font-bold">Beschreibung</div>
            <div>{{ sourceItem?.description }}</div>
          </div>
        </div>
        <div v-show="authors">
          <div class="flex flex-col gap-2 text-black">
            <div class="text-lg font-bold">Autoren</div>
            <ul class="list-disc list-inside">
              <li v-for="author in authors">{{ author }}</li>
            </ul>
          </div>
        </div>
        <div v-show="links">
          <div class="flex flex-col gap-2">
            <div class="text-lg text-black font-bold">Weiterführende Links</div>
            <ul class="list-disc list-inside">
              <li v-for="link in sourceItem?.links">
                <NuxtLink :to="link" target="_blank" class="text-blue-700">{{
                  link
                }}</NuxtLink>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="sourceItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black">{{ sourceItem?.generalNotes }}</p>
        </Panel>
        <Divider />
        <div class="flex flex-col">
          <div
            v-if="sourceItem?.createdDate"
            class="flex flex-row space-x-2 text-sm text-black"
          >
            <p class="font-bold">Erstellt am:</p>
            <p>{{ new Date(sourceItem?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div
            v-if="sourceItem?.lastModifiedDate"
            class="flex flex-row space-x-2 text-sm text-black"
          >
            <p class="font-bold">Stand:</p>
            <p>
              {{ new Date(sourceItem?.lastModifiedDate).toLocaleDateString() }}
            </p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped></style>
