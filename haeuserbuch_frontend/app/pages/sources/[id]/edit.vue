<script setup lang="ts">
import SourceForm from "~/components/forms/SourceForm.vue";

definePageMeta({
  middleware: 'auth',
});

const route = useRoute();
const sourceId = Number(route.params.id);
const source_store = useSourceStore();

const { data: source_item } = await useAsyncData(`source-${ sourceId }`, () => source_store.fetchSourceById(sourceId));

useHead(() => ({
  title: `Quelle bearbeiten`
}));
</script>

<template>
  <SourceForm header="Quelle bearbeiten" action="edit" :source="source_item ?? undefined"/>
</template>
