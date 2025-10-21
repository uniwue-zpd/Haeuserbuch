<script setup lang="ts">
const source_store = useSourceStore();
const sources = computed(() => source_store.sources.sort((a, b) => a.title.localeCompare(b.title)));

useHead(() => ({
  title: 'Quellenverzeichnis'
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black montserrat-headline pb-4">Quellenverzeichnis</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-4">
        <div class="text-base text-black roboto-plain">
          Hier finden Sie eine Übersicht aller im Häuserbuch verwendeten Primär- und Sekundärquellen.
        </div>
        <div class="flex flex-col gap-4 md:grid md:grid-cols-3 md:gap-5">
          <NuxtLink
              v-for="source in sources"
              class="rounded-md p-4 bg-[#F1F2F2] shadow-md hover:shadow-lg transition-shadow duration-200"
              :to="`/sources/${source.id}`"
          >
            <div class="flex flex-col items-center">
              <div class="montserrat-headline font-bold text-lg text-black line-clamp-2 text-center">{{ source.title }}</div>
              <div v-show="source.type" class="roboto-plain text-base text-gray-500 font-semibold">{{ source.type }}</div>
            </div>
          </NuxtLink>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
