<script setup lang="ts">
import { computed, onMounted } from "vue";
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification } from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";
import { MaplibreTerradrawControl } from '@watergis/maplibre-gl-terradraw';
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';
import type { Position } from 'geojson';

const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  building?: Feature;
}>();

const toast = useToast();
const submitted = ref(false);
const building_store = useBuildingStore();

const tile_store = useTileStore();
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;
const draw = new MaplibreTerradrawControl({
  modes: ['render','point', 'polygon','select','delete-selection','delete','download'],
  open: true,
});
const coordinates = ref<Position | Position[] | Position[][] | null>(null);
const geometry_type = ref<string | null>(null);

type BuildingInput = Omit<Feature, 'id' | 'createdBy' | 'createdDate' | 'lastModifiedBy' | 'lastModifiedDate'>;

const submit = async (formData: Partial<BuildingInput>) => {
  try {
    if (props.action === 'create') {
      await building_store.createBuilding(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('building_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.building?.id) {
      await building_store.updateBuilding(formData, props.building.id);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/buildings/${props.building?.id}`);
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
  map = initMap(
      'form_map_building',
      DEFAULT_MAP_CENTER,
      13,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  map.addControl(draw, "top-left");
  const drawInstance = draw.getTerraDrawInstance();
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
    <div id="form_map_building" class="h-[500px] w-full rounded-md"/>
    <FormKit
        type="form"
        id="building_creation"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="props.building ? props.building : {}"
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
            <div class="flex flex-row space-x-5">
              <FormKit
                  type="text"
                  name="name"
                  label="Name"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="number"
                  number
                  name="houseNumber"
                  label="Hausnummer"
                  prefix-icon="number"
                  outer-class="max-w-full"
              />
            </div>
            <div class="flex flex-row space-x-5">
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
            <div class="flex flex-row space-x-5">
              <FormKit
                  type="text"
                  name="quarter"
                  label="Viertel"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="select"
                  name="district"
                  label="Distrikt"
                  :options="[
                    { label: '', value: null },
                    { label: 'I', value: 'I' },
                    { label: 'II', value: 'II' },
                    { label: 'III', value: 'IV' },
                    { label: 'IV', value: 'III' },
                    { label: 'V', value: 'V' }
                  ]"
                  select-icon="select"
                  outer-class="max-w-full"
              />
              <FormKit
                  type="text"
                  name="districtHouseNumber"
                  label="Distrikt & Hausnummer"
                  prefix-icon="text"
                  outer-class="max-w-full"
              />
            </div>
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
      <div class="border-solid border-2 rounded-md p-5 bg-[#F1F2F5] mb-2">
        <div class="font-mono">geoJSON-Preview</div>
        <hr>
        <pre wrap class="text-sm">{{ value }}</pre>
      </div>
      <FormKit
          type="submit"
          label="Erstellen"
      />
    </FormKit>
  </div>
</template>

<style scoped>

</style>