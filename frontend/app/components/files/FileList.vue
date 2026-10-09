<script setup lang="ts">
import type { FileDTO, FilePreviewDTO, Pageable } from "~/utils/types";
import { DOCUMENT_TYPES } from "~/utils/types";
import { formatFileSize } from "~/utils/helpers";

type ListFile = FileDTO | FilePreviewDTO;

const props = withDefaults(
  defineProps<{
    // Static list of files (e.g. attachments of a building).
    // If omitted, the component loads all stored files page by page from the API.
    // The paged list is the file management view (auth-protected page), so it allows deleting.
    files?: ListFile[] | null;
    compact?: boolean;
    pageSize?: number;
  }>(),
  {
    files: null,
    compact: false,
    pageSize: 5,
  },
);

const fileApi = useFiles();

// Paged loading, only used when no static files are given
const isPaged = computed(() => props.files == null);

const pageable = ref<Pageable>({
  page: 0,
  size: props.pageSize,
  sort: "createdDate,desc",
});

const { data: pageData, pending, error, refresh } = useAsyncData(
  () => `files-${pageable.value.page}-${pageable.value.size}`,
  () => fileApi.getFiles(pageable.value),
  { immediate: isPaged.value },
);

const displayedFiles = computed<ListFile[]>(() => {
  if (!isPaged.value) return props.files ?? [];
  if (pending.value || error.value) return [];
  return pageData.value?.content ?? [];
});

const pageOptions = computed(() =>
  Array.from({ length: pageData.value?.totalPages ?? 0 }, (_, index) => ({
    label: `${index + 1}`,
    value: index,
  })),
);

const changePage = (page: number) => {
  const totalPages = pageData.value?.totalPages ?? 0;
  if (page < 0 || page >= totalPages || page === pageData.value?.number) return;
  pageable.value = { ...pageable.value, page };
};

const removeFile = async (file: ListFile) => {
  await fileApi.deleteFileById(file.id);
  const currentPage = pageable.value.page ?? 0;
  if (displayedFiles.value.length === 1 && currentPage > 0) {
    pageable.value = { ...pageable.value, page: currentPage - 1 };
  } else {
    await refresh();
  }
};

const isImageFile = (file: { type?: string | null }) => {
  return !!file.type?.startsWith("image/");
};

const getFileIcon = (file: { type?: string | null }) => {
  if (file.type === "application/pdf") return "i-lucide-file-text";
  return "i-lucide-file";
};

const getDisplayName = (file: { id: number; originalName?: string | null }) => {
  return file.originalName || `Datei ${file.id}`;
};

// Files without an explicit downloadAllowed=false are downloadable
const isDownloadAllowed = (file: { downloadAllowed?: boolean | null }) => {
  return file.downloadAllowed !== false;
};

const isFullFile =(file: ListFile): file is FileDTO => "createdDate" in file;

