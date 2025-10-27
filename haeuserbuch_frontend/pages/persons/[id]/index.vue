<script setup lang="ts">
import { onMounted } from "vue";

const person_store = usePersonStore();

const route = useRoute();
const person_id = Number(route.params.id);
const person_item = computed(() => person_store.current_person);

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
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ person_item?.fullName }}</h1>
    </template>
    <template #content>
      <table class="text-black roboto-plain w-full table-auto">
        <tbody class="divide-y divide-gray-200">
        <tr v-show="person_item?.firstName">
          <td class="px-6 py-4 whitespace-nowrap">Vorname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.firstName }}</td>
        </tr>
        <tr v-show="person_item?.lastName">
          <td class="px-6 py-4 whitespace-nowrap">Nachname</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.lastName }}</td>
        </tr>
        <tr v-show="person_item?.sex">
          <td class="px-6 py-4 whitespace-nowrap">Geschlecht</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.sex }}</td>
        </tr>
        <tr v-show="person_item?.occupation">
          <td class="px-6 py-4 whitespace-nowrap">Beruf</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.occupation }}</td>
        </tr>
        <tr v-show="person_item?.occupationCategory">
          <td class="px-6 py-4 whitespace-nowrap">Kategorie des Berufs</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.occupationCategory }}</td>
        </tr>
        <tr v-show="person_item?.isCitizen">
          <td class="px-6 py-4 whitespace-nowrap">Bürger</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <i class="pi pi-check" style="color: green"/>
          </td>
        </tr>
        <tr v-show="person_item?.confession">
          <td class="px-6 py-4 whitespace-nowrap">Religion</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ person_item?.confession }}</td>
        </tr>
        <tr v-show="person_item?.origin">
          <td class="px-6 py-4 whitespace-nowrap">Herkunft</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <NuxtLink :to="`/places/${person_item?.origin?.id}`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-[#F1F2F2]">
              {{ person_item?.origin?.realName }}
            </NuxtLink>
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
          <div v-if="person_item?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(person_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="person_item?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(person_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
