/*
    Loads the tile index once before the app renders,
    so every map has its MapLibre sources and layers available at mount.
    Never blocks the app: if the tile server is down, `loadTiles` logs a warning and maps use the base layer only.
*/
export default defineNuxtPlugin(async () => {
    await useTiles().loadTiles();
});
