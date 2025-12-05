<script setup lang="ts">
import QuickNavigation from "~/components/homepage/QuickNavigation.vue";

useHead(() => ({
  title: 'Häuserbuch - Hauptseite'
}));
</script>

<template>
  <div class="flex flex-col gap-4">
    <div class="text-3xl font-bold montserrat-headline flex flex-row space-x-2 uppercase">
      <p>Das Würzburger Häuserprojekt - Häuserbuch</p>
      <p class="text-black text-opacity-40"> ohne Häuser</p>
    </div>
    <!--
    <div class="intro flex flex-col gap-2">
      <h2 class="text-2xl  font-bold montserrat-headline">Einführung</h2>
    </div>
    -->
    <div class="navigation flex flex-col gap-2">
      <h2 class="text-2xl font-bold montserrat-headline">Schnellzugriff</h2>
      <QuickNavigation/>
    </div>
  </div>
</template>

<style scoped>

</style>
