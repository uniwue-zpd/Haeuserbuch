import maplibregl, { type RasterLayerSpecification } from "maplibre-gl";

export function initMap(
    containerId: string,
    center: [number, number],
    zoom: number = 12,
    sources: Record<string, maplibregl.SourceSpecification>,
    layers: RasterLayerSpecification[]
) {
    return new maplibregl.Map({
        container: containerId,
        zoom: zoom,
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
