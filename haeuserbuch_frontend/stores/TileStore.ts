import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Tile } from '~/utils/types';
import type { RasterLayerSpecification, RasterSourceSpecification} from "maplibre-gl";

export const useTileStore = defineStore('tile', () => {
    const tiles = ref<Tile[]>([] as Tile[]);
    const sources = ref<Record<string, RasterSourceSpecification>>({
        osm: {
            type: 'raster',
            tiles: ['https://tile.openstreetmap.org/{z}/{x}/{y}.png'],
            tileSize: 256,
            attribution: '&copy; OpenStreetMap Contributors'
        }
    });
    const layers = ref<RasterLayerSpecification[]>([
        {
            id: 'osm-layer',
            type: 'raster',
            source: 'osm'
        }
    ]);

    const isLoaded = computed(() => tiles.value.length > 0);

    async function fetchTiles() {
        if (isLoaded.value) return;
        const {data, error} = await useFetch('/index.json', { baseURL: useRuntimeConfig().tileserverApiUrl });
        if (error.value) {
            console.error('Error fetching tiles:', error.value, 'Setting OSM as default tile');
            return;
        }
        tiles.value = data.value as Tile[];
        getMaplibreSources(data.value as Tile[]);
    }

    function getMaplibreSources(tileList: Tile[]): void {
        tileList.forEach(tile => {
            if (!sources.value[tile.id]) {
                sources.value[tile.id] = {
                    type: 'raster',
                    tiles: tile.tiles,
                    tileSize: 256,
                    attribution: '&copy;'
                };
                layers.value.push({
                    id: `${tile.id}-layer`,
                    type: 'raster',
                    source: tile.id
                });
            }
        });
    }

    return {
        tiles,
        sources,
        layers,
        fetchTiles
    };
});
