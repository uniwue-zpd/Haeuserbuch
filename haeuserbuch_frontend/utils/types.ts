/* Represents administrative data about each database item */
export interface Auditable {
    id: number;
    createdDate: string;
    createdBy: string | null;
    lastModifiedDate: string;
    lastModifiedBy: string | null;
    internalNotes: string | null;
    generalNotes: string | null;
}

/* Represents a small metadata subset of the {@link BuildingProperties} */
export interface BuildingDTO {
    id: number | null;
    districtHouseNumber: string | null;
}

/* Available filters for buildings. Applies only to the `filterBuildingsByPropertyId` action in the {@link useBuildingStore} */
export type FilterBuilding =
    | "name"
    | "districtId"
    | "districtName"
    | "streetId"
    | "streetName"
    | "quarterId"
    | "quarterName"
    | "sourceId"
    | "sourceName";

/* Represents an object with data about a name of the {@link BuildingProperties} */
export interface BuildingNameDTO {
    name: string | null;
    source: string | null;
}

/* Represents a naturalisation event listed in the citizen register */
export interface CitizenshipDTO extends Auditable {
    signature: string | null;
    persons: PersonDTO[] | [];
    source: SourceDTO;
    place: PlaceDTO | null;
    number: number | null;
    date: string | null;
    entryText: string | null;
    addendum: string | null;
}

/* Represents an ownership event written in the register */
export interface Ownership extends Auditable {
    type: string | null;
    date: string | null;
    price: number | null;
    owner: Person;
    seller: Person;
    buildings: Feature[] | [];
    source: Source;
    entryText: string | null;
}

/* Represents a person */
export interface Person extends Auditable {
    firstName: string | null;
    lastName: string | null;
    fullName: string | null;
    sex: "männlich" | "weiblich" | null;
    occupation: string | null;
    occupationCategory: string | null;
    isCitizen: boolean | null;
    confession: string | null;
}

/* DTO projection of a {@link Person} object */
export interface PersonDTO {
    id: number | null;
    firstName: string | null;
    lastName: string | null;
    fullName: string | null;
    sex: "männlich" | "weiblich" | null;
    occupation: string | null;
    occupationCategory: string | null;
    isCitizen: boolean | null;
    confession: string | null;
}

export interface PlaceDTO {
    id: number | null;
    realName: string | null;
    altNames: string[] | [];
}

/* Represents an item from the project's bibliography */
export interface Source extends Auditable {
    type: string | null;
    title: string | null;
    authors: string[] | [];
    signature: string | null;
    description: string | null;
    links: string[] | [];
}

/* DTO projection of a {@link Source} object */
export interface SourceDTO {
    id: number | null;
    title: string | null;
}

/* Represents a street */
export interface Street extends Auditable {
    name: string | null;
    altNames: string[] | [];
    description: string | null;
}

/* DTO projection of a {@link Street} object */
export interface StreetDTO {
    id: number | null;
    name: string | null;
}

/* Represents a district */
export interface District extends Auditable {
    name: string | null;
    description: string | null;
}

/* DTO projection of a {@link District} object */
export interface DistrictDTO {
    id: number | null;
    name: string | null;
}

/* Represents a quarter */
export interface Quarter extends Auditable {
    name: string | null;
    description: string | null;
}

/* DTO projection of a {@link Quarter} object */
export interface QuarterDTO {
    id: number | null;
    name: string | null;
}

/* Represents the response object from the `tileserver-gl` API */
export interface Tile {
    tiles: string[];
    name: string;
    format: string;
    basename: string;
    id: string;
    description: string;
    version: string;
    type: string;
    minzoom: number;
    maxzoom: number;
    bounds: [number, number, number, number];
    center: [number, number, number];
    tilejson: string;
}
