<script setup lang="ts">
const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  citizenship?: CitizenshipDTO;
}>();

const toast = useToast();
const submitted = ref(false);

const citizenship_store = useCitizenshipStore();
const person_store = usePersonStore();
const source_store = useSourceStore();
type CitizenshipInput = Omit<CitizenshipDTO, 'id' | 'createdBy' | 'createdDate' | 'lastModifiedBy' | 'lastModifiedDate'>;

const sources = computed(() => source_store.sources.map(s => ({ label: s.title, value: { id: s.id, title: s.title } })));
const persons = computed(() => person_store.persons.map(p => ({ label: `${p.firstName} ${p.lastName}`, value: { id: p.id, firstName: p.firstName, lastName: p.lastName } })));

const submit = async (formData: Partial<CitizenshipInput>) => {
  try {
    if (props.action === 'create') {
      await citizenship_store.createCitizenship(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('citizenship_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.citizenship?.id) {
      const id = props.citizenship.id;
      await citizenship_store.updateCitizenship(formData, props.citizenship.id);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/citizenships/${id}`);
    }
  } catch (error) {
    console.log(error)
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Erstellen des Bürgermatrikel-Objektes',
      life: 3000
    });
  }
};
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
              type="select"
              name="primarySource"
              label="Primärquelle"
              outer-class="max-w-full"
              select-icon="select"
              :options="[{ label: 'Keine Auswahl', value: null },
              ...sources as any
              ]"
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
              name="date"
              label="Datum"
              placeholder="08.04.1412"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="select"
              name="person"
              label="Eingebürgerte Person"
              outer-class="max-w-full"
              select-icon="select"
              :options="[{ label: 'Keine Auswahl', value: null },
              ...persons as any
              ]"
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
              type="select"
              name="secondarySource"
              label="Sekundärquelle"
              outer-class="max-w-full"
              select-icon="select"
              :options="[{ label: 'Keine Auswahl', value: null },
              ...sources as any
              ]"
          />
          <FormKit
              type="number"
              number
              name="refNumber"
              label="Referenz Mayer-Erlach"
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
