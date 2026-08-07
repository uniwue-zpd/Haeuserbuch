<script setup lang="ts">
import { computed, useId } from "vue";
import type { BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";
import { title_shortener } from "~/utils/helpers";

const props = withDefaults(
  defineProps<{
    building: BuildingFeature;
    associatedPeople?: PersonPreviewDTO[] | null;
    associatedPeoplePending?: boolean;
    associatedPeopleError?: boolean;
    compact?: boolean;
  }>(),
  {
    associatedPeople: () => [],
    associatedPeoplePending: false,
    associatedPeopleError: false,
    compact: false,
  },
);

defineEmits<{
  retryRelationships: [];
}>();

const details = computed(() => props.building.properties);
const headingPrefix = useId();
const addressesCurrent = computed(() =>
  details.value.addresses.filter(
    (address) => String(address.fromDate ?? "") === "2025",
  ),
);
const addressesOld = computed(() =>
  details.value.addresses.filter(
    (address) => String(address.fromDate ?? "") !== "2025",
  ),
);

function formatDate(value: number | null) {
  if (!value) return null;
  return new Intl.DateTimeFormat("de-DE").format(new Date(value));
}
</script>

<template>
  <div
    class="building-details flex flex-col gap-4"
    :class="{ 'building-details--compact': compact }"
  >
    <div
      :class="
        compact
          ? 'flex flex-col gap-4'
          : 'grid grid-cols-1 gap-4 md:grid-cols-2'
      "
    >
      <section
        class="detail-section"
        :aria-labelledby="`${headingPrefix}-location`"
      >
        <h2 :id="`${headingPrefix}-location`">Adressen und Flurstücke</h2>

        <div v-if="details.propertyNumber" class="detail-row">
          <p class="detail-label">Historische Besitznummer</p>
          <p class="font-semibold">{{ details.propertyNumber }}</p>
        </div>

        <div v-if="details.district" class="detail-row">
          <p class="detail-label">Distrikt</p>
          <div>
            <NuxtLink
              :to="`/districts/${details.district.id}`"
              class="detail-link"
            >
              {{ details.district.name }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="details.quarter" class="detail-row">
          <p class="detail-label">Viertel</p>
          <div>
            <NuxtLink
              :to="`/quarters/${details.quarter.id}`"
              class="detail-link"
            >
              {{ details.quarter.name }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="addressesOld.length" class="detail-row">
          <p class="detail-label">Adressen um 1869</p>
          <div class="flex flex-wrap gap-2">
            <NuxtLink
              v-for="address in addressesOld"
              :key="
                address.id ??
                `${address.street?.id}-${address.houseNumber}-${address.fromDate}`
              "
              :to="`/streets/${address.street?.id}`"
              class="detail-link"
            >
              {{ address.street?.name }} {{ address.houseNumber }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="addressesCurrent.length" class="detail-row">
          <p class="detail-label">Aktuelle Adressen</p>
          <div class="flex flex-wrap gap-2">
            <NuxtLink
              v-for="address in addressesCurrent"
              :key="
                address.id ??
                `${address.street?.id}-${address.houseNumber}-${address.fromDate}`
              "
              :to="`/streets/${address.street?.id}`"
              class="detail-link"
            >
              {{ address.street?.name }} {{ address.houseNumber }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="details.parcelNumber" class="detail-row">
          <p class="detail-label">Flurstücksnummer</p>
          <p class="font-semibold">{{ details.parcelNumber }}</p>
        </div>

        <div v-if="details.parcelNumberCounter" class="detail-row">
          <p class="detail-label">Flurstücksnummerzähler</p>
          <p class="font-semibold">{{ details.parcelNumberCounter }}</p>
        </div>
      </section>

      <section
        class="detail-section"
        :aria-labelledby="`${headingPrefix}-description`"
      >
        <h2 :id="`${headingPrefix}-description`">Objektbeschreibung</h2>

        <div v-if="details.year" class="detail-row">
          <p class="detail-label">Jahr</p>
          <p class="font-semibold">{{ details.year }}</p>
        </div>

        <div v-if="details.names.length" class="detail-row">
          <p class="detail-label">Gebäudenamen</p>
          <ul class="list-inside list-disc">
            <li
              v-for="(name, index) in details.names"
              :key="`${name.name}-${index}`"
              class="font-semibold"
            >
              {{ name.name }}
              <NuxtLink
                v-if="name.source?.id"
                :to="`/sources/${name.source.id}`"
                class="text-blue-600 hover:text-blue-800"
                title="Quelle öffnen"
              >
                <Icon
                  name="material-symbols-book-2-outline"
                  class="text-base opacity-70"
                />
              </NuxtLink>
            </li>
          </ul>
        </div>

        <div v-if="details.object" class="detail-row">
          <p class="detail-label">Objekt</p>
          <p class="font-semibold">{{ details.object }}</p>
        </div>

        <div v-if="details.partType" class="detail-row">
          <p class="detail-label">Bauteil</p>
          <p class="font-semibold">{{ details.partType }}</p>
        </div>
      </section>
    </div>

    <section
      v-if="details.sources.length || details.literature.length"
      class="detail-section"
      :aria-labelledby="`${headingPrefix}-sources`"
    >
      <h2 :id="`${headingPrefix}-sources`">Quellen- und Literaturangaben</h2>

      <div v-if="details.sources.length" class="detail-row">
        <p class="detail-label">Quellen</p>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
            v-for="source in details.sources"
            :key="source.id ?? source.title ?? 'source'"
            :to="`/sources/${source.id}`"
            class="detail-link"
          >
            {{
              source.title
                ? title_shortener(source.title, 4)
                : "Unbenannte Quelle"
            }}
          </NuxtLink>
        </div>
      </div>

      <div v-if="details.literature.length" class="detail-row">
        <p class="detail-label">Literatur</p>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
            v-for="source in details.literature"
            :key="source.id ?? source.title ?? 'literature'"
            :to="`/sources/${source.id}`"
            class="detail-link"
          >
            {{
              source.title
                ? title_shortener(source.title, 4)
                : "Unbenannte Quelle"
            }}
          </NuxtLink>
        </div>
      </div>
    </section>

    <section
      v-if="details.generalNotes || details.internalNotes"
      class="detail-section"
      :aria-labelledby="`${headingPrefix}-notes`"
    >
      <h2 :id="`${headingPrefix}-notes`">Notizen und Anmerkungen</h2>
      <div v-if="details.generalNotes" class="detail-row">
        <p class="detail-label">Anmerkungen</p>
        <p class="whitespace-pre-wrap">{{ details.generalNotes }}</p>
      </div>
      <div v-if="details.internalNotes" class="detail-row">
        <p class="detail-label">Notizen</p>
        <p class="whitespace-pre-wrap">{{ details.internalNotes }}</p>
      </div>
    </section>

    <section
      class="detail-section"
      :aria-labelledby="`${headingPrefix}-relations`"
    >
      <h2 :id="`${headingPrefix}-relations`">
        Beziehungen zu anderen Entitäten
      </h2>
      <div class="detail-row">
        <p class="detail-label">Personen</p>
        <div
          v-if="associatedPeoplePending"
          class="flex items-center gap-2 text-sm text-muted"
          role="status"
        >
          <Icon
            name="material-symbols-progress-activity"
            class="animate-spin text-lg"
            aria-hidden="true"
          />
          Relationen werden geladen …
        </div>
        <div
          v-else-if="associatedPeopleError"
          class="rounded-lg bg-red-50 p-3 text-sm text-red-800"
        >
          <p>Die verknüpften Personen konnten nicht geladen werden.</p>
          <button
            class="mt-2 font-semibold underline"
            type="button"
            @click="$emit('retryRelationships')"
          >
            Erneut versuchen
          </button>
        </div>
        <div v-else-if="associatedPeople?.length" class="flex flex-wrap gap-2">
          <NuxtLink
            v-for="person in associatedPeople"
            :key="person.id ?? person.fullName ?? 'person'"
            :to="`/persons/${person.id}`"
            class="detail-link"
          >
            {{ person.fullName || `Person mit ID ${person.id}` }}
          </NuxtLink>
        </div>
        <p v-else class="text-sm text-muted">
          Bisher keine Relationen gefunden
        </p>
      </div>
    </section>

    <section class="detail-section" :aria-labelledby="`${headingPrefix}-audit`">
      <h2 :id="`${headingPrefix}-audit`">Über den Eintrag</h2>
      <div v-if="formatDate(details.createdDate)" class="detail-row">
        <p class="detail-label">Erstellt am</p>
        <p>{{ formatDate(details.createdDate) }}</p>
      </div>
      <div v-if="formatDate(details.lastModifiedDate)" class="detail-row">
        <p class="detail-label">Zuletzt aktualisiert am</p>
        <p>{{ formatDate(details.lastModifiedDate) }}</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.detail-section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  border: 1px solid rgb(209 213 219);
  border-radius: 0.75rem;
  background: white;
  padding: 1rem;
  box-shadow: 0 8px 24px rgb(15 23 42 / 0.06);
}

.detail-section h2 {
  font-family: "Archivo", sans-serif;
  font-size: 1.25rem;
  font-weight: 650;
  color: #22374b;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.375rem 0;
}

.detail-label {
  color: rgb(107 114 128);
  font-size: 0.8125rem;
  font-weight: 600;
}

.detail-link {
  display: inline-flex;
  border: 1px solid rgb(209 213 219);
  border-radius: 0.5rem;
  padding: 0.35rem 0.55rem;
  color: #22374b;
  font-size: 0.875rem;
  font-weight: 600;
  transition:
    border-color 150ms ease,
    box-shadow 150ms ease,
    color 150ms ease;
}

.detail-link:hover,
.detail-link:focus-visible {
  border-color: #d9a441;
  color: #172a3a;
  box-shadow: 0 4px 12px rgb(15 23 42 / 0.12);
  outline: none;
}

.building-details--compact {
  gap: 0;
}

.building-details--compact > div {
  gap: 0;
}

.building-details--compact .detail-section {
  gap: 0;
  border: 0;
  border-bottom: 1px solid var(--ui-border-muted);
  border-radius: 0;
  background: transparent;
  padding: 1.25rem 0;
  color: var(--ui-text);
  box-shadow: none;
}

.building-details--compact .detail-section h2 {
  margin-bottom: 0.5rem;
  color: var(--ui-text-highlighted);
  font-size: 0.95rem;
  font-weight: 700;
}

.building-details--compact .detail-row {
  display: grid;
  grid-template-columns: minmax(0, 9.5rem) minmax(0, 1fr);
  align-items: start;
  gap: 0.75rem;
  padding: 0.625rem 0;
}

.building-details--compact .detail-label {
  color: var(--ui-text-muted);
  line-height: 1.35rem;
  overflow-wrap: anywhere;
}

.building-details--compact .detail-link {
  border: 0;
  border-radius: 0;
  padding: 0;
  color: var(--ui-text-highlighted);
  text-decoration: underline;
  text-decoration-color: var(--ui-text-dimmed);
  text-underline-offset: 0.2rem;
}

.building-details--compact .detail-link:hover,
.building-details--compact .detail-link:focus-visible {
  box-shadow: none;
  text-decoration-color: currentColor;
}
</style>
