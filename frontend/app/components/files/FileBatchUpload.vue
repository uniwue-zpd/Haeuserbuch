<script setup lang="ts">
import { useToast as useNuxtToast } from "@nuxt/ui/composables";
import type { FileDTO } from "~/utils/types";

const emit = defineEmits<{
  uploaded: [files: FileDTO[]];
}>();

const toast = useNuxtToast();
const fileApi = useFiles();

const onFileSelect = async (event: { files: File[] }) => {
  try {
    const uploaded = await fileApi.uploadFiles(event.files);
    toast.add({
      color: "success",
      title: "Upload erfolgreich",
      description:
        uploaded.length === 1
          ? "1 Datei erfolgreich hochgeladen"
          : `${uploaded.length} Dateien erfolgreich hochgeladen`,
      duration: 3000,
    });
    emit("uploaded", uploaded);
  } catch (error) {
    console.error(error);
    toast.add({
      color: "error",
      title: "Fehler",
      description: "Datei-Upload fehlgeschlagen",
      duration: 3000,
    });
  }
};
</script>

<template>
  <div class="flex min-h-64 flex-col items-center justify-center gap-4 py-10 text-center">
    <UIcon name="i-lucide-upload-cloud" class="size-12 text-muted" />
    <p class="max-w-md text-sm roboto-plain">
      Mehrere Dateien auf einmal hochladen. Metadaten können über den Tab
      „Einzelne Datei mit Metadaten“ erfasst werden.
    </p>
    <FileUpload
      mode="basic"
      name="files"
      accept=".png,.jpg,.jpeg,image/png,image/jpeg,application/pdf"
      chooseLabel="Dateien hinzufügen"
      :maxFileSize="10000000"
      :multiple="true"
      :customUpload="true"
      auto
      @select="onFileSelect"
      :pt="{ pcChooseButton: { root: { class: 'p-button-secondary p-button-outlined' } } }"
    />
    <p class="text-sm text-muted roboto-plain">
      Unterstützte Dateitypen: PNG, JPG, JPEG, PDF. Maximalgröße pro Datei: 10 MB.
    </p>
  </div>
</template>
