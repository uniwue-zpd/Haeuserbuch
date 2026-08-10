/*
    Initializes all available stores
*/
import { useTileStore } from "~/stores/TileStore";

export default defineNuxtPlugin(async (nuxtApp) => {
    const tileStore = useTileStore();
    try {
        await tileStore.fetchTiles();
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
