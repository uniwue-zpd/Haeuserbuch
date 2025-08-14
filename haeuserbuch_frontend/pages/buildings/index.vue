<script setup lang="ts">
import maplibregl, {LngLat, type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { computed, onMounted } from 'vue';
import type { FeatureCollection } from "~/utils/GeoJsonTypes";
import { initMap } from "~/service/map_init";
import { FilterMatchMode } from "@primevue/core";

const router = useRouter();
const building_store = useBuildingStore();
const tile_store = useTileStore();
const buildings = computed(() => building_store.buildings);
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
const buildingCount = computed(() => buildings.value?.features.length);
let map: maplibregl.Map | null = null;

useHead(() => ({
  title: 'Gebäude - Gebäudeverzeichnis',
}));

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  'properties.districtHouseNumber': { value: null, matchMode: FilterMatchMode.CONTAINS },
  'properties.district': { value: null, matchMode: FilterMatchMode.IN }
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
        'fill-color': [
          'match',
          ['get', 'district'],
          'I', '#e41a1c',
          'II', '#377eb8',
          'III', '#4daf4a',
          'IV', '#ff7f00',
          'V', '#984ea3',
          '#999999'
        ],
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
          <div class="flex flex-col gap-2">
            <p class="roboto-plain text-base text-black">
              Aktuell sind insgesamt <span class="font-semibold">{{ buildingCount }}</span> Gebäude erfasst.
            </p>
            <DataTable
                :value="buildings?.features"
                v-model:filters="filters" filter-display="row"
                :global-filter-fields="['properties.districtHouseNumber', 'properties.name', 'properties.partType', 'properties.specialStatus', 'properties.quarter', 'properties.district']"
                stateStorage="session" stateKey="dt-state-demo-session" paginator :rows="7"
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
              <Column field="properties.districtHouseNumber" header="Bezeichnung" :sortable="true">
                <template #body="{ data }">
                  <div class="flex flex-row space-x-5 items-center">
                    <NuxtLink
                        :to="`/buildings/${data.id}`"
                        class="roboto-plain font-semibold"
                    >
                      {{ data.properties.districtHouseNumber }}
                    </NuxtLink>
                  </div>
                </template>
                <template #filter="{ filterModel, filterCallback }">
                  <InputText
                      v-model="filterModel.value"
                      type="text" @input="filterCallback()"
                      placeholder="Suchen..."
                  />
                </template>
              </Column>
              <Column field="properties.name" header="Name" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.name">{{ slotProps.data.properties.name }}</div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column field="properties.partType" header="Bauteil" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.partType">{{ slotProps.data.properties.partType }}</div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column field="properties.specialStatus" header="Status" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.specialStatus">{{ slotProps.data.properties.specialStatus }}</div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column field="properties.quarter" header="Viertel" class="roboto-plain" :sortable="true">
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.quarter">{{ slotProps.data.properties.quarter }}</div>
                  <div v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</div>
                </template>
              </Column>
              <Column
                  filterField="properties.district" field="properties.district"
                  :showFilterMenu="false"
                  :sortable="true"
                  header="Distrikt"
                  class="roboto-plain"
              >
                <template #body="slotProps">
                  <div v-if="slotProps.data.properties.district">
                    {{ slotProps.data.properties.district }}
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
      </TabPanels>
    </Tabs>
  </div>
</template>

<style scoped>

</style>
