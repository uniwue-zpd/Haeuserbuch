<script setup lang="ts">
const source_store = useSourceStore();

const { data: sources } = useAsyncData('sources', () => source_store.fetchSources());

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
      <div class="flex flex-col gap-2">
        <div
            v-for="source in sources"
            class="bg-gray-200 shadow-md rounded-md p-4 hover:shadow-lg hover:translate-x-2 transition-transform duration-300"
        >
          <NuxtLink :to="`/sources/${ source.id }`" class="flex flex-col gap-2 no-underline">
            <h2 class="montserrat-headline font-bold text-lg text-black line-clamp-1">{{ source.title }}</h2>
            <div class="roboto-plain text-base text-gray-500 font-semibold">{{ source.type || 'Nicht eingeordnet' }}</div>
          </NuxtLink>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
