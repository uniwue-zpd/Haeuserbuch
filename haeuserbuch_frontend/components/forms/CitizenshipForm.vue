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
  <div class="flex flex-col gap-2">
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
      <div class="flex flex-col gap-2 p-2 bg-[#F3F4F6] rounded-md shadow-md">
        <div class="flex flex-col gap-2 md:flex-row md:space-x-5">
          <FormKit
              type="text"
              name="signature"
              label="Signatur"
              placeholder="StAWü RB 207, 9"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="number"
              number
              name="number"
              label="Nummer"
              placeholder="65"
              prefix-icon="number"
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
        </div>
        <Divider/>
        <div class="flex flex-col gap-2 md:grid md:grid-cols-2 md:space-x-5">
          <div class="flex flex-col">
            <FormKit
                type="select"
                multiple
                name="persons"
                label="Eingebürgerte Personen"
                outer-class="max-w-full"
                select-icon="select"
                :options="[{ label: 'Keine Auswahl', value: null },
              ...persons as any
              ]"
                help="Halten Sie die Strg-Taste gedrückt, um mehrere Quellen auszuwählen"
            />
            <button type="button" @click="value.persons = []" class="border border-blue-600 text-blue-600 p-1 rounded-md shadow-sm hover:shadow-md bg-red-100 font-bold max-w-1/7 mx-auto">Alle Personen entfernen</button>
          </div>
          <FormKit
              type="select"
              name="source"
              label="Quelle"
              outer-class="max-w-full"
              select-icon="select"
              :options="[{ label: 'Keine Auswahl', value: null },
              ...sources as any
              ]"
          />
        </div>
        <Divider/>
        <div class="flex flex-col gap-2 md:flex-row md:space-x-5">
          <FormKit
              type="textarea"
              name="entryText"
              label="Eintrag als Fließtext"
              prefix-icon="list"
              outer-class="max-w-full"
          />
          <FormKit
              type="textarea"
              name="addendum"
              label="Nachtrag"
              prefix-icon="list"
              outer-class="max-w-full"
          />
        </div>
        <Divider/>
        <div class="flex flex-col gap-2 md:flex-row md:space-x-5">
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
        <div class="max-h-[500px] overflow-y-auto bg-gray-100 border border-gray-300 rounded-md">
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
