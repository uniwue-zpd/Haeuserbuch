<script setup lang="ts">
import {ref, onMounted} from 'vue';
import maplibregl, {LngLat} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import type {FeatureCollection} from "~/utils/GeoJsonTypes";
import apiClient from "~/service/api";

const data_fetched = ref(false);
const places_geojson = ref<FeatureCollection>({} as FeatureCollection);
const center = ref<LngLat>(new LngLat(9.946569, 49.787604));

onMounted(async ()=> {
  const map = new maplibregl.Map({
    container: 'map',
    zoom: 10,
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
    const response = await apiClient.get('places?output=geojson');
    places_geojson.value = response.data;
    data_fetched.value = true;

    map.on('load', async () => {
      map.addSource('places', {
        type: 'geojson',
        // @ts-ignore
        data: places_geojson.value,
      });
      map.addLayer({
        'id': 'places',
        'type': 'circle',
        'source': 'places',
        'paint': {
          'circle-radius': 8,
          'circle-color': '#3254a8',
          'circle-opacity': 0.8
        }
      });
    });
    map.on('click', 'places', (e) => {
      if (!e.features) {
        return;
      }
      const geometry = e.features[0].geometry as Point;
      const coordinates = new LngLat(
          (geometry.coordinates[0]),
          (geometry.coordinates[1])
      );
      new maplibregl.Popup()
          .setLngLat(coordinates)
          .setHTML(`<a href="/places/${e.features[0].properties.id}">${(e.features[0].properties?.real_name)}</a>`)
          .addTo(map);
      map.flyTo({
        center: coordinates,
        zoom: 14
      });
    });
    map.on('mouseenter', 'places', () => {
      map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseleave', 'places', () => {
      map.getCanvas().style.cursor = '';
    });
  } catch (error) {
    console.log(error);
  }
});
</script>

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Orte im Überblick</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
