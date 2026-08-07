<script setup lang="ts">
import PlaceForm from "~/components/forms/PlaceForm.vue";

useHead(() => ({
  title: 'Ort erstellen'
}));
</script>

<template>
  <PlaceForm header="Neuen Ort erstellen" action="create"/>
</template>
