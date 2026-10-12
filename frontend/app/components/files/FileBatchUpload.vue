<script setup lang="ts">
import { useToast as useNuxtToast } from "@nuxt/ui/composables";
import type { FileDTO } from "~/utils/types";
import { formatFileSize } from "~/utils/helpers";
import { ALLOWED_UPLOAD_TYPES, MAX_UPLOAD_FILE_SIZE, UPLOAD_ACCEPT } from "~/utils/constant_values";

const emit = defineEmits<{
  uploaded: [files: FileDTO[]];
}>();

const maxSizeLabel = formatFileSize(MAX_UPLOAD_FILE_SIZE);

const toast = useNuxtToast();
const fileApi = useFiles();

const files = ref<File[]>([]);
const uploadPending = ref(false);

// UFileUpload has no size limit and drag & drop bypasses `accept`, so invalid files are removed here
watch(files, (selected) => {
  const rejected = selected.filter(
    (file) => file.size > MAX_UPLOAD_FILE_SIZE || !ALLOWED_UPLOAD_TYPES.includes(file.type)
  );
  if (!rejected.length) return;

  files.value = selected.filter((file) => !rejected.includes(file));
  toast.add({
    color: "error",
    title: "Dateien abgelehnt",
    description: `Nicht unterstützt oder größer als ${maxSizeLabel}: ${rejected.map((file) => file.name).join(", ")}`,
    duration: 5000,
  });
});

const upload = async () => {
  if (!files.value.length) return;

  uploadPending.value = true;
  try {
    const uploaded = await fileApi.uploadFiles(files.value);
    toast.add({
      color: "success",
      title: "Upload erfolgreich",
      description:
        uploaded.length === 1
          ? "1 Datei erfolgreich hochgeladen"
          : `${uploaded.length} Dateien erfolgreich hochgeladen`,
      duration: 3000,
    });
    files.value = [];
    emit("uploaded", uploaded);
  } catch (error) {
    console.error(error);
    toast.add({
      color: "error",
      title: "Fehler",
      description: "Datei-Upload fehlgeschlagen",
      duration: 3000,
    });
  } finally {
    uploadPending.value = false;
  }
};
</script>

<template>
  <div class="flex flex-col gap-4 py-6">
    <p class="text-sm roboto-plain">
      Mehrere Dateien auf einmal hochladen. Metadaten können über den Tab
      „Einzelne Datei mit Metadaten“ erfasst werden.
    </p>
    <UFileUpload
      v-model="files"
      multiple
      :accept="UPLOAD_ACCEPT"
      icon="i-lucide-upload-cloud"
      label="Dateien hierher ziehen oder klicken"
      :description="`PNG, JPG, JPEG, PDF (max. ${maxSizeLabel} pro Datei)`"
      layout="list"
      :disabled="uploadPending"
      class="min-h-48 w-full"
    />
    <UButton
      icon="i-lucide-upload"
      :label="files.length > 1 ? `${files.length} Dateien hochladen` : 'Hochladen'"
      :loading="uploadPending"
      :disabled="!files.length || uploadPending"
      class="w-fit self-end"
      @click="upload"
    />
  </div>
</template>
