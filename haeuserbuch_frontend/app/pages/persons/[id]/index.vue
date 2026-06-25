<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import CitizenshipPreview from "~/components/UI/preview_cards/CitizenshipPreview.vue";
import PersonJobPreview from "~/components/UI/preview_cards/PersonJobPreview.vue";
import PersonReligionPreview from "~/components/UI/preview_cards/PersonReligionPreview.vue";
import PersonWeaponsPreview from "~/components/UI/preview_cards/PersonWeaponsPreview.vue";
import PersonOriginPreview from "~/components/UI/preview_cards/PersonOriginPreview.vue";

const person_store = usePersonStore();
const citizenshipStore = useCitizenshipStore();

const route = useRoute();
const personId = Number(route.params.id);
const person_origin = computed(() => personItem.value?.origin);
const person_job = computed(() => personItem.value?.job);
const person_religion = computed(() => personItem.value?.religion);
const person_weapons = computed(() => personItem.value?.weapons);

const { data: personItem } = await useAsyncData(`person-${ personId }`, () => person_store.fetchPersonById(personId));
const { data: naturalizationEntry } = await useAsyncData(`person-${ personId }-naturalization`, () => citizenshipStore.filterCitizenships({'naturalizedperson-id': personId}));

useHead(() => ({
  title: personItem.value ? `${personItem.value.fullName} - Personenverzeichnis` : 'Nicht gefunden',
}));
</script>

<template>
  <div class="flex flex-col gap-4 rounded-lg shadow-md p-4 border border-gray-200">
    <div class="flex flex-row justify-between">
      <h1 class="text-3xl montserrat-headline font-bold">{{ personItem?.fullName }}</h1>
      <TaskBar :id="personId" entity_type="persons"/>
    </div>
    <div class="p-4 rounded-md shadow-md bg-gray-100 border border-gray-200">
      <table class="text-black roboto-plain w-full table-auto">
        <tbody class="divide-y divide-gray-200">
        <tr v-show="personItem?.firstName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Vorname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ personItem?.firstName }}</td>
        </tr>
        <tr v-show="personItem?.lastName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Nachname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ personItem?.lastName }}</td>
        </tr>
        <tr v-show="personItem?.altNames && personItem?.altNames.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Namensvarianten</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <ul class="list-disc list-inside">
              <li v-for="name in personItem?.altNames" :key="name">{{ name }}</li>
            </ul>
          </td>
        </tr>
        <tr v-show="personItem?.sex">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Geschlecht</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ personItem?.sex }}</td>
        </tr>
        <tr v-show="personItem?.isCitizen">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bürger</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <i class="pi pi-check" style="color: green"/>
          </td>
        </tr>
        <tr v-if="naturalizationEntry && naturalizationEntry.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold align-top">Nachweis der Einbürgerung</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <CitizenshipPreview :citizenship="naturalizationEntry[0]"/>
          </td>
        </tr>
        <tr v-show="person_origin?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold align-top">Herkunft</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <PersonOriginPreview v-if="personItem && personItem.origin" :personId="personId" :personOrigin="personItem.origin"/>
          </td>
        </tr>
        <tr v-show="personItem?.associatedBuilding">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bezug zum Gebäude</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <NuxtLink
                :to="`/buildings/${ personItem?.associatedBuilding?.id }`"
                class="p-1.5 bg-gray-300 rounded-md shadow-md hover:shadow-lg font-medium"
            >
              {{ personItem?.associatedBuilding?.districtHouseNumber }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-if="person_job?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold align-top">Berufliche Situation</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <PersonJobPreview :job="person_job"/>
          </td>
        </tr>
        <tr v-if="person_religion?.originalText">
          <td class="px-6 py-4 whitespace-nowrap font-bold align-top">Religiöse Zugehörigkeit</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <PersonReligionPreview :religion="person_religion"/>
          </td>
        </tr>
        <tr v-if="person_weapons && person_weapons.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold align-top">Bewaffnung</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <PersonWeaponsPreview :weapons="person_weapons"/>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="flex flex-col gap-2 p-4 rounded-md shadow-md bg-gray-100 border border-gray-200">
      <Panel header="Notizen" toggleable v-show="personItem?.generalNotes">
        <template #header>
          <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
        </template>
        <p class="text-sm text-black roboto-plain">{{ personItem?.generalNotes }}</p>
      </Panel>
      <div class="flex flex-col">
        <div v-if="personItem?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Erstellt am:</p>
          <p>{{ new Date(personItem?.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="personItem?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
          <p class="font-bold">Stand:</p>
          <p>{{ new Date(personItem?.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
