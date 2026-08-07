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
  <div v-else>
    <div
      v-if="citizenshipItem"
      class="flex flex-col gap-4 p-4 rounded-lg shadow-lg border-2 border-gray-300"
    >
      <div class="flex flex-row justify-between">
        <h1 class="text-3xl font-bold">{{ citizenshipItem?.signature }}</h1>
        <TaskBar :id="citizenship_id" entity_type="citizenships" />
      </div>
      <div class="flex flex-col gap-4">
        <div
          class="flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
        >
          <h2 class="text-2xl font-semibold">Informationen zum Eintrag</h2>
          <div
            v-if="citizenshipItem.signature"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">Signatur</p>
            <p class="font-semibold">{{ citizenshipItem.signature }}</p>
          </div>
          <div
            v-if="citizenshipItem.refNumber"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">
              Referenz Meyer-Erlach
            </p>
            <p class="font-semibold">{{ citizenshipItem.refNumber }}</p>
          </div>
          <div
            v-if="citizenshipItem.dateNaturalization"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">
              Datum der Einbürgerung
            </p>
            <p class="font-semibold">
              {{ citizenshipItem.dateNaturalization }}
            </p>
          </div>
          <div
            v-if="citizenshipItem.dateMisc"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">
              Andere Datumsangaben
            </p>
            <p class="font-semibold">{{ citizenshipItem.dateMisc }}</p>
          </div>
          <div
            v-if="citizenshipItem.person"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">
              Eingebürgerte Person
            </p>
            <PersonPreview :person="citizenshipItem.person" />
          </div>
          <div
            v-if="citizenshipItem.mentionedPersons.length > 0"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">
              Weitere erwähnte Personen
            </p>
            <div class="flex flex-wrap gap-2">
              <PersonPreview
                v-for="person in citizenshipItem.mentionedPersons"
                :person="person"
              />
            </div>
          </div>
          <div
            v-if="citizenshipItem.entryText"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">Text des Eintrags</p>
            <p class="text-justify">{{ citizenshipItem.entryText }}</p>
          </div>
          <div
            v-if="citizenshipItem.addendum"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">Nachtrag</p>
            <p class="text-justify">{{ citizenshipItem.addendum }}</p>
          </div>
        </div>
        <div
          v-if="
            citizenshipItem.primarySource || citizenshipItem.secondarySource
          "
          class="flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
        >
          <h2 class="text-2xl font-semibold">Quellen- und Literaturangaben</h2>
          <div
            v-if="citizenshipItem.primarySource"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">Primärquelle</p>
            <SourcePreview :source="citizenshipItem.primarySource" />
          </div>
          <div
            v-if="citizenshipItem.secondarySource"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">Sekundärquelle</p>
            <SourcePreview :source="citizenshipItem.secondarySource" />
          </div>
        </div>
        <div
          class="flex flex-col gap-5 p-4 rounded-lg shadow-lg border border-gray-300"
        >
          <h2 class="text-2xl font-semibold">Über den Eintrag</h2>
          <div
            v-if="citizenshipItem.createdDate"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">Erstellt am</p>
            <p class="text-justify">
              {{ new Date(citizenshipItem.createdDate).toLocaleDateString() }}
            </p>
          </div>
          <div
            v-if="citizenshipItem.lastModifiedDate"
            class="flex flex-col gap-2 border-l-4 border-gray-300 pl-3 py-1.5"
          >
            <p class="text-sm text-gray-500 font-medium">
              Zuletzt aktualisiert am
            </p>
            <p class="text-justify">
              {{
                new Date(citizenshipItem.lastModifiedDate).toLocaleDateString()
              }}
            </p>
          </div>
          <!-- TO-DO: After Keycloak integration: createdBy & lastModifiedBy -->
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