const timestampToDate = (timestamp: string) => {
  return new Date(timestamp).toLocaleDateString("de-DE", {
    weekday: "long",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// Details popup
const detailsOpen = ref(false);
const selectedFile = ref<ListFile | null>(null);
const details = ref<FileDTO | null>(null);
const detailsPending = ref(false);
const detailsError = ref(false);

const openDetails = async (file: ListFile) => {
  selectedFile.value = file;
  details.value = null;
  detailsError.value = false;
  detailsOpen.value = true;
  detailsPending.value = true;
  try {
    details.value = await fileApi.getFileById(file.id);
  } catch (error) {
    console.error(error);
    detailsError.value = true;
  } finally {
    detailsPending.value = false;
  }
};

watch(detailsOpen, (open) => {
  if (!open) {
    selectedFile.value = null;
    details.value = null;
  }
});

const metadataGroups = computed(() => {
  const file = details.value;
  if (!file) return [];
  const groups: { title: string; entries: [string, string | null | undefined][] }[] = [
    {
      title: "Klassifikation",
      entries: [
        ["Kategorie", file.documentCategory],
        ["Dokumenttyp", file.documentType ? DOCUMENT_TYPES[file.documentType] : null],
      ],
    },
    {
      title: "Datierung",
      entries: [
        ["Aufnahmedatum", file.dateCaptured],
        ["Genauigkeit", file.dateCapturedPrecision],
        ["Datiert von", file.dateFrom],
        ["Datiert bis", file.dateTo],
      ],
    },
    {
      title: "Provenienz",
      entries: [
        ["Quelle", file.source],
        ["Sammlung", file.collection],
        ["Signatur", file.signature],
      ],
    },
    {
      title: "Urheberschaft",
      entries: [
        ["Urheber", file.creator],
        ["Rechteinhaber", file.rightsHolder],
        ["Lizenz", file.license],
      ],
    },
    {
      title: "Beschreibung",
      entries: [
        ["Bildunterschrift", file.caption],
        ["Beschreibung", file.description],
      ],
    },
  ];
  return groups
    .map((group) => ({ ...group, entries: group.entries.filter(([, value]) => !!value) }))
    .filter((group) => group.entries.length > 0);
});

// Prevent saving images via context menu or drag & drop
const blockImageContextMenu = (e: MouseEvent) => {
  if (e.target instanceof HTMLImageElement) e.preventDefault();
};
const blockImageDrag = (e: DragEvent) => {
  if (e.target instanceof HTMLImageElement) e.preventDefault();
};

onMounted(() => {
  document.addEventListener("contextmenu", blockImageContextMenu);
  document.addEventListener("dragstart", blockImageDrag);
});

onBeforeUnmount(() => {
  document.removeEventListener("contextmenu", blockImageContextMenu);
  document.removeEventListener("dragstart", blockImageDrag);
});
</script>

<template>
  <div
    :class="
      props.compact
        ? 'flex flex-col gap-2'
        : 'flex flex-col gap-3'
    "
  >
    <template v-if="isPaged">
      <div class="text-sm roboto-plain">
        Dateien:
        <span class="font-semibold">{{ pageData?.totalElements ?? 0 }}</span>
      </div>
      <hr class="border-2 border-default" />
      <div v-if="pending" class="flex flex-row items-center gap-3">
        <i class="pi pi-spin pi-spinner text-lg" />
        <span class="text-sm roboto-plain">Metadaten werden geladen</span>
      </div>
      <div v-else-if="error" class="flex flex-row gap-2 roboto-plain">
        <span>Fehler:</span>
        <code class="text-red-600">{{ error.message }}</code>
      </div>
      <div v-else-if="pageData && pageData.totalElements === 0" class="text-sm roboto-plain">
        Keine Dateien vorhanden.
      </div>
    </template>

    <article
      v-for="file in displayedFiles"
      :key="file.id"
      :class="
        props.compact
          ? 'flex min-w-0 items-center gap-2 rounded-md border border-default bg-elevated/50 px-2 py-1.5'
          : 'flex flex-row items-center gap-4 rounded-md border border-default bg-default p-3 shadow-md'
      "
    >
      <UIcon
        v-if="props.compact"
        :name="isImageFile(file) ? 'i-lucide-image' : getFileIcon(file)"
        class="size-5 shrink-0 text-muted"
      />
      <button
        v-else
        type="button"
        :class="[
          'flex h-20 w-20 shrink-0 cursor-pointer items-center justify-center overflow-hidden rounded-md border border-default bg-elevated transition hover:opacity-90',
        ]"
        :aria-label="`Details anzeigen: ${getDisplayName(file)}`"
        @click="openDetails(file)"
      >
        <img
          v-if="isImageFile(file)"
          :src="fileApi.getFileContentUrl(file.id)"
          :alt="file.originalName ?? 'Datei'"
          class="h-full w-full object-contain"
          draggable="false"
        >
        <UIcon
          v-else
          :name="getFileIcon(file)"
          class="size-8 text-muted"
        />
      </button>

      <div :class="['flex min-w-0 flex-1 flex-col', props.compact ? 'gap-0' : 'gap-1']">
        <span
          :class="
            props.compact
              ? 'truncate text-xs font-semibold text-highlighted'
              : 'truncate font-semibold roboto-plain'
          "
        >
          {{ getDisplayName(file) }}
        </span>
        <template v-if="isFullFile(file)">
          <span v-if="file.caption" class="truncate text-sm roboto-plain">{{ file.caption }}</span>
          <span class="text-sm text-muted roboto-plain">{{ timestampToDate(file.createdDate) }}</span>
          <span class="text-xs text-muted roboto-plain">{{ formatFileSize(file.size) }}</span>
        </template>
        <span v-else class="truncate text-[11px] text-muted">
          {{ file.type || "Unbekannter Dateityp" }}
        </span>
      </div>

      <div class="flex shrink-0 items-center gap-1">
        <UButton
          icon="i-lucide-info"
          color="neutral"
          :variant="props.compact ? 'outline' : 'ghost'"
          :size="props.compact ? 'xs' : 'md'"
          aria-label="Details anzeigen"
          class="cursor-pointer"
          @click="openDetails(file)"
        />
        <UButton
          v-if="isDownloadAllowed(file)"
          :to="fileApi.getFileDownloadUrl(file.id)"
          external
          icon="i-lucide-download"
          color="neutral"
          :variant="props.compact ? 'outline' : 'ghost'"
          :size="props.compact ? 'xs' : 'md'"
          aria-label="Datei herunterladen"
          :title="`Datei herunterladen: ${getDisplayName(file)}`"
          class="cursor-pointer"
        />
        <UButton
          v-if="isPaged"
          icon="i-lucide-trash-2"
          color="error"
          variant="ghost"
          aria-label="Datei löschen"
          class="shrink-0 cursor-pointer"
          @click="removeFile(file)"
        />
      </div>
    </article>

    <nav
      v-if="isPaged && pageData && pageData.totalPages > 1"
      aria-label="Seitennavigation"
      class="mt-3 flex flex-row items-center justify-center gap-2"
    >
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevrons-left"
        aria-label="Erste Seite"
        :disabled="pageData.first"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(0)"
      />
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevron-left"
        aria-label="Vorherige Seite"
        :disabled="pageData.first"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(pageData.number - 1)"
      />
      <div class="flex items-center gap-2 text-sm roboto-plain">
        <USelect
          :model-value="pageData.number"
          :items="pageOptions"
          value-key="value"
          class="w-20"
          :ui="{
            item: 'cursor-pointer data-[state=checked]:bg-primary/15 data-[state=checked]:text-primary',
          }"
          @update:model-value="changePage"
        />
        <span>von {{ pageData.totalPages }}</span>
      </div>
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevron-right"
        aria-label="Nächste Seite"
        :disabled="pageData.last"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(pageData.number + 1)"
      />
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-chevrons-right"
        aria-label="Letzte Seite"
        :disabled="pageData.last"
        class="cursor-pointer disabled:cursor-not-allowed"
        @click="changePage(pageData.totalPages - 1)"
      />
    </nav>

    <UModal
      v-model:open="detailsOpen"
      :title="selectedFile ? getDisplayName(selectedFile) : 'Dateidetails'"
      :ui="{ content: 'max-w-5xl' }"
    >
      <template #body>
        <div v-if="selectedFile" class="flex flex-col gap-4">
          <img
            v-if="isImageFile(selectedFile)"
            :src="fileApi.getFileContentUrl(selectedFile.id)"
            :alt="selectedFile.originalName ?? 'Dateivorschau'"
            class="max-h-[60vh] w-full object-contain"
            draggable="false"
          >
          <div v-else class="flex justify-center py-4">
            <UIcon :name="getFileIcon(selectedFile)" class="size-16 text-muted" />
          </div>

          <div v-if="detailsPending" class="flex flex-row items-center gap-3">
            <i class="pi pi-spin pi-spinner text-lg" />
            <span class="text-sm roboto-plain">Metadaten werden geladen</span>
          </div>
          <p v-else-if="detailsError" class="text-sm text-red-600 roboto-plain">
            Metadaten konnten nicht geladen werden.
          </p>
          <template v-else-if="details">
            <div
              v-if="metadataGroups.length || details.sourceUrl"
              class="grid grid-cols-1 gap-4 md:grid-cols-2"
            >
              <section v-for="group in metadataGroups" :key="group.title" class="flex flex-col gap-1">
                <h3 class="font-semibold text-highlighted">{{ group.title }}</h3>
                <dl class="flex flex-col gap-1">
                  <div
                    v-for="[label, value] in group.entries"
                    :key="label"
                    class="grid grid-cols-[minmax(0,9rem)_minmax(0,1fr)] gap-2 text-sm"
                  >
                    <dt class="font-semibold text-muted">{{ label }}</dt>
                    <dd class="whitespace-pre-line wrap-anywhere">{{ value }}</dd>
                  </div>
                </dl>
              </section>
              <section v-if="details.sourceUrl" class="flex flex-col gap-1">
                <h3 class="font-semibold text-highlighted">Quell-URL</h3>
                <a
                  :href="details.sourceUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="text-sm underline underline-offset-4 wrap-anywhere hover:text-primary"
                >
                  {{ details.sourceUrl }}
                </a>
              </section>
            </div>
            <p v-else class="text-sm text-muted roboto-plain">Keine Metadaten vorhanden.</p>

            <div class="flex items-center justify-between gap-2 border-t border-default pt-3 text-sm text-muted">
              <span>{{ details.type }} · {{ formatFileSize(details.size) }}</span>
              <UButton
                v-if="isDownloadAllowed(details)"
                :to="fileApi.getFileDownloadUrl(details.id)"
                external
                icon="i-lucide-download"
                color="neutral"
                variant="outline"
                label="Herunterladen"
                class="cursor-pointer"
              />
              <span v-else class="flex items-center gap-1">
                <UIcon name="i-lucide-lock" class="size-4" />
                Download nicht erlaubt
              </span>
            </div>
          </template>
        </div>
      </template>
    </UModal>
  </div>
</template>
