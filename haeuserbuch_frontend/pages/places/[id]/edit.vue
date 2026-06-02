<script setup lang="ts">
import PlaceForm from "~/components/forms/PlaceForm.vue";
import FetchError from "~/components/UI/FetchError.vue";

useHead(() => ({
  title: 'Ort bearbeiten'
}));

const route = useRoute();
const placeId = Number(route.params.id);
const placeStore = usePlaceStore();
const { data: placeItem, error: hasError } = useAsyncData(() => placeStore.getPlace(placeId));
</script>

<template>
  <FetchError v-if="hasError" :error="hasError"/>
  <PlaceForm
      v-else
      header="Ort bearbeiten"
      action="edit"
      :place="placeItem ?? undefined"
  />
</template>

<style scoped>

</style>
