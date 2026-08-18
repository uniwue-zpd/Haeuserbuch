<script setup lang="ts">
import {
  SEARCH_ENTITY_TYPES,
  SEARCH_FIELDS,
  parseSearchEntityTypes,
  parseSearchFields,
  searchEntityConfig,
  searchFieldConfig,
  type SearchEntityType,
  type SearchField,
} from "~/utils/globalSearch";

const route = useRoute();
const router = useRouter();
const searchStore = useGlobalSearchStore();

const routeQuery = computed(() => String(Array.isArray(route.query.q) ? route.query.q[0] ?? "" : route.query.q ?? ""));
const inputQuery = ref(routeQuery.value);
const selectedFields = computed(() => parseSearchFields(route.query.fields));
const selectedTypes = computed(() => parseSearchEntityTypes(route.query.types));
const exactFullText = computed(() => route.query.exact === "true" && selectedFields.value.includes("FULL_TEXT"));
const currentPage = computed(() => {
  const parsed = Number.parseInt(String(route.query.page ?? "1"), 10);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : 1;
});
const canSearch = computed(() => routeQuery.value.trim().length >= 2);

const { data, pending, error } = await useAsyncData(
  "global-research-search",
  () => {
    if (!canSearch.value) return Promise.resolve(null);
    return searchStore.search({
      query: routeQuery.value.trim(),
      fields: selectedFields.value,
      types: selectedTypes.value,
      exactFullText: exactFullText.value,
      page: currentPage.value - 1,
      size: 20,
      includeFacets: true,
    });
  },
  { watch: [() => route.fullPath] },
);

let queryTimer: ReturnType<typeof setTimeout> | undefined;
watch(inputQuery, (value) => {
  clearTimeout(queryTimer);
  queryTimer = setTimeout(() => {
    const normalized = value.trim();
    if (normalized === routeQuery.value.trim()) return;
    replaceSearchQuery({ q: normalized || undefined, page: undefined });
  }, 350);
});
watch(routeQuery, (value) => {
  if (value !== inputQuery.value) inputQuery.value = value;
});
onBeforeUnmount(() => clearTimeout(queryTimer));

useHead(() => ({ title: routeQuery.value ? `Suche nach ${routeQuery.value}` : "Suche" }));

function serializedSelection<T extends string>(selection: T[], allValues: readonly T[]) {
  return selection.length === allValues.length ? undefined : selection.join(",").toLowerCase();
}

function replaceSearchQuery(changes: Record<string, string | undefined>) {
  router.replace({ query: { ...route.query, ...changes } });
}

function toggleField(field: SearchField, checked: boolean | "indeterminate") {
  if (checked === "indeterminate") return;
  const next = checked
    ? [...new Set([...selectedFields.value, field])]
    : selectedFields.value.filter((entry) => entry !== field);
  if (!next.length) return;
  replaceSearchQuery({
    fields: serializedSelection(next, SEARCH_FIELDS),
    exact: next.includes("FULL_TEXT") && exactFullText.value ? "true" : undefined,
    page: undefined,
  });
}

function toggleType(type: SearchEntityType, checked: boolean | "indeterminate") {
  if (checked === "indeterminate") return;
  const next = checked
    ? [...new Set([...selectedTypes.value, type])]
    : selectedTypes.value.filter((entry) => entry !== type);
  if (!next.length) return;
  replaceSearchQuery({ types: serializedSelection(next, SEARCH_ENTITY_TYPES), page: undefined });
}

function setExact(value: boolean | "indeterminate") {
  if (value === "indeterminate") return;
  replaceSearchQuery({ exact: value ? "true" : undefined, page: undefined });
}

function setPage(page: number) {
  router.push({ query: { ...route.query, page: page > 1 ? String(page) : undefined } });
  if (import.meta.client) window.scrollTo({ top: 0, behavior: "smooth" });
}

function clearSearch() {
  inputQuery.value = "";
  router.replace({ query: {} });
}
</script>

