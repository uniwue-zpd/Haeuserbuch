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
        person.associatedBuilding ||
        person.religion?.originalText ||
        person.weapons?.length),
  );
});
const hasOriginContent = computed(() => {
  const origin = personItem.value?.origin;
  return Boolean(origin && (origin.originalText || origin.places.length || origin.certainty));
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
    <header class="person-page__header">
      <h1 class="person-title">{{ personItem.fullName }}</h1>
      <TaskBar :id="personId" entity_type="persons" />
    </header>
    <div class="person-bento-grid">
      <div
        v-if="hasProfileContent"
        class="person-card person-card--profile"
      >
        <h2>Über die Person</h2>
        <div
          v-if="personItem.firstName || personItem.lastName"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <template v-if="personItem.firstName">
            <p class="text-sm text-gray-500 font-medium">Vorname</p>
            <p class="font-semibold">{{ personItem.firstName }}</p>
          </template>
          <template v-if="personItem.lastName">
            <p class="text-sm text-gray-500 font-medium">Nachname</p>
            <p class="font-semibold">{{ personItem.lastName }}</p>
          </template>
        </div>
        <div
          v-if="personItem.sex"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-sm text-gray-500 font-medium">Geschlecht</p>
          <p class="font-semibold">{{ personItem.sex }}</p>
        </div>
        <div
          v-if="personItem.altNames.length > 0"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
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
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-sm text-gray-500 font-medium">Bürger?</p>
          <Icon
            name="material-symbols-check-circle-outline"
            class="text-4xl text-green-600"
          />
        </div>
        <div
          v-if="naturalizationEntry && naturalizationEntry.length > 0"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
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
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
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
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-sm text-gray-500 font-medium">Berufliche Situation</p>
          <div>
            <PersonJobPreview v-if="personJob" :job="personJob" />
            <p v-else class="text-sm text-muted">
              Bisher keine berufliche Situation erfasst
            </p>
          </div>
        </div>
        <div
          v-if="personReligion && personReligion.originalText"
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
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
          class="grid grid-cols-1 items-start gap-x-4 gap-y-1 border-t border-muted py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]"
        >
          <p class="text-sm text-gray-500 font-medium">Bewaffnung</p>
          <div>
            <PersonWeaponsPreview :weapons="personWeapons" />
          </div>
        </div>
      </div>
      <PersonOriginPreview
        v-if="hasOriginContent && personItem?.origin"
        :personId="personId"
        :personOrigin="personItem.origin"
      />
      <div
        v-if="personItem.generalNotes"
        class="person-card"
      >
        <h2>Notizen und Anmerkungen</h2>
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
    </div>
    <UIContentMetadata
      :created-date="personItem.createdDate"
      :last-modified-date="personItem.lastModifiedDate"
    />
  </div>
</template>

<style scoped>
.person-page__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1.5rem;
}

.person-title {
  color: var(--ui-text-highlighted);
  font-size: clamp(2.25rem, 5vw, 4.5rem);
  font-weight: 750;
  letter-spacing: -0.055em;
  line-height: 0.98;
}

.person-bento-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 1rem;
}

.person-card {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 1.25rem;
  border: 1px solid rgb(209 213 219);
  border-radius: 1rem;
  background: var(--ui-bg);
  padding: 1.25rem;
  box-shadow: 0 8px 24px rgb(15 23 42 / 0.06);
}

.person-card h2 {
  color: var(--ui-text-highlighted);
  font-size: 1.2rem;
  font-weight: 650;
  line-height: 1.25;
}

.person-card :deep(.border-l-4) {
  gap: 0.45rem;
  border-top: 1px solid var(--ui-border-muted);
  border-left-width: 0;
  padding: 0.75rem 0;
}

.person-card :deep(.border-l-4 > p:first-child) {
  color: var(--ui-text-muted);
  font-size: 0.75rem;
  font-weight: 700;
  line-height: 1.25;
}

.person-card :deep(.border-l-4 > p:not(:first-child)) {
  color: var(--ui-text-highlighted);
  line-height: 1.45;
}

.person-card--profile {
  grid-row: span 2;
}

.person-bento-grid :deep(.person-origin-card) {
  min-width: 0;
  border: 1px solid rgb(209 213 219);
  border-radius: 1rem;
  background: var(--ui-bg);
  padding: 1.25rem;
  box-shadow: 0 8px 24px rgb(15 23 42 / 0.06);
}

.person-bento-grid :deep(.person-origin-card h2) {
  color: var(--ui-text-highlighted);
  font-size: 1.2rem;
  font-weight: 650;
  line-height: 1.25;
}

.person-bento-grid :deep(.person-origin-card > div:not(.h-\[220px\])) {
  gap: 0.45rem;
  border-top: 1px solid var(--ui-border-muted);
  padding: 0.75rem 0;
}

.person-bento-grid :deep(.person-origin-card .text-xs) {
  color: var(--ui-text-muted);
  font-size: 0.75rem;
  font-weight: 700;
  line-height: 1.25;
}

.person-bento-grid :deep(.person-origin-card .h-\[220px\]) {
  border: 0;
  border-radius: 0.75rem;
  box-shadow: none;
}

@media (min-width: 1024px) {
  .person-bento-grid {
    grid-template-columns: repeat(12, minmax(0, 1fr));
    grid-auto-rows: minmax(10rem, auto);
    align-items: start;
  }

  .person-card--profile {
    grid-column: span 7;
  }

  .person-card--profile + * {
    grid-column: span 5;
  }

  .person-card:not(.person-card--profile) {
    grid-column: span 5;
  }
}

@media (max-width: 640px) {
  .person-page__header {
    align-items: center;
  }
}
</style>
