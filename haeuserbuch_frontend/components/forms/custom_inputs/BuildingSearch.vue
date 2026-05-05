<script setup lang="ts">
const props = defineProps({
  context: Object,
});

const buildingStore = useBuildingStore();

const suggestions = ref<BuildingDTO[]>([]);
const loading = ref(false);

const debouncedSearch = debounce(async (query: string) => {
  loading.value = true;
  suggestions.value = await buildingStore.searchBuildings(query);
  loading.value = false;
}, 300);

const onComplete = (event: any) => {
  debouncedSearch(event.query)
}

const value = computed({
  get: () => props.context?.value ?? null,
  set: (val) => props.context?.node.input(val),
});
</script>

<template>
  <AutoComplete
      v-model="value"
      :suggestions="suggestions"
      :loading="loading"
      @complete="onComplete"
      @clear="props.context?.node.input(null)"
      optionLabel="districtHouseNumber"
      dropdown
      showClear
      class="min-w-full"
  />
</template>

<style scoped>

</style>
