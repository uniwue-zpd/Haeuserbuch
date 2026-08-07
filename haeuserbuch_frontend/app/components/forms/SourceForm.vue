<script setup lang="ts">
import { useToast as useNuxtToast } from "@nuxt/ui/composables";

const props = defineProps<{
  header: string;
  action: "create" | "edit";
  source?: Source;
}>();

const toast = useNuxtToast();
const submitted = ref(false);

const source_store = useSourceStore();
type SourceInput = Omit<
  Source,
  "id" | "createdBy" | "createdDate" | "lastModifiedBy" | "lastModifiedDate"
>;

const submit = async (formData: Partial<SourceInput>) => {
  try {
    if (props.action === "create") {
      await source_store.createSource(formData);
      submitted.value = true;
      toast.add({
        color: "success",
        title: "Erfolg",
        description: "Erfolgreich erstellt",
        duration: 3000,
      });
      const form = getNode("source_create");
      form?.reset();
    } else if (props.action === "edit" && props.source?.id) {
      await source_store.updateSource(props.source.id, formData);
      submitted.value = true;
      toast.add({
        color: "success",
        title: "Erfolg",
        description: "Erfolgreich upgedated",
        duration: 3000,
      });
      const form = getNode("source_edit");
      form?.reset();
      navigateTo(`/sources/${props.source?.id}`);
    }
  } catch (error) {
    console.log(error);
    toast.add({
      color: "error",
      title: "Fehler",
      description: "Fehler beim Erstellen der Quelle",
      duration: 3000,
    });
  }
};
</script>

<template>
  <div class="flex flex-col gap-2 w-[80%] mx-auto">
    <h1 class="text-2xl text-black font-bold">{{ props.header }}</h1>
    <p>
      Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen
      oder anzupassen.
    </p>
    <div class="p-3 bg-[#F1F2F2] shadow-md rounded-md">
      <FormKit
        type="form"
        :id="action === 'create' ? 'source_create' : 'source_edit'"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="props.source ? props.source : {}"
        :key="props.source?.id || 'create'"
        #default="{ value }"
      >
        <div class="flex flex-col gap-2">
          <FormKit
            type="textarea"
            name="title"
            label="Titel"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="type"
            label="Kategorie"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="textInput"
            name="authors"
            :isMultiple="true"
            label="Autoren"
            outer-class="max-w-full"
          />
          <FormKit
            type="text"
            name="signature"
            label="Signatur"
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
            type="textInput"
            name="links"
            :isMultiple="true"
            label="Weiterführende Links"
            outer-class="max-w-full"
          />
          <FormKit
            type="textarea"
            name="internalNotes"
            label="Notizen intern"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <FormKit
            type="textarea"
            name="generalNotes"
            label="Notizen allgemein"
            prefix-icon="text"
            outer-class="max-w-full"
          />
          <div class="border-solid border-2 rounded-md p-5 bg-[#F1F2F5] mb-2">
            <div class="font-mono">JSON-Preview</div>
            <hr />
            <pre wrap class="text-sm">{{ value }}</pre>
          </div>
          <FormKit type="submit" label="Erstellen" />
        </div>
      </FormKit>
    </div>
  </div>
</template>
