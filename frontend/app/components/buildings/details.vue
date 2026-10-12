<script setup lang="ts">
import { computed, useId } from "vue";
import type { BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";
import { title_shortener } from "~/utils/helpers";
import FileList from "~/components/files/FileList.vue";

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

const ui = computed(() => {
  const { compact, bento } = props;
  return {
    root: bento
      ? "col-span-full columns-1 gap-4 pt-4 lg:columns-2"
      : compact
        ? "flex flex-col pt-4"
        : "flex flex-col gap-4 pt-4",
    group: bento
      ? "contents"
      : compact
        ? "flex flex-col"
        : "grid grid-cols-1 gap-4 md:grid-cols-2",
    section: bento
      ? "mb-4 flex break-inside-avoid flex-col gap-5 rounded-md border border-default bg-default p-5 shadow-md"
      : compact
        ? "flex flex-col border-b border-muted px-4 py-5 text-default"
        : "flex flex-col gap-5 rounded-md border border-default bg-default p-4 shadow-md",
    heading:
      compact && !bento
        ? "mb-2 font-sans text-base font-bold text-highlighted"
        : "font-sans text-xl font-semibold text-highlighted",
    row: compact
      ? "grid grid-cols-[minmax(0,9.5rem)_minmax(0,1fr)] items-start gap-3 py-2.5"
      : "flex flex-col gap-2 py-1.5",
    label: compact
      ? "text-sm font-semibold leading-5 text-muted wrap-anywhere"
      : "text-sm font-semibold text-muted",
    link: compact
      ? "inline-flex text-sm font-semibold text-highlighted underline decoration-muted underline-offset-4 transition-colors duration-150 hover:text-primary hover:decoration-current focus-visible:text-primary focus-visible:decoration-current focus-visible:outline-none"
      : "inline-flex rounded-md border border-default px-2 py-1 text-sm font-semibold text-highlighted transition-colors duration-150 hover:border-accented hover:text-primary hover:shadow-md focus-visible:border-accented focus-visible:text-primary focus-visible:shadow-md focus-visible:outline-none",
  };
});

</script>

<template>
  <div :class="ui.root">
    <div :class="ui.group">
      <section
        :class="ui.section"
        :aria-labelledby="`${headingPrefix}-location`"
      >
        <h2 :id="`${headingPrefix}-location`" :class="ui.heading">
          Adressen und Flurstücke
        </h2>

        <div v-if="details.propertyNumber" :class="ui.row">
          <p :class="ui.label">Historische Besitznummer</p>
          <p class="font-semibold">{{ details.propertyNumber }}</p>
        </div>

        <div v-if="details.district" :class="ui.row">
          <p :class="ui.label">Distrikt</p>
          <div>
            <NuxtLink
              :to="`/districts/${details.district.id}`"
              :class="ui.link"
            >
              {{ details.district.name }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="details.quarter" :class="ui.row">
          <p :class="ui.label">Viertel</p>
          <div>
            <NuxtLink
              :to="`/quarters/${details.quarter.id}`"
              :class="ui.link"
            >
              {{ details.quarter.name }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="addressesOld.length" :class="ui.row">
          <p :class="ui.label">Adressen um 1869</p>
          <div class="flex flex-wrap gap-2">
            <NuxtLink
              v-for="address in addressesOld"
              :key="
                address.id ??
                `${address.street?.id}-${address.houseNumber}-${address.fromDate}`
              "
              :to="`/streets/${address.street?.id}`"
              :class="ui.link"
            >
              {{ address.street?.name }} {{ address.houseNumber }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="addressesCurrent.length" :class="ui.row">
          <p :class="ui.label">Aktuelle Adressen</p>
          <div class="flex flex-wrap gap-2">
            <NuxtLink
              v-for="address in addressesCurrent"
              :key="
                address.id ??
                `${address.street?.id}-${address.houseNumber}-${address.fromDate}`
              "
              :to="`/streets/${address.street?.id}`"
              :class="ui.link"
            >
              {{ address.street?.name }} {{ address.houseNumber }}
            </NuxtLink>
          </div>
        </div>

        <div v-if="details.parcelNumber" :class="ui.row">
          <p :class="ui.label">Flurstücksnummer</p>
          <p class="font-semibold">{{ details.parcelNumber }}</p>
        </div>

        <div v-if="details.parcelNumberCounter" :class="ui.row">
          <p :class="ui.label">Flurstücksnummerzähler</p>
          <p class="font-semibold">{{ details.parcelNumberCounter }}</p>
        </div>
      </section>

      <section
        :class="ui.section"
        :aria-labelledby="`${headingPrefix}-description`"
      >
        <h2 :id="`${headingPrefix}-description`" :class="ui.heading">Objektbeschreibung</h2>

        <div v-if="details.year" :class="ui.row">
          <p :class="ui.label">Jahr</p>
          <p class="font-semibold">{{ details.year }}</p>
        </div>

        <div v-if="details.names.length" :class="ui.row">
          <p :class="ui.label">Gebäudenamen</p>
          <ul class="list-inside list-disc">
            <li
              v-for="(name, index) in details.names"
              :key="`${name.name}-${index}`"
              class="font-semibold"
            >
              {{ name.name }}
              <NuxtLink
                v-if="name.source?.id"
                :to="`/quellen/${name.source.id}`"
                class="text-primary hover:text-highlighted"
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

        <div v-if="details.object" :class="ui.row">
          <p :class="ui.label">Objekt</p>
          <p class="font-semibold">{{ details.object }}</p>
        </div>

        <div v-if="details.partType" :class="ui.row">
          <p :class="ui.label">Bauteil</p>
          <p class="font-semibold">{{ details.partType }}</p>
        </div>
      </section>
    </div>

    <section
      v-if="details.sources.length || details.literature.length"
      :class="ui.section"
      :aria-labelledby="`${headingPrefix}-sources`"
    >
      <h2 :id="`${headingPrefix}-sources`" :class="ui.heading">Quellen- und Literaturangaben</h2>

      <div v-if="details.sources.length" :class="ui.row">
        <p :class="ui.label">Quellen</p>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
            v-for="source in details.sources"
            :key="source.id ?? source.title ?? 'source'"
            :to="`/quellen/${source.id}`"
            :class="ui.link"
          >
            {{
              source.title
                ? title_shortener(source.title, 4)
                : "Unbenannte Quelle"
            }}
          </NuxtLink>
        </div>
      </div>

      <div v-if="details.literature.length" :class="ui.row">
        <p :class="ui.label">Literatur</p>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
            v-for="source in details.literature"
            :key="source.id ?? source.title ?? 'literature'"
            :to="`/quellen/${source.id}`"
            :class="ui.link"
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
      :class="ui.section"
      :aria-labelledby="`${headingPrefix}-notes`"
    >
      <h2 :id="`${headingPrefix}-notes`" :class="ui.heading">Notizen und Anmerkungen</h2>
      <div v-if="details.generalNotes" :class="ui.row">
        <p :class="ui.label">Anmerkungen</p>
        <p class="whitespace-pre-wrap">{{ details.generalNotes }}</p>
      </div>
      <div v-if="details.internalNotes" :class="ui.row">
        <p :class="ui.label">Notizen</p>
        <p class="whitespace-pre-wrap">{{ details.internalNotes }}</p>
      </div>
    </section>

    <section
      :class="ui.section"
      :aria-labelledby="`${headingPrefix}-relations`"
    >
      <h2 :id="`${headingPrefix}-relations`" :class="ui.heading">
        Beziehungen zu anderen Entitäten
      </h2>
      <div :class="ui.row">
        <p :class="ui.label">Personen</p>
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
          class="rounded-md bg-error/10 p-3 text-sm text-error"
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
            :to="`/personen/${person.id}`"
            :class="ui.link"
          >
            {{ person.fullName || `Person mit ID ${person.id}` }}
          </NuxtLink>
        </div>
        <p v-else class="text-sm text-muted">
          Bisher keine Relationen gefunden
        </p>
      </div>
    </section>

    <section
      v-if="details.files.length"
      :class="ui.section"
      :aria-labelledby="`${headingPrefix}-files`"
    >
      <h2 :id="`${headingPrefix}-files`" :class="ui.heading">Dateien</h2>
      <FileList :files="details.files" compact />

    </section>

  </div>
</template>
