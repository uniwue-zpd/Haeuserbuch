<script setup lang="ts">
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, ref, onMounted } from 'vue';
import type { Feature, Polygon } from "~/utils/GeoJsonTypes";
import { MaplibreTerradrawControl } from '@watergis/maplibre-gl-terradraw';
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';
import { initMap } from "~/service/map_init";

const route = useRoute();
const building_id = Number(route.params.id);

const building_store = useBuildingStore();
const tile_store = useTileStore();
const building_item = computed(() => building_store.current_building);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

let map: maplibregl.Map | null = null;
const center = ref<[number, number] | null>(null);
const draw = new MaplibreTerradrawControl({
  modes: ['render','point','linestring','polygon','select','delete-selection','delete','download'],
  open: true,
});

onMounted(async () => {
  await building_store.fetchBuildingById(building_id);
  center.value = [
    (building_item.value?.geometry as Polygon).coordinates[0][0][0],
    (building_item.value?.geometry as Polygon).coordinates[0][0][1]
  ];
  map = initMap(
      'map',
      center.value ? center.value : DEFAULT_MAP_CENTER,
      15,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.addControl(draw, "top-left");
  map.on('load', () => {
    if (!building_item.value) return;
    map!.addSource('building', {
      type: 'geojson',
      // @ts-ignore
      data: building_item.value as Feature,
    });
    map!.addLayer({
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
});

onBeforeUnmount(() => {
  if (map) {
    map.remove();
    map = null;
  }
});
</script>

<template v-if="data_fetched">
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">{{ building_item?.properties?.name }}</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
  </div>
</template>

<style scoped>

</style>
