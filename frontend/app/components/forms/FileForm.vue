<script setup lang="ts">
import { useToast as useNuxtToast } from "@nuxt/ui/composables";
import type { FileDTO, FileMetadata } from "~/utils/types";
import { DOCUMENT_TYPES } from "~/utils/types";
import { UPLOAD_ACCEPT } from "~/utils/constant_values";

const emit = defineEmits<{
  uploaded: [file: FileDTO];
}>();

const toast = useNuxtToast();
const fileApi = useFiles();

const documentTypeOptions = [
  { label: "unbekannt", value: null },
  ...Object.entries(DOCUMENT_TYPES).map(([value, label]) => ({ label, value })),
];

type FileFormValue = Partial<FileMetadata> & {
  file?: { name: string; file?: File }[];
};

const submit = async (formData: FileFormValue) => {
  const { file: fileList, ...metadata } = formData;
  const file = fileList?.[0]?.file;
  if (!file) return;

  try {
    const uploaded = await fileApi.uploadFile(file, metadata);
    toast.add({
      color: "success",
      title: "Erfolg",
      description: "Datei erfolgreich hochgeladen",
      duration: 3000,
    });
    getNode("file_create")?.reset();
    emit("uploaded", uploaded);
  } catch (error) {
    console.error(error);
    toast.add({
      color: "error",
      title: "Fehler",
      description: "Fehler beim Hochladen der Datei",
      duration: 3000,
    });
  }
};
</script>

<template>
  <div class="flex flex-col gap-2">
    <p class="text-sm roboto-plain">
      Wählen Sie bitte eine Datei aus und füllen Sie die untenstehenden Felder aus,
      um die Datei mit Metadaten hochzuladen.
    </p>
    <div class="rounded-md bg-gray-100 p-3 shadow-md">
      <FormKit
        type="form"
        id="file_create"
        submit-label="Hochladen"
        @submit="submit"
        :actions="false"
        #default="{ value }"
      >
        <div class="flex flex-col gap-2">
          <FormKit
            type="file"
            name="file"
            label="Datei"
            :accept="UPLOAD_ACCEPT"
            help="PNG, JPG, JPEG, PDF (max. 30 MB)"
            validation="required"
            :validation-messages="{ required: 'Bitte eine Datei auswählen' }"
            outer-class="max-w-full"
          />

          <div class="text-center font-bold text-2xl">Klassifikation</div>
          <FormKit
            type="text"
            name="documentCategory"
            label="Kategorie"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="select"
            name="documentType"
            label="Dokumenttyp"
            :options="documentTypeOptions"
            select-icon="select"
            outer-class="max-w-full"
          />

          <div class="text-center font-bold text-2xl">Datierung</div>
          <FormKit
            type="text"
            name="dateCaptured"
            label="Aufnahmedatum"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="dateCapturedPrecision"
            label="Genauigkeit des Aufnahmedatums"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="dateFrom"
            label="Datiert von"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="dateTo"
            label="Datiert bis"
            prefix-icon="text"
            outer-class="max-w-full"
          />

          <div class="text-center font-bold text-2xl">Provenienz</div>
          <FormKit
            type="text"
            name="source"
            label="Quelle"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="collection"
            label="Sammlung"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="signature"
            label="Signatur"
            prefix-icon="text"
            outer-class="max-w-full"
          />

          <div class="text-center font-bold text-2xl">Urheberschaft</div>
          <FormKit
            type="text"
            name="creator"
            label="Urheber"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="rightsHolder"
            label="Rechteinhaber"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="license"
            label="Lizenz"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="select"
            name="downloadAllowed"
            label="Download erlaubt"
            :options="[
              { label: 'unbekannt', value: null },
              { label: 'ja', value: true },
              { label: 'nein', value: false },
            ]"
            select-icon="select"
            outer-class="max-w-full"
          />

          <div class="text-center font-bold text-2xl">Beschreibung</div>
          <FormKit
            type="text"
            name="caption"
            label="Bildunterschrift"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="textarea"
            name="description"
            label="Beschreibung"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="url"
            name="sourceUrl"
            label="Quell-URL"
            prefix-icon="url"
            validation="url"
            outer-class="max-w-full"
          />

          <div class="mb-2 rounded-md border-2 border-solid bg-gray-100 p-5">
            <div class="font-mono">JSON-Preview</div>
            <hr />
            <pre wrap class="text-sm">{{ value }}</pre>
          </div>
          <FormKit type="submit" label="Hochladen" />
        </div>
      </FormKit>
    </div>
  </div>
</template>
