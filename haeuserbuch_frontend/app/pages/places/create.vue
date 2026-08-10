<script setup lang="ts">
import PlaceForm from "~/components/forms/PlaceForm.vue";

definePageMeta({
  middleware: 'auth',
});

useHead(() => ({
  title: 'Ort erstellen'
}));
</script>

<template>
  <PlaceForm header="Neuen Ort erstellen" action="create"/>
</template>
