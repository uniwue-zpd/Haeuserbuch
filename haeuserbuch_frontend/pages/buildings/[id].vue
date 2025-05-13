<script setup lang="ts">
import maplibregl, {LngLat} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { ref, onMounted } from 'vue';
import apiClient from "~/service/api";
import type {Feature, Polygon} from "~/utils/GeoJsonTypes";
import { MaplibreTerradrawControl } from '@watergis/maplibre-gl-terradraw';
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';

// Page title
const route = useRoute();
const building_id = route.params.id;

// Put fetched data into a ref
const building_geojson = ref<Feature>({} as Feature);
const center = ref<LngLat>(new LngLat(9.969929, 49.786181));
const data_fetched = ref(false);

// Initialize drawing panel for the map
const draw = new MaplibreTerradrawControl({
  modes: ['render','point','linestring','polygon','select','delete-selection','delete','download'],
  open: true,
});
// TODO: Define the function for fetching the data from the objects drawn on the map

onMounted(async () => {
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
  map.addControl(draw, "top-left");

  try {
    const response = await apiClient.get(`buildings/${building_id}?output=geojson`);
    building_geojson.value = response.data;
    data_fetched.value = true;

    center.value = new LngLat(
        (building_geojson.value.geometry as Polygon).coordinates[0][0][0],
        (building_geojson.value.geometry as Polygon).coordinates[0][0][1]
    );
    map.setCenter(center.value);
    map.on('load', () => {
      map.addSource('building', {
        type: 'geojson',
        // @ts-ignore
        data: building_geojson.value,
      });
      map.addLayer({
        'id': 'building',
        'type': 'fill',
        'source': 'building',
        'layout': {},
        'paint': {
          'fill-color': '#176363',
          'fill-opacity': 0.5
        }
      });
    });
  } catch (error) {
    console.log("Error fetching building data: ", error);
  }
});
</script>

<template v-if="data_fetched">
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">{{ building_geojson.properties?.name }}</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
