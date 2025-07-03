<script setup lang="ts">
import { onMounted } from 'vue';
import maplibregl from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { usePlaceStore } from "~/stores/PlaceStore";
import { DEFAULT_MAP_CENTER } from "~/utils/constant_values";

const router = useRoute();
const place_id = Number(router.params.id);
const store = usePlaceStore();
const place_item = computed(() => store.current_place);
const geometry = computed(() => place_item.value?.geometry as Point | null);
const properties = computed(() => place_item.value?.properties as PlaceProperties | null);

onMounted(async () => {
  await store.fetchPlaceById(place_id);
  const center = (geometry.value) ? geometry.value.coordinates : DEFAULT_MAP_CENTER;

  const map = new maplibregl.Map({
    container: 'map',
    zoom: 12,
    center: center,
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
    },
  });
  map.addControl(new maplibregl.NavigationControl({
    showCompass: true,
    showZoom: true,
    visualizePitch: true,
    visualizeRoll: true
  }));
  map.on('load', () => {
    if (!place_item.value) return;
    map.addSource('place', {
      type: 'geojson',
      // @ts-ignore
      data: place_item.value,
    });
    map.addLayer({
      id: 'place',
      type: 'circle',
      source: 'place',
      paint: {
        'circle-radius': 8,
        'circle-color': '#3254a8',
        'circle-opacity': 0.8,
      },
    });
  });
});
</script>

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">{{ properties?.real_name }}</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
