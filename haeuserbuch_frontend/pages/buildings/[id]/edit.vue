<script setup lang="ts">
import BuildingForm from "~/components/forms/BuildingForm.vue";
import { computed, onMounted } from "vue";

useHead(() => ({
  title: 'Gebäude bearbeiten'
}));

const route = useRoute();
const building_id = Number(route.params.id);
const building_store = useBuildingStore();
const building_item = computed(() => building_store.current_building);

onMounted(async () => {
  await building_store.fetchBuildingById(building_id);
})
</script>

<template>
  <BuildingForm
    header="Gebäude bearbeiten"
    action="edit"
    :building="building_item ?? undefined"
  />
</template>

<style scoped>

</style>