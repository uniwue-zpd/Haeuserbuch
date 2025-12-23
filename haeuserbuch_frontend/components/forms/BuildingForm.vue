<script setup lang="ts">
import { computed, onMounted } from "vue";
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification } from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";
import { MaplibreTerradrawControl, roundFeatureCoordinates } from '@watergis/maplibre-gl-terradraw';
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';
import type { Position } from 'geojson';
import { type GeoJSONStoreGeometries } from "terra-draw";
import type { Feature } from "~/utils/GeoJsonTypes";

const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  building?: Feature;
}>();

const toast = useToast();
const submitted = ref(false);
const building_store = useBuildingStore();
const source_store = useSourceStore();
const district_store = useDistrictStore();
const quarter_store = useQuarterStore();
const street_store = useStreetStore();

const tile_store = useTileStore();
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;
const draw = new MaplibreTerradrawControl({
  modes: ['render','point', 'polygon','select','delete-selection','delete','download'],
  open: true,
  adapterOptions: {
    coordinatePrecision: 9
  }
});
const coordinates = ref<Position | Position[] | Position[][] | null>(null);
const geometry_type = ref<string | null>(null);

const center = computed<[number, number]>(() => {
  if (!props.building || !props.building.geometry) {
    console.warn('No building geometry, using default map center');
    return DEFAULT_MAP_CENTER
  }
  const geom = props.building.geometry;
  switch (geom.type) {
    case 'Point':
      return geom.coordinates as [number, number];
    case 'Polygon':
      return (geom.coordinates as number[][][])[0][0] as [number, number];
    case 'MultiPolygon':
      return (geom.coordinates as number[][][][])[0][0][0] as [number, number];
    default:
      return DEFAULT_MAP_CENTER;
  }
});

/* FormKit-friendly initial value */
const initialValue = computed(() => {
  if (!props.building) return {};
  const clone = JSON.parse(JSON.stringify(props.building));
  if (!clone.geometry || typeof clone.geometry !== 'object' || !clone.geometry.coordinates) {
    delete clone.geometry;
  }
  return clone;
});

