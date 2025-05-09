<script setup lang="ts">
import maplibregl, {LngLat} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { ref, onMounted } from 'vue';
import type { FeatureCollection } from "~/utils/GeoJsonTypes";
import apiClient from "~/service/api";

const data_fetched = ref(false);
const center = ref<LngLat>(new LngLat(9.969929, 49.786181));
const buildings_geojson = ref<FeatureCollection>({} as FeatureCollection);

onMounted(async ()=> {
  const map = new maplibregl.Map({
    container: 'map',
    zoom: 18,
    center:  [center.value.lng, center.value.lat],
    style: {
      version: 8,
      sources: {
        osm: {
          type: "raster",
          tiles: ["https://tile.openstreetmap.de/{z}/{x}/{y}.png"],
          tileSize: 256,
          attribution: "&copy; OpenStreetMap Contributors"
        }
      },
      layers: [
        {
          id: "osm-layer",
          type: "raster",
          source: "osm"
        }
      ]
    }
  });
  map.addControl(new maplibregl.NavigationControl({
    showCompass: true,
    showZoom: true,
    visualizePitch: true,
    visualizeRoll: true
  }));

  try {
    const response = await apiClient.get('buildings?output=geojson');
    buildings_geojson.value = response.data;
    data_fetched.value = true;

    // TODO: Draw polygons from GeoJSON FeatureCollection
  } catch (error) {
    console.log(error);
  }
})
</script>

<template v-show="data_fetched">
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl font-bold">Die Gebäude</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
