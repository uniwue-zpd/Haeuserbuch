<script setup lang="ts">
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, ref, onMounted } from 'vue';
import type { Feature, Polygon } from "~/utils/GeoJsonTypes";
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

useHead(() => ({
  title: building_item.value ? `${building_item_properties.value?.districtHouseNumber} - Gebäudeverzeichnis` : 'Nicht gefunden',
}));

onMounted(async () => {
  await building_store.fetchBuildingById(building_id);
  center.value = [
    (building_item.value?.geometry as Polygon).coordinates[0][0][0],
    (building_item.value?.geometry as Polygon).coordinates[0][0][1]
  ];
  map = initMap(
      'map',
      center.value ? center.value : DEFAULT_MAP_CENTER,
      14,
      70,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!building_item.value) return;
    map!.addSource('building', {
      type: 'geojson',
      // @ts-ignore
      data: building_item.value as Feature,
    });
    map!.addLayer({
      'id': 'building',
      'type': 'fill-extrusion',
      'source': 'building',
      'layout': {},
      'paint': {
        'fill-extrusion-color': 'rgba(255,250,0,0.8)',
        'fill-extrusion-opacity': 0.8,
        'fill-extrusion-height': 10
      }
    });
    map!.flyTo({
      center: center.value ? center.value : DEFAULT_MAP_CENTER,
      zoom: 17,
      speed: 0.2
    })
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
      <template #title v-show="building_item_properties.districtHouseNumber">
        <h1 class="text-3xl montserrat-headline font-bold">{{ building_item_properties?.districtHouseNumber }}</h1>
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
                      <tr v-if="building_item_properties.names.length > 0">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Namen</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">
                          <ul class="list-disc list-inside">
                            <li v-for="(name, index) in building_item_properties.names" :key="index">
                              {{ name.name }} (Quelle: {{ name.source }})
                            </li>
                          </ul>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.addresses">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Adressen</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">
                          <div class="flex flex-wrap gap-3.5">
                            <div
                                v-for="address in building_item_properties.addresses"
                                class="p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md"
                            >
                              <div class="flex flex-row space-x-2">
                                <NuxtLink
                                    :to="`/streets/${address.street?.id}`"
                                    class="text-blue-700"
                                >{{ address.street?.name }}</NuxtLink>
                                <span>{{ address.houseNumber }}</span>
                              </div>
                            </div>
                          </div>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.houseNumber">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Hausnummer</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">{{ building_item_properties.houseNumber }}</td>
                      </tr>
                      <tr v-if="building_item_properties.partType">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Bauteil</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">{{ building_item_properties.partType }}</td>
                      </tr>
                      <tr v-if="building_item_properties.specialStatus">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Sonderstatus</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">{{ building_item_properties.specialStatus }}</td>
                      </tr>
                      <tr v-if="building_item_properties.quarter">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Viertel</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">
                          <NuxtLink :to="`/quarters/${building_item_properties.quarter.id}`" class="text-blue-700 p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md">{{ building_item_properties.quarter.name }}</NuxtLink>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.district">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Distrikt</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">
                          <NuxtLink :to="`/districts/${building_item_properties.district.id}`" class="text-blue-700 p-1.5 bg-gray-200 rounded-md shadow-sm hover:shadow-md">{{ building_item_properties.district.name }}</NuxtLink>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.primarySources.length > 0">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Primärquellen</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">
                          <ul>
                            <li v-for="(source, index) in building_item_properties.primarySources" :key="index">
                              <NuxtLink :to="`/sources/${source.id}`" class="text-blue-700">{{ source.title }}</NuxtLink>
                            </li>
                          </ul>
                        </td>
                      </tr>
                      <tr v-if="building_item_properties.secondarySources.length > 0">
                        <td class="px-6 py-4 whitespace-nowrap font-bold">Sekundärquellen</td>
                        <td class="px-6 py-4 whitespace-nowrap font-medium">
                          <ul>
                            <li v-for="(source, index) in building_item_properties.secondarySources" :key="index">
                              <NuxtLink :to="`/sources/${source.id}`">{{ source.title }}</NuxtLink>
                            </li>
                          </ul>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </template>
                  <template #footer>
                    <div class="flex flex-col gap-2">
                      <Panel header="Notizen" toggleable v-show="building_item_properties?.generalNotes">
                        <template #header>
                          <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
                        </template>
                        <p class="text-sm text-black roboto-plain">{{ building_item_properties?.generalNotes }}</p>
                      </Panel>
                      <Divider/>
                      <div class="flex flex-col">
                        <div v-if="building_item_properties?.createdDate" class="flex flex-row space-x-2 text-black roboto-plain">
                          <p class="font-bold">Erstellt am:</p>
                          <p>{{ new Date(building_item_properties.createdDate).toLocaleDateString() }}</p>
                        </div>
                        <div v-if="building_item_properties?.lastModifiedDate" class="flex flex-row space-x-2 text-black roboto-plain">
                          <p class="font-bold">Stand:</p>
                          <p>{{ new Date(building_item_properties.lastModifiedDate).toLocaleDateString() }}</p>
                        </div>
                      </div>
                    </div>
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
