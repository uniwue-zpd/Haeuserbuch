<script setup lang="ts">
import type { FileDTO, Pageable } from "~/utils/types";

definePageMeta({
  middleware: 'auth',
});

const fileApi = useFiles();

const pageable = ref<Pageable>({
  page: 0,
  size: 5,
  sort: "createdDate,desc"
});

const { data, pending, error, refresh } = await useAsyncData(
  () => `files-${pageable.value.page}-${pageable.value.size}`,
  () => fileApi.getFiles(pageable.value)
);

const files = computed(() => data.value?.content ?? []);

const pageOptions = computed(() =>
  Array.from({ length: data.value?.totalPages ?? 0 }, (_, index) => ({
    label: `${index + 1}`,
    value: index,
  }))
);

const changePage = (page: number) => {
  const totalPages = data.value?.totalPages ?? 0;
  if (page < 0 || page >= totalPages) return;
  pageable.value = { ...pageable.value, page };
};

const onFileSelect = async (event: any) => {
  await fileApi.uploadFiles(event.files);
  const currentPage = pageable.value.page ?? 0;
  if (currentPage !== 0) {
    pageable.value = { ...pageable.value, page: 0 };
  } else {
    await refresh();
  }
};

const removeFile = async (file: FileDTO) => {
  await fileApi.deleteFileById(file.id);
  const currentPage = pageable.value.page ?? 0;
  if (files.value.length === 1 && currentPage > 0) {
    pageable.value = { ...pageable.value, page: currentPage - 1 };
  } else {
    await refresh();
  }
};

const getPreviewUrl = (id: number) => {
  return fileApi.getFileContentUrl(id);
};

const getDownloadName = (file: FileDTO) => {
  return file.originalName || `datei-${file.id}`;
};

const isImageFile = (file: FileDTO) => {
  return !!file.type?.startsWith("image/");
};

const getFileIcon = (file: FileDTO) => {
  if (file.type === "application/pdf") return "i-lucide-file-text";
  return "i-lucide-file";
};

const imagePreviewOpen = ref(false);
const previewFile = ref<FileDTO | null>(null);

const openImagePreview = (file: FileDTO) => {
  if (!isImageFile(file)) return;
  previewFile.value = file;
  imagePreviewOpen.value = true;
};

watch(imagePreviewOpen, (open) => {
  if (!open) previewFile.value = null;
});

const blockImageContextMenu = (e: MouseEvent) => {
  if (e.target instanceof HTMLImageElement) e.preventDefault();
};
const blockImageDrag = (e: DragEvent) => {
  if (e.target instanceof HTMLImageElement) e.preventDefault();
};

onMounted(() => {
  document.addEventListener('contextmenu', blockImageContextMenu);
  document.addEventListener('dragstart', blockImageDrag);
});

onBeforeUnmount(() => {
  document.removeEventListener('contextmenu', blockImageContextMenu);
  document.removeEventListener('dragstart', blockImageDrag);
});


const timestampToDate = (timestamp: string) => {
  return new Date(timestamp).toLocaleDateString("de-DE", {
    weekday: "long",
    year: "numeric",
    month: "2-digit",
    day: "2-digit"
  });
};

const formatFileSize = (bytes?: number) => {
  if (!bytes || bytes < 0) return "Unbekannte Dateigroesse";
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
};
</script>

