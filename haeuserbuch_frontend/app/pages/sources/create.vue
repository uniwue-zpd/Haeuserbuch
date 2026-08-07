<script setup lang="ts">
import SourceForm from "~/components/forms/SourceForm.vue";

useHead({
  title: 'Neue Quelle hinzufügen - Häuserbuch',
});
</script>

<template>
  <SourceForm header="Neue Quelle hinzufügen" action="create"/>
</template>
