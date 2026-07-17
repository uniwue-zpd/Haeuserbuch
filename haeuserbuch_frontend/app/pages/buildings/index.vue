<script setup lang="ts">
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, onMounted } from 'vue';
import type { FeatureCollection } from "~/utils/GeoJsonTypes";
import { initMap } from "~/service/map_init";
import { FilterMatchMode } from "@primevue/core";
import FetchError from "~/components/UI/FetchError.vue";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";

const buildingStore = useBuildingStore();
const tile_store = useTileStore();
const { data: buildings, error: hasError, pending: isLoading } = await useAsyncData('buildings-feature-collection', () => buildingStore.getBuildings());
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
const buildingCount = computed(() => buildings.value?.features.length);
let map: maplibregl.Map | null = null;

useHead(() => ({
  title: 'Gebäude - Gebäudeverzeichnis',
}));

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  'properties.districtPropertyNumber': { value: null, matchMode: FilterMatchMode.CONTAINS },
  'properties.district.name': { value: null, matchMode: FilterMatchMode.IN }
});

const districts = ref([
  {type: 'I', value: 'I'},
  {type: 'II', value: 'II'},
  {type: 'III', value: 'III'},
  {type: 'IV', value: 'IV'},
  {type: 'V', value: 'V'},
  {type: 'unbekannt', value: null},
]);

