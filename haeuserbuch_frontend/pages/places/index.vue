<script setup lang="ts">
import { onMounted } from 'vue';
import maplibregl, {LngLat, type RasterLayerSpecification, type RasterSourceSpecification} from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";
import {FilterMatchMode} from "@primevue/core";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";
import ErrorComponent from "~/components/UI/FetchError.vue";

const placeStore = usePlaceStore();
const tile_store = useTileStore();
const { data: places, pending: loadingData, error: hasError } = await useAsyncData('places', () => placeStore.getPlaces());
const places_datatable = computed(() => (places.value?.features ?? []).map(
    (p) => {
      const props = p.properties as PlaceProperties;
      return {
        type: 'Feature',
        id: p.id,
        properties: props,
        geometry: p.geometry as Point
      }
    }
));
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

onMounted(async () => {
  if (hasError.value) return;
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
    if (!e.features) return;
    const geometry = e.features[0].geometry as Point;
    const coordinates = new LngLat(
        (geometry.coordinates[0]),
        (geometry.coordinates[1])
    );
    const popUpLink = document.createElement('div');
    popUpLink.innerHTML = e.features[0].properties?.realName ?? 'Unbekannter Ort';
    popUpLink.setAttribute('class', 'cursor-pointer font-bold montserrat-headline');
    const id = e.features[0].id;
    popUpLink.addEventListener('click', () => {
      navigateTo(`/places/${ id }`)
    });
    new maplibregl.Popup()
        .setLngLat(coordinates)
        .setDOMContent(popUpLink)
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
  <UniversalSkeleton v-if="loadingData"/>
  <ErrorComponent :error="hasError" v-else-if="hasError"/>
  <div v-else class="flex flex-col gap-2">
    <h1 class="text-3xl montserrat-headline font-bold">Die Orte im Überblick</h1>
    <div id="map" class="h-[500px] w-full rounded-md"/>
    <Divider/>
    <DataTable
        :value="places_datatable"
        v-model:filters="filters" filter-display="row"
        :global-filter-fields="['properties.realName']"
        paginator :rows="10" stripedRows removableSort
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
          <span v-else class="roboto-italic p-2 bg-red-100 rounded-md">unbekannt</span>
        </template>
      </Column>
      <Column
          field="geometry.coordinates"
          header="Georeferenziert"
          class="roboto-plain"
          :sortable="true"
      >
        <template #body="slotProps">
          <i :class="[slotProps.data.geometry ? 'pi pi-check text-green-500' : 'pi pi-times text-red-500']"/>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<style scoped>

</style>
