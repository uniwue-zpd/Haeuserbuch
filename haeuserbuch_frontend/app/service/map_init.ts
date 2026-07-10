import maplibregl, { type RasterLayerSpecification } from "maplibre-gl";

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
    layers: RasterLayerSpecification[]
) {
    return new maplibregl.Map({
        container: containerId,
        zoom: zoom,
        pitch: pitch,
        center: center,
        style: {
            version: 8,
            sources: sources,
            layers: layers
        }
    })
        .addControl(new maplibregl.NavigationControl({
        showCompass: true,
        showZoom: true,
        visualizePitch: true,
        visualizeRoll: true
        }));
}
