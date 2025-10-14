<script setup lang="ts">
const citizenship_store = useCitizenshipStore();

const route = useRoute();
const citizenship_id = Number(route.params.id);
const citizenship_item = computed(() => citizenship_store.current_citizenship);

onMounted(async () => {
  await citizenship_store.fetchCitizenshipById(citizenship_id);
});

useHead(() => ({
  title: citizenship_item.value ? `${citizenship_item.value.number} - Bürgermatrikel` : 'Nicht gefunden',
}));
</script>

<template>
  <Card v-show="citizenship_item">
    <template #title>
      <h1 class="text-3xl montserrat-headline font-bold text-black">{{ citizenship_item?.number }}</h1>
    </template>
    <template #content>
      <table class="text-black roboto-plain w-full table-auto">
        <tbody v-if="citizenship_item" class="divide-y divide-gray-200">
        <tr v-show="citizenship_item?.signature">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Signatur</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ citizenship_item?.signature }}</td>
        </tr>
        <tr v-show="citizenship_item?.number">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Nummer</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ citizenship_item?.number }}</td>
        </tr>
        <tr v-show="citizenship_item?.date">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Datum</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ citizenship_item?.date }}</td>
        </tr>
        <tr v-if="citizenship_item.persons.length > 0">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Personen</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex flex-row space-x-2">
              <div v-for="(person, index) in citizenship_item?.persons" :key="index">
                <NuxtLink :to="`/api/citizenships/${person.id}`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-[#F1F2F2]">
                  {{ person.firstName }} {{ person.lastName }}
                </NuxtLink>
              </div>
            </div>
          </td>
        </tr>
        <tr v-show="citizenship_item?.place">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Ort</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <NuxtLink :to="`/places/${citizenship_item?.place?.id}`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-[#F1F2F2]">
              {{ citizenship_item?.place?.realName }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-show="citizenship_item?.source">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Quelle</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <NuxtLink :to="`/sources/${citizenship_item?.source.id}`" class="rounded-md shadow-md hover:shadow-lg p-2 bg-[#F1F2F2]">
              {{ citizenship_item?.source.title }}
            </NuxtLink>
          </td>
        </tr>
        <tr v-show="citizenship_item?.entryText">
          <td class="px-6 py-4 whitespace-nowrap font-bold">Text</td>
          <td class="px-6 py-4">{{ citizenship_item?.entryText }}</td>
        </tr>
        </tbody>
      </table>
    </template>
    <template #footer>
      <div class="flex flex-col gap-2">
        <Panel header="Notizen" toggleable v-show="citizenship_item?.generalNotes">
          <template #header>
            <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
          </template>
          <p class="text-sm text-black roboto-plain">{{ citizenship_item?.generalNotes }}</p>
        </Panel>
        <Divider/>
        <div class="flex flex-col">
          <div v-if="citizenship_item?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Erstellt am:</p>
            <p>{{ new Date(citizenship_item?.createdDate).toLocaleDateString() }}</p>
          </div>
          <div v-if="citizenship_item?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
            <p>Stand:</p>
            <p>{{ new Date(citizenship_item?.lastModifiedDate).toLocaleDateString() }}</p>
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