<template>
  <div class="flex flex-col gap-6">
    <h1 class="text-3xl font-bold montserrat-headline">Dateimanagement</h1>
    <div class="flex flex-col items-center justify-between gap-3 md:flex-row">
      <FileUpload
        mode="basic"
        name="files"
        accept=".png,.jpg,.jpeg,image/png,image/jpeg,application/pdf"
        chooseLabel="Dateien hinzufügen"
        :maxFileSize="20000000"
        :multiple="true"
        :customUpload="true"
        auto
        @select="onFileSelect"
        :pt="{ pcChooseButton: { root: { class: 'p-button-secondary p-button-outlined' } } }"
      />
      <div class="text-sm roboto-plain">
        Dateien:
        <span class="font-semibold">{{ data?.totalElements ?? 0 }}</span>
      </div>
    </div>
    <p class="text-sm text-muted roboto-plain">
      Unterstützte Dateitypen: PNG, JPG, JPEG, PDF. Maximalgröße pro Datei: 20 MB.
    </p>
    <hr class="border-2 border-default" />
    <div v-if="pending" class="flex flex-row items-center gap-3">
      <i class="pi pi-spin pi-spinner text-lg" />
      <span class="text-sm roboto-plain">Metadaten werden geladen</span>
    </div>
    <div v-else-if="files.length > 0" class="flex flex-col gap-3">
      <div
        v-for="file in files"
        :key="file.id"
        class="flex flex-row items-center gap-4 rounded-md border border-default bg-default p-3 shadow-md"
      >
        <div class="flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-md border border-default bg-elevated">
          <button
            v-if="isImageFile(file)"
            type="button"
            class="h-full w-full cursor-zoom-in"
            @click="openImagePreview(file)"
          >
            <img
              :src="getPreviewUrl(file.id)"
              :alt="file.originalName ?? 'Datei'"
              class="h-full w-full object-contain"
              draggable="false"
            >
          </button>
          <UIcon
            v-else
            :name="getFileIcon(file)"
            class="size-8 text-muted"
          />
        </div>
        <div class="flex min-w-0 flex-1 flex-col gap-1">
          <span class="truncate font-semibold roboto-plain">{{ file.originalName }}</span>
          <span class="text-sm text-muted roboto-plain">{{ timestampToDate(file.createdDate) }}</span>
          <span class="text-xs text-muted roboto-plain">{{ formatFileSize(file.size) }}</span>
        </div>

        <div class="flex items-center gap-1">
          <a
            :href="getPreviewUrl(file.id)"
            :download="getDownloadName(file)"
            class="inline-flex"
            target="_blank"
            rel="noopener noreferrer"
            :title="`Datei herunterladen: ${getDownloadName(file)}`"
          >
            <UButton
              icon="i-lucide-download"
              color="neutral"
              variant="ghost"
              aria-label="Datei herunterladen"
              class="cursor-pointer"
            />
          </a>

          <UButton
            icon="i-lucide-trash-2"
            color="error"
            variant="ghost"
            aria-label="Datei loeschen"
            class="shrink-0 cursor-pointer"
            @click="removeFile(file)"
          />
        </div>
      </div>
    </div>
    <div v-else-if="data && data.totalElements === 0" class="text-sm roboto-plain">
      Keine Dateien vorhanden.
    </div>
    <div v-else-if="error" class="flex flex-row gap-2 roboto-plain">
      <span>Fehler:</span>
      <code class="text-red-600">{{ error.message }}</code>
    </div>
    <div
      v-if="data && data.totalPages > 1"
      class="mt-3 flex flex-row items-center justify-center gap-2"
    >
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevrons-left"
        aria-label="Erste Seite"
        :disabled="data.first"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(0)"
      />
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevron-left"
        aria-label="Vorherige Seite"
        :disabled="data.first"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(data.number - 1)"
      />
      <div class="flex items-center gap-2 text-sm roboto-plain">
        <USelect
          :model-value="data.number"
          :items="pageOptions"
          value-key="value"
          class="w-20"
          :ui="{
            item: 'cursor-pointer data-[state=checked]:bg-primary/15 data-[state=checked]:text-primary',
          }"
          @update:model-value="changePage"
        />
        <span>von {{ data.totalPages }}</span>
      </div>

      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevron-right"
        aria-label="Naechste Seite"
        :disabled="data.last"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(data.number + 1)"
      />
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevrons-right"
        aria-label="Letzte Seite"
        :disabled="data.last"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(data.totalPages - 1)"
      />
    </div>
    <UModal
      v-model:open="imagePreviewOpen"
      :title="previewFile?.originalName ?? 'Dateivorschau'"
      :ui="{ content: 'max-w-5xl' }"
    >
      <template #content>
        <div class="p-4 sm:p-6">
          <img
            v-if="previewFile"
            :src="getPreviewUrl(previewFile.id)"
            :alt="previewFile.originalName ?? 'Dateivorschau'"
            class="max-h-[80vh] w-full object-contain"
            draggable="false"
          >
        </div>
      </template>
    </UModal>
  </div>
</template>
