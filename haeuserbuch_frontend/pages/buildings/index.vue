<script setup lang="ts">
import maplibregl, {LngLat, type RasterLayerSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { ref, onMounted } from 'vue';
import type { FeatureCollection } from "~/utils/GeoJsonTypes";
import apiClient from "~/service/api";
import { useTileStore } from "~/stores/TileStore";

const data_fetched = ref(false);
const center = ref<LngLat>(new LngLat(9.969929, 49.786181));
const buildings_geojson = ref<FeatureCollection>({} as FeatureCollection);
const tilestore = useTileStore();

onMounted(async () => {
  await tilestore.fetchTiles();
  try {
    const response = await apiClient.get('buildings?output=geojson');
    buildings_geojson.value = response.data;
    data_fetched.value = true;

    const map = new maplibregl.Map({
      container: 'map',
      zoom: 12,
      center:  [center.value.lng, center.value.lat],
      style: {
        version: 8,
        sources: tilestore.sources as Record<string, maplibregl.SourceSpecification>,
        layers: tilestore.layers as RasterLayerSpecification[],
      }
    });
    map.addControl(new maplibregl.NavigationControl({
      showCompass: true,
      showZoom: true,
      visualizePitch: true,
      visualizeRoll: true
    }));
    center.value = new LngLat(
        (buildings_geojson.value.features[0].geometry as Polygon).coordinates[0][0][0],
        (buildings_geojson.value.features[0].geometry as Polygon).coordinates[0][0][1]
    )
    map.setCenter([center.value.lng, center.value.lat]);
    map.on('load', () => {
      map.addSource('buildings', {
        type: 'geojson',
        // @ts-ignore
        data: buildings_geojson.value,
      });
      map.addLayer({
        'id': 'buildings',
        'type': 'fill',
        'source': 'buildings',
        'layout': {},
        'paint': {
          'fill-color': '#176363',
          'fill-opacity': 0.5
        }
      });
    });
    map.on('click', 'buildings', (e) => {
      if (!e.features || e.features.length === 0) {
        console.warn('No features found');
        return;
      }
      const geometry = e.features[0].geometry as Polygon;
      const coordinates = new LngLat(
          (geometry.coordinates[0][1][0]),
          (geometry.coordinates[0][1][1])
      );
      new maplibregl.Popup()
          .setLngLat(coordinates)
          .setHTML(`<a href="/buildings/${e.features[0].properties?.id}">${(e.features[0].properties?.name)}</a>`)
          .addTo(map);
      map.flyTo({
        center: coordinates,
        zoom: 17
      });
    });
    map.on('mouseenter', 'buildings', () => {
      map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseleave', 'buildings', () => {
      map.getCanvas().style.cursor = '';
    });
  } catch (error) {
    console.log(error);
  }
})
</script>

<template v-show="data_fetched">
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Häuser im Überblick</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
