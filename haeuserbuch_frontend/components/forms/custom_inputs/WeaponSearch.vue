<script setup lang="ts">
const props = defineProps({
  context: Object,
});

const weaponStore = useWeaponStore();

const suggestions = ref<WeaponDTO[]>([]);
const loading = ref(false);

const debouncedSearch = debounce(async (query: string) => {
  loading.value = true;
  suggestions.value = await weaponStore.searchWeapons(query);
  loading.value = false;
}, 300);

const onComplete = (event: any) => {
  debouncedSearch(event.query)
}

const value = computed({
  get: () => props.context?.value ?? null,
  set: (val) => props.context?.node.input(val),
})
</script>

<template>
  <AutoComplete
      v-model="value"
      :suggestions="suggestions"
      :loading="loading"
      @complete="onComplete"
      @clear="props.context?.node.input(null)"
      optionLabel="name"
      dropdown
      showClear
      class="min-w-full"
  />
</template>

<style scoped>

</style>
