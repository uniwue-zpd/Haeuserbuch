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
            //@ts-ignore
            sources: sources,
            //@ts-ignore
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
