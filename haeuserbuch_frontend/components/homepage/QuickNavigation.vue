<script setup lang="ts">

</script>

<template>
  <div class="flex flex-col gap-3 md:grid md:grid-cols-3">
    <NuxtLink to="/buildings" prefetch>
      <Card class="hover:shadow-md hover:scale-105 transition-transform duration-300 bg-[#F1F2F2]">
        <template #header>
          <div class="flex justify-center pt-5">
            <i class="pi pi-home text-5xl md:text-7xl"/>
          </div>
        </template>
        <template #title>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Katasterplan</h3>
        </template>
      </Card>
    </NuxtLink>
    <NuxtLink to="/places" prefetch>
      <Card class="hover:shadow-md hover:scale-105 transition-transform duration-300 bg-[#F1F2F2]">
        <template #header>
          <div class="flex justify-center pt-5">
            <i class="pi pi-map-marker text-5xl md:text-7xl"/>
          </div>
        </template>
        <template #title>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Orte</h3>
        </template>
      </Card>
    </NuxtLink>
    <NuxtLink to="/persons" prefetch>
      <Card class="hover:shadow-md hover:scale-105 transition-transform duration-300 bg-[#F1F2F2]">
        <template #header>
          <div class="flex justify-center pt-5">
            <i class="pi pi-user text-5xl md:text-7xl"/>
          </div>
        </template>
        <template #title>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Personen</h3>
        </template>
      </Card>
    </NuxtLink>
    <Card class="hover:shadow-md hover:scale-105 transition-transform duration-300 bg-[#F1F2F2]">
      <template #header>
        <div class="flex justify-center pt-5">
          <i class="pi pi-id-card text-5xl md:text-7xl"/>
        </div>
      </template>
      <template #title>
        <h3 class="text-center text-xl text-black montserrat-headline font-bold">Bürgermatrikel</h3>
      </template>
    </Card>
    <Card class="hover:shadow-md hover:scale-105 transition-transform duration-300 bg-[#F1F2F2]">
      <template #header>
        <div class="flex justify-center pt-5">
          <i class="pi pi-book text-5xl md:text-7xl"/>
        </div>
      </template>
      <template #title>
        <h3 class="text-center text-xl text-black montserrat-headline font-bold">Eigentumsverhältnisse</h3>
      </template>
    </Card>
    <Card class="hover:shadow-md hover:scale-105 transition-transform duration-300 bg-[#F1F2F2]">
      <template #header>
        <div class="flex justify-center pt-5">
          <i class="pi pi-building-columns text-5xl md:text-7xl"/>
        </div>
      </template>
      <template #title>
        <h3 class="text-center text-xl text-black montserrat-headline font-bold">Steuerbucheinträge</h3>
      </template>
    </Card>
  </div>
</template>

<style scoped>

</style>