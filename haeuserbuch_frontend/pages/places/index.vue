<script setup lang="ts">
import { onMounted } from 'vue';
import maplibregl, {LngLat, type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";
import {FilterMatchMode} from "@primevue/core";

const place_store = usePlaceStore();
const tile_store = useTileStore();
const places = computed(() => place_store.places);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  'properties.realName': { value: null, matchMode: FilterMatchMode.CONTAINS }
});

useHead(() => ({
  title: 'Orte - Orteverzeichnis',
}));

const show_datatable = computed(() => {
  return places.value?.features && places.value.features.length > 0;
});

onMounted(async ()=> {
  map = initMap(
      'map',
      DEFAULT_MAP_CENTER,
      5,
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
        .setHTML(`<a href="/places/${e.features[0].id}" class="font-bold roboto-plain">${(e.features[0].properties?.realName)}</a>`)
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
    <Divider/>
    <div v-show="show_datatable">
      <DataTable
          :value="place_store.places?.features"
          v-model:filters="filters" filter-display="row"
          :global-filter-fields="['properties.realName']"
          paginator :rows="10" stripedRows
      >
        <template #header>
          <div class="flex flex-row justify-end">
            <IconField>
              <InputIcon>
                <i class="pi pi-search"/>
              </InputIcon>
              <InputText
                  v-model="filters['global'].value"
                  type="text"
                  placeholder="Schlagwortsuche"
              />
            </IconField>
          </div>
        </template>
        <Column field="properties.realName" header="Name" :sortable="true">
          <template #body="{ data }">
            <NuxtLink
                :to="`/places/${data.id}`"
                class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                prefetch
            >
              {{ data.properties.realName }}
            </NuxtLink>
          </template>
          <template #filter="{ filterModel, filterCallback }">
            <InputText
                v-model="filterModel.value"
                type="text" @input="filterCallback()"
                placeholder="Suchen..."
            />
          </template>
        </Column>
        <Column field="properties.altNames" header="Namensvarianten" class="roboto-plain" :sortable="true">
          <template #body="slotProps">
            <div v-if="slotProps.data.properties.altNames.length > 0">
              <ul class="list-disc list-inside">
                <li v-for="(name, index) in slotProps.data.properties.altNames" :key="index">
                  {{ name }}
                </li>
              </ul>
            </div>
            <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
          </template>
        </Column>
        <Column
            field="geometry.coordinates"
            header="Georeferenziert"
            class="roboto-plain"
            :sortable="true"
        >
          <template #body="slotProps">
            <i :class="[slotProps.data.geometry.coordinates ? 'pi pi-check text-green-500' : 'pi pi-times text-red-500']"/>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<style scoped>

</style>