<template>
  <div class="flex min-h-full flex-col gap-8">
    <header class="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
      <div class="max-w-3xl">
        <h1 class="mt-2 text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">Suche</h1>
        <p class="mt-4 max-w-2xl text-base leading-7 text-muted">
          Durchsuchen Sie Namen, Titel, Metadaten und die Volltexte der Bürgermatrikel.
        </p>
      </div>
      <UButton
        to="/buergermatrikel/fulltextsearch"
        color="neutral"
        variant="outline"
        icon="i-lucide-file-search"
        label="Zur reinen Volltextsuche"
      />
    </header>

    <section class="rounded-md border border-default bg-default p-5 shadow-sm sm:p-6" aria-labelledby="global-search-heading">
      <div class="flex flex-col gap-4 sm:flex-row sm:items-end">
        <div class="min-w-0 flex-1">
          <label id="global-search-heading" for="global-search" class="mb-2 block text-sm font-semibold text-highlighted">Suchbegriff</label>
          <UInput
            id="global-search"
            v-model="inputQuery"
            type="search"
            icon="i-lucide-search"
            size="xl"
            placeholder="Mindestens zwei Zeichen eingeben…"
            autocomplete="off"
            class="w-full"
          />
        </div>
        <UButton v-if="inputQuery" color="neutral" variant="ghost" icon="i-lucide-x" label="Zurücksetzen" @click="clearSearch" />
      </div>
      <p class="mt-2 text-sm text-muted">Die Suche ist unabhängig von Groß- und Kleinschreibung.</p>
    </section>

    <div class="grid gap-6 lg:grid-cols-[18rem_minmax(0,1fr)] lg:items-start">
      <aside class="space-y-6 rounded-md border border-default bg-default p-5 shadow-sm lg:sticky lg:top-28" aria-label="Suchfilter">
        <section aria-labelledby="field-filter-heading">
          <h2 id="field-filter-heading" class="font-semibold text-highlighted">Treffer in</h2>
          <div class="mt-3 space-y-3">
            <UCheckbox
              v-for="field in SEARCH_FIELDS"
              :key="field"
              :model-value="selectedFields.includes(field)"
              :disabled="selectedFields.length === 1 && selectedFields.includes(field)"
              :label="searchFieldConfig[field].label"
              :description="`${searchFieldConfig[field].description}${data?.facets ? ` (${data.facets.fields[field] ?? 0})` : ''}`"
              @update:model-value="toggleField(field, $event)"
            />
          </div>
          <div v-if="selectedFields.includes('FULL_TEXT')" class="mt-4 border-t border-default pt-4">
            <UCheckbox
              :model-value="exactFullText"
              label="Exakte Volltextsuche"
              description="Sucht die eingegebene Wortfolge exakt im Bürgerartikel."
              @update:model-value="setExact"
            />
            <details class="mt-3 text-sm text-muted">
              <summary class="cursor-pointer font-medium text-highlighted">Volltext-Suchsyntax</summary>
              <p class="mt-2 leading-6">AND verknüpft Begriffe, OR sucht Alternativen, ein vorangestelltes Minus schließt Begriffe aus und Anführungszeichen suchen Wortgruppen.</p>
            </details>
          </div>
        </section>

        <section class="border-t border-default pt-5" aria-labelledby="type-filter-heading">
          <h2 id="type-filter-heading" class="font-semibold text-highlighted">Datentypen</h2>
          <div class="mt-3 space-y-3">
            <UCheckbox
              v-for="type in SEARCH_ENTITY_TYPES"
              :key="type"
              :model-value="selectedTypes.includes(type)"
              :disabled="selectedTypes.length === 1 && selectedTypes.includes(type)"
              :label="searchEntityConfig[type].pluralLabel"
              :description="data?.facets ? `${data.facets.entityTypes[type] ?? 0} Treffer` : undefined"
              @update:model-value="toggleType(type, $event)"
            />
          </div>
        </section>
      </aside>

      <section class="min-w-0 overflow-hidden rounded-md border border-default bg-default shadow-sm" aria-labelledby="search-results-heading" aria-live="polite">
        <div class="flex flex-col gap-2 border-b border-default p-5 sm:flex-row sm:items-center sm:justify-between sm:p-6">
          <div>
            <h2 id="search-results-heading" class="text-xl font-semibold text-highlighted">Suchergebnisse</h2>
            <p v-if="data" class="mt-1 text-sm text-muted">{{ data.totalElements }} Treffer für „{{ routeQuery }}“</p>
          </div>
          <UIcon v-if="pending" name="i-lucide-loader-circle" class="size-5 animate-spin text-muted" aria-label="Suche läuft" />
        </div>

        <div v-if="!canSearch" class="p-10 text-center">
          <UIcon name="i-lucide-search" class="mx-auto size-9 text-dimmed" />
          <h3 class="mt-4 font-semibold text-highlighted">Forschungsdaten durchsuchen</h3>
          <p class="mt-2 text-sm text-muted">Geben Sie mindestens zwei Zeichen ein, um die Suche zu starten.</p>
        </div>
        <UAlert
          v-else-if="error"
          class="m-5"
          color="error"
          variant="soft"
          icon="i-lucide-circle-alert"
          title="Die Suche konnte nicht ausgeführt werden"
          description="Bitte versuchen Sie es erneut."
        />
        <div v-else-if="pending && !data" class="divide-y divide-default">
          <div v-for="index in 5" :key="index" class="space-y-3 p-5 sm:p-6">
            <div class="h-5 w-2/5 animate-pulse rounded bg-elevated" />
            <div class="h-4 w-3/4 animate-pulse rounded bg-elevated" />
          </div>
        </div>
        <div v-else-if="data?.content.length" class="divide-y divide-default">
          <NuxtLink
            v-for="result in data.content"
            :key="`${result.entityType}-${result.entityId}`"
            :to="searchEntityConfig[result.entityType].to(result.entityId)"
            class="group block p-5 transition-colors hover:bg-elevated/70 focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-primary sm:p-6"
          >
            <div class="flex items-start gap-4">
              <div class="mt-0.5 flex size-10 shrink-0 items-center justify-center rounded-md bg-elevated text-muted group-hover:text-primary">
                <UIcon :name="searchEntityConfig[result.entityType].icon" class="size-5" />
              </div>
              <div class="min-w-0 flex-1">
                <div class="flex flex-wrap items-center gap-2">
                  <h3 class="font-semibold text-highlighted underline decoration-transparent underline-offset-4 group-hover:decoration-current">{{ result.title }}</h3>
                  <span class="rounded-full bg-elevated px-2 py-0.5 text-xs font-medium text-muted">{{ searchEntityConfig[result.entityType].label }}</span>
                </div>
                <p v-if="result.subtitle" class="mt-1 text-sm text-muted">{{ result.subtitle }}</p>
                <div class="mt-3 flex flex-wrap gap-2">
                  <span v-for="field in result.matchedFields" :key="field" class="rounded-full border border-default px-2 py-0.5 text-xs text-muted">{{ searchFieldConfig[field].label }}</span>
                </div>
                <p v-if="result.excerpt" class="search-match-excerpt mt-4 line-clamp-4 text-sm leading-6 text-highlighted" v-html="result.excerpt" />
              </div>
              <UIcon name="i-lucide-arrow-up-right" class="mt-1 size-4 shrink-0 text-dimmed transition group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary" />
            </div>
          </NuxtLink>
        </div>
        <div v-else-if="data" class="p-10 text-center">
          <UIcon name="i-lucide-search-x" class="mx-auto size-9 text-dimmed" />
          <h3 class="mt-4 font-semibold text-highlighted">Keine Treffer gefunden</h3>
          <p class="mt-2 text-sm text-muted">Passen Sie den Suchbegriff oder die ausgewählten Filter an.</p>
        </div>

        <div v-if="data && data.totalPages > 1" class="flex justify-center border-t border-default p-5">
          <UPagination
            :page="currentPage"
            :total="data.totalElements"
            :items-per-page="data.size"
            show-edges
            @update:page="setPage"
          />
        </div>
      </section>
    </div>
  </div>
</template>
