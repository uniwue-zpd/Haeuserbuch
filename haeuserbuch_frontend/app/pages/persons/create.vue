<script setup lang="ts">
import PersonForm from "~/components/forms/PersonForm.vue";

definePageMeta({
  middleware: 'auth',
});

useHead(() => ({
  title: 'Neue Person erstellen',
}));
</script>

<template>
  <PersonForm header="Neues Person-Objekt erstellen" action="create"/>
</template>
