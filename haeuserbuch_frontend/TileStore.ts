import { defineStore } from 'pinia';
import { ref } from 'vue';
import apiClient from '~/service/api';
import type { Tile } from '~/utils/types';
import type {RasterSourceSpecification} from "maplibre-gl";

export const useTileStore = defineStore('tile', () => {
    const tiles = ref<Tile[]>([] as Tile[]);

    const isLoaded = computed(() => tiles.value.length > 0);

    async function fetchTiles() {
        if (!isLoaded.value) {
            try {
                const response = await apiClient.get('/tiles/index.json');
                tiles.value = response.data;
            } catch (error) {
                console.log('Error fetching tiles:', error);
            }
        }
    }

    function getMaplibreSources(): Record<string, Partial<RasterSourceSpecification>> {
        const sources: Record<string, Partial<RasterSourceSpecification>> = {};
        tiles.value.forEach(tile => {
            sources[tile.id] = {
                type: 'raster',
                tiles: tile.tiles,
                tileSize: 256,
                attribution: '&copy;'
            }
        })
        return sources;
    }

    return {
        tiles,
        isLoaded,
        fetchTiles,
        getMaplibreSources
    };
})
