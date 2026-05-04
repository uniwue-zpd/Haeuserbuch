<script setup lang="ts">
import CitizenshipForm from "~/components/forms/CitizenshipForm.vue";

useHead(() => ({
  title: 'Bürgermatrikel bearbeiten'
}));

const route = useRoute();
const citizenship_id = Number(route.params.id);
const citizenship_store = useCitizenshipStore();

const { data: citizenshipItem, status } = await useAsyncData(`citizenship-${ citizenship_id }`, () => citizenship_store.fetchCitizenshipById(citizenship_id));
</script>

<template>
  <CitizenshipForm header="Bürgermatrikel bearbeiten" action="edit" :citizenship="citizenshipItem ?? undefined"/>
</template>

<style scoped>

</style>
