<script setup lang="ts">
import { onMounted } from "vue";

const route = useRoute();
const district_store = useDistrictStore();
const district_id = Number(route.params.id);
const district_item = computed(() => district_store.current_district);

onMounted(async () => {
  await district_store.fetchDistrictById(district_id);
});

useHead(() => ({
  title: district_item.value ? `${district_item.value.name} - Distriktverzeichnis` : 'Nicht gefunden',
}));
</script>

<template>
  <Card v-show="district_item">
    <template #title>
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ district_item?.name }}</h1>
    </template>
    <template>
      <div v-show="district_item?.description">
        <div class="text-xs roboto-plain font-bold">Beschreibung</div>
        <div>{{ district_item?.description }}</div>
      </div>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="district_item?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ district_item?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="district_item?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(district_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="district_item?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(district_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>