<script setup lang="ts">
const citizenshipStore = useCitizenshipStore();

const query = ref("");

const pageOptions = computed(() =>
  Array.from({ length: data.value?.totalPages ?? 0 }, (_, index) => ({
    label: `${index + 1}`,
    value: index,
  })),
);

const searchParams = ref<SearchCitizenshipFullText>({
  query: "",
  page: 0,
  size: 10,
  exact: false,
});

const { data, refresh, pending, error } = await useAsyncData<
  Page<CitizenshipFullTextResult>
>(
  "citizenship-fulltext-search",
  () => citizenshipStore.searchFullText(searchParams.value),
  { immediate: false },
);

useHead(() => ({
  title: "Volltextsuche - Bürgermatrikel",
}));

const changePage = (page: number) => {
  if (!data.value) return;
  if (page < 0 || page >= data.value.totalPages) return;
  searchParams.value.page = page;
  refresh();
};

const debouncedSearch = debounce(() => {
  const trimmedQuery = query.value.trim();
  if (!trimmedQuery) {
    data.value = undefined;
    return;
  }
  searchParams.value = {
    query: trimmedQuery,
    page: 0,
    size: 10,
    exact: searchParams.value.exact,
  };
  refresh();
}, 1000);

watch([query, () => searchParams.value.exact], () => {
  debouncedSearch();
});
</script>

<template>
  <div class="citizenship-search flex min-h-full flex-col gap-8">
    <header class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <h1 class="text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl">
          Volltextsuche
        </h1>
      </div>
      <UButton to="/citizenships" color="neutral" variant="outline" icon="i-lucide-arrow-left" label="Zum Verzeichnis" />
    </header>

    <div class="grid grid-cols-1 gap-4 lg:grid-cols-12 lg:items-start">
      <section class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-8">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="text-xl font-semibold leading-tight text-highlighted">Im Eintragstext suchen</h2>
            <p class="mt-1 text-sm text-muted">Die Suche startet automatisch nach kurzer Eingabepause.</p>
          </div>
          <UButton color="neutral" variant="ghost" icon="i-lucide-x" label="Zurücksetzen" @click="query = ''" />
        </div>
        <div class="flex flex-col gap-2">
          <label for="search" class="text-sm font-semibold text-muted">Suchbegriff</label>
          <UInput id="search" v-model="query" placeholder="z. B. Müller OR Schneider" icon="i-lucide-search" size="lg" />
        </div>
        <label class="flex items-center gap-3 text-sm font-medium text-highlighted">
          <UCheckbox v-model="searchParams.exact" />
          Exakte Suche
        </label>
      </section>

      <section class="flex min-w-0 flex-col gap-4 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-4">
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Suchsyntax</h2>
        <dl class="flex flex-col">
          <div class="content-table-row !grid-cols-1 !gap-1 sm:!grid-cols-1">
            <dt class="content-table-label">AND</dt>
            <dd class="text-sm text-highlighted">Mehrere Begriffe werden gemeinsam gesucht.</dd>
          </div>
          <div class="content-table-row !grid-cols-1 !gap-1 sm:!grid-cols-1">
            <dt class="content-table-label">OR</dt>
            <dd class="text-sm text-highlighted">Alternative Begriffe, z. B. <code>Müller OR Schneider</code>.</dd>
          </div>
          <div class="content-table-row !grid-cols-1 !gap-1 sm:!grid-cols-1">
            <dt class="content-table-label">-</dt>
            <dd class="text-sm text-highlighted">Begriffe ausschließen, z. B. <code>Müller -Johann</code>.</dd>
          </div>
          <div class="content-table-row !grid-cols-1 !gap-1 sm:!grid-cols-1">
            <dt class="content-table-label">&quot;…&quot;</dt>
            <dd class="text-sm text-highlighted">Feste Wortgruppen suchen, z. B. <code>&quot;Hans Müller&quot;</code>.</dd>
          </div>
        </dl>
      </section>
    </div>

    <section class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md">
      <div class="flex flex-col gap-4 border-b border-muted pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">Treffer</h2>
          <span v-if="data" class="rounded-full border border-accented px-2.5 py-1 text-sm font-semibold text-highlighted">
            {{ data.totalElements }}
          </span>
        </div>
        <div v-if="data && data.totalPages > 1" class="flex items-center gap-2">
          <UButton color="neutral" variant="outline" icon="i-lucide-chevrons-left" aria-label="Erste Seite" :disabled="data.first" @click="changePage(0)" />
          <UButton color="neutral" variant="outline" icon="i-lucide-chevron-left" aria-label="Vorherige Seite" :disabled="data.first" @click="changePage(data.number - 1)" />
          <USelect :model-value="data.number" :items="pageOptions" value-key="value" class="w-20" @update:model-value="changePage" />
          <span class="text-sm text-muted">von {{ data.totalPages }}</span>
          <UButton color="neutral" variant="outline" icon="i-lucide-chevron-right" aria-label="Nächste Seite" :disabled="data.last" @click="changePage(data.number + 1)" />
          <UButton color="neutral" variant="outline" icon="i-lucide-chevrons-right" aria-label="Letzte Seite" :disabled="data.last" @click="changePage(data.totalPages - 1)" />
        </div>
      </div>

      <div v-if="pending" class="flex items-center gap-3 py-8 text-sm text-muted">
        <UIcon name="i-lucide-loader-circle" class="size-5 animate-spin" />
        Suche läuft …
      </div>
      <div v-else-if="data && data.totalElements > 0" class="flex flex-col gap-3">
        <article v-for="result in data.content" :key="result.id" class="overflow-hidden rounded-2xl border border-default bg-elevated/40">
          <div class="grid grid-cols-1 gap-x-4 gap-y-1 border-b border-muted px-4 py-3 sm:grid-cols-[minmax(10rem,12rem)_minmax(0,1fr)]">
            <p class="content-table-label">Eintrag</p>
            <NuxtLink :to="`/citizenships/${result.id}`" class="font-semibold text-highlighted underline decoration-muted underline-offset-4 hover:decoration-current">
              {{ result.signature || "Signatur fehlt" }}
            </NuxtLink>
            <p class="content-table-label">Referenz Meyer-Erlach</p>
            <p class="text-sm text-highlighted">{{ result.refNumber || "unbekannt" }}</p>
          </div>
          <div class="px-4 py-4 leading-7 text-highlighted" v-html="result.queryResult" />
        </article>
      </div>
      <p v-else-if="data && data.totalElements === 0" class="py-4 text-sm text-muted">Keine Ergebnisse gefunden.</p>
      <p v-else-if="error" class="py-4 text-sm text-red-600">Fehler: {{ error.status }} {{ error.statusText }}</p>
      <p v-else class="py-4 text-sm text-muted">Geben Sie einen Suchbegriff ein, um Einträge zu durchsuchen.</p>
    </section>
  </div>
</template>
