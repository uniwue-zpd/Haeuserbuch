<script setup lang="ts">
import type { Color, PickingInfo } from '@deck.gl/core';
import { PathLayer, PolygonLayer, ScatterplotLayer } from '@deck.gl/layers';
import { MapboxOverlay } from '@deck.gl/mapbox';
import maplibregl, { type MapMouseEvent, type RasterLayerSpecification, type RasterSourceSpecification } from 'maplibre-gl';
import 'maplibre-gl/dist/maplibre-gl.css';
import { computed, onMounted, ref, watch } from 'vue';
import { initMap } from '~/service/map_init';
import type { BuildingFeature } from '~/utils/GeoJsonTypes';

type Position = [number, number];

interface PolygonDatum {
  feature: BuildingFeature;
  polygon: Position[][];
}

interface LineDatum {
  feature: BuildingFeature;
  path: Position[];
}

interface PointDatum {
  feature: BuildingFeature;
  position: Position;
}

type BuildingDatum = PolygonDatum | LineDatum | PointDatum;

const props = withDefaults(defineProps<{
  features: BuildingFeature[];
  filteredIds: number[];
  selectedId?: number | null;
  selectionBottomPadding?: number;
}>(), {
  selectedId: null,
  selectionBottomPadding: 0,
});

const emit = defineEmits<{
  select: [id: number];
}>();

const tileStore = useTileStore();
const mapContainer = ref<HTMLElement | null>(null);
const mapTilesLoaded = ref(false);
const buildingExtrusionsRendered = ref(false);
const sources = computed(() => tileStore.historicalSources);
const layers = computed(() => tileStore.historicalLayers);
const isMapLoading = computed(() => !mapTilesLoaded.value || !buildingExtrusionsRendered.value);
const georeferencedFeatures = computed(() => props.features.filter(feature => feature.geometry !== null));
const featureById = computed(() => new Map(props.features.map(feature => [feature.id, feature])));
const matchingIds = computed(() => new Set(props.filteredIds));
const filteredIdKey = computed(() => props.filteredIds.join(','));

const polygonData = computed<PolygonDatum[]>(() => georeferencedFeatures.value.flatMap((feature) => {
  if (feature.geometry?.type === 'Polygon') {
    return [{ feature, polygon: feature.geometry.coordinates }];
  }
  if (feature.geometry?.type === 'MultiPolygon') {
    return feature.geometry.coordinates.map(polygon => ({ feature, polygon }));
  }
  return [];
}));

const lineData = computed<LineDatum[]>(() => georeferencedFeatures.value.flatMap((feature) => {
  if (feature.geometry?.type !== 'LineString') return [];
  return [{ feature, path: feature.geometry.coordinates }];
}));

const pointData = computed<PointDatum[]>(() => georeferencedFeatures.value.flatMap((feature) => {
  if (feature.geometry?.type !== 'Point') return [];
  return [{ feature, position: feature.geometry.coordinates }];
}));

const selectedPolygonData = computed<PolygonDatum[]>(() => {
  const feature = props.selectedId ? featureById.value.get(props.selectedId) : null;
  if (feature?.geometry?.type === 'Polygon') {
    return [{ feature, polygon: feature.geometry.coordinates }];
  }
  if (feature?.geometry?.type === 'MultiPolygon') {
    return feature.geometry.coordinates.map(polygon => ({ feature, polygon }));
  }
  return [];
});

const selectedLineData = computed<LineDatum[]>(() => {
  const feature = props.selectedId ? featureById.value.get(props.selectedId) : null;
  if (feature?.geometry?.type !== 'LineString') return [];
  return [{ feature, path: feature.geometry.coordinates }];
});

const selectedPointData = computed<PointDatum[]>(() => {
  const feature = props.selectedId ? featureById.value.get(props.selectedId) : null;
  if (feature?.geometry?.type !== 'Point') return [];
  return [{ feature, position: feature.geometry.coordinates }];
});

const districtColors: Record<string, readonly [number, number, number]> = {
  I: [216, 73, 69],
  II: [63, 120, 168],
  III: [79, 147, 96],
  IV: [216, 135, 40],
  V: [130, 91, 148],
};

const defaultDistrictColor = [127, 137, 146] as const;
let map: maplibregl.Map | null = null;
let deckOverlay: MapboxOverlay | null = null;
let resizeObserver: ResizeObserver | null = null;
let mapLoaded = false;
let baseStyleRevision = 0;
let selectionCameraFrame: number | null = null;

function isMatching(feature: BuildingFeature) {
  return matchingIds.value.has(feature.id);
}

function getDistrictColor(feature: BuildingFeature) {
  return districtColors[feature.properties.district?.name ?? ''] ?? defaultDistrictColor;
}

function getPolygonColor({ feature }: PolygonDatum): Color {
  if (!isMatching(feature)) return [184, 190, 194, 68];
  const [red, green, blue] = getDistrictColor(feature);
  return [red, green, blue, 220];
}

function getPolygonWireframeColor({ feature }: PolygonDatum): Color {
  if (!isMatching(feature)) return [100, 116, 139, 42];
  return [51, 65, 85, 145];
}

function getBuildingElevation({ feature }: PolygonDatum) {
  return isMatching(feature) ? 10 : 6;
}

function getLineColor({ feature }: LineDatum): Color {
  if (isMatching(feature)) return [200, 101, 32, 225];
  return [127, 137, 146, 58];
}

function getPointColor({ feature }: PointDatum): Color {
  if (isMatching(feature)) return [217, 111, 30, 235];
  return [127, 137, 146, 68];
}

function getPolygonGeometry(datum: PolygonDatum) {
  return datum.polygon;
}

function getLineGeometry(datum: LineDatum) {
  return datum.path;
}

function getPointPosition(datum: PointDatum) {
  return datum.position;
}

function getLineWidth({ feature }: LineDatum) {
  return isMatching(feature) ? 7 : 6;
}

function getPointRadius({ feature }: PointDatum) {
  return isMatching(feature) ? 6 : 5;
}

function getPointLineWidth({ feature }: PointDatum) {
  return isMatching(feature) ? 2 : 1;
}

function pickBuildingAt(point: { x: number; y: number }) {
  if (!deckOverlay) return null;

  const info = deckOverlay.pickObject({
    x: point.x,
    y: point.y,
    radius: 3,
  }) as PickingInfo<BuildingDatum> | null;

  return info?.object?.feature ?? null;
}

function handleMapClick(event: MapMouseEvent) {
  const feature = pickBuildingAt(event.point);
  if (feature) emit('select', feature.id);
}

