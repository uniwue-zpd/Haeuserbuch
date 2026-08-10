<script setup lang="ts">

import BuildingForm from "~/components/forms/BuildingForm.vue";

definePageMeta({
  middleware: 'auth',
});
</script>

<template>
  <BuildingForm action="create" header="Neues Gebäude-Objekt erstellen"/>
</template>
