import type {
    AddressDTO,
    BuildingNameDTO,
    DistrictDTO,
    QuarterDTO,
    SourceDTO,
} from "~/utils/types";

/* GeoJSON `Feature` */
export interface Feature {
    type: 'Feature';
    id?: number;
    properties: PropertiesType | null;
    geometry: GeometryType | null;
}

/* GeoJSON `FeatureCollection` */
export interface FeatureCollection {
    type: 'FeatureCollection';
    features: Feature[];
}

/* GeoJSON `geometry` types */
export type GeometryType = Point | Polygon | LineString | MultiPolygon;

/* GeoJSON `properties` types */
export type PropertiesType = BuildingProperties | PlaceProperties;

/* GeoJSON `Point` */
export interface Point {
    type: 'Point';
    coordinates: [number, number];
}

/* GeoJSON `Polygon` */
export interface Polygon {
    type: 'Polygon';
    coordinates: [number, number][][];
}

/* GeoJSON `LineString` */
export interface LineString {
    type: 'LineString';
    coordinates: [number, number][];
}

/* GeoJSON MultiPolygon */
export interface MultiPolygon {
    type: 'MultiPolygon';
    coordinates: [number, number][][][];
}

/* GeoJSON `properties` for a building feature */
export interface BuildingProperties {
    type: string | null;
    year: number | null;
    parcelNumber: number | null;
    parcelNumberCounter: number | null;
    names: BuildingNameDTO[] | [];
    addresses: AddressDTO[] | [];
    partType: string | null;
    object: string | null;
    quarter: QuarterDTO | null;
    district: DistrictDTO | null;
    houseNumber: string | null;
    districtHouseNumber: string | null;
    primarySources: SourceDTO[] | [];
    secondarySources: SourceDTO[] | [];
    internalNotes: string | null;
    generalNotes: string | null;
    createdDate: number | null;
    createdBy: string | null;
    lastModifiedDate: number | null;
    lastModifiedBy: string | null;
}

/* GeoJSON `properties` for a place feature */
export interface PlaceProperties {
    type: string | null;
    realName: string | null;
    altNames: string[] | [];
    isUncertain: boolean | null;
    internalNotes: string | null;
    generalNotes: string | null;
    createdDate: number | null;
    createdBy: string | null;
    lastModifiedDate: number | null;
    lastModifiedBy: string | null;
}
