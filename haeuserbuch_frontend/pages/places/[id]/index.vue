<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import maplibregl, {type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { usePlaceStore } from "~/stores/PlaceStore";
import { DEFAULT_MAP_CENTER } from "~/utils/constant_values";
import type { Feature } from "~/utils/GeoJsonTypes";
import {initMap} from "~/service/map_init";

const router = useRoute();
const place_id = Number(router.params.id);
const place_store = usePlaceStore();
const place_item = computed(() => place_store.current_place);
const tile_store = useTileStore();
const geometry = computed(() => place_item.value?.geometry as Point | null);
const properties = computed(() => place_item.value?.properties as PlaceProperties | null);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

let map: maplibregl.Map | null = null;
const center = ref<[number, number] | null>(null);

useHead(() => ({
  title: place_item.value ? `${properties.value?.realName} - Orteverzeichnis` : 'Nicht gefunden',
}));

onMounted(async () => {
  await place_store.fetchPlaceById(place_id);
  center.value = (geometry.value)
          ? [(geometry.value as Point).coordinates[0], (geometry.value as Point).coordinates[1]]
          : DEFAULT_MAP_CENTER;

  map = initMap(
      'map',
      center.value,
      14,
      70,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!place_item.value) return;
    map!.addSource('place', {
      type: 'geojson',
      // @ts-ignore
      data: place_item.value as Feature,
    });
    map!.addLayer({
      id: 'place',
      type: 'circle',
      source: 'place',
      paint: {
        'circle-radius': 8,
        'circle-color': '#3254a8',
        'circle-opacity': 0.8,
      },
    });
    map!.flyTo({
      center: center.value ? center.value : DEFAULT_MAP_CENTER,
      zoom: 17,
      speed: 0.2
    })
  });
});
</script>

<template>
  <div v-show="place_item">
    <Card>
      <template #title>
        <h1 class="text-3xl montserrat-headline text-black font-bold">{{ properties?.realName }}</h1>
      </template>
      <template #content>
        <div class="flex flex-col gap-2">
          <div id="map" v-show="place_item?.geometry" class="h-[500px] w-full rounded-md"/>
          <div v-show="properties?.altNames" class="flex flex-col">
            <h2 class="text-xl montserrat-headline text-black font-bold">Namensvarianten</h2>
            <ul class="list-inside list-disc">
              <li v-for="name in properties?.altNames">
                <span class="text-lg text-black roboto-plain">{{ name }}</span>
              </li>
            </ul>
          </div>
        </div>
      </template>
      <template #footer>
        <div class="flex flex-col gap-2">
          <Panel header="Notizen" toggleable v-show="properties?.generalNotes">
            <template #header>
              <p class="text-sm text-black roboto-plain font-bold">Notizen</p>
            </template>
            <p class="text-sm text-black roboto-plain">{{ properties?.generalNotes }}</p>
          </Panel>
          <Divider/>
          <div class="flex flex-col">
            <div v-if="properties?.createdDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
              <p>Erstellt am:</p>
              <p>{{ new Date(properties?.createdDate).toLocaleDateString() }}</p>
            </div>
            <div v-if="properties?.lastModifiedDate" class="flex flex-row space-x-2 text-sm text-black roboto-plain">
              <p>Stand:</p>
              <p>{{ new Date(properties?.lastModifiedDate).toLocaleDateString() }}</p>
            </div>
          </div>
        </div>
      </template>
    </Card>
  </div>
</template>

<style scoped>

</style>
