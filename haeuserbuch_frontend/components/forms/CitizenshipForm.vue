<script setup lang="ts">
const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  citizenship?: CitizenshipDTO;
}>();

const toast = useToast();
const submitted = ref(false);

const citizenship_store = useCitizenshipStore();
type CitizenshipInput = Omit<CitizenshipDTO, 'id' | 'createdBy' | 'createdDate' | 'lastModifiedBy' | 'lastModifiedDate'>;

const submit = async (formData: Partial<CitizenshipInput>) => {
  try {
    if (props.action === 'create') {
      await citizenship_store.createCitizenship(formData);
      toast.add({
        severity: 'success',
        summary: 'Erfolg',
        detail: 'Erfolgreich erstellt',
        life: 3000
      });
      const form = getNode('citizenship_creation');
      form?.reset();
    }
    else if (props.action === 'edit' && props.citizenship?.id) {
      const id = props.citizenship.id;
      await citizenship_store.updateCitizenship(id, formData);
      toast.add({
        severity: 'success',
        summary: 'Erfolg',
        detail: 'Erfolgreich upgedated',
        life: 3000
      });
      navigateTo(`/citizenships/${id}`);
    }
    submitted.value = true
  } catch (error) {
    console.error(error);
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Speichern des Eintrags',
      life: 3000
    });
  }
}
</script>

<template>
  <div class="flex flex-col gap-2 w-[80%] mx-auto">
    <h1 class="text-2xl montserrat-headline-headline text-black font-bold">{{ props.header }}</h1>
    <p class="roboto-plain">Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen oder anzupassen.</p>
    <FormKit
        type="form"
        id="citizenship_creation"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="props.citizenship ? props.citizenship : {}"
        :key="props.citizenship?.id || 'create'"
        #default="{ value }"
    >
      <div class="flex flex-col gap-4 p-4 bg-gray-100 rounded-md shadow-md border border-gray-200 mb-4">
        <div class="flex flex-col gap-3">
          <p class="montserrat-headline font-semibold text-black text-xl">Informationen zum Eintrag</p>
          <FormKit
              type="sourceAutocomplete"
              label="Primärquelle"
              name="primarySource"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="signature"
              label="Signatur"
              placeholder="StAWü RB 207, 9"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="dateNaturalization"
              label="Datum der Einbürgerung"
              placeholder="08.04.1412"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="dateMisc"
              label="Andere Datumsangaben"
              placeholder="z.B. Datum der Heirat"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="personAutocomplete"
              label="Eingebürgerte Person"
              name="person"
              outer-class="max-w-full"
          />
          <FormKit
              type="personAutocomplete"
              :isMultiple="true"
              label="Erwähnte Personen (Mehrfachauswahl)"
              name="mentionedPersons"
              outer-class="max-w-full"
          />
          <FormKit
              type="textarea"
              name="entryText"
              label="Text des Eintrags"
              prefix-icon="list"
              outer-class="max-w-full"
          />
        </div>
        <Divider/>
        <div class="flex flex-col gap-3">
          <p class="montserrat-headline font-semibold text-black text-xl">Ergänzende Informationen</p>
          <FormKit
              type="sourceAutocomplete"
              label="Sekundärquelle"
              name="secondarySource"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="refNumber"
              label="Referenz Meyer-Erlach"
              placeholder="65"
              prefix-icon="number"
              outer-class="max-w-full"
          />
          <FormKit
              type="textarea"
              name="addendum"
              label="Nachtrag"
              prefix-icon="list"
              outer-class="max-w-full"
          />
          <FormKit
              type="textarea"
              name="internalNotes"
              label="Notizen intern"
              prefix-icon="list"
              outer-class="max-w-full"
          />
          <FormKit
              type="textarea"
              name="generalNotes"
              label="Notizen allgemein"
              prefix-icon="list"
              outer-class="max-w-full"
          />
        </div>
      </div>
      <!-- Preview of the input values -->
      <Fieldset class="mb-4 mt-4">
        <template #legend>
          <div class="montserrat-headline font-semibold text-black text-xl">Eingabe-Vorschau</div>
        </template>
        <div class="max-h-[30vh] overflow-y-auto bg-gray-100 border border-gray-200 rounded-md">
          <pre wrap class="text-sm p-2">{{ value }}</pre>
        </div>
      </Fieldset>
      <FormKit
          type="submit"
          :label="props.action === 'create' ? 'Erstellen' : 'Ändern'"
      />
    </FormKit>
  </div>
</template>

<style scoped>

</style>
