<script setup lang="ts">
import PersonForm from "~/components/forms/PersonForm.vue";

useHead(() => ({
  title: 'Person bearbeiten',
}));

const route = useRoute();
const person_id = Number(route.params.id);
const person_store = usePersonStore();

const { data: personItem, status } = await useAsyncData(`person-edit-${ person_id }`, () => person_store.fetchPersonById(person_id));
</script>

<template>
  <PersonForm
      header="Person-Objekt bearbeiten"
      action="edit"
      :person="personItem ?? undefined"
  />
</template>

<style scoped>

</style>
