<script setup lang="ts">
const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  person?: PersonDTO;
}>();

const toast = useToast();
const submitted = ref(false);

const person_store = usePersonStore();

const place_store = usePlaceStore();
const places = computed(() => (place_store.places?.features ?? []).map(
    (p) => {
      const props = p.properties as PlaceProperties;
      return {
        label: props.realName,
        value: {
          id: p.id,
          realName: props.realName,
          altNames: props.altNames,
        }
      }
    }
));

const building_store = useBuildingStore();
const buildings = computed(() => (building_store.buildings?.features ?? []).map(
    (b) => {
      const props = b.properties as BuildingProperties;
      return {
        label: props.districtHouseNumber,
        value: {
          id: b.id,
          districtHouseNumber: props.districtHouseNumber
        }
      }
    }
));

const occupationStore = useOccupationStore();
const occupations = computed(() => (occupationStore.occupations).map(
    (occupation) => {
      return {
        label: occupation.name,
        value: {
          id: occupation.id,
          name: occupation.name,
          description: occupation.description
        }
      }
    }
));

const religionStore = useReligionStore();
const religions = computed(() => (religionStore.religions).map(
    (religion) => {
      return {
        label: religion.name,
        value: {
          id: religion.id,
          name: religion.name,
          description: religion.description
        }
      }
    }
));
  
