<script setup lang="ts">

const districtStore = useDistrictStore();

const { data: districts } = useAsyncData('districts', () => districtStore.fetchDistricts());

useHead(() => ({
  title: 'Distrikte - Distriktverzeichnis'
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black montserrat-headline">Distrikte</h1>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <Panel class="rounded-md">
          <template #header>
            <h1 class="text-base font-bold text-black montserrat-headline">Hinweise</h1>
          </template>
          <div class="description roboto-plain">
            <div>Distrikte sind Einheiten, die ...</div>
            <div>
              Beim Klicken auf den jeweiligen Distrikt gelangen Sie auf die Übersichtsseite,
              wo Sie die weiterführenden Informationen finden.
            </div>
          </div>
        </Panel>
        <div
            v-for="district in districts"
            class="bg-[#F1F2F2] shadow-md rounded-md p-3 hover:shadow-lg hover:translate-x-2 transition-transform duration-300"
        >
          <NuxtLink :to="`/districts/${district.id}`" class="no-underline">
            <h2 class="text-xl font-bold text-black montserrat-headline">{{ district.name }}</h2>
          </NuxtLink>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>
