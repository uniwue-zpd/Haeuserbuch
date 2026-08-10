<script setup lang="ts">
const quarterStore = useQuarterStore();

const { data: quarters } = useAsyncData("quarters", () =>
  quarterStore.fetchQuarters(),
);

useHead(() => ({
  title: "Viertel - Verzeichnis der Viertel",
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black">Viertel</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <Panel class="rounded-md">
          <template #header>
            <h1 class="text-base font-bold text-black">Hinweise</h1>
          </template>
          <div class="description">
            <div>Viertel sind Einheiten, die ...</div>
            <div>
              Beim Klicken auf das jeweilige Viertel gelangen Sie auf die
              Übersichtsseite, wo Sie die weiterführenden Informationen finden.
            </div>
          </div>
        </Panel>
        <div
          v-for="quarter in quarters"
          class="rounded-md bg-gray-100 p-3 shadow-md transition-transform duration-300 hover:translate-x-2 hover:shadow-lg"
        >
          <NuxtLink :to="`/quarters/${quarter.id}`" class="no-underline">
            <h2 class="text-xl font-bold text-black">{{ quarter.name }}</h2>
          </NuxtLink>
        </div>
      </div>
    </template>
  </Card>
</template>
