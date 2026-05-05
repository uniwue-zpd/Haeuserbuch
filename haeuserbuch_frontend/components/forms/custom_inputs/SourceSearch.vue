<script setup lang="ts">
const props = defineProps({
  context: Object,
});

const sourceStore = useSourceStore();

const suggestions = ref<SourceDTO[]>([]);
const loading = ref(false);

const debouncedSearch = debounce(async (query: string) => {
  loading.value = true;
  suggestions.value = await sourceStore.searchSources(query);
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
      optionLabel="title"
      dropdown
      showClear
      class="min-w-full"
  />
</template>

<style scoped>

</style>
