<script setup lang="ts">
import maplibregl, { type RasterLayerSpecification, type RasterSourceSpecification } from 'maplibre-gl';
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";
import '@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css';
import type { Feature } from "~/utils/GeoJsonTypes";
import {computed} from "vue";

const props = defineProps<{
  header: string;
  action: 'create' | 'edit';
  place?: Feature;
}>();

// Import store
const tile_store = useTileStore();
const place_store = usePlaceStore();

// Map-related constants and variables
const coordinates = ref<[number, number] | null>(null);
let map: maplibregl.Map;
let marker: maplibregl.Marker | null;

// Compute center of the map
const center = computed(() => {
  if (!props.place || !props.place.geometry) {
    console.warn('No place or geometry provided');
    return DEFAULT_MAP_CENTER;
  }
  const place_geometry = props.place.geometry;
  return place_geometry.coordinates as [number, number];
});

// Map sources and layers from the store
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);

// Handle updates of coordinates
const onCoordinatesUpdate = () => {
  if (coordinates.value == null) return;
  if (marker) {
    marker.setLngLat(coordinates.value);
  } else {
    marker = new maplibregl.Marker({ color: "#063D79" })
        .setLngLat(coordinates.value)
        .addTo(map);
  }
  map.setCenter(coordinates.value);
  map.setZoom(7);
};

// Initial value for the form
const initialValue = computed(() => {
  if (!props.place) return {};
  const clone = JSON.parse(JSON.stringify(props.place));
  if (!clone.geometry || typeof clone.geometry !== 'object' || !clone.geometry.coordinates) {
    delete clone.geometry;
  }
  return clone;
});

// API submission
const toast = useToast();
const submitted = ref(false);
const submit = async (formData: Partial<Feature>) => {
  try {
    if (props.action === 'create') {
      await place_store.createPlace(formData);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich erstellt', life: 3000});
      const form = getNode('place_creation');
      form?.reset();
    } else if (props.action === 'edit' && props.place?.id) {
      const id = props.place.id;
      await place_store.updatePlace(formData, props.place.id);
      submitted.value = true;
      toast.add({severity: 'success', summary: 'Erfolg', detail: 'Erfolgreich upgedated', life: 3000});
      navigateTo(`/places/${id}`);
    }
  } catch (error) {
    console.log(error)
    toast.add({
      severity: 'error',
      summary: 'Fehler',
      detail: 'Fehler beim Erstellen des Orts',
      life: 3000
    });
  }
};

onMounted(async () => {
  await nextTick();
  if (props.place?.geometry?.coordinates) {
    coordinates.value = props.place.geometry.coordinates as [number, number];
  }
  map = initMap(
      'form_map_place',
      center.value,
      14,
      0,
      sources.value as Record<string, RasterSourceSpecification>,
      // @ts-ignore
      layers.value as RasterLayerSpecification[]
  );
  if (coordinates.value != null) {
    marker = new maplibregl.Marker({ color: "#063D79" })
        .setLngLat(coordinates.value)
        .addTo(map);
  }
  map.on('click', (e) => {
    coordinates.value = [e.lngLat.lng, e.lngLat.lat];
    if (marker) {
      marker.setLngLat(e.lngLat);
    } else {
      marker = new maplibregl.Marker({ color: "#063D79" })
          .setLngLat(e.lngLat)
          .addTo(map);
    }
  });
  map.on('contextmenu', () => {
    coordinates.value = null;
    if (marker) {
      marker.remove();
      marker = null;
    }
  });
});

onBeforeUnmount(() => {
  if (map) map.remove();
});
</script>

<template>
  <div class="flex flex-col gap-2">
    <h1 class="text-2xl montserrat-headline-headline text-black font-bold">{{ props.header }}</h1>
    <p class="roboto-plain">
      Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen oder anzupassen.
      Falls Sie einen Ort mit Koordinaten versehen möchten, können Sie dies auf der Karte tun.
    </p>
    <div id="form_map_place" class="h-[500px] w-full rounded-md"/>
    <FormKit
        type="form"
        id="place_creation"
        submit-label="Erstellen"
        @submit="submit"
        :actions="false"
        :value="initialValue"
        :key="props.place?.id || 'create'"
        #default="{ value }"
    >
      <div class="flex flex-col gap-2">
        <FormKit type="hidden" name="type" value="Feature" />
        <FormKit type="group" name="properties">
          <div class="flex flex-col gap-2">
            <FormKit
                type="hidden"
                name="type"
                value="place"
                contenteditable="false"
            />
            <FormKit
                type="text"
                name="realName"
                label="Realer Name"
                placeholder="Estenfeld"
                outer-class="max-w-full"
            />
            <div class="max-h-[30vh] overflow-y-auto border border-gray-300 rounded-md p-2 bg-[#F9F9F9]">
              <FormKit type="list" :value="[]" name="altNames" dynamic #default="{ items, node, value }">
                <FormKit
                    v-for="(item, index) in items"
                    :key="item"
                    :index="index"
                    label="Andere bekannte Namen"
                    suffix-icon="trash"
                    @suffix-icon-click="() => node.input(value?.filter((_, i) => i !== index))"
                    :sections-schema="{ suffixIcon: { $el: 'button', attrs: { type: 'button' } } }"
                    outer-class="max-w-full"
                />
                <button
                    type="button"
                    @click="() => node.input(value?.concat(''))"
                    class="border border-blue-600 text-blue-600 p-1 rounded-md shadow-sm hover:shadow-md bg-red-100 font-bold max-w-1/7 mx-auto"
                >Andere Ortsnamen hinzufügen</button>
              </FormKit>
            </div>
            <FormKit
                type="select"
                name="isUncertain"
                label="Ort unsicher?"
                :options="[
                    { label: 'unbekannt', value: null },
                    { label: 'Unsicher', value: true },
                    { label: 'Sicher', value: false }
                  ]"
                select-icon="select"
                outer-class="max-w-full"
            />
            <div class="flex flex-col gap-1 md:flex-row md:space-x-5">
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
        <div v-if="coordinates">
          <FormKit type="group" name="geometry">
            <div class="flex flex-col gap-2">
              <FormKit
                  type="hidden"
                  name="type"
                  value="Point"
                  contenteditable="false"
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
      <!-- Preview of the input value -->
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
