<script setup lang="ts">
import FileBatchUpload from "~/components/files/FileBatchUpload.vue";
import FileForm from "~/components/forms/FileForm.vue";

definePageMeta({
  middleware: 'auth',
});

useHead(() => ({
  title: 'Dateien hochladen'
}));

const uploadTabs = [
  { label: "Mehrere Dateien", slot: "batch" as const, icon: "i-lucide-files" },
  { label: "Einzelne Datei mit Metadaten", slot: "single" as const, icon: "i-lucide-file-plus" },
];

const onUploaded = () => navigateTo("/dateien");
</script>

<template>
  <div class="flex flex-col gap-6">
    <div class="flex flex-col gap-2">
      <UButton
        to="/dateien"
        icon="i-lucide-arrow-left"
        color="neutral"
        variant="link"
        label="Zurück zur Dateiübersicht"
        class="w-fit px-0"
      />
      <h1 class="text-3xl font-bold montserrat-headline">Dateien hochladen</h1>
    </div>
    <UTabs :items="uploadTabs" variant="link" class="w-full">
      <template #batch>
        <FileBatchUpload @uploaded="onUploaded" />
      </template>
      <template #single>
        <div class="pt-4">
          <FileForm @uploaded="onUploaded" />
        </div>
      </template>
    </UTabs>
  </div>
</template>
