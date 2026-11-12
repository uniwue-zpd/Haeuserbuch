<script setup lang="ts">
import PersonForm from "~/components/forms/PersonForm.vue";

definePageMeta({
  middleware: 'auth',
});

useHead(() => ({
  title: 'Person bearbeiten',
}));

const route = useRoute();
const person_id = Number(route.params.id);
const personApi = usePersons();

const { data: personItem, status } = await useAsyncData(`person-edit-${ person_id }`, () => personApi.getPerson(person_id));
</script>

<template>
  <PersonForm
      header="Person-Objekt bearbeiten"
      action="edit"
      :person="personItem ?? undefined"
  />
</template>
