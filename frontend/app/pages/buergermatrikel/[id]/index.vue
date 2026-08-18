<script setup lang="ts">
import CitizenshipSkeleton from "~/components/UI/skeletons/CitizenshipSkeleton.vue";
import TaskBar from "~/components/UI/page_actions/TaskBar.vue";
import PersonPreview from "~/components/UI/preview_cards/PersonPreview.vue";
import SourcePreview from "~/components/UI/preview_cards/SourcePreview.vue";
import FetchError from "~/components/UI/FetchError.vue";

const citizenship_store = useCitizenshipStore();

const route = useRoute();
const citizenship_id = Number(route.params.id);

const {
  data: citizenshipItem,
  pending: isLoading,
  error: hasError,
} = await useAsyncData(`citizenship-${citizenship_id}-details`, () =>
  citizenship_store.fetchCitizenshipById(citizenship_id),
);

useHead(() => ({
  title: citizenshipItem.value
    ? `${citizenshipItem.value.signature} - Bürgermatrikel`
    : "Nicht gefunden",
}));
</script>

<template>
  <CitizenshipSkeleton v-if="isLoading" />
  <FetchError v-else-if="hasError" :error="hasError" />
  <div
    v-else-if="citizenshipItem"
    class="citizenship-page flex min-h-full flex-col gap-8"
  >
    <header class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <h1 class="text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">
          {{ citizenshipItem.signature || `Eintrag ${citizenship_id}` }}
        </h1>
      </div>
      <TaskBar :id="citizenship_id" entity_type="citizenships" />
    </header>

    <div class="grid grid-cols-1 gap-4 lg:auto-rows-auto lg:grid-cols-12 lg:items-start">
      <section class="flex min-w-0 flex-col gap-5 rounded-2xl border border-default bg-default p-5 shadow-md lg:col-span-7">
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Angaben zum Eintrag</h2>
        <div class="flex flex-col">
          <div v-if="citizenshipItem.signature" class="content-table-row">
            <p class="content-table-label">Signatur</p>
            <p class="font-semibold">{{ citizenshipItem.signature }}</p>
          </div>
          <div v-if="citizenshipItem.refNumber" class="content-table-row">
            <p class="content-table-label">Referenz Meyer-Erlach</p>
            <p class="font-semibold">{{ citizenshipItem.refNumber }}</p>
          </div>
          <div v-if="citizenshipItem.dateNaturalization" class="content-table-row">
            <p class="content-table-label">Datum der Einbürgerung</p>
            <p class="font-semibold">{{ citizenshipItem.dateNaturalization }}</p>
          </div>
          <div v-if="citizenshipItem.dateMisc" class="content-table-row">
            <p class="content-table-label">Andere Datumsangaben</p>
            <p class="font-semibold">{{ citizenshipItem.dateMisc }}</p>
          </div>
          <div v-if="citizenshipItem.person" class="content-table-row">
            <p class="content-table-label">Eingebürgerte Person</p>
            <PersonPreview :person="citizenshipItem.person" />
          </div>
          <div v-if="citizenshipItem.mentionedPersons.length > 0" class="content-table-row">
            <p class="content-table-label">Weitere erwähnte Personen</p>
            <div class="flex flex-wrap gap-2">
              <PersonPreview
                v-for="(person, personIndex) in citizenshipItem.mentionedPersons"
                :key="person.id ?? person.fullName ?? `mentioned-person-${personIndex}`"
                :person="person"
              />
            </div>
          </div>
        </div>
      </section>

      <section class="flex min-w-0 flex-col gap-5 rounded-2xl border border-default bg-default p-5 shadow-md lg:col-span-5">
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Quellen</h2>
        <div class="flex flex-col gap-3">
          <div v-if="citizenshipItem.primarySource" class="content-table-row !grid-cols-1 !gap-2 sm:!grid-cols-1">
            <p class="content-table-label">Quellen</p>
            <SourcePreview :source="citizenshipItem.primarySource" />
          </div>
          <div v-if="citizenshipItem.secondarySource" class="content-table-row !grid-cols-1 !gap-2 sm:!grid-cols-1">
            <p class="content-table-label">Literatur</p>
            <SourcePreview :source="citizenshipItem.secondarySource" />
          </div>
        </div>
      </section>

      <section
        v-if="citizenshipItem.entryText || citizenshipItem.addendum"
        class="flex min-w-0 flex-col gap-5 rounded-2xl border border-default bg-default p-5 shadow-md lg:col-span-12"
      >
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Text des Eintrags</h2>
        <div class="flex flex-col">
          <div v-if="citizenshipItem.entryText" class="border-t border-muted py-3 first:border-t-0 first:pt-0">
            <p class="whitespace-pre-wrap text-justify leading-7 text-highlighted">{{ citizenshipItem.entryText }}</p>
          </div>
          <div v-if="citizenshipItem.addendum" class="content-table-row !grid-cols-1 !gap-2 sm:!grid-cols-1">
            <p class="content-table-label">Nachtrag</p>
            <p class="whitespace-pre-wrap text-justify leading-7 text-highlighted">{{ citizenshipItem.addendum }}</p>
          </div>
        </div>
      </section>
    </div>
    <UIContentMetadata
      :created-date="citizenshipItem.createdDate"
      :last-modified-date="citizenshipItem.lastModifiedDate"
    />
  </div>
</template>
