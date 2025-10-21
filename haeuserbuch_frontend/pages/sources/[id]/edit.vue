<script setup lang="ts">
import SourceForm from "~/components/forms/SourceForm.vue";

const route = useRoute();
const source_id = Number(route.params.id);
const source_store = useSourceStore();
const source_item = computed(() => source_store.currentSource);

onMounted(async () => {
  await source_store.fetchSourceById(source_id);
});

useHead(() => ({
  title: `Quelle bearbeiten`
}));
</script>

<template>
  <SourceForm header="Quelle bearbeiten" action="edit" :source="source_item ?? undefined"/>
</template>

<style scoped>

</style>
