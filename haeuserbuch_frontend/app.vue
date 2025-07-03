<script setup lang="ts">
import { onMounted } from "vue";

const building_store = useBuildingStore();
const place_store = usePlaceStore();
const tile_store = useTileStore();

onMounted(async () => {
  await building_store.fetchBuildings();
  await place_store.fetchPlaces();
  await tile_store.fetchTiles();
});
</script>

<template>
  <AppHeader/>
  <MainContent>
    <NuxtPage/>
  </MainContent>
  <AppFooter/>
</template>