function createDeckLayers() {
  const revision = baseStyleRevision;

  return [
    new PolygonLayer<PolygonDatum>({
      id: 'building-polygons-solid',
      data: polygonData.value,
      getPolygon: getPolygonGeometry,
      extruded: true,
      filled: true,
      stroked: false,
      wireframe: false,
      getElevation: getBuildingElevation,
      getFillColor: getPolygonColor,
      material: {
        ambient: 0.55,
        diffuse: 0.65,
        shininess: 18,
        specularColor: [60, 60, 60],
      },
      pickable: true,
      updateTriggers: {
        getElevation: revision,
        getFillColor: revision,
      },
    }),
    new PolygonLayer<PolygonDatum>({
      id: 'building-polygons-wireframe',
      data: polygonData.value,
      getPolygon: getPolygonGeometry,
      extruded: true,
      filled: false,
      stroked: false,
      wireframe: true,
      getElevation: getBuildingElevation,
      getLineColor: getPolygonWireframeColor,
      pickable: false,
      updateTriggers: {
        getElevation: revision,
        getLineColor: revision,
      },
    }),
    new PathLayer<LineDatum>({
      id: 'building-lines',
      data: lineData.value,
      getPath: getLineGeometry,
      getColor: getLineColor,
      getWidth: getLineWidth,
      widthUnits: 'pixels',
      capRounded: true,
      jointRounded: true,
      pickable: true,
      updateTriggers: {
        getColor: revision,
        getWidth: revision,
      },
    }),
    new ScatterplotLayer<PointDatum>({
      id: 'building-points',
      data: pointData.value,
      getPosition: getPointPosition,
      getRadius: getPointRadius,
      radiusUnits: 'pixels',
      filled: true,
      stroked: true,
      getFillColor: getPointColor,
      getLineColor: [255, 255, 255, 230],
      getLineWidth: getPointLineWidth,
      lineWidthUnits: 'pixels',
      pickable: true,
      updateTriggers: {
        getRadius: revision,
        getFillColor: revision,
        getLineColor: revision,
        getLineWidth: revision,
      },
    }),
    new PolygonLayer<PolygonDatum>({
      id: 'selected-building-polygon-solid',
      data: selectedPolygonData.value,
      getPolygon: getPolygonGeometry,
      extruded: true,
      filled: true,
      stroked: false,
      wireframe: false,
      getElevation: 18,
      getFillColor: [242, 173, 53, 238],
      material: {
        ambient: 0.55,
        diffuse: 0.65,
        shininess: 18,
        specularColor: [60, 60, 60],
      },
      pickable: true,
    }),
    new PolygonLayer<PolygonDatum>({
      id: 'selected-building-polygon-wireframe',
      data: selectedPolygonData.value,
      getPolygon: getPolygonGeometry,
      extruded: true,
      filled: false,
      stroked: false,
      wireframe: true,
      getElevation: 18,
      getLineColor: [23, 42, 58, 255],
      pickable: false,
    }),
    new PathLayer<LineDatum>({
      id: 'selected-building-line-halo',
      data: selectedLineData.value,
      getPath: getLineGeometry,
      getColor: [23, 42, 58, 242],
      getWidth: 15,
      widthUnits: 'pixels',
      capRounded: true,
      jointRounded: true,
      pickable: false,
    }),
    new PathLayer<LineDatum>({
      id: 'selected-building-line',
      data: selectedLineData.value,
      getPath: getLineGeometry,
      getColor: [242, 173, 53, 255],
      getWidth: 9,
      widthUnits: 'pixels',
      capRounded: true,
      jointRounded: true,
      pickable: true,
    }),
    new ScatterplotLayer<PointDatum>({
      id: 'selected-building-point',
      data: selectedPointData.value,
      getPosition: getPointPosition,
      getRadius: 9,
      radiusUnits: 'pixels',
      filled: true,
      stroked: true,
      getFillColor: [242, 173, 53, 255],
      getLineColor: [23, 42, 58, 255],
      getLineWidth: 3,
      lineWidthUnits: 'pixels',
      pickable: true,
    }),
  ];
}

function updateBaseLayers() {
  if (!mapLoaded || !deckOverlay) return;
  baseStyleRevision += 1;
  deckOverlay.setProps({ layers: createDeckLayers() });
}

function updateSelectionLayers() {
  if (!mapLoaded || !deckOverlay) return;
  deckOverlay.setProps({ layers: createDeckLayers() });
}

function handleBuildingOverlayRender() {
  if (!mapTilesLoaded.value || !map?.areTilesLoaded() || map.isMoving()) return;
  buildingExtrusionsRendered.value = true;
}

function addBuildingOverlay() {
  if (!map) return;
  deckOverlay = new MapboxOverlay({
    interleaved: false,
    layers: createDeckLayers(),
    onAfterRender: handleBuildingOverlayRender,
    onError: (error) => console.error('Unable to render the building overlay', error),
  });
  map.addControl(deckOverlay as unknown as maplibregl.IControl);
}

function handleMapIdle() {
  if (!map || !map.areTilesLoaded()) return;

  mapTilesLoaded.value = true;
  if (buildingExtrusionsRendered.value || !deckOverlay) return;
  deckOverlay.setProps({ layers: createDeckLayers() });
}

