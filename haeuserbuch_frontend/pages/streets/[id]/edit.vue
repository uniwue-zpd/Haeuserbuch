<script setup lang="ts">
import StreetForm from "~/components/forms/StreetForm.vue";

const route = useRoute();
const street_id = Number(route.params.id);
const street_store = useStreetStore();
const street_item = computed(() => street_store.current_street);

onMounted(async () => {
  await street_store.fetchStreetById(street_id);
});

useHead(() => ({
  title: `Straße bearbeiten`
}));
</script>

<template>
  <StreetForm
      header=""
      action="edit"
      :street="street_item ?? undefined"
  />
</template>

<style scoped>

</style>
