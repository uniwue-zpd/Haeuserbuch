<script setup lang="ts">

</script>

<template>
  <div class="flex flex-col gap-4 md:grid md:grid-cols-3">
    <NuxtLink to="/buildings" prefetch>
      <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
        <div class="flex flex-col gap-3 items-center">
          <i class="pi pi-home" style="font-size: 4.5rem"/>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Katasterplan</h3>
        </div>
      </div>
    </NuxtLink>
    <NuxtLink to="/places" prefetch>
      <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
        <div class="flex flex-col gap-3 items-center">
          <i class="pi pi-map-marker" style="font-size: 4.5rem"/>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Orte</h3>
        </div>
      </div>
    </NuxtLink>
    <NuxtLink to="/persons" prefetch>
      <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
        <div class="flex flex-col gap-3 items-center">
          <i class="pi pi-user" style="font-size: 4.5rem"/>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Personen</h3>
        </div>
      </div>
    </NuxtLink>
    <NuxtLink to="/citizenships" prefetch>
      <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
        <div class="flex flex-col gap-3 items-center">
          <i class="pi pi-id-card" style="font-size: 4.5rem"/>
          <h3 class="text-center text-xl text-black montserrat-headline font-bold">Bürgermatrikel</h3>
        </div>
      </div>
    </NuxtLink>
    <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
      <div class="flex flex-col gap-3 items-center">
        <i class="pi pi-book" style="font-size: 4.5rem"/>
        <h3 class="text-center text-xl text-black montserrat-headline font-bold">Eigentumsverhältnisse</h3>
      </div>
    </div>
    <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
      <div class="flex flex-col gap-3 items-center">
        <i class="pi pi-building-columns" style="font-size: 4.5rem"/>
        <h3 class="text-center text-xl text-black montserrat-headline font-bold">Steuerbucheinträge</h3>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