const submit = async (formData: Partial<Feature>) => {
  try {
    if (props.action === 'create') {
      await building_store.createBuilding(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('building_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.building?.id) {
      const id = props.building.id;
      await building_store.updateBuilding(formData, props.building.id);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/buildings/${id}`);
    }
  } catch (error) {
    console.log(error)
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Erstellen des Gebäude-Objektes',
      life: 3000
    });
  }
};

onMounted(async () => {
  await nextTick();
  map = initMap(
      'form_map_building',
      center.value,
      14,
      0,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.addControl(draw, "top-left");
  const drawInstance = draw.getTerraDrawInstance();
  map.once('load', () => {
    if (drawInstance && props.building && props.building.geometry) {
      const geojson = roundFeatureCoordinates([
        {
          type: 'Feature',
          geometry: {
            type: props.building.geometry.type,
            coordinates: props.building.geometry.coordinates
          } as GeoJSONStoreGeometries,
          properties: {
            mode: props.building.geometry.type.toLowerCase(),
          }
        }
      ], 9);
      drawInstance?.addFeatures(geojson);
      coordinates.value = props.building.geometry.coordinates;
      geometry_type.value = props.building.geometry.type;
    }
  });
  if (drawInstance) {
    drawInstance.on('finish', (id) => {
      const snapshot = drawInstance.getSnapshot();
      const feature = snapshot?.find((feature) => feature.id === id);
      if (feature) {
        coordinates.value = feature.geometry.coordinates;
        geometry_type.value = feature.geometry.type;
      }
    });
  }
  draw.on('feature-deleted', () => {
    coordinates.value = null;
    geometry_type.value = null;
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
    <h1 class="text-2xl montserrat-headline-headline text-black font-bold">{{ props.header }}</h1>
    <p class="roboto-plain">
      Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen oder anzupassen.
      Falls Sie ein Gebäude mit Koordinaten versehen möchten, können Sie dies auf der Karte tun.
    </p>
    <Message v-if="(props.building?.geometry?.coordinates.length ?? 0) > 1 " severity="error">
      Polygone mit inneren Ringen können aktuell nicht angezeigt werden
    </Message>
    <div id="form_map_building" class="h-[500px] w-full rounded-md"/>
    <FormKit
        type="form"
        id="building_creation"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="initialValue"
        :key="props.building?.id || 'create'"
        #default="{ value }"
    >
      <div class="flex flex-col gap-2">
        <FormKit type="hidden" name="type" value="Feature" />
        <FormKit type="group" name="properties">
          <div class="flex flex-col gap-2">
            <FormKit
                type="hidden"
                name="type"
                value="building"
                contenteditable="false"
            />
            <Divider/>
            <FormKit type="list" :value="[]" name="names" dynamic #default="{ items, node, value }">
              <FormKit
                  type="group"
                  v-for="(item, index) in items"
                  :key="item"
                  :index="index"
              >
                <div class="flex flex-col gap-1 bg-gray-100 rounded-md shadow-md p-2 border border-gray-300">
                  <div class="grid grid-cols-2 gap-2">
                    <FormKit
                        type="text"
                        name="name"
                        label="Name"
                        placeholder="Name eingeben"
                        outer-class="max-w-full"
                    />
                    <FormKit
                        type="text"
                        name="source"
                        label="Quelle"
                        placeholder="Quelle eingeben"
                        outer-class="max-w-full"
                    />
                  </div>
                  <button
                      type="button"
                      @click="() => node.input(value?.filter((_, i) => i !== index))"
                      class="border border-blue-600 text-blue-600 p-1 rounded-md shadow-sm hover:shadow-md bg-red-100 font-bold max-w-1/7 mx-auto"
                  >
                    Entfernen
                  </button>
                </div>
              </FormKit>
              <button
                  type="button"
                  @click="() => node.input(value?.concat({ name: '', quelle: '' }))"
                  class="border border-blue-600 text-blue-600 p-2 rounded-md shadow-sm hover:shadow-md bg-blue-50 font-bold max-w-1/6 mx-auto"
              >Namen hinzufügen</button>
            </FormKit>
            <Divider/>
            <FormKit type="list" :value="[]" name="addresses" dynamic #default="{ items, node, value }">
              <FormKit
                  type="group"
                  v-for="(item, index) in items"
                  :key="item"
                  :index="index"
              >
                <div class="flex flex-col gap-1 bg-gray-100 rounded-md shadow-md p-2 border border-gray-300">
                  <div class="grid grid-cols-2 gap-2">
                    <FormKit
                        type="select"
                        name="street"
                        label="Straße"
                        outer-class="max-w-full"
                        select-icon="select"
                        :options="[{ label: 'Keine Auswahl', value: null },
                        ...street_store.streets.map(p => ({label: p.name, value: { id: p.id, name: p.name }})) as any
                        ]"
                    />
                    <FormKit
                        type="text"
                        name="houseNumber"
                        label="Hausnummer"
                        placeholder="145"
                        outer-class="max-w-full"
                    />
                    <FormKit
                        type="text"
                        name="fromDate"
                        label="Von Datum"
                        placeholder="1600"
                        outer-class="max-w-full"
                    />
                    <FormKit
                        type="text"
                        name="toDate"
                        label="Bis Datum"
                        placeholder="1865"
                        outer-class="max-w-full"
                    />
                  </div>
                  <button
                      type="button"
                      @click="() => node.input(value?.filter((_, i) => i !== index))"
                      class="border border-blue-600 text-blue-600 p-1 rounded-md shadow-sm hover:shadow-md bg-red-100 font-bold max-w-1/7 mx-auto"
                  >
                    Entfernen
                  </button>
                </div>
              </FormKit>
              <button
                  type="button"
                  @click="() => node.input(value?.concat({ street: {}, houseNumber: '', fromDate: '', toDate: '' }))"
                  class="border border-blue-600 text-blue-600 p-2 rounded-md shadow-sm hover:shadow-md bg-blue-50 font-bold max-w-1/6 mx-auto"
              >Adressen hinzufügen</button>
            </FormKit>
            <Divider/>
            <div class="md:grid md:grid-cols-2 gap-2 flex flex-col">
              <FormKit
                  type="text"
                  name="partType"
                  label="Bauteil"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                type="text"
                name="specialStatus"
                label="Sonderstatus"
                prefix-icon="text"
                outer-class="max-w-full"
              />
            </div>
            <div class="md:grid md:grid-cols-2 gap-2 flex flex-col">
              <FormKit
                  type="select"
                  name="quarter"
                  label="Viertel"
                  outer-class="max-w-full"
                  select-icon="select"
                  :options="[{ label: 'Keine Auswahl', value: null },
                  ...quarter_store.quarters.map(p => ({label: p.name, value: { id: p.id, name: p.name }})) as any
                  ]"
              />
              <FormKit
                  type="select"
                  name="district"
                  label="Distrikt"
                  outer-class="max-w-full"
                  select-icon="select"
                  :options="[{ label: 'Keine Auswahl', value: null },
                  ...district_store.districts.map(p => ({label: p.name, value: { id: p.id, name: p.name }})) as any
                  ]"
              />
            </div>
            <div class="md:grid md:grid-cols-2 gap-2 flex flex-col">
              <FormKit
                  type="number"
                  number
                  name="houseNumber"
                  label="Hausnummer"
                  prefix-icon="number"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="text"
                  name="districtHouseNumber"
                  label="Distrikt & Hausnummer"
                  prefix-icon="text"
                  outer-class="max-w-full"
                  help="Schreibweise: Distrikt/Historische Hausnummer"
              />
            </div>
            <div class="md:grid md:grid-cols-2 flex flex-col gap-2">
              <FormKit
                  type="select"
                  multiple
                  name="primarySources"
                  label="Primärquellen"
                  outer-class="max-w-full"
                  select-icon="select"
                  :options="[{ label: 'Keine Auswahl', value: null },
                  ...source_store.sources.map(p => ({label: p.title, value: { id: p.id, title: p.title }})) as any
                  ]"
                  help="Halten Sie die Strg-Taste gedrückt, um mehrere Quellen auszuwählen"
              />
              <FormKit
                  type="select"
                  multiple
                  name="secondarySources"
                  label="Sekundärquellen"
                  outer-class="max-w-full"
                  select-icon="select"
                  :options="[{ label: 'Keine Auswahl', value: null },
                  ...source_store.sources.map(p => ({label: p.title, value: { id: p.id, title: p.title }})) as any
                  ]"
                  help="Halten Sie die Strg-Taste gedrückt, um mehrere Quellen auszuwählen"
              />
            </div>
            <div class="flex flex-col gap-2">
              <FormKit
                  type="textarea"
                  name="internalNotes"
                  label="Notizen intern"
                  prefix-icon="list"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="textarea"
                  name="generalNotes"
                  label="Notizen allgemein"
                  prefix-icon="list"
                  outer-class="max-w-full"
              />
            </div>
          </div>
        </FormKit>
        <div v-if="geometry_type">
          <FormKit type="group" name="geometry">
            <div class="flex flex-col gap-2">
              <FormKit
                  type="hidden"
                  name="type"
                  label="Geometrietyp"
                  v-model="geometry_type"
              />
              <FormKit
                  type="hidden"
                  name="coordinates"
                  label="Koordinaten"
                  v-model="coordinates"
              />
            </div>
          </FormKit>
        </div>
      </div>
      <Fieldset class="mb-4">
        <template #legend>
          <div class="montserrat-headline font-semibold text-black text-xl">Eingabe-Vorschau</div>
        </template>
        <div class="max-h-[500px] overflow-y-auto bg-gray-100 border border-gray-300 rounded-md">
          <pre wrap class="text-sm p-2">{{ value }}</pre>
        </div>
      </Fieldset>
      <FormKit
          type="submit"
          :label="props.action === 'create' ? 'Erstellen' : 'Ändern'"
      />
    </FormKit>
  </div>
</template>

<style scoped>

</style>
