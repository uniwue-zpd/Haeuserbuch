<script setup lang="ts">
const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  person?: PersonDTO;
}>();

const toast = useToast();
const submitted = ref(false);

const person_store = usePersonStore();
type PersonInput = Omit<PersonDTO, 'id' | 'createdBy' | 'createdDate' | 'lastModifiedBy' | 'lastModifiedDate'>;

const submit = async (formData: Partial<PersonInput>) => {
  try {
    if (props.action === 'create') {
      await person_store.createPerson(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('person_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.person?.id) {
      await person_store.updatePerson(formData, props.person.id);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/persons/${props.person?.id}`);
    }
  } catch (error) {
    console.log(error)
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Erstellen des Person-Objektes',
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
        id="person_creation"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="props.person ? props.person : {}"
        :key="props.person?.id || 'create'"
        #default="{ value }"
    >
      <div class="flex flex-col gap-2">
        <div class="flex flex-row space-x-5">
          <FormKit
              type="text"
              name="firstName"
              label="Vorname"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="lastName"
              label="Nachname"
              prefix-icon="text"
              outer-class="max-w-full"
          />
        </div>
        <FormKit
            type="text"
            name="fullName"
            label="Voller Name"
            prefix-icon="text"
            outer-class="max-w-full"
            help="Tragen Sie hier den vollen Namen der Person ein, auch wenn dieser mit dem Vor- und Nachnamen identisch ist"
        />
        <FormKit
            type="select"
            name="sex"
            label="Geschlecht"
            :options="[
                    { label: 'unbekannt', value: null },
                    { label: 'männlich', value: 'mänlich' },
                    { label: 'weiblich', value: 'weiblich' }
                  ]"
            select-icon="select"
            outer-class="max-w-full"
        />
        <div class="flex flex-row space-x-5">
          <FormKit
              type="text"
              name="occupation"
              label="Beruf"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="occupationCategory"
              label="Berufskategorie"
              prefix-icon="text"
              outer-class="max-w-full"
              help="Standardisierte Berufskategorie"
          />
        </div>
        <div class="flex flex-row space-x-5">
          <FormKit
              type="select"
              name="isCitizen"
              label="Bürger"
              :options="[
                    { label: 'unbekannt', value: null },
                    { label: 'ja', value: true },
                    { label: 'nein', value: false }
                  ]"
              select-icon="select"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="confession"
              label="Religion"
              prefix-icon="text"
              outer-class="max-w-full"
          />
        </div>
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
        <div class="border-solid border-2 rounded-md p-5 bg-[#F1F2F5] mb-2">
          <div class="font-mono">JSON-Preview</div>
          <hr>
          <pre wrap class="text-sm">{{ value }}</pre>
        </div>
        <FormKit
            type="submit"
            label="Erstellen"
        />
      </div>
    </FormKit>
  </div>
</template>

<style scoped>

</style>
