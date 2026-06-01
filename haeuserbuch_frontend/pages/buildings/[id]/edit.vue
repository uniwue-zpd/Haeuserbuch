<script setup lang="ts">
import BuildingForm from "~/components/forms/BuildingForm.vue";

useHead(() => ({
  title: 'Gebäude bearbeiten'
}));

const route = useRoute();
const building_id = Number(route.params.id);
const buildingStore = useBuildingStore();
const { data: buildingItem } = await useAsyncData(() => buildingStore.getBuilding(building_id));
</script>

<template>
  <BuildingForm
    header="Gebäude bearbeiten"
    action="edit"
    :building="buildingItem ?? undefined"
  />
</template>

<style scoped>

</style>
