<script setup lang="ts">
const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  street?: Street;
}>();

const toast = useToast();
const submitted = ref(false);

const street_store = useStreetStore();
type StreetInput = Omit<Street, 'id' | 'createdBy' | 'createdDate' | 'lastModifiedBy' | 'lastModifiedDate'>;

const submit = async (formData: Partial<StreetInput>) => {
  try {
    if (props.action === 'create') {
      await street_store.createStreet(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('street_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.street?.id) {
      await street_store.updateStreet(props.street.id, formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/streets/${props.street?.id}`);
    }
  } catch (error) {
    console.log(error)
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Erstellen der Straße',
      life: 3000
    });
  }
};
</script>

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-2xl montserrat-headline-headline text-black font-bold">{{ props.header }}</h1>
    <p class="roboto-plain">Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen oder anzupassen.</p>
    <div class="p-3 bg-[#F1F2F2] shadow-md rounded-md">
      <FormKit
          type="form"
          id="street_creation"
          submit-label="Erstellen"
          @submit="submit"
          :actions="false"
          :value="props.street ? props.street : {}"
          :key="props.street?.id || 'create'"
          #default="{ value }"
      >
        <div class="flex flex-col gap-2">
          <FormKit
              type="text"
              name="name"
              label="Name"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="textInput"
              name="altNames"
              :isMultiple="true"
              label="Andere Namen"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="description"
              label="Beschreibung"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="internalNotes"
              label="Notizen intern"
              prefix-icon="text"
              outer-class="max-w-full"
          />
          <FormKit
              type="text"
              name="generalNotes"
              label="Notizen allgemein"
              prefix-icon="text"
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
  </div>
</template>

<style scoped>

</style>