onMounted(async () => {
  map = initMap(
      'map_buildings',
      DEFAULT_MAP_CENTER,
      14,
      70,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.on('load', () => {
    if (!buildings.value) return;
    map!.addSource('buildings', {
      type: "geojson",
      //@ts-ignore
      data: {
        ...buildings.value,
        features: buildings.value.features.filter(f => f.geometry !== null)
      } as FeatureCollection
    });
    map!.addLayer({
      'id': 'buildings',
      'type': 'fill-extrusion',
      'source': 'buildings',
      'layout': {},
      'paint': {
        'fill-extrusion-color': [
          'match',
          ['get', 'name', ['get', 'district']],
          'I', '#e41a1c',
          'II', '#377eb8',
          'III', '#4daf4a',
          'IV', '#ff7f00',
          'V', '#984ea3',
          '#999999'
        ],
        'fill-extrusion-opacity': 0.8,
        'fill-extrusion-height': 10
      },
      filter: ['==', '$type', 'Polygon']
    });
    map!.addLayer({
      'id': 'walls',
      'type': 'line',
      'source': 'buildings',
      'layout': {
        'line-cap': 'round',
        'line-join': 'round'
      },
      'paint': {
        'line-color': '#E66101',
        'line-width': 7,
        'line-opacity': 0.9,
        'line-blur': 0.3,
      },
      filter: ['==', '$type', 'LineString']
    });
    map!.addLayer({
      id: 'towers',
      type: 'circle',
      source: 'buildings',
      paint: {
        'circle-radius': 5,
        'circle-color': '#E66101',
        'circle-opacity': 0.9,
        'circle-stroke-width': 2,
        'circle-stroke-color': '#ffffff',
        'circle-stroke-opacity': 0.9
      },
      filter: ['==', '$type', 'Point']
    });
  });
  map.on('click', ['buildings', 'walls', 'towers'], (e) => {
    if (!e.features || e.features.length === 0) {
      console.warn('No features found');
      return;
    }
    const popup_html = document.createElement('div');
    popup_html.innerHTML = e.features[0]?.properties?.districtPropertyNumber || e.features[0]?.properties?.object || 'Unbekanntes Gebäude';
    popup_html.setAttribute('class',  'cursor-pointer font-bold montserrat-headline');
    const popup_link = `/buildings/${ e.features[0]?.id }`;
    popup_html.addEventListener('click', () => {navigateTo(popup_link)});
    const popup = new maplibregl.Popup()
        .setLngLat(e.lngLat)
        .setDOMContent(popup_html);
    popup.addTo(map!);
    map!.flyTo({
      center: e.lngLat,
      zoom: 17
    });
  });
  map.on('mouseenter', ['buildings', 'walls', 'towers'], () => {
    map!.getCanvas().style.cursor = 'pointer';
  });
  map.on('mouseleave', ['buildings', 'walls', 'towers'], () => {
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
  <UniversalSkeleton v-if="isLoading"/>
  <FetchError v-else-if="hasError" :error="hasError"/>
  <div v-else class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Häuser im Überblick</h1>
    <Tabs value="0">
      <TabList>
        <Tab value="0" class="montserrat-headline font-semibold text-lg">Karte</Tab>
        <Tab value="1" class="montserrat-headline font-semibold text-lg">Tabellarische Übersicht</Tab>
        <Tab value="2" class="montserrat-headline font-semibold text-lg">Siehe auch</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div id="map_buildings" class="h-[50vh] md:h-[60vh] w-full rounded-md"/>
        </TabPanel>
        <TabPanel value="1">
          <div class="flex flex-col gap-2">
            <p class="roboto-plain text-base text-black">
              Aktuell sind insgesamt <span class="font-semibold">{{ buildingCount }}</span> Gebäude erfasst.
            </p>
            <DataTable
                :value="buildings?.features"
                v-model:filters="filters" filter-display="row"
                :global-filter-fields="['properties.districtPropertyNumber', 'properties.partType', 'properties.object', 'properties.quarter.name', 'properties.district.name']"
                stateStorage="session" stateKey="dt-state-demo-session-buildings" paginator :rows="7"
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
              <Column field="properties.districtPropertyNumber" header="Bezeichnung" :sortable="true">
                <template #body="{ data }">
                  <NuxtLink
                      :to="`/buildings/${data.id}`"
                      class="roboto-plain text-black font-semibold p-2 rounded-md hover:shadow-md"
                      prefetch
                  >
                    {{ data.properties.districtPropertyNumber || 'Ohne Bezeichnung' }}
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
              <Column field="properties.names" header="Namen" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.names.length > 0">
                    <ul class="list-disc list-inside">
                      <li v-for="(name, index) in slotProps.data.properties.names" :key="index">
                        {{ name.name }}
                      </li>
                    </ul>
                  </div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column field="properties.partType" header="Bauteil" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.partType">{{ slotProps.data.properties.partType }}</div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column field="properties.object" header="Objekt" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.object">{{ slotProps.data.properties.object }}</div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column field="properties.quarter" header="Viertel" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.quarter">
                    <NuxtLink :to="`/quarters/${slotProps.data.properties.quarter.id}`">
                      {{ slotProps.data.properties.quarter.name }}
                    </NuxtLink>
                  </div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column
                  filterField="properties.district.name" field="properties.district.name"
                  :showFilterMenu="false"
                  :sortable="true"
                  header="Distrikt"
                  class="roboto-plain"
              >
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.district">
                    <NuxtLink :to="`/districts/${slotProps.data.properties.district.id}`">
                      {{ slotProps.data.properties.district.name }}
                    </NuxtLink>
                  </div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">
                    unbekannt
                  </div>
                </template>
                <template #filter="{ filterModel, filterCallback }">
                  <MultiSelect
                      v-model="filterModel.value"
                      @change="filterCallback()"
                      :options="districts" optionLabel="type" :option-value="option => option.value"
                      placeholder="Beliebige"
                  >
                    <template #option="slotProps">
                      <div>{{ slotProps.option.value }}</div>
                    </template>
                  </MultiSelect>
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
        </TabPanel>
        <TabPanel value="2">
          <div class="misc">
            <div class="flex flex-col gap-4 md:grid md:grid-cols-3">
              <NuxtLink to="/districts" prefetch>
                <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
                  <div class="flex flex-col gap-3 items-center">
                    <i class="pi pi-map" style="font-size: 4.5rem"/>
                    <h3 class="text-center text-xl text-black montserrat-headline font-bold">Distrikte</h3>
                  </div>
                </div>
              </NuxtLink>
              <NuxtLink to="/quarters" prefetch>
                <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
                  <div class="flex flex-col gap-3 items-center">
                    <i class="pi pi-th-large" style="font-size: 4.5rem"/>
                    <h3 class="text-center text-xl text-black montserrat-headline font-bold">Viertel</h3>
                  </div>
                </div>
              </NuxtLink>
              <NuxtLink to="/streets" prefetch>
                <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
                  <div class="flex flex-col gap-3 items-center">
                    <i class="pi pi-list" style="font-size: 4.5rem"/>
                    <h3 class="text-center text-xl text-black montserrat-headline font-bold">Straßen</h3>
                  </div>
                </div>
              </NuxtLink>
              <NuxtLink to="/sources" prefetch>
                <div class="bg-[#F1F2F2] shadow-md rounded-md p-5 hover:shadow-lg hover:scale-105 transition-transform duration-300">
                  <div class="flex flex-col gap-3 items-center">
                    <i class="pi pi-book" style="font-size: 4.5rem"/>
                    <h3 class="text-center text-xl text-black montserrat-headline font-bold">Quellenverzeichnis</h3>
                  </div>
                </div>
              </NuxtLink>
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </div>
</template>

<style scoped>

</style>
