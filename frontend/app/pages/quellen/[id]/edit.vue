<script setup lang="ts">
import SourceForm from "~/components/forms/SourceForm.vue";

definePageMeta({
  middleware: 'auth',
});

const route = useRoute();
const sourceId = Number(route.params.id);
const sourceApi = useSources();

const { data: source_item } = await useAsyncData(`source-${sourceId}-details`, () => sourceApi.getSource(sourceId));

useHead(() => ({
  title: `Quelle bearbeiten`
}));
</script>

<template>
  <SourceForm header="Quelle bearbeiten" action="edit" :source="source_item ?? undefined"/>
</template>
