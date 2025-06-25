<script setup lang="ts">
import { onMounted } from "vue";

const tile_store = useTileStore();
const place_store = usePlaceStore();
onMounted(async ()=> {
  await tile_store.fetchTiles();
  await place_store.fetchPlaces();
});
</script>

<template>
  <AppHeader/>
  <MainContent>
    <NuxtPage/>
  </MainContent>
  <AppFooter/>
</template>
