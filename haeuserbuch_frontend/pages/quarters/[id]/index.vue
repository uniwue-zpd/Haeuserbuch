<script setup lang="ts">
const route = useRoute();
const quarter_store = useQuarterStore();
const quarter_id = Number(route.params.id);
const quarter_item = computed(() => quarter_store.current_quarter);

onMounted(async () => {
  await quarter_store.fetchQuarterById(quarter_id);
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
    <template>
      <div v-show="quarter_item?.description">
        <div class="text-xs roboto-plain font-bold">Beschreibung</div>
        <div>{{ quarter_item?.description }}</div>
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
