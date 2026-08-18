<script setup lang="ts">
const districtStore = useDistrictStore();

const { data: districts } = useAsyncData("districts", () =>
  districtStore.fetchDistricts(),
);

useHead(() => ({
  title: "Distrikte - Distriktverzeichnis",
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-highlighted">Distrikte</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <Panel class="rounded-md">
          <template #header>
            <h1 class="text-base font-bold text-highlighted">Hinweise</h1>
          </template>
          <div class="description">
            <div>Distrikte sind Einheiten, die ...</div>
            <div>
              Beim Klicken auf den jeweiligen Distrikt gelangen Sie auf die
              Übersichtsseite, wo Sie die weiterführenden Informationen finden.
            </div>
          </div>
        </Panel>
        <div
          v-for="district in districts"
          class="rounded-md border border-default bg-elevated p-3 shadow-md transition-transform duration-300 hover:translate-x-2 hover:shadow-lg"
        >
          <NuxtLink :to="`/districts/${district.id}`" class="no-underline">
            <h2 class="text-xl font-bold text-highlighted">{{ district.name }}</h2>
          </NuxtLink>
        </div>
      </div>
    </template>
  </Card>
</template>