function fitSelectedFeature() {
  if (!map || !props.selectedId) return;
  const feature = featureById.value.get(props.selectedId);
  if (!feature?.geometry) return;

  const canvasHeight = map.getCanvas().clientHeight;
  const bottom = Math.max(60, Math.min(props.selectionBottomPadding + 48, canvasHeight * 0.55));
  if (feature.geometry.type === 'Point') {
    map.easeTo({
      center: feature.geometry.coordinates,
      zoom: 17,
      padding: { top: 60, right: 60, bottom, left: 60 },
      duration: 450,
    });
    return;
  }

  const bounds = new maplibregl.LngLatBounds();
  const extend = (coordinates: unknown): void => {
    if (
        Array.isArray(coordinates)
        && coordinates.length === 2
        && typeof coordinates[0] === 'number'
        && typeof coordinates[1] === 'number'
    ) {
      bounds.extend(coordinates as [number, number]);
      return;
    }
    if (Array.isArray(coordinates)) coordinates.forEach(extend);
  };
  extend(feature.geometry.coordinates);
  map.fitBounds(bounds, {
    padding: { top: 60, right: 60, bottom, left: 60 },
    maxZoom: 18,
    duration: 450,
  });
}

function scheduleFitSelectedFeature() {
  if (selectionCameraFrame !== null) cancelAnimationFrame(selectionCameraFrame);
  selectionCameraFrame = requestAnimationFrame(() => {
    selectionCameraFrame = requestAnimationFrame(() => {
      selectionCameraFrame = null;
      fitSelectedFeature();
    });
  });
}

watch(filteredIdKey, updateBaseLayers);

watch(() => props.features, updateBaseLayers);

watch(() => props.selectedId, () => {
  if (!mapLoaded || !map) return;
  updateSelectionLayers();
  scheduleFitSelectedFeature();
});

watch(() => props.selectionBottomPadding, () => {
  if (!mapLoaded || !map) return;
  map.resize();
});

onMounted(() => {
  if (!mapContainer.value) return;
  map = initMap(
       mapContainer.value.id,
       DEFAULT_MAP_CENTER,
       15.5,
       sources.value as Record<string, RasterSourceSpecification>,
      layers.value as RasterLayerSpecification[],
      {
        // Keep the small, static archive tiles available while the user wheel-zooms
        // through intermediate zoom levels instead of cancelling and re-requesting them.
        cancelPendingTileRequestsWhileZooming: false,
        maxTileCacheSize: 512,
        maxTileCacheZoomLevels: 8,
      },
  );
  map.on('idle', handleMapIdle);
  map.on('load', () => {
    addBuildingOverlay();
    mapLoaded = true;
    map?.on('click', handleMapClick);
    fitSelectedFeature();
  });
  resizeObserver = new ResizeObserver(() => map?.resize());
  resizeObserver.observe(mapContainer.value);
});

onBeforeUnmount(() => {
  if (selectionCameraFrame !== null) cancelAnimationFrame(selectionCameraFrame);
  resizeObserver?.disconnect();
  resizeObserver = null;
  map?.off('idle', handleMapIdle);
  map?.off('click', handleMapClick);
  if (map && deckOverlay && map.hasControl(deckOverlay as unknown as maplibregl.IControl)) {
    map.removeControl(deckOverlay as unknown as maplibregl.IControl);
  }
  deckOverlay = null;
  map?.remove();
  map = null;
});
</script>

<template>
  <div
      class="relative h-full w-full border-t border-stone-300 bg-stone-50 dark:border-zinc-700 dark:bg-zinc-950"
      role="region"
      aria-label="Interaktive Karte der Gebäude"
      :aria-busy="isMapLoading"
  >
    <div
        id="buildings-explorer-map"
        ref="mapContainer"
        class="h-full w-full"
    />
    <div
        v-if="isMapLoading"
        class="absolute inset-0 z-20 grid place-items-center bg-stone-50 px-6 text-center dark:bg-zinc-950"
        role="status"
        aria-live="polite"
    >
      <div class="flex flex-col items-center gap-3">
        <span
            class="size-7 animate-spin rounded-full border-2 border-slate-300 border-t-slate-600 motion-reduce:animate-none"
            aria-hidden="true"
        />
        <p class="text-sm font-medium text-slate-600">Karte wird geladen</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.maplibregl-ctrl-top-right) {
  top: 0.75rem;
  right: 0.75rem;
}

:deep(.maplibregl-ctrl-group) {
  overflow: hidden;
  border: 1px solid rgb(255 255 255 / 0.65);
  border-radius: 0.65rem;
  box-shadow: 0 8px 24px rgb(15 23 42 / 0.2);
}

</style>
