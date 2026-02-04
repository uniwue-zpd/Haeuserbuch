<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import {title_shortener} from "~/utils/helpers";

const citizenship_store = useCitizenshipStore();

const route = useRoute();
const citizenship_id = Number(route.params.id);
const citizenship_item = computed(() => citizenship_store.current_citizenship);

onMounted(async () => {
  await citizenship_store.fetchCitizenshipById(citizenship_id);
});

useHead(() => ({
  title: citizenship_item.value ? `${ citizenship_item.value.signature } - Bürgermatrikel` : 'Nicht gefunden',
}));
</script>

<template>
  <div v-if="citizenship_item" class="flex flex-col gap-4 rounded-md shadow-md p-4">
    <div class="flex flex-row justify-between">
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ citizenship_item?.signature }}</h1>
      <TaskBar :id="citizenship_id" entity_type="citizenships"/>
    </div>
    <div class="rounded-md shadow-md p-4 bg-gray-100">
      <table class="text-black roboto-plain w-full table-auto">
        <tbody v-if="citizenship_item" class="divide-y divide-gray-200">
        <tr v-if="citizenship_item.signature">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Signatur</td>
          <td class="py-4 whitespace-nowrap">{{ citizenship_item.signature }}</td>
        </tr>
        <tr v-if="citizenship_item.refNumber">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Referenz Mayer-Erlach</td>
          <td class="py-4 whitespace-nowrap">{{ citizenship_item.refNumber }}</td>
        </tr>
        <tr v-if="citizenship_item.date">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Datum</td>
          <td class="py-4 whitespace-nowrap">{{ citizenship_item.date }}</td>
        </tr>
        <tr v-if="citizenship_item.person">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Eingebürgerte Person</td>
          <td class="py-4 whitespace-nowrap">
            <NuxtLink :to="`/persons/${ citizenship_item.person.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
              {{ citizenship_item.person.firstName }} {{ citizenship_item.person.lastName }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-if="citizenship_item.primarySource">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Primärquelle</td>
          <td class="py-4 whitespace-nowrap">
            <NuxtLink :to="`/sources/${ citizenship_item.primarySource.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
              {{ citizenship_item.primarySource.title ? title_shortener(citizenship_item.primarySource.title) : citizenship_item.primarySource.title }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-if="citizenship_item.secondarySource">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Sekundärquelle</td>
          <td class="py-4 whitespace-nowrap">
            <NuxtLink :to="`/sources/${ citizenship_item.secondarySource.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
              {{ citizenship_item.secondarySource.title ? title_shortener(citizenship_item.secondarySource.title, 4) : citizenship_item.secondarySource.title }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-if="citizenship_item.entryText">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Text des Eintrags</td>
          <td class="py-4">{{ citizenship_item.entryText }}</td>
        </tr>
        <tr v-if="citizenship_item.addendum">
          <td class="py-4 pr-4 whitespace-nowrap font-bold">Nachtrag</td>
          <td class="py-4">{{ citizenship_item.addendum }}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="flex flex-col gap-2 p-4 rounded-md shadow-md bg-gray-100">
      <Panel header="Notizen" toggleable v-show="citizenship_item?.generalNotes">
        <template #header>
          <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
        </template>
        <p class="text-sm text-black roboto-plain">{{ citizenship_item?.generalNotes }}</p>
      </Panel>
      <div class="flex flex-col">
        <div v-if="citizenship_item?.createdDate" class="flex flex-row space-x-2 text-base text-black roboto-plain">
          <p class="font-bold">Erstellt am:</p>
          <p>{{ new Date(citizenship_item?.createdDate).toLocaleDateString() }}</p>
        </div>
        <div v-if="citizenship_item?.lastModifiedDate" class="flex flex-row space-x-2 text-base text-black roboto-plain">
          <p class="font-bold">Stand:</p>
          <p>{{ new Date(citizenship_item?.lastModifiedDate).toLocaleDateString() }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
