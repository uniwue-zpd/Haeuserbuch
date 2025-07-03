<script setup lang="ts">
import maplibregl, {LngLat, type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, onMounted } from 'vue';
import type { FeatureCollection } from "~/utils/GeoJsonTypes";
import { initMap } from "~/service/map_init";

const building_store = useBuildingStore();
const tile_store = useTileStore();
const buildings = computed(() => building_store.buildings);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;

onMounted(async () => {
  map = initMap(
      'map',
      DEFAULT_MAP_CENTER,
      13,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!buildings.value) return;
    console.log('Buildings are being loaded')
    map!.addSource('buildings', {
      type: "geojson",
      //@ts-ignore
      data: buildings.value as FeatureCollection
    });
    map!.addLayer({
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
        .setHTML(`<a href="/buildings/${e.features[0].id}">${(e.features[0].properties?.name)}</a>`)
        .addTo(map!);
    map!.flyTo({
      center: coordinates,
      zoom: 17
    });
  });
  map.on('mouseenter', 'buildings', () => {
    map!.getCanvas().style.cursor = 'pointer';
  });
  map.on('mouseleave', 'buildings', () => {
    map!.getCanvas().style.cursor = '';
  });
});

onBeforeUnmount(() => {
  if (map) {
    map.remove();
    map = null;
  }
});
</script>

<template v-show="data_fetched">
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Häuser im Überblick</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
