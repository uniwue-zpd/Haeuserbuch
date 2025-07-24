<script setup lang="ts">
import maplibregl, {LngLat, type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, onMounted } from 'vue';
import type { FeatureCollection } from "~/utils/GeoJsonTypes";
import { initMap } from "~/service/map_init";

const router = useRouter();
const building_store = useBuildingStore();
const tile_store = useTileStore();
const buildings = computed(() => building_store.buildings);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;

useHead(() => ({
  title: 'Gebäude - Gebäudeverzeichnis',
}));

onMounted(async () => {
  map = initMap(
      'map_buildings',
      DEFAULT_MAP_CENTER,
      13,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!buildings.value) return;
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
        'fill-color': 'rgba(6,61,121,0.8)',
        'fill-opacity': 0.7
      }
    });
  });
  map.on('click', 'buildings', (e) => {
    if (!e.features || e.features.length === 0) {
      console.warn('No features found');
      return;
    }
    const feature = e.features[0];
    const geometry = feature.geometry as Polygon;
    const coordinates = new LngLat(
        (geometry.coordinates[0][1][0]),
        (geometry.coordinates[0][1][1])
    );
    const popup = new maplibregl.Popup()
        .setLngLat(coordinates)
        .setHTML(`<div class="cursor-pointer montserrat-headline font-semibold text-black">${(feature.properties.name
            ? feature.properties.name
            : feature.properties.districtHouseNumber)}</div>`)
        .addTo(map!);
    popup.getElement().addEventListener('click', ()=> {
      router.push(`/buildings/${feature.id}`)
    });
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

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Häuser im Überblick</h1>
    <Tabs value="0">
      <TabList>
        <Tab value="0" class="montserrat-headline font-semibold text-lg">Karte</Tab>
        <Tab value="1" class="montserrat-headline font-semibold text-lg">Tabellarische Übersicht</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div id="map_buildings" class="h-[500px] w-full rounded-md"/>
        </TabPanel>
        <TabPanel value="1">
          <p>Hier entsteht die Tabelle mit Metadaten</p>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </div>
</template>

<style scoped>

</style>
