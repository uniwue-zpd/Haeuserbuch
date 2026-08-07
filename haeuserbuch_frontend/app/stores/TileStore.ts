import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Tile } from '~/utils/types';
import type { RasterLayerSpecification, RasterSourceSpecification} from "maplibre-gl";

export const useTileStore = defineStore('tile', () => {
    const tiles = ref<Tile[]>([] as Tile[]);
    const baseSources = ref<Record<string, RasterSourceSpecification>>({
        osm: {
            type: 'raster',
            tiles: ['https://tile.openstreetmap.org/{z}/{x}/{y}.png'],
            tileSize: 256,
            attribution: '&copy; OpenStreetMap Contributors'
        }
    });
    const baseLayers = ref<RasterLayerSpecification[]>([
        {
            id: 'osm-layer',
            type: 'raster',
            source: 'osm'
        }
    ]);
    const historicalSources = ref<Record<string, RasterSourceSpecification>>({});
    const historicalLayers = ref<RasterLayerSpecification[]>([]);
    const sources = computed(() => ({
        ...baseSources.value,
        ...historicalSources.value,
    }));
    const layers = computed(() => [
        ...baseLayers.value,
        ...historicalLayers.value,
    ]);

    const isLoaded = computed(() => tiles.value.length > 0);

    async function fetchTiles() {
        if (isLoaded.value) return;
        const {data, error} = await useFetch('/tiles/index.json');
        if (error.value) {
            console.error('Error fetching tiles:', error.value);
            return;
        }
        const renderedTiles = (data.value as Tile[]).filter(
            tile => tile.format === 'webp'
        );
        tiles.value = renderedTiles;
        getMaplibreSources(renderedTiles);
    }

    function getMaplibreSources(tileList: Tile[]): void {
        tileList.forEach(tile => {
            if (!historicalSources.value[tile.id]) {
                const source: RasterSourceSpecification = {
                    type: 'raster',
                    tiles: tile.tiles,
                    tileSize: 256,
                    bounds: tile.bounds,
                    minzoom: tile.minzoom,
                    maxzoom: tile.maxzoom,
                    attribution: '&copy;'
                };
                const layer: RasterLayerSpecification = {
                    id: `${tile.id}-layer`,
                    type: 'raster',
                    source: tile.id,
                    paint: {
                        'raster-fade-duration': 0
                    }
                };
                historicalSources.value[tile.id] = source;
                historicalLayers.value.push(layer);
            }
        });
    }

    return {
        tiles,
        baseSources,
        baseLayers,
        historicalSources,
        historicalLayers,
        sources,
        layers,
        fetchTiles
    };
});
