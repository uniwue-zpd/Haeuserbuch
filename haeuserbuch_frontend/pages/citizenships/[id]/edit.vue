<script setup lang="ts">
import CitizenshipForm from "~/components/forms/CitizenshipForm.vue";

useHead(() => ({
  title: 'Bürgermatrikel bearbeiten'
}));

const route = useRoute();
const citizenship_id = Number(route.params.id);
const citizenship_store = useCitizenshipStore();
const citizenship_item = computed(() => citizenship_store.current_citizenship);

onMounted(async () => {
  await citizenship_store.fetchCitizenshipById(citizenship_id);
});
</script>

<template>
  <CitizenshipForm header="Bürgermatrikel bearbeiten" action="edit" :citizenship="citizenship_item ?? undefined"/>
</template>

<style scoped>

</style>
