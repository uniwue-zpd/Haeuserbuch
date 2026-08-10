<script setup lang="ts">
import StreetForm from "~/components/forms/StreetForm.vue";

definePageMeta({
  middleware: 'auth',
});

const route = useRoute();
const streetId = Number(route.params.id);
const street_store = useStreetStore();

const { data: streetItem } = await useAsyncData(`street-${streetId}`, () => street_store.fetchStreetById(streetId));

useHead(() => ({
  title: `Straße bearbeiten`
}));
</script>

<template>
  <StreetForm
      header="Straße bearbeiten"
      action="edit"
      :street="streetItem ?? undefined"
  />
</template>
