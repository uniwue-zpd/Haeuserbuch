// GeoJSON single feature
export interface Feature {
    type: 'Feature';
    properties: {
        [key: string]: string | number | boolean | null;
    };
    geometry: Geometry;
}

// GeoJSON geometry types
export type GeometryType = 'Point' | 'LineString' | 'Polygon';

// GeoJSON geometry object
export interface Geometry {
    type: GeometryType;
    coordinates: number[] | number[][] | number[][][];
}

// GeoJSON collection of features
export interface FeatureCollection {
    type: 'FeatureCollection';
    features: Feature[];
}