const weapon_store = useWeaponStore();
const weapons = computed(() => weapon_store.weapons.map(
    (weapon) => {
      return {
        label: weapon.name,
        value: {
          id: weapon.id,
          name: weapon.name,
          description: weapon.description
        }
      }
    }
));

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
  <div class="flex flex-col gap-2 w-[80%] mx-auto">
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
      <div class="flex flex-col gap-3 p-4 bg-gray-100 border border-gray-200 rounded-md shadow-md">
        <div class="text-center roboto-plain font-bold text-2xl">Allgemeine Angaben</div>
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
        <div class="max-h-[30vh] overflow-y-auto border border-gray-300 rounded-md p-4 bg-gray-200">
          <FormKit type="list" :value="[]" name="altNames" dynamic #default="{ items, node, value }">
            <FormKit
                v-for="(item, index) in items"
                :key="item"
                :index="index"
                label="Namensvariationen"
                suffix-icon="trash"
                @suffix-icon-click="() => node.input(value?.filter((_, i) => i !== index))"
                :sections-schema="{ suffixIcon: { $el: 'button', attrs: { type: 'button' } } }"
                outer-class="max-w-full"
            />
            <button
                type="button"
                @click="() => node.input(value?.concat(''))"
                class="border border-blue-600 text-blue-600 p-1 rounded-md shadow-sm hover:shadow-md bg-red-100 font-bold max-w-1/7 mx-auto"
            >Weitere Namen hinzufügen</button>
          </FormKit>
        </div>
        <FormKit
            type="select"
            name="sex"
            label="Geschlecht"
            :options="[
                    { label: 'unbekannt', value: null },
                    { label: 'männlich', value: 'männlich' },
                    { label: 'weiblich', value: 'weiblich' }
                  ]"
            select-icon="select"
            outer-class="max-w-full"
        />
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
        <div class="text-center roboto-plain font-bold text-2xl">Religiöse Zugehörigkeit</div>
        <FormKit type="group" name="religion">
          <div class="flex flex-col gap-2 p-4 bg-gray-200 border border-gray-300 rounded-md shadow-sm">
            <FormKit
                type="text"
                name="originalText"
                label="Eingetragener Beruf"
                prefix-icon="text"
                outer-class="max-w-full"
            />
            <FormKit
                type="select"
                name="religionCategory"
                label="Standardisierte Religionskategorie"
                outer-class="max-w-full"
                select-icon="select"
                :options="[{ label: 'Keine Auswahl', value: null },
                ...religions as any
                ]"
            />
          </div>
        </FormKit>
        <div class="text-center roboto-plain font-bold text-2xl">Berufliche Situation</div>
        <FormKit type="group" name="occupation">
          <div class="flex flex-col gap-2 p-4 bg-gray-200 border border-gray-300 rounded-md shadow-sm">
            <FormKit
                type="text"
                name="originalText"
                label="Eingetragener Beruf"
                prefix-icon="text"
                outer-class="max-w-full"
            />
            <FormKit
                type="select"
                name="occupationCategory"
                label="Standardisierte Berufskategorie"
                outer-class="max-w-full"
                select-icon="select"
                :options="[{ label: 'Keine Auswahl', value: null },
                ...occupations as any
                ]"
            />
          </div>
        </FormKit>
        <div class="text-center roboto-plain font-bold text-2xl">Bezug zum Gebäude</div>
        <FormKit
            type="select"
            name="associatedBuilding"
            label="Erwähntes Gebäude"
            outer-class="max-w-full"
            select-icon="select"
            :options="[{ label: 'Keine Auswahl', value: null },
            ...buildings as any
            ]"
        />
        <div class="text-center roboto-plain font-bold text-2xl">Herkunft</div>
        <FormKit type="group" name="origin">
          <div class="flex flex-col gap-2 p-4 bg-gray-200 border border-gray-300 rounded-md shadow-sm">
            <FormKit
                type="text"
                name="originalText"
                label="Eingetragener Ortsname"
                prefix-icon="text"
                outer-class="max-w-full"
            />
            <FormKit
                type="select"
                multiple
                name="places"
                label="Mögliche Herkunftsorte"
                outer-class="max-w-full"
                select-icon="select"
                :options="[{ label: 'Keine Auswahl', value: null },
                ...places as any
                ]"
                help="Halten Sie die Strg-Taste gedrückt, um mehrere Orte auszuwählen"
            />
            <FormKit
                type="select"
                name="certainty"
                label="Herkunftsort lokalisierbar"
                :options="[
                    { label: 'Unbekannt', value: null },
                    { label: 'Nicht identifizierbar', value: 'UNKNOWN' },
                    { label: 'Unsicher', value: 'AMBIGUOUS' },
                    { label: 'Sicher', value: 'IDENTIFIED' }
                  ]"
                select-icon="select"
                outer-class="max-w-full"
            />
          </div>
        </FormKit>
        <div class="text-center roboto-plain font-bold text-2xl">Bewaffnung</div>
        <FormKit type="list" :value="[]" name="weapons" dynamic #default="{ items, node, value }">
          <FormKit
              type="group"
              v-for="(item, index) in items"
              :key="item"
              :index="index"
          >
            <div class="flex flex-col gap-1 bg-gray-200 rounded-md shadow-md p-4 border border-gray-300">
              <div class="grid grid-cols-2 gap-2">
                <FormKit
                    type="select"
                    name="weapon"
                    label="Waffe"
                    outer-class="max-w-full"
                    select-icon="select"
                    :options="[{ label: 'Keine Auswahl', value: null },
                    ...weapons as any
                    ]"
                />
                <FormKit
                    type="text"
                    name="originalText"
                    label="Originaler Text"
                    placeholder="Spitzhacke"
                    outer-class="max-w-full"
                />
              </div>
              <button
                  type="button"
                  @click="() => node.input(value?.filter((_, i) => i !== index))"
                  class="text-sm roboto-plain border border-red-600 text-red-600 p-1 rounded-md shadow-sm hover:shadow-md bg-blue-50 font-medium max-w-1/7 mx-auto"
              >
                Entfernen
              </button>
            </div>
          </FormKit>
          <button
              type="button"
              @click="() => node.input(value?.concat({ weapon: {}, originalText: '' }))"
              class="text-sm roboto-plain border border-blue-600 text-blue-600 p-1 rounded-md bg-blue-50 font-medium max-w-1/6 mx-auto"
          >Waffen hinzufügen</button>
        </FormKit>
        <div class="text-center roboto-plain font-bold text-2xl">Notizen</div>
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
      <!-- Preview of the input values -->
      <Fieldset class="mb-4 mt-4">
        <template #legend>
          <div class="montserrat-headline font-semibold text-black text-xl">Eingabe-Vorschau</div>
        </template>
        <div class="max-h-[500px] overflow-y-auto bg-gray-100 border border-gray-200 rounded-md">
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
