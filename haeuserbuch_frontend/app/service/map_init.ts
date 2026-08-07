import maplibregl, {
    type MapOptions,
    type RasterLayerSpecification,
} from "maplibre-gl";

function getHistoricalBounds(sources: Record<string, maplibregl.SourceSpecification>): maplibregl.LngLatBoundsLike | undefined {
    const rasterBounds = Object.values(sources)
        .filter((source): source is maplibregl.RasterSourceSpecification => source.type === 'raster' && source.bounds !== undefined)
        .map(source => source.bounds!);

    if (rasterBounds.length === 0) return undefined;

    const [west, south, east, north] = rasterBounds.reduce(
        ([minWest, minSouth, maxEast, maxNorth], [sourceWest, sourceSouth, sourceEast, sourceNorth]) => [
            Math.min(minWest, sourceWest),
            Math.min(minSouth, sourceSouth),
            Math.max(maxEast, sourceEast),
            Math.max(maxNorth, sourceNorth)
        ],
        [Infinity, Infinity, -Infinity, -Infinity]
    );

    return [[west, south], [east, north]];
}

/*
    * Initializes a MapLibre `Map` instance with the given parameters.
    * @param containerId - The ID of the HTML element that will contain the map.
    * @param center - The initial center of the map as a [longitude, latitude] tuple.
    * @param zoom - The initial zoom level of the map (default is 12).
    * @param sources - An object containing the map sources, where keys are source IDs and values are source specifications.
    * @param layers - An array of layer specifications to be added to the map.
*/
export function initMap(
    containerId: string,
    center: [number, number],
    zoom: number = 12,
    pitch: number = 0,
    sources: Record<string, maplibregl.SourceSpecification>,
    layers: RasterLayerSpecification[],
    mapOptions: Pick<
        MapOptions,
        'cancelPendingTileRequestsWhileZooming'
        | 'maxTileCacheSize'
        | 'maxTileCacheZoomLevels'
        | 'refreshExpiredTiles'
        | 'fadeDuration'
    > = {},
) {
    const historicalBounds = getHistoricalBounds(sources);
    const map = new maplibregl.Map({
        container: containerId,
        ...mapOptions,
        zoom: zoom,
        pitch: 0,
        pitchWithRotate: false,
        touchPitch: false,
        center: center,
        maxBounds: historicalBounds,
        style: {
            version: 8,
            sources: sources,
            layers: layers
        }
    });

    if (historicalBounds) {
        map.once('load', () => {
            const minimumCamera = map.cameraForBounds(historicalBounds, { padding: 0 });
            if (minimumCamera?.zoom !== undefined) map.setMinZoom(minimumCamera.zoom - 0.5);
        });
    }

    return map.addControl(new maplibregl.NavigationControl({
        showCompass: true,
        showZoom: true,
        visualizePitch: false,
        visualizeRoll: false
    }));
}
