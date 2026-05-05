<script setup lang="ts">
import CitizenshipSkeleton from "~/components/UI/skeletons/CitizenshipSkeleton.vue";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import { title_shortener } from "~/utils/helpers";

const citizenship_store = useCitizenshipStore();

const route = useRoute();
const citizenship_id = Number(route.params.id);

const loading = computed(() => status.value === 'pending');

const { data: citizenshipItem, status } = await useAsyncData(`citizenship-${ citizenship_id }`, () => citizenship_store.fetchCitizenshipById(citizenship_id));

useHead(() => ({
  title: citizenshipItem.value ? `${ citizenshipItem.value.signature } - Bürgermatrikel` : 'Nicht gefunden',
}));
</script>

<template>
  <CitizenshipSkeleton v-if="loading"/>
  <div v-else>
    <div v-if="citizenshipItem" class="flex flex-col gap-4 rounded-md shadow-md p-4">
      <div class="flex flex-row justify-between">
        <h1 class="text-3xl montserrat-headline font-bold text-black">{{ citizenshipItem?.signature }}</h1>
        <TaskBar :id="citizenship_id" entity_type="citizenships"/>
      </div>
      <div class="rounded-md shadow-md p-4 bg-gray-100">
        <table class="text-black roboto-plain w-full table-auto">
          <tbody v-if="citizenshipItem" class="divide-y divide-gray-200">
          <tr v-if="citizenshipItem.signature">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Signatur</td>
            <td class="py-4 whitespace-nowrap">{{ citizenshipItem.signature }}</td>
          </tr>
          <tr v-if="citizenshipItem.refNumber">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Referenz Meyer-Erlach</td>
            <td class="py-4 whitespace-nowrap">{{ citizenshipItem.refNumber }}</td>
          </tr>
          <tr v-if="citizenshipItem.dateNaturalization">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Datum der Einbürgerung</td>
            <td class="py-4 whitespace-nowrap">{{ citizenshipItem.dateNaturalization }}</td>
          </tr>
          <tr v-if="citizenshipItem.dateMisc">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Andere Datumsangaben</td>
            <td class="py-4 whitespace-nowrap">{{ citizenshipItem.dateMisc }}</td>
          </tr>
          <tr v-if="citizenshipItem.person">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Eingebürgerte Person</td>
            <td class="py-4 whitespace-nowrap">
              <NuxtLink :to="`/persons/${ citizenshipItem.person.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
                {{ citizenshipItem.person.fullName }}
              </NuxtLink>
            </td>
          </tr>
          <tr v-if="citizenshipItem.mentionedPersons.length > 0">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Weitere erwähnte Personen</td>
            <td class="py-4 whitespace-nowrap">
              <div class="flex flex-wrap gap-5">
                <div v-for="person in citizenshipItem.mentionedPersons">
                  <NuxtLink :to="`/persons/${ person.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
                    {{ person.fullName }}
                  </NuxtLink>
                </div>
              </div>
            </td>
          </tr>
          <tr v-if="citizenshipItem.primarySource">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Primärquelle</td>
            <td class="py-4 whitespace-nowrap">
              <NuxtLink :to="`/sources/${ citizenshipItem.primarySource.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
                {{ citizenshipItem.primarySource.title ? title_shortener(citizenshipItem.primarySource.title) : citizenshipItem.primarySource.title }}
              </NuxtLink>
            </td>
          </tr>
          <tr v-if="citizenshipItem.secondarySource">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Sekundärquelle</td>
            <td class="py-4 whitespace-nowrap">
              <NuxtLink :to="`/sources/${ citizenshipItem.secondarySource.id }`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-gray-200 font-medium">
                {{ citizenshipItem.secondarySource.title ? title_shortener(citizenshipItem.secondarySource.title, 4) : citizenshipItem.secondarySource.title }}
              </NuxtLink>
            </td>
          </tr>
          <tr v-if="citizenshipItem.entryText">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Text des Eintrags</td>
            <td class="py-4">{{ citizenshipItem.entryText }}</td>
          </tr>
          <tr v-if="citizenshipItem.addendum">
            <td class="py-4 pr-4 whitespace-nowrap font-bold">Nachtrag</td>
            <td class="py-4">{{ citizenshipItem.addendum }}</td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="flex flex-col gap-2 p-4 rounded-md shadow-md bg-gray-100">
        <Panel header="Notizen" toggleable v-show="citizenshipItem?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ citizenshipItem?.generalNotes }}</p>
        </Panel>
        <div class="flex flex-col">
          <div v-if="citizenshipItem?.createdDate" class="flex flex-row space-x-2 text-base text-black roboto-plain">
            <p class="font-bold">Erstellt am:</p>
            <p>{{ new Date(citizenshipItem?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="citizenshipItem?.lastModifiedDate" class="flex flex-row space-x-2 text-base text-black roboto-plain">
            <p class="font-bold">Stand:</p>
            <p>{{ new Date(citizenshipItem?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
