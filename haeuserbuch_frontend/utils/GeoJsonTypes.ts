// GeoJSON single feature
export interface Feature {
    type: 'Feature';
    properties: {
        [key: string]: string | number | boolean | null;
    };
    geometry: GeometryType;
}

// GeoJSON geometry types
export type GeometryType = Point | Polygon | LineString;

// GeoJSON Point
export interface Point {
    type: 'Point';
    coordinates: [number, number];
}

// GeoJSON Polygon
export interface Polygon {
    type: 'Polygon';
    coordinates: [number, number][][];
}

// GeoJSON LineString
export interface LineString {
    type: 'LineString';
    coordinates: [number, number][];
}

// GeoJSON collection of features
export interface FeatureCollection {
    type: 'FeatureCollection';
    features: Feature[];
}
