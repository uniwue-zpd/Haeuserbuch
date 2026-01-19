<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";

const route = useRoute();
const source_id = Number(route.params.id);
const source_store = useSourceStore();
const source_item = computed(() => source_store.currentSource);
const links = computed(() =>
    source_store.currentSource?.links?.length ? source_store.currentSource.links : null
);
const authors = computed(() =>
    source_store.currentSource?.authors?.length ? source_store.currentSource.authors : null
);

onMounted(async () => {
  await source_store.fetchSourceById(source_id);
});

useHead({
  title: source_item.value ? `${source_item.value.title} - Quellenverzeichnis` : 'Nicht gefunden'
});
</script>

<template>
  <Card>
    <template #title>
      <div class="flex flex-col gap-2 montserrat-headline">
        <div class="flex flex-row justify-between">
          <h1 class="text-3xl font-bold text-black max-w-[80%]">{{ source_item?.title }}</h1>
          <TaskBar :id="source_id" entity_type="sources"/>
        </div>
        <h2 class="text-2xl font-semibold text-gray-600" v-show="source_item?.type">{{ source_item?.type }}</h2>
      </div>
    </template>
    <template #content>
      <Divider/>
      <div class="flex flex-col gap-4">
        <div v-show="source_item?.signature">
          <div class="flex flex-col gap-2 roboto-plain text-black">
            <div class="text-lg roboto-plain font-bold">Signatur</div>
            <div>{{ source_item?.signature }}</div>
          </div>
        </div>
        <div v-show="source_item?.description">
          <div class="flex flex-col gap-2 roboto-plain text-black">
            <div class="text-lg roboto-plain font-bold">Beschreibung</div>
            <div>{{ source_item?.description }}</div>
          </div>
        </div>
        <div v-show="authors">
          <div class="flex flex-col gap-2 roboto-plain text-black">
            <div class="text-lg roboto-plain font-bold">Autoren</div>
            <ul class="list-disc list-inside">
              <li v-for="author in source_item?.authors">{{ author }}</li>
            </ul>
          </div>
        </div>
        <div v-show="links">
          <div class="flex flex-col gap-2 roboto-plain">
            <div class="text-lg roboto-plain text-black font-bold">Weiterführende Links</div>
            <ul class="list-disc list-inside">
              <li v-for="link in source_item?.links">
                <NuxtLink :to="link" target="_blank" class="text-blue-700">{{ link }}</NuxtLink>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="source_item?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ source_item?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="source_item?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p class="font-bold">Erstellt am:</p>
            <p>{{ new Date(source_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="source_item?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p class="font-bold">Stand:</p>
            <p>{{ new Date(source_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
