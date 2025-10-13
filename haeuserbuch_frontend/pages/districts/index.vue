<script setup lang="ts">
const districts = ref<District[]>([]);

onMounted(async () => {
  districts.value = useDistrictStore().districts;
})
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
        <Card
            v-for="district in districts"
            class="hover:shadow-md bg-[#F1F2F2]"
        >
          <template #content>
            <NuxtLink :to="`/districts/${district.id}`" class="no-underline">
              <h2 class="text-xl font-bold text-black montserrat-headline">{{ district.name }}</h2>
            </NuxtLink>
          </template>
        </Card>
      </div>
    </template>
  </Card>
</template>

<style scoped>

</style>