<script setup lang="ts">
const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  building?: Feature;
}>();

const toast = useToast();
const submitted = ref(false);
const building_store = useBuildingStore();

type BuildingInput = Omit<Feature, 'id' | 'createdBy' | 'createdDate' | 'lastModifiedBy' | 'lastModifiedDate'>;

const submit = async (formData: Partial<BuildingInput>) => {
  try {
    if (props.action === 'create') {
      await building_store.createBuilding(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('building_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.building?.id) {
      await building_store.updateBuilding(formData, props.building.id);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/buildings/${props.building?.id}`);
    }
  } catch (error) {
    console.log(error)
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Erstellen des Gebäude-Objektes',
      life: 3000
    });
  }
};
</script>

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-2xl montserrat-headline-headline text-black font-bold">{{ props.header }}</h1>
    <p class="roboto-plain">
      Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen oder anzupassen
    </p>
    <FormKit
        type="form"
        id="building_creation"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="props.building ? props.building : {}"
        :key="props.building?.id || 'create'"
        #default="{ value }"
    >
      <div class="flex flex-col gap-2">
        <FormKit type="hidden" name="type" value="Feature" />
        <FormKit type="group" name="properties">
          <div class="flex flex-col gap-2">
            <FormKit
                type="hidden"
                name="type"
                value="building"
                contenteditable="false"
            />
            <div class="flex flex-row space-x-5">
              <FormKit
                  type="text"
                  name="name"
                  label="Name"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="number"
                  number
                  name="houseNumber"
                  label="Hausnummer"
                  prefix-icon="number"
                  outer-class="max-w-full"
              />
            </div>
            <div class="flex flex-row space-x-5">
              <FormKit
                  type="text"
                  name="partType"
                  label="Bauteil"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                type="text"
                name="specialStatus"
                label="Sonderstatus"
                prefix-icon="text"
                outer-class="max-w-full"
              />
            </div>
            <div class="flex flex-row space-x-5">
              <FormKit
                  type="text"
                  name="querter"
                  label="Viertel"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="text"
                  name="district"
                  label="Distrikt"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="text"
                  name="districtHouseNumber"
                  label="Distrikt & Hausnummer"
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
          </div>
        </FormKit>
        <FormKit type="group" name="geometry">
          <div class="flex flex-col gap-2">
            <FormKit
                type="hidden"
                name="type"
                label="Geometrietyp"
            />
            <FormKit
                type="hidden"
                name="coordinates"
                label="Koordinaten"
            />
          </div>
        </FormKit>
      </div>
      <div class="border-solid border-2 rounded-md p-5 bg-[#F1F2F5] mb-2">
        <div class="font-mono">geoJSON-Preview</div>
        <hr>
        <pre wrap class="text-sm">{{ value }}</pre>
      </div>
    </FormKit>
  </div>
</template>

<style scoped>

</style>