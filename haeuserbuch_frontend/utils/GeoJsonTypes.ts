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
    type: string | null;
    name: string | null;
    altNames: string[] | [];
    houseNumber: string | null;
    partType: string | null;
    specialStatus: string | null;
    quarter: string | null;
    district: string | null;
    districtHouseNumber: string | null;
    primarySources: Source[] | [];
    secondarySources: string[] | [];
    internalNotes: string | null;
    generalNotes: string | null;
}

export interface PlaceProperties {
    type: string | null;
    realName: string | null;
    altNames: string[] | [];
    internalNotes: string | null;
    generalNotes: string | null;
}
