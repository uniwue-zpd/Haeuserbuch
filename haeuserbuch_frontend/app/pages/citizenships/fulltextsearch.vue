<script setup lang="ts">
const citizenshipStore = useCitizenshipStore();

const query = ref("");

const pageOptions = computed(() =>
    Array.from({ length: data.value?.totalPages ?? 0 }, (_, index) => ({
      label: `${index + 1}`,
      value: index
    }))
);

const searchParams = ref<SearchCitizenshipFullText>({
  query: "",
  page: 0,
  size: 10
});

const { data, refresh, pending, error } = await useAsyncData<Page<CitizenshipFullTextResult>>(
    "citizenship-fulltext-search",
    () => citizenshipStore.searchFullText(searchParams.value),
    { immediate: false }
);

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
    size: 10
  };
  refresh();
}, 1000);

watch(query, () => {
  debouncedSearch();
});
</script>

<template>
  <div class="flex flex-col gap-4 rounded-lg shadow-lg p-4 border-2 border-gray-200">
    <h1 class="text-3xl font-bold montserrat-headline">Volltextsuche in Bürgermatrikeln</h1>
    <div class="rounded-md border border-gray-200 p-3 shadow-md">
      <div class="flex items-center gap-2">
        <Icon
            name="material-symbols-info-outline-rounded"
            class="text-lg"
        />
        <span class="font-semibold roboto-plain">Hinweise zur Suche</span>
      </div>
      <ul class="mt-2 list-disc list-inside text-sm roboto-plain">
        <li>
          Mehrere Begriffe werden standardmäßig mit <code class="font-mono font-bold">AND</code> verknüpft.
        </li>
        <li>
          Verwenden Sie <code class="font-mono font-bold">OR</code> für alternative Suchbegriffe.
          Beispiel: <code class="font-mono font-bold">Müller OR Schneider</code>
        </li>
        <li>
          Schließen Sie Begriffe mit <code class="font-mono font-bold">-</code> aus.
          Beispiel: <code class="font-mono font-bold">Müller -Johann</code>
        </li>
        <li>
          Verwenden Sie Anführungszeichen für feste Wortgruppen.
          Beispiel: <code class="font-mono font-bold">"Hans Müller"</code>
        </li>
        <li>
          Die gefundenen Stellen werden im Eintragstext hervorgehoben.
        </li>
      </ul>
    </div>
    <div class="flex flex-col md:flex-row gap-3 items-center">
      <div class="flex flex-col gap-2 flex-1">
        <label for="search" class="text-base font-semibold roboto-plain">
          Suchbegriff
        </label>
        <InputText
            id="search"
            v-model="query"
            placeholder="Suche..."
            class="w-full h-9"
        />
        <small class="text-sm roboto-plain">
          Geben Sie den Suchbegriff ein, um die Volltextsuche zu starten
        </small>
      </div>
      <button
          @click="query = ''"
          class="flex h-9 items-center gap-2 rounded-lg border border-gray-200 px-3 shadow-sm transition-all duration-200 hover:border-gray-300 hover:shadow-md cursor-pointer text-sm roboto-plain"
      >
        <i class="pi pi-times text-sm leading-none"/>
        <span>Zurücksetzen</span>
      </button>
    </div>
    <hr class="border-2 border-gray-200"/>
    <div v-if="pending" class="flex flex-row gap-3 items-center">
      <i class="pi pi-spin pi-spinner text-lg text-gray-600"/>
      <span class="text-sm roboto-plain">Suche läuft...</span>
    </div>
    <div
        v-else-if="data && data.totalElements > 0"
        class="flex flex-col gap-3"
    >
      <div class="flex flex-row items-center gap-2 text-base roboto-plain font-medium">
        <span>Treffer:</span>
        <span :class="[
            'flex items-center justify-center rounded-full border-2 border-green-500',
            data.totalElements > 999 ? 'h-13 min-w-13' : 'h-10 w-10'
            ]"
        >
          {{ data.totalElements }}
        </span>
      </div>
      <div
          v-if="data.totalPages > 1"
          class="flex flex-row justify-center items-center gap-2 mt-3"
      >
        <button
            @click="changePage(0)"
            :disabled="data.first"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm roboto-plain shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-double-left"/>
        </button>
        <button
            @click="changePage(data.number - 1)"
            :disabled="data.first"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm roboto-plain shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-left"/>
        </button>
        <div class="flex items-center gap-2 text-sm roboto-plain">
          <Select
              :model-value="data.number"
              :options="pageOptions"
              option-label="label"
              option-value="value"
              class="h-9"
              @update:modelValue="changePage($event)"
          />
          <span>von {{ data.totalPages }}</span>
        </div>
        <button
            @click="changePage(data.number + 1)"
            :disabled="data.last"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm roboto-plain shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-right"/>
        </button>
        <button
            @click="changePage(data.totalPages - 1)"
            :disabled="data.last"
            class="flex h-9 items-center justify-center rounded-lg border border-gray-200 px-2 text-sm roboto-plain shadow-sm transition-all hover:border-gray-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        >
          <i class="pi pi-angle-double-right"/>
        </button>
      </div>
      <div
          v-for="result in data.content"
          :key="result.id"
          class="rounded-lg border border-gray-200 p-3 shadow-md"
      >
        <div class="flex flex-col gap-2">
          <NuxtLink
              :to="`/citizenships/${result.id}`"
              class="text-lg group relative w-fit font-bold montserrat-headline"
          >
            {{ result.signature || 'Signatur fehlt' }}
            <span class="absolute bottom-0 left-0 h-px w-0 bg-current transition-all duration-300 group-hover:w-full"/>
          </NuxtLink>
          <div class="text-sm roboto-plain">Referenz Meyer-Erlach: {{ result.refNumber }}</div>
          <div
              class="border-l-4 border-gray-300 px-4 py-1 roboto-plain leading-6 text-justify"
              v-html="result.queryResult"
          />
        </div>
      </div>
    </div>
    <div
        v-else-if="data && data.totalElements === 0"
        class="text-sm roboto-plain"
    >
      Keine Ergebnisse gefunden.
    </div>
    <div
        v-else-if="error"
        class="flex flex-row gap-2 roboto-plain"
    >
      <span>Fehler:</span>
      <code class="text-red-600">{{ error.status }} {{ error.statusText }}</code>
    </div>
  </div>
</template>
