<script setup lang="ts">
const props = defineProps({
  context: Object,
});

const isMultiple = props.context?.isMultiple;

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
      @clear="isMultiple ? props.context?.node.input([]) : props.context?.node.input(null)"
      optionLabel="title"
      class="min-w-full"
      :dropdown="!isMultiple"
      showClear
      :multiple="isMultiple"
  />
</template>

<style scoped>

</style>
