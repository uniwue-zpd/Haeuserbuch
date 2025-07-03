import { defineStore } from 'pinia';
import { ref } from 'vue';
import apiClient from '~/service/api';
import type { Tile } from '~/utils/types';
import type { RasterLayerSpecification, RasterSourceSpecification} from "maplibre-gl";

export const useTileStore = defineStore('tile', () => {
    const tiles = ref<Tile[]>([] as Tile[]);
    const sources = ref<Record<string, RasterSourceSpecification>>({
        osm: {
            type: 'raster',
            tiles: ['https://tile.openstreetmap.de/{z}/{x}/{y}.png'],
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
        try {
            const response = await apiClient.get('/tiles/index.json');
            tiles.value = response.data;
            getMaplibreSources(response.data)
        } catch (error) {
            console.warn('Error fetching tiles:', error, 'Setting OSM as default tile');
        }
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
