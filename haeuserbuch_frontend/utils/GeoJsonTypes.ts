// GeoJSON single feature
export interface Feature {
    type: 'Feature';
    id?: number;
    properties: PropertiesType | null;
    geometry: GeometryType | null;
}

// GeoJSON geometry types
export type GeometryType = Point | Polygon | LineString;

// GeoJSON properties types
export type PropertiesType = BuildingProperties | PlaceProperties;

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

export interface BuildingProperties {
    type?: string;
    name?: string;
    house_number?: string;
    part_type?: string;
    special_status?: string;
    quarter?: string;
    district?: string;
    district_house_number?: string;
    source?: Source;
    secondary_sources?: string[];
    notes?: string;
}

export interface PlaceProperties {
    type?: string;
    real_name?: string;
    alt_names?: string[];
    notes?: string;
}
