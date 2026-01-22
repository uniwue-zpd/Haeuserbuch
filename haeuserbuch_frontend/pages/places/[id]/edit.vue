<script setup lang="ts">
import PlaceForm from "~/components/forms/PlaceForm.vue";

useHead(() => ({
  title: 'Ort bearbeiten'
}));

const route = useRoute();
const place_id = Number(route.params.id);
const place_store = usePlaceStore();
const place_item = computed(() => place_store.current_place);

onMounted(async () => {
  await place_store.fetchPlaceById(place_id);
});
</script>

<template>
  <PlaceForm header="Ort bearbeiten" action="edit" :place="place_item ?? undefined"/>
</template>

<style scoped>

</style>
