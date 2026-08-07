<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import CitizenshipPreview from "~/components/UI/preview_cards/CitizenshipPreview.vue";
import PersonJobPreview from "~/components/UI/preview_cards/PersonJobPreview.vue";
import PersonReligionPreview from "~/components/UI/preview_cards/PersonReligionPreview.vue";
import PersonWeaponsPreview from "~/components/UI/preview_cards/PersonWeaponsPreview.vue";
import PersonOriginPreview from "~/components/UI/preview_cards/PersonOriginPreview.vue";
import PersonSkeleton from "~/components/UI/skeletons/PersonSkeleton.vue";
import FetchError from "~/components/UI/FetchError.vue";

const person_store = usePersonStore();
const citizenshipStore = useCitizenshipStore();

const route = useRoute();
const personId = Number(route.params.id);
const personOrigin = computed(() => personItem.value?.origin);
const personJob = computed(() => personItem.value?.job);
const personReligion = computed(() => personItem.value?.religion);
const personWeapons = computed(() => personItem.value?.weapons);

const {
  data: personItem,
  pending: isLoading,
  error: hasError,
} = await useAsyncData(`person-${personId}-details`, () =>
  person_store.fetchPersonById(personId),
);
const { data: naturalizationEntry } = await useAsyncData(
  `person-${personId}-naturalization`,
  () =>
    citizenshipStore.filterCitizenships({ "naturalizedperson-id": personId }),
);

useHead(() => ({
  title: personItem.value
    ? `${personItem.value.fullName} - Personenverzeichnis`
    : "Nicht gefunden",
}));
</script>

<template>
  <PersonSkeleton v-if="isLoading" />
  <FetchError v-else-if="hasError" :error="hasError" />
  <div
    v-else-if="personItem"
    class="flex flex-col gap-4 p-4 rounded-lg shadow-lg border-2 border-gray-300"
  >
    <div class="flex flex-row justify-between">
      <h1 class="text-3xl font-bold">{{ personItem.fullName }}</h1>
      <TaskBar :id="personId" entity_type="persons" />
    </div>
    <div class="flex flex-col gap-4">
      <div
        class="flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
      >
        <h2 class="text-2xl font-semibold">Über die Person</h2>
        <div
          v-if="personItem.firstName || personItem.lastName"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Vor- und Nachname</p>
          <p class="font-semibold">
            {{ personItem.firstName }} {{ personItem.lastName }}
          </p>
        </div>
        <div
          v-if="personItem.sex"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Geschlecht</p>
          <p class="font-semibold">{{ personItem.sex }}</p>
        </div>
        <div
          v-if="personItem.altNames.length > 0"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Namensvarianten</p>
          <ul class="list-inside list-disc">
            <li v-for="name in personItem.altNames" class="font-semibold">
              {{ name }}
            </li>
          </ul>
        </div>
        <div
          v-if="personItem.isCitizen"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Bürger?</p>
          <Icon
            name="material-symbols-check-circle-outline"
            class="text-4xl text-green-600"
          />
        </div>
        <div
          v-if="naturalizationEntry && naturalizationEntry.length > 0"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">
            Nachweis der Einbürgerung
          </p>
          <div>
            <CitizenshipPreview
              :citizenship="naturalizationEntry[0] as CitizenshipPreviewDTO"
            />
          </div>
        </div>
        <div
          v-if="personItem.associatedBuilding"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Bezug zum Gebäude</p>
          <div>
            <NuxtLink
              :to="`/buildings/${personItem.associatedBuilding.id}`"
              class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
            >
              {{ personItem.associatedBuilding.districtPropertyNumber }}
            </NuxtLink>
          </div>
        </div>
        <div
          v-if="personJob"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Berufliche Situation</p>
          <div>
            <PersonJobPreview :job="personJob" />
          </div>
        </div>
        <div
          v-if="personReligion && personReligion.originalText"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">
            Religiöse Zugehörigkeit
          </p>
          <div>
            <PersonReligionPreview :religion="personReligion" />
          </div>
        </div>
        <div
          v-if="personWeapons && personWeapons.length > 0"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Bewaffnung</p>
          <div>
            <PersonWeaponsPreview :weapons="personWeapons" />
          </div>
        </div>
      </div>
      <PersonOriginPreview
        v-if="personItem && personItem.origin"
        :personId="personId"
        :personOrigin="personItem.origin"
      />
      <div
        v-if="personItem.generalNotes"
        class="flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
      >
        <h2 class="text-2xl font-semibold">Notizen und Anmerkungen</h2>
        <div class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5">
          <p class="text-sm text-gray-500 font-medium">Anmerkungen</p>
          <p class="text-justify">{{ personItem.generalNotes }}</p>
        </div>
        <div
          v-if="personItem.internalNotes"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Notizen</p>
          <p class="text-justify">{{ personItem.internalNotes }}</p>
        </div>
      </div>
      <div
        class="flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
      >
        <h2 class="text-2xl font-semibold">Über den Eintrag</h2>
        <div
          v-if="personItem.createdDate"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">Erstellt am</p>
          <p class="text-justify">
            {{ new Date(personItem.createdDate).toLocaleDateString() }}
          </p>
        </div>
        <div
          v-if="personItem.lastModifiedDate"
          class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
        >
          <p class="text-sm text-gray-500 font-medium">
            Zuletzt aktualisiert am
          </p>
          <p class="text-justify">
            {{ new Date(personItem.lastModifiedDate).toLocaleDateString() }}
          </p>
        </div>
        <!-- TO-DO: After Keycloak integration: createdBy & lastModifiedBy -->
      </div>
    </div>
  </div>
</template>

<style scoped></style>
