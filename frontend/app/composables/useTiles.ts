import type { RasterLayerSpecification, RasterSourceSpecification } from "maplibre-gl";

/** Base OpenStreetMap source, static for the whole app. */
const OSM_SOURCES: Record<string, RasterSourceSpecification> = {
    osm: {
        type: "raster",
        tiles: ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
        tileSize: 256,
        attribution: "&copy; OpenStreetMap Contributors"
    }
};

/** Base OpenStreetMap layer, static for the whole app. */
const OSM_LAYERS: RasterLayerSpecification[] = [
    { id: "osm-layer", type: "raster", source: "osm" }
];

export const useTiles = () => {

    /** Rendered (`webp`) historical tile sets, shared app-wide and transferred from SSR via the payload. */
    const tiles = useState<Tile[]>("tiles", () => []);

    /**
     * Whether loading the tile index failed (e.g. no tile server running in dev).
     * Transferred from SSR, so the client doesn't retry a request the server already saw fail.
     */
    const tilesUnavailable = useState<boolean>("tiles-unavailable", () => false);

    /**
     * `GET` Loads the tile index once and keeps the rendered (`webp`) tile sets.
     * Call it once before maps mount (see `plugins/tiles.ts`); later calls are no-ops.
     * If the tile server is unreachable, maps fall back to the OpenStreetMap base layer and a short warning is logged.
     * @returns Promise resolving when the tiles are stored or marked as unavailable; never rejects
     */
    const loadTiles = async (): Promise<void> => {
        if (tiles.value.length || tilesUnavailable.value) return;
        try {
            const index = await $fetch<Tile[]>("/tiles/index.json");
            tiles.value = index.filter((tile) => tile.format === "webp");
        } catch (error) {
            tilesUnavailable.value = true;
            const status = (error as { statusCode?: number }).statusCode;
            console.warn(`Historical tiles unavailable${status ? ` (HTTP ${status})` : ""}; maps use the OpenStreetMap base layer only.`);
        }
    };

    /** MapLibre sources of the historical tile sets, keyed by tile ID. */
    const historicalSources = computed<Record<string, RasterSourceSpecification>>(() =>
        Object.fromEntries(tiles.value.map((tile) => [tile.id, {
            type: "raster",
            tiles: tile.tiles,
            tileSize: 256,
            bounds: tile.bounds,
            minzoom: tile.minzoom,
            maxzoom: tile.maxzoom,
            attribution: "&copy;"
        } satisfies RasterSourceSpecification]))
    );

    /** MapLibre layers of the historical tile sets, one per source. */
    const historicalLayers = computed<RasterLayerSpecification[]>(() =>
        tiles.value.map((tile) => ({
            id: `${tile.id}-layer`,
            type: "raster",
            source: tile.id,
            paint: { "raster-fade-duration": 0 }
        }))
    );

    /** OpenStreetMap base source only. */
    const baseSources = computed(() => OSM_SOURCES);

    /** OpenStreetMap base layer only. */
    const baseLayers = computed(() => OSM_LAYERS);

    /** Base and historical sources combined. */
    const sources = computed(() => ({ ...OSM_SOURCES, ...historicalSources.value }));

    /** Base and historical layers combined; historical layers are drawn on top of the base layer. */
    const layers = computed(() => [...OSM_LAYERS, ...historicalLayers.value]);

    return {
        tiles,
        tilesUnavailable,
        baseSources,
        baseLayers,
        historicalSources,
        historicalLayers,
        sources,
        layers,
        loadTiles
    };
};
