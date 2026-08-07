<script setup lang="ts">
import BuildingForm from "~/components/forms/BuildingForm.vue";
import FetchError from "~/components/UI/FetchError.vue";

useHead(() => ({
  title: 'Gebäude bearbeiten'
}));

const route = useRoute();
const building_id = Number(route.params.id);
const buildingStore = useBuildingStore();
const { data: buildingItem, error: hasError } = await useAsyncData(() => buildingStore.getBuilding(building_id));
</script>

<template>
  <FetchError v-if="hasError" :error="hasError"/>
  <BuildingForm
      v-else
      header="Gebäude bearbeiten"
      action="edit"
      :building="buildingItem ?? undefined"
  />
</template>
