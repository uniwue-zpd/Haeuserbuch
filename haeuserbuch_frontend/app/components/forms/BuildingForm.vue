<script setup lang="ts">
import { computed, onMounted } from "vue";
import maplibregl, {
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import { initMap } from "~/service/map_init";
import {
  MaplibreTerradrawControl,
  roundFeatureCoordinates,
} from "@watergis/maplibre-gl-terradraw";
import "@watergis/maplibre-gl-terradraw/dist/maplibre-gl-terradraw.css";
import type { Position } from "geojson";
import type { GeoJSONStoreGeometries } from "terra-draw";
import type { Feature } from "~/utils/GeoJsonTypes";
import { useToast as useNuxtToast } from "@nuxt/ui/composables";

const props = defineProps<{
  header: string;
  action: "create" | "edit";
  building?: Feature;
}>();

const toast = useNuxtToast();
const buildingStore = useBuildingStore();

const tile_store = useTileStore();
const sources = computed(() => tile_store.sources);
const layers = computed(() => tile_store.layers);
let map: maplibregl.Map | null = null;
const draw = new MaplibreTerradrawControl({
  modes: [
    "render",
    "point",
    "polygon",
    "linestring",
    "select",
    "delete-selection",
    "delete",
    "download",
  ],
  open: true,
  adapterOptions: {
    coordinatePrecision: 9,
  },
});
const coordinates = ref<Position | Position[] | Position[][] | null>(null);
const geometry_type = ref<string | null>(null);

const center = computed<[number, number]>(() => {
  if (!props.building || !props.building.geometry) {
    console.warn("No building geometry, using default map center");
    return DEFAULT_MAP_CENTER;
  }
  const geom = props.building.geometry;
  switch (geom.type) {
    case "Point":
      return geom.coordinates as [number, number];
    case "Polygon":
      return (geom.coordinates as number[][][])[0][0] as [number, number];
    case "MultiPolygon":
      return (geom.coordinates as number[][][][])[0][0][0] as [number, number];
    case "LineString":
      return (geom.coordinates as number[][])[0] as [number, number];
    default:
      return DEFAULT_MAP_CENTER;
  }
});

/* FormKit-friendly initial value */
const initialValue = computed(() => {
  if (!props.building) return {};
  const clone = JSON.parse(JSON.stringify(props.building));
  if (
    !clone.geometry ||
    typeof clone.geometry !== "object" ||
    !clone.geometry.coordinates
  ) {
    delete clone.geometry;
  }
  return clone;
});

const submit = async (formData: Partial<Feature>) => {
  if (props.action === "create") {
    try {
      await buildingStore.createBuilding(formData);
      const form = getNode(`${props.action}_building`);
      form?.reset();
      toast.add({
        color: "success",
        title: "Erfolg",
        description: "Erfolgreich erstellt",
        duration: 3000,
      });
    } catch (e) {
      console.error(e);
      toast.add({
        color: "error",
        title: "Fehler",
        description: "Fehler beim Erstellen des Gebäude-Objektes",
        duration: 3000,
      });
    }
  } else if (props.action === "edit" && props.building?.id) {
    try {
      const id = props.building.id;
      await buildingStore.updateBuilding(id, formData);
      toast.add({
        color: "success",
        title: "Erfolg",
        description: "Erfolgreich upgedated",
        duration: 3000,
      });
      navigateTo(`/buildings/${id}`);
    } catch (e) {
      console.error(e);
      toast.add({
        color: "error",
        title: "Fehler",
        description: "Fehler beim Updaten des Gebäude-Objektes",
        duration: 3000,
      });
    }
  }
};

onMounted(async () => {
  await nextTick();
  map = initMap(
    "form_map_building",
    center.value,
    17,
    sources.value as Record<string, RasterSourceSpecification>,
    // @ts-ignore
    layers.value as RasterLayerSpecification[],
  );
  map.addControl(draw, "top-left");
  const drawInstance = draw.getTerraDrawInstance();
  map.once("load", () => {
    if (drawInstance && props.building && props.building.geometry) {
      const geojson = roundFeatureCoordinates(
        [
          {
            type: "Feature",
            geometry: {
              type: props.building.geometry.type,
              coordinates: props.building.geometry.coordinates,
            } as GeoJSONStoreGeometries,
            properties: {
              mode: props.building.geometry.type.toLowerCase(),
            },
          },
        ],
        9,
      );
      drawInstance?.addFeatures(geojson);
      coordinates.value = props.building.geometry.coordinates as any;
      geometry_type.value = props.building.geometry.type;
    }
  });
  if (drawInstance) {
    drawInstance.on("finish", (id) => {
      const snapshot = drawInstance.getSnapshot();
      const feature = snapshot?.find((feature) => feature.id === id);
      if (feature) {
        coordinates.value = feature.geometry.coordinates;
        geometry_type.value = feature.geometry.type;
      }
    });
  }
  draw.on("feature-deleted", () => {
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
  <div class="mx-auto flex w-4/5 flex-col gap-4">
    <h1 class="text-2xl text-black font-bold">{{ props.header }}</h1>
    <p>
      Füllen Sie bitte die untenstehenden Felder aus, um ein Objekt zu erstellen
      oder anzupassen. Falls Sie ein Gebäude mit Koordinaten versehen möchten,
      können Sie dies auf der Karte tun.
    </p>
    <Message
      v-if="(props.building?.geometry?.coordinates.length ?? 0) > 1"
      severity="error"
    >
      Polygone mit inneren Ringen können aktuell nicht angezeigt werden
    </Message>
    <div id="form_map_building" class="h-128 w-full rounded-md" />
    <FormKit
      type="form"
      :id="`${props.action}_building`"
      submit-label="Erstellen"
      @submit="submit"
      :actions="false"
      :value="initialValue"
      :key="props.building?.id || 'create'"
      #default="{ value }"
    >
      <div
        class="flex flex-col gap-2 bg-gray-100 rounded-md shadow-md p-4 border border-gray-200 mb-4"
      >
        <FormKit type="hidden" name="type" value="Feature" />
        <FormKit type="group" name="properties">
          <div class="flex flex-col gap-2">
            <FormKit
              type="hidden"
              name="type"
              value="building"
              contenteditable="false"
            />
            <FormKit
              type="list"
              :value="[]"
              name="names"
              dynamic
              #default="{ items, node, value }"
            >
              <FormKit
                type="group"
                v-for="(item, index) in items"
                :key="item"
                :index="index"
              >
                <div
                  class="flex flex-col gap-1 bg-gray-200 rounded-md p-4 border border-gray-300"
                >
                  <div class="grid grid-cols-2 gap-2">
                    <FormKit
                      type="entityAutocomplete"
                      entityType="source"
                      optionLabel="title"
                      name="source"
                      label="Quelle"
                      outer-class="max-w-full"
                    />
                    <FormKit
                      type="text"
                      name="name"
                      label="Name"
                      placeholder="Zum goldenen Löwen"
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
                    @click="
                      () => node.input(value?.filter((_, i) => i !== index))
                    "
                    class="text-sm border border-red-600 text-red-600 p-1 rounded-md shadow-sm hover:shadow-md bg-blue-50 font-medium max-w-1/7 mx-auto"
                  >
                    Entfernen
                  </button>
                </div>
              </FormKit>
              <button
                type="button"
                @click="
                  () =>
                    node.input(
                      value?.concat({
                        source: {},
                        name: '',
                        fromDate: '',
                        toDate: '',
                      }),
                    )
                "
                class="text-sm border border-blue-600 text-blue-600 p-1 rounded-md bg-blue-50 font-medium max-w-1/6 mx-auto"
              >
                Namen hinzufügen
              </button>
            </FormKit>
            <Divider />
            <FormKit
              type="list"
              :value="[]"
              name="addresses"
              dynamic
              #default="{ items, node, value }"
            >
              <FormKit
                type="group"
                v-for="(item, index) in items"
                :key="item"
                :index="index"
              >
                <div
                  class="flex flex-col gap-1 bg-gray-200 rounded-md shadow-md p-4 border border-gray-300"
                >
                  <div class="grid grid-cols-2 gap-2">
                    <FormKit
                      type="entityAutocomplete"
                      entityType="street"
                      optionLabel="name"
                      name="street"
                      label="Straße"
                      outer-class="max-w-full"
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
                    @click="
                      () => node.input(value?.filter((_, i) => i !== index))
                    "
                    class="text-sm border border-red-600 text-red-600 p-1 rounded-md shadow-sm hover:shadow-md bg-blue-50 font-medium max-w-1/7 mx-auto"
                  >
                    Entfernen
                  </button>
                </div>
              </FormKit>
              <button
                type="button"
                @click="
                  () =>
                    node.input(
                      value?.concat({
                        street: {},
                        houseNumber: '',
                        fromDate: '',
                        toDate: '',
                      }),
                    )
                "
                class="text-sm border border-blue-600 text-blue-600 p-1 rounded-md bg-blue-50 font-medium max-w-1/6 mx-auto"
              >
                Adressen hinzufügen
              </button>
            </FormKit>
            <Divider />
            <FormKit
              type="number"
              name="year"
              label="Jahr"
              placeholder="1832"
              outer-class="max-w-full"
            />
            <FormKit
              type="number"
              name="parcelNumber"
              label="Flurstücksnummer"
              placeholder="88356"
              outer-class="max-w-full"
            />
            <FormKit
              type="number"
              name="parcelNumberCounter"
              label="Flurstücksnummerzähler"
              placeholder="3"
              outer-class="max-w-full"
            />
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
                name="object"
                label="Objekt"
                prefix-icon="text"
                outer-class="max-w-full"
              />
            </div>
            <div class="md:grid md:grid-cols-2 gap-2 flex flex-col">
              <FormKit
                type="entityAutocomplete"
                entityType="quarter"
                optionLabel="name"
                name="quarter"
                label="Viertel"
                outer-class="max-w-full"
              />
              <FormKit
                type="entityAutocomplete"
                entityType="district"
                optionLabel="name"
                name="district"
                label="Distrikt"
                outer-class="max-w-full"
              />
            </div>
            <div class="md:grid md:grid-cols-2 gap-2 flex flex-col">
              <FormKit
                type="number"
                number
                name="propertyNumber"
                label="Besitznummer"
                prefix-icon="number"
                outer-class="max-w-full"
              />
              <FormKit
                type="text"
                name="districtPropertyNumber"
                label="Distrikt & Besitznummer"
                prefix-icon="text"
                outer-class="max-w-full"
                help="Schreibweise: Distrikt/Historische Besitznummer"
              />
            </div>
            <FormKit
              type="entityAutocomplete"
              entityType="source"
              optionLabel="title"
              name="sources"
              label="Quellen (Mehrfachauswahl möglich)"
              :isMultiple="true"
              outer-class="max-w-full"
            />
            <FormKit
              type="entityAutocomplete"
              entityType="source"
              optionLabel="title"
              name="literature"
              label="Literatur (Mehrfachauswahl möglich)"
              :isMultiple="true"
              outer-class="max-w-full"
            />
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
          <div class="font-semibold text-black text-xl">Eingabe-Vorschau</div>
        </template>
        <div
          class="max-h-128 overflow-y-auto rounded-md border border-gray-300 bg-gray-100"
        >
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
