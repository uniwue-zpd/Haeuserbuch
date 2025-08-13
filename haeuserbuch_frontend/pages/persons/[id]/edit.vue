<script setup lang="ts">
import { onMounted } from "vue";
import PersonForm from "~/components/forms/PersonForm.vue";

useHead(() => ({
  title: 'Person bearbeiten',
}));

const route = useRoute();
const person_id = Number(route.params.id);
const person_store = usePersonStore();
const person_item = computed(() => person_store.current_person);

onMounted(async () => {
  await person_store.fetchPersonById(person_id);
});
</script>

<template>
  <PersonForm
      header="Person-Objekt bearbeiten"
      action="edit"
      :person="person_item ?? undefined"
  />
</template>

<style scoped>

</style>
