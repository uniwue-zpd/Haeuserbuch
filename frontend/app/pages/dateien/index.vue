<script setup lang="ts">
import FileList from "~/components/files/FileList.vue";

definePageMeta({
  middleware: 'auth',
});

useHead(() => ({
  title: 'Dateimanagement'
}));
</script>

<template>
  <div class="flex flex-col gap-6">
    <div class="flex flex-col items-start justify-between gap-3 md:flex-row md:items-center">
      <h1 class="text-3xl font-bold montserrat-headline">Dateimanagement</h1>
      <UButton
        to="/dateien/create"
        icon="i-lucide-upload"
        color="neutral"
        variant="outline"
        label="Dateien hochladen"
        class="cursor-pointer"
      />
    </div>
    <FileList />
  </div>
</template>
