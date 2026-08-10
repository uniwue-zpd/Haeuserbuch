<script setup lang="ts">
import StreetForm from "~/components/forms/StreetForm.vue";

useHead(() => ({
  title: 'Neue Straße erstellen',
}));
</script>

<template>
  <StreetForm header="Neues Straßen-Objekt erstellen" action="create"/>
</template>
