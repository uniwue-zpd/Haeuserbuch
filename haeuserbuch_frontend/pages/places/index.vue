<script setup lang="ts">
import { onMounted } from 'vue';
import maplibregl, {LngLat, type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";

const place_store = usePlaceStore();
const tile_store = useTileStore();
const places = computed(() => place_store.places);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;

useHead(() => ({
  title: 'Orte - Orteverzeichnis',
}));

onMounted(async ()=> {
  map = initMap(
      'map',
      DEFAULT_MAP_CENTER,
      12,
      0,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );

  map.on('load', () => {
    if (!places.value) return;
    map!.addSource('places', {
      type: 'geojson',
      // @ts-ignore
      data: places.value as FeatureCollection
    });
    map!.addLayer({
      id: 'places',
      type: 'circle',
      source: 'places',
      paint: {
        'circle-radius': 8,
        'circle-color': '#3254a8',
        'circle-opacity': 0.8,
      },
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
        .setHTML(`<a href="/places/${e.features[0].id}">${(e.features[0].properties?.realName)}</a>`)
        .addTo(map!);
    map!.flyTo({
      center: coordinates,
      zoom: 14
    });
  });
  map.on('mouseenter', 'places', () => {
    map!.getCanvas().style.cursor = 'pointer';
  });
  map.on('mouseleave', 'places', () => {
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

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Orte im Überblick</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
