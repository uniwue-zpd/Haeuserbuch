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
const building_item_properties = computed(() => building_item.value?.properties as BuildingProperties | null);
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
        'fill-color': 'rgba(6,61,121,0.8)',
        'fill-opacity': 0.7
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

<template>
  <div class="flex flex-col gap-2">
    <Card v-show="building_item">
      <template #title>
        <h1 class="text-3xl montserrat-headline font-bold">{{ building_item_properties?.name }}</h1>
      </template>
      <template #content>
        <div class="flex flex-col gap-2">
          <div id="map" class="h-[500px] w-full rounded-md"/>
          <Accordion value="null" v-show="building_item_properties">
            <AccordionPanel value="0">
              <AccordionHeader>
                <h2 class="text-2xl text-black font-semibold montserrat-headline">Daten zum Gebäude</h2>
              </AccordionHeader>
              <AccordionContent>
                <Card>
                  <template #content>
                    <table class="min-w-full divide-y divide-gray-200">
                      <tbody v-if="building_item_properties" class="bg-white divide-y divide-gray-200">
                      <tr v-if="building_item_properties.name">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Name</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.name }}</td>
                      </tr>
                      <tr v-if="building_item_properties.house_number">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Hausnummer</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.house_number }}</td>
                      </tr>
                      <tr v-if="building_item_properties.part_type">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Bauteil</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.part_type }}</td>
                      </tr>
                      <tr v-if="building_item_properties.special_status">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Sonderstatus</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.special_status }}</td>
                      </tr>
                      <tr v-if="building_item_properties.quarter">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Viertel</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.quarter }}</td>
                      </tr>
                      <tr v-if="building_item_properties.district">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Distrikt</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.district }}</td>
                      </tr>
                      <tr v-if="building_item_properties.primary_sources.length > 0">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Primärquellen</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <ul>
                            <li v-for="(source, index) in building_item_properties.primary_sources" :key="index">
                              {{ source.title }}, {{ source.signature }}
                            </li>
                          </ul>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.secondary_sources.length > 0">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Sekundärquellen</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <ul>
                            <li v-for="(source, index) in building_item_properties.secondary_sources" :key="index">
                              {{ source }}
                            </li>
                          </ul>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.notes">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">Notizen</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ building_item_properties.notes }}</td>
                      </tr>
                      </tbody>
                    </table>
                  </template>
                </Card>
              </AccordionContent>
            </AccordionPanel>
          </Accordion>
        </div>
      </template>
    </Card>
  </div>
</template>

<style scoped>

</style>
