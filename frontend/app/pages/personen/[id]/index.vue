<script setup lang="ts">
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import CitizenshipPreview from "~/components/UI/preview_cards/CitizenshipPreview.vue";
import PersonJobPreview from "~/components/UI/preview_cards/PersonJobPreview.vue";
import PersonReligionPreview from "~/components/UI/preview_cards/PersonReligionPreview.vue";
import PersonWeaponsPreview from "~/components/UI/preview_cards/PersonWeaponsPreview.vue";
import PersonOriginPreview from "~/components/UI/preview_cards/PersonOriginPreview.vue";
import PersonEmigrationPreview from "~/components/UI/preview_cards/PersonEmigrationPreview.vue";
import PersonMap from "~/components/UI/preview_cards/PersonMap.vue";
import PersonSkeleton from "~/components/UI/skeletons/PersonSkeleton.vue";
import FetchError from "~/components/UI/FetchError.vue";

const person_store = usePersonStore();
const citizenshipStore = useCitizenshipStore();

const route = useRoute();
const personId = Number(route.params.id);
const personJob = computed(() => personItem.value?.job);
const hasJobContent = computed(() =>
  Boolean(personJob.value?.originalText || personJob.value?.jobCategory),
);
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
const { data: guarantorEntries } = await useAsyncData(
  `person-${personId}-guarantor`,
  () => citizenshipStore.filterCitizenships({ "guarantor-id": personId }),
);

const hasOriginContent = computed(() => {
  const origin = personItem.value?.origin;
  return Boolean(origin && (origin.originalText || origin.places.length || origin.certainty));
});
const hasEmigrationContent = computed(() => {
  const emigration = personItem.value?.emigration;
  return Boolean(emigration && (emigration.originalText || emigration.place));
});
const hasProfileContent = computed(() => {
  const person = personItem.value;
  return Boolean(
    person &&
      (person.firstName ||
        person.lastName ||
        person.sex ||
        person.altNames.length ||
        person.isCitizen ||
        naturalizationEntry.value?.length ||
        guarantorEntries.value?.length ||
        hasOriginContent.value ||
        hasEmigrationContent.value ||
        person.associatedBuilding ||
        hasJobContent.value ||
        person.religion?.originalText ||
        person.weapons?.length),
  );
});
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
    class="person-page flex min-h-full flex-col gap-8"
  >
    <header class="flex items-start justify-between gap-6 max-sm:items-center">
      <h1 class="text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">{{ personItem.fullName }}</h1>
      <TaskBar :id="personId" entity_type="persons" />
    </header>
    <div class="flex flex-col gap-4">
      <PersonMap
        v-if="hasOriginContent || hasEmigrationContent || personItem.associatedBuilding"
        :personId="personId"
        :personOrigin="hasOriginContent ? personItem.origin : null"
        :personEmigration="hasEmigrationContent ? personItem.emigration : null"
        :associatedBuilding="personItem.associatedBuilding"
      />
      <div
        v-if="hasProfileContent"
        class="flex min-w-0 flex-col gap-5 rounded-md border border-default bg-default p-5 shadow-md"
      >
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Über die Person</h2>
        <div
          v-if="personItem.firstName || personItem.lastName"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <template v-if="personItem.firstName">
            <p class="text-base font-medium text-muted">Vorname</p>
            <p class="font-semibold">{{ personItem.firstName }}</p>
          </template>
          <template v-if="personItem.lastName">
            <p class="text-base font-medium text-muted">Nachname</p>
            <p class="font-semibold">{{ personItem.lastName }}</p>
          </template>
        </div>
        <div
          v-if="personItem.sex"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Geschlecht</p>
          <p class="font-semibold">{{ personItem.sex }}</p>
        </div>
        <div
          v-if="personItem.altNames.length > 0"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Namensvarianten</p>
          <ul class="list-inside list-disc">
            <li v-for="name in personItem.altNames" class="font-semibold">
              {{ name }}
            </li>
          </ul>
        </div>
        <div
          v-if="personItem.isCitizen"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Bürger?</p>
          <Icon
            name="material-symbols-check-circle-outline"
            class="text-4xl text-green-600"
          />
        </div>
        <div
          v-if="naturalizationEntry && naturalizationEntry.length > 0"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">
            Nachweis der Einbürgerung
          </p>
          <div>
            <CitizenshipPreview
              :citizenship="naturalizationEntry[0] as CitizenshipPreviewDTO"
            />
          </div>
        </div>
        <div
          v-if="guarantorEntries && guarantorEntries.length > 0"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Bürge bei</p>
          <div class="flex flex-wrap gap-2">
            <CitizenshipPreview
              v-for="entry in guarantorEntries"
              :key="entry.id"
              :citizenship="entry"
            />
          </div>
        </div>
        <div
          v-if="hasOriginContent"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Herkunft</p>
          <div>
            <PersonOriginPreview :origin="personItem.origin!" />
          </div>
        </div>
        <div
          v-if="hasEmigrationContent"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Wegzug</p>
          <div>
            <PersonEmigrationPreview :emigration="personItem.emigration!" />
          </div>
        </div>
        <div
          v-if="personItem.associatedBuilding"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Bezug zum Gebäude</p>
          <div>
            <NuxtLink
              :to="`/katasterplan/${personItem.associatedBuilding.id}`"
              class="rounded-md border border-default px-1.5 py-0.5 text-sm font-medium text-highlighted shadow-sm hover:border-accented hover:shadow-md"
            >
              {{ personItem.associatedBuilding.districtPropertyNumber ?? "Zum Gebäude" }}
            </NuxtLink>
          </div>
        </div>
        <div
          v-if="personJob && hasJobContent"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Berufliche Situation</p>
          <div>
            <PersonJobPreview :job="personJob" />
          </div>
        </div>
        <div
          v-if="personReligion && personReligion.originalText"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">
            Religiöse Zugehörigkeit
          </p>
          <div>
            <PersonReligionPreview :religion="personReligion" />
          </div>
        </div>
        <div
          v-if="personWeapons && personWeapons.length > 0"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-base font-medium text-muted">Bewaffnung</p>
          <div>
            <PersonWeaponsPreview :weapons="personWeapons" />
          </div>
        </div>
      </div>
      <div
        v-if="personItem.generalNotes"
        class="flex min-w-0 flex-col gap-5 rounded-md border border-default bg-default p-5 shadow-md"
      >
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Notizen und Anmerkungen</h2>
        <div class="flex flex-col gap-2 border-l-0 border-t border-muted py-3">
          <p class="text-sm font-bold leading-tight text-muted">Anmerkungen</p>
          <p class="text-justify leading-normal text-highlighted">{{ personItem.generalNotes }}</p>
        </div>
        <div
          v-if="personItem.internalNotes"
          class="flex flex-col gap-2 border-l-0 border-t border-muted py-3"
        >
          <p class="text-sm font-bold leading-tight text-muted">Notizen</p>
          <p class="text-justify leading-normal text-highlighted">{{ personItem.internalNotes }}</p>
        </div>
      </div>
    </div>
    <UIContentMetadata
      :created-date="personItem.createdDate"
      :created-by="personItem.createdBy"
      :last-modified-date="personItem.lastModifiedDate"
      :last-modified-by="personItem.lastModifiedBy"
    />
  </div>
</template>
