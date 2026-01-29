<script setup lang="ts">
import { onMounted } from "vue";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";

const person_store = usePersonStore();

const route = useRoute();
const person_id = Number(route.params.id);
const person_item = computed(() => person_store.current_person);
const person_origin = computed(() => person_item.value?.origin);
const originCertainty = ref<Record<string, { label: string; color: string }>>({
  IDENTIFIED: { label: 'Identifiziert', color: 'bg-green-600' },
  AMBIGUOUS: { label: 'Mehrdeutig', color: 'bg-yellow-300' },
  UNKNOWN: { label: 'Unbekannt', color: 'bg-red-600' }
});

onMounted(async () => {
  await person_store.fetchPersonById(person_id);
});

useHead(() => ({
  title: person_item.value ? `${person_item.value.fullName} - Personenverzeichnis` : 'Nicht gefunden',
}));
</script>

<template>
  <Card v-show="person_item">
    <template #title>
      <div class="flex flex-row justify-between">
        <h1 class="text-3xl montserrat-headline font-bold text-black">{{ person_item?.fullName }}</h1>
        <TaskBar :id="person_id" entity_type="persons"/>
      </div>
    </template>
    <template #content>
      <table class="text-black roboto-plain w-full table-auto">
        <tbody class="divide-y divide-gray-200">
        <tr v-show="person_item?.firstName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Vorname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.firstName }}</td>
        </tr>
        <tr v-show="person_item?.lastName">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Nachname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.lastName }}</td>
        </tr>
        <tr v-show="person_item?.altNames && person_item?.altNames.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Namensvarianten</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <ul class="list-disc list-inside">
              <li v-for="name in person_item?.altNames" :key="name">{{ name }}</li>
            </ul>
          </td>
        </tr>
        <tr v-show="person_item?.sex">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Geschlecht</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.sex }}</td>
        </tr>
        <tr v-show="person_item?.occupation">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Beruf</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.occupation }}</td>
        </tr>
        <tr v-show="person_item?.occupationCategory">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Kategorie des Berufs</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.occupationCategory }}</td>
        </tr>
        <tr v-show="person_item?.isCitizen">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Bürger</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <i class="pi pi-check" style="color: green"/>
          </td>
        </tr>
        <tr v-show="person_item?.confession">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Religion</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.confession }}</td>
        </tr>
        <tr v-show="person_origin">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Herkunft</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-col gap-1 rounded-md shadow-md p-2 bg-gray-100">
              <div v-if="person_origin?.originalText" class="flex flex-row space-x-3">
                <span class="font-bold">Eingetragener Ort:</span>
                <span>{{ person_origin.originalText }}</span>
              </div>
              <div v-if="person_origin?.places && person_origin?.places.length > 0" class="flex flex-row space-x-3">
                <span class="font-bold">Möglicherweise:</span>
                <div class="flex flex-wrap gap-2">
                  <div v-for="place in person_origin.places">
                    <NuxtLink
                        :to="`/places/${ place.id }`"
                        class="p-1 bg-gray-200 rounded-md shadow-md hover:shadow-lg"
                    >
                      {{ place.realName }}
                    </NuxtLink>
                  </div>
                </div>
              </div>
              <div v-if="person_origin?.certainty" class="flex flex-row space-x-3 items-center">
                <span class="font-bold">Unsicherheitsfaktor:</span>
                <div :class="`h-[19px] w-[19px] rounded-full shadow-md border border-black ${ originCertainty[person_origin.certainty].color }`"></div>
              </div>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="person_item?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ person_item?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="person_item?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
            <p class="font-bold">Erstellt am:</p>
            <p>{{ new Date(person_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="person_item?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
            <p class="font-bold">Stand:</p>
            <p>{{ new Date(person_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
