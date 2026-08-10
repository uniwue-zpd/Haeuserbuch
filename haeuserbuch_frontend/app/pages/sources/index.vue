<script setup lang="ts">
const sourceStore = useSourceStore();
const query = ref("");

const {
  data: sources,
  pending: loadingSources,
  error: sourcesError,
} = await useAsyncData("sources", () => sourceStore.fetchSources());

const filteredSources = computed(() => {
  const searchTerm = query.value.trim().toLocaleLowerCase();

  if (!searchTerm) return sources.value ?? [];

  return (sources.value ?? []).filter((source) => {
    const searchableText = [
      source.title,
      source.type,
      source.signature,
      ...(source.authors ?? []),
    ]
      .filter(Boolean)
      .join(" ")
      .toLocaleLowerCase();

    return searchableText.includes(searchTerm);
  });
});

useHead(() => ({
  title: "Quellenverzeichnis",
}));
</script>

<template>
  <main class="flex min-h-full flex-col gap-8">
    <header
      class="flex flex-col gap-6 pb-8 sm:flex-row sm:items-end sm:justify-between"
    >
      <div class="max-w-3xl">
        <h1
          class="mt-3 text-4xl font-bold leading-none tracking-tighter text-highlighted sm:text-6xl"
        >
          Quellenverzeichnis
        </h1>
      </div>
    </header>

    <section
      class="overflow-hidden rounded-2xl border border-default bg-default shadow-sm"
      aria-labelledby="source-list-heading"
    >
      <div
        class="flex flex-col gap-4 border-b border-default p-5 sm:flex-row sm:items-center sm:justify-between sm:p-6"
      >
        <div>
          <h2
            id="source-list-heading"
            class="text-xl font-semibold text-highlighted"
          >
            Alle Quellen
          </h2>
          <p class="mt-1 text-sm text-muted">
            {{ filteredSources.length }}
            {{ filteredSources.length === 1 ? "Eintrag" : "Einträge" }}
            <span v-if="query.trim()"> für „{{ query }}“</span>
          </p>
        </div>

        <UInput
          v-model="query"
          icon="i-lucide-search"
          placeholder="Quellen durchsuchen..."
          aria-label="Quellen durchsuchen"
          class="w-full sm:max-w-xs"
        />
      </div>

      <div v-if="loadingSources" class="divide-y divide-default">
        <div
          v-for="index in 5"
          :key="index"
          class="flex items-center gap-4 px-5 py-5 sm:px-6"
        >
          <div class="size-10 animate-pulse rounded-xl bg-elevated" />
          <div class="flex-1 space-y-2">
            <div class="h-5 w-2/3 animate-pulse rounded bg-elevated" />
            <div class="h-4 w-1/3 animate-pulse rounded bg-elevated" />
          </div>
        </div>
      </div>

      <UAlert
        v-else-if="sourcesError"
        color="error"
        variant="soft"
        icon="i-lucide-circle-alert"
        title="Quellen konnten nicht geladen werden"
        description="Bitte versuchen Sie es später erneut."
        class="m-5 sm:m-6"
      />

      <div
        v-else-if="filteredSources.length"
        class="divide-y divide-default"
      >
        <NuxtLink
          v-for="source in filteredSources"
          :key="source.id"
          :to="`/sources/${source.id}`"
          class="group grid gap-4 px-5 py-5 transition-colors hover:bg-elevated/50 focus-visible:bg-elevated/70 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary sm:grid-cols-[auto_minmax(0,1fr)_auto] sm:items-center sm:px-6"
        >
          <span
            class="flex size-10 items-center justify-center rounded-xl border border-default text-muted transition-colors group-hover:border-primary/40 group-hover:text-primary"
          >
            <UIcon name="i-lucide-library" class="size-5" />
          </span>

          <span class="min-w-0">
            <span
              class="block truncate text-base font-semibold text-highlighted"
            >
              {{ source.title || "Ohne Titel" }}
            </span>
            <span
              v-if="source.type || source.signature || source.authors?.length"
              class="mt-1 flex flex-wrap items-center gap-x-3 gap-y-1 text-sm text-muted"
            >
              <span v-if="source.type">{{ source.type }}</span>
              <span v-if="source.signature" class="text-dimmed">
                {{ source.signature }}
              </span>
              <span v-if="source.authors?.length" class="truncate">
                {{ source.authors.join(", ") }}
              </span>
            </span>
          </span>

          <UIcon
            name="i-lucide-arrow-up-right"
            class="size-5 text-dimmed transition-transform group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary"
          />
        </NuxtLink>
      </div>

      <div
        v-else
        class="flex flex-col items-center justify-center px-6 py-16 text-center"
      >
        <UIcon name="i-lucide-search-x" class="size-8 text-dimmed" />
        <h3 class="mt-4 font-semibold text-highlighted">
          Keine Quellen gefunden
        </h3>
        <p class="mt-1 text-sm text-muted">
          Prüfen Sie den Suchbegriff oder versuchen Sie eine andere Suche.
        </p>
      </div>
    </section>
  </main>
</template>
