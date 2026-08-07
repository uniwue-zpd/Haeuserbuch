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
    bento?: boolean;
  }>(),
  {
    associatedPeople: () => [],
    associatedPeoplePending: false,
    associatedPeopleError: false,
    compact: false,
    bento: false,
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

</script>

<template>
  <div
    class="flex flex-col gap-4 [&_.detail-section]:flex [&_.detail-section]:flex-col [&_.detail-section]:gap-5 [&_.detail-section]:rounded-xl [&_.detail-section]:border [&_.detail-section]:border-gray-300 [&_.detail-section]:bg-white [&_.detail-section]:p-4 [&_.detail-section]:shadow-md [&_.detail-section_h2]:font-sans [&_.detail-section_h2]:text-xl [&_.detail-section_h2]:font-[650] [&_.detail-section_h2]:text-[#22374b] [&_.detail-row]:flex [&_.detail-row]:flex-col [&_.detail-row]:gap-2 [&_.detail-row]:py-1.5 [&_.detail-label]:text-[0.8125rem] [&_.detail-label]:font-semibold [&_.detail-label]:text-gray-500 [&_.detail-link]:inline-flex [&_.detail-link]:rounded-lg [&_.detail-link]:border [&_.detail-link]:border-gray-300 [&_.detail-link]:px-[0.55rem] [&_.detail-link]:py-[0.35rem] [&_.detail-link]:text-sm [&_.detail-link]:font-semibold [&_.detail-link]:text-[#22374b] [&_.detail-link]:transition-colors [&_.detail-link]:duration-150 [&_.detail-link:hover]:border-[#d9a441] [&_.detail-link:hover]:text-[#172a3a] [&_.detail-link:hover]:shadow-[0_4px_12px_rgb(15_23_42_/_0.12)] [&_.detail-link:focus-visible]:border-[#d9a441] [&_.detail-link:focus-visible]:text-[#172a3a] [&_.detail-link:focus-visible]:shadow-[0_4px_12px_rgb(15_23_42_/_0.12)] [&_.detail-link:focus-visible]:outline-none"
    :class="{
      'building-details--compact gap-0 [&>div]:gap-0 [&_.detail-section]:gap-0 [&_.detail-section]:rounded-none [&_.detail-section]:border-0 [&_.detail-section]:border-b [&_.detail-section]:border-muted [&_.detail-section]:bg-transparent [&_.detail-section]:px-0 [&_.detail-section]:py-5 [&_.detail-section]:text-[var(--ui-text)] [&_.detail-section]:shadow-none [&_.detail-section_h2]:mb-2 [&_.detail-section_h2]:text-[0.95rem] [&_.detail-section_h2]:font-bold [&_.detail-section_h2]:text-highlighted [&_.detail-row]:grid [&_.detail-row]:grid-cols-[minmax(0,9.5rem)_minmax(0,1fr)] [&_.detail-row]:items-start [&_.detail-row]:gap-3 [&_.detail-row]:py-2.5 [&_.detail-label]:leading-[1.35rem] [&_.detail-label]:text-muted [&_.detail-label]:[overflow-wrap:anywhere] [&_.detail-link]:rounded-none [&_.detail-link]:border-0 [&_.detail-link]:p-0 [&_.detail-link]:text-highlighted [&_.detail-link]:underline [&_.detail-link]:decoration-[var(--ui-text-dimmed)] [&_.detail-link]:underline-offset-[0.2rem] [&_.detail-link:hover]:shadow-none [&_.detail-link:hover]:decoration-current [&_.detail-link:focus-visible]:shadow-none [&_.detail-link:focus-visible]:decoration-current': compact,
      'building-details--bento col-span-full columns-1 gap-4 lg:columns-2 [&>div]:contents [&_.detail-section]:mb-4 [&_.detail-section]:break-inside-avoid [&_.detail-section]:gap-5 [&_.detail-section]:rounded-2xl [&_.detail-section]:border [&_.detail-section]:border-gray-300 [&_.detail-section]:bg-default [&_.detail-section]:p-5 [&_.detail-section]:shadow-md [&_.detail-section_h2]:mb-0 [&_.detail-section_h2]:text-[1.2rem]': bento,
    }"
  >
    <div
      :class="
        compact
          ? 'flex flex-col gap-0'
          : bento
            ? 'contents'
            : 'grid grid-cols-1 gap-4 md:grid-cols-2'
      "
    >
      <section
        class="flex flex-col gap-5 rounded-xl border border-gray-300 bg-white p-4 shadow-md"
        :class="compact
          ? 'gap-0 rounded-none border-0 border-b border-muted bg-transparent px-0 py-5 text-[var(--ui-text)] shadow-none'
          : bento
            ? 'mb-4 break-inside-avoid rounded-2xl bg-default p-5 shadow-md'
            : ''"
        :aria-labelledby="`${headingPrefix}-location`"
      >
        <h2 class="font-sans text-xl font-[650] text-[#22374b]" :class="compact ? 'mb-2 text-[0.95rem] font-bold text-highlighted' : bento ? 'mb-0 text-[1.2rem]' : ''" :id="`${headingPrefix}-location`">Adressen und Flurstücke</h2>

         <div v-if="details.propertyNumber" class="flex flex-col gap-2 py-1.5" :class="compact ? 'grid grid-cols-[minmax(0,9.5rem)_minmax(0,1fr)] items-start gap-3 py-2.5' : ''">
           <p class="text-[0.8125rem] font-semibold text-gray-500" :class="compact ? 'leading-[1.35rem] text-muted [overflow-wrap:anywhere]' : ''">Historische Besitznummer</p>
          <p class="font-semibold">{{ details.propertyNumber }}</p>
        </div>

         <div v-if="details.district" class="flex flex-col gap-2 py-1.5" :class="compact ? 'grid grid-cols-[minmax(0,9.5rem)_minmax(0,1fr)] items-start gap-3 py-2.5' : ''">
           <p class="text-[0.8125rem] font-semibold text-gray-500" :class="compact ? 'leading-[1.35rem] text-muted [overflow-wrap:anywhere]' : ''">Distrikt</p>
          <div>
            <NuxtLink
              :to="`/districts/${details.district.id}`"
              class="inline-flex rounded-lg border border-gray-300 px-[0.55rem] py-[0.35rem] text-sm font-semibold text-[#22374b] transition-colors duration-150 hover:border-[#d9a441] hover:text-[#172a3a] hover:shadow-[0_4px_12px_rgb(15_23_42_/_0.12)] focus-visible:border-[#d9a441] focus-visible:text-[#172a3a] focus-visible:shadow-[0_4px_12px_rgb(15_23_42_/_0.12)] focus-visible:outline-none"
              :class="compact ? 'rounded-none border-0 p-0 text-highlighted underline decoration-[var(--ui-text-dimmed)] underline-offset-[0.2rem] hover:shadow-none hover:decoration-current focus-visible:shadow-none focus-visible:decoration-current' : ''"
            >
              {{ details.district.name }}
            </NuxtLink>
          </div>
        </div>

         <div v-if="details.quarter" class="flex flex-col gap-2 py-1.5" :class="compact ? 'grid grid-cols-[minmax(0,9.5rem)_minmax(0,1fr)] items-start gap-3 py-2.5' : ''">
           <p class="text-[0.8125rem] font-semibold text-gray-500" :class="compact ? 'leading-[1.35rem] text-muted [overflow-wrap:anywhere]' : ''">Viertel</p>
          <div>
            <NuxtLink
              :to="`/quarters/${details.quarter.id}`"
              class="inline-flex rounded-lg border border-gray-300 px-[0.55rem] py-[0.35rem] text-sm font-semibold text-[#22374b] transition-colors duration-150 hover:border-[#d9a441] hover:text-[#172a3a] hover:shadow-[0_4px_12px_rgb(15_23_42_/_0.12)] focus-visible:border-[#d9a441] focus-visible:text-[#172a3a] focus-visible:shadow-[0_4px_12px_rgb(15_23_42_/_0.12)] focus-visible:outline-none"
              :class="compact ? 'rounded-none border-0 p-0 text-highlighted underline decoration-[var(--ui-text-dimmed)] underline-offset-[0.2rem] hover:shadow-none hover:decoration-current focus-visible:shadow-none focus-visible:decoration-current' : ''"
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

  </div>
</template>
