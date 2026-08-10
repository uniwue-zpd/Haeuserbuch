<script setup lang="ts">
import type { FileDTO, Pageable } from "~/utils/types";
import NotAuthorized from "~/components/UI/NotAuthorized.vue";

definePageMeta({
  middleware: 'auth',
});

const { loggedIn } = useUserSession();

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
    Array.from(
        { length: data.value?.totalPages ?? 0 },
        (_, index) => ({
          label: `${index + 1}`,
          value: index
        })
    )
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
  <AuthState v-slot="{ loggedIn }">
    <div v-if="loggedIn" class="flex flex-col gap-6">
      <h1 class="text-3xl font-bold montserrat-headline">Dateimanagement</h1>
      <div class="flex flex-col md:flex-row justify-between items-center gap-3">
        <FileUpload
            mode="basic"
            name="files"
            accept=".png,.jpg,.jpeg,image/png,image/jpeg"
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
          <span class="font-semibold">
          {{ data?.totalElements ?? 0 }}
        </span>
        </div>
      </div>
      <p class="text-sm text-gray-600 roboto-plain">Hinweis: Es sind nur Bilddateien erlaubt</p>
      <hr class="border-2 border-gray-200"/>
      <div
          v-if="pending"
          class="flex flex-row gap-3 items-center"
      >
        <i class="pi pi-spin pi-spinner text-lg"/>
        <span class="text-sm roboto-plain">Metadaten werden geladen</span>
      </div>
      <div
          v-else-if="files.length > 0"
          class="flex flex-col gap-3"
      >
        <div
            v-for="file in files"
            :key="file.id"
            class="flex flex-row items-center gap-4 rounded-lg border border-gray-200 p-3 shadow-md"
        >
          <div class="w-20 h-20 rounded-lg overflow-hidden bg-gray-100 border flex items-center justify-center shrink-0">
            <Image
                :src="getPreviewUrl(file.id)"
                :alt="file.originalName ?? 'Datei'"
                preview
                class="w-full h-full"
                :pt="{
                image: { class: 'w-full h-full object-contain', draggable: 'false' },
                previewImage: { draggable: 'false', class: 'select-none' }
              }"
            />
          </div>
          <div class="flex-1 flex flex-col gap-1 min-w-0">
            <span class="font-semibold roboto-plain truncate">{{ file.originalName }}</span>
            <span class="text-sm text-gray-500 roboto-plain">{{ timestampToDate(file.createdDate) }}</span>
            <span class="text-xs text-gray-500 roboto-plain">{{ formatFileSize(file.size) }}</span>
          </div>
          <Button
              icon="pi pi-trash"
              severity="danger"
              variant="text"
              rounded
              class="hover:bg-red-50"
              @click="removeFile(file)"
          />
        </div>
      </div>
      <div
          v-else-if="data && data.totalElements === 0"
          class="text-sm roboto-plain"
      >
        Keine Dateien vorhanden.
      </div>
      <div
          v-else-if="error"
          class="flex flex-row gap-2 roboto-plain"
      >
        <span>Fehler:</span>
        <code class="text-red-600">
          {{ error.message }}
        </code>
      </div>
      <div
          v-if="data && data.totalPages > 1"
          class="flex flex-row justify-center items-center gap-2 mt-3"
      >
        <button
            @click="changePage(0)"
            :disabled="data.first"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-double-left"/>
        </button>
        <button
            @click="changePage(data.number - 1)"
            :disabled="data.first"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-left"/>
        </button>
        <div class="flex items-center gap-2 text-sm roboto-plain">
          <Select
              :model-value="data.number"
              :options="pageOptions"
              option-label="label"
              option-value="value"
              class="h-9"
              @update:modelValue="changePage"
          />
          <span>von {{ data.totalPages }}</span>
        </div>
        <button
            @click="changePage(data.number + 1)"
            :disabled="data.last"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-right"/>
        </button>
        <button
            @click="changePage(data.totalPages - 1)"
            :disabled="data.last"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-double-right"/>
        </button>
      </div>
    </div>
    <NotAuthorized v-else/>
  </AuthState>
</template>
