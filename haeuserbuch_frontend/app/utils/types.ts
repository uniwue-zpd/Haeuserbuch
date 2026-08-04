// AUDITABLE

/** Represents metadata fields that are available in all entities of the project. */
export interface Auditable {
    id: number;
    createdDate: string;
    createdBy: string | null;
    lastModifiedDate: string;
    lastModifiedBy: string | null;
    internalNotes: string | null;
    generalNotes: string | null;
}

// BUILDINGS

/** Represents a preview DTO of a building. */
export interface BuildingDTO {
    id: number | null;
    districtPropertyNumber: string | null;
}

/** Represents an object containing data of available names of a building. */
export interface BuildingName extends Auditable {
    name: string | null;
    source: Source | null;
    dateFrom: string | null;
    dateTo: string | null;
}

/** Represents an object with data about a name of the {@link BuildingProperties} */
export interface BuildingNameDTO {
    name: string | null;
    source: SourceDTO | null;
    dateFrom: string | null;
    dateTo: string | null;
}

/** Represents an address of a building */
export interface Address extends Auditable {
    street: Street | null;
    houseNumber: string | null;
    fromDate: string | null;
    toDate: string | null;
}

/** DTO projection of an Address object */
export interface AddressDTO {
    id: number | null;
    street: StreetDTO | null;
    houseNumber: string | null;
    fromDate: string | null;
    toDate: string | null;
}

/** Available filters for buildings. Applies only to the `filterBuildings` action in the {@link useBuildingStore} */
export interface FilterBuilding {
    name?: string;
    districtId?: number;
    districtName?: string;
    streetId?: number;
    streetName?: string;
    quarterId?: number;
    quarterName?: string;
    sourceId?: number;
    sourceName?: string;
}

    // CITIZENSHIPS

/** Represents a naturalization event listed in the citizen register */
export interface CitizenshipDTO extends Auditable {
    signature: string | null;
    person: PersonPreviewDTO | null;
    mentionedPersons: PersonPreviewDTO[] | [];
    primarySource: SourceDTO;
    secondarySource: SourceDTO | null;
    refNumber: string | null;
    dateNaturalization: string | null;
    dateMisc: string | null;
    entryText: string | null;
    addendum: string | null;
}

/** Represents a preview DTO of a citizenship entry */
export interface CitizenshipPreviewDTO {
    id: number;
    signature: string | null;
    refNumber: string | null;
}

/** Represents an object describing available citizenship filtering options */
export interface FilterCitizenship {
    page?: number;
    size?: number;
    sort?: string;
    refnumber?: string;
    signature?: string;
    naturalizedperson?: string;
    'naturalizedperson-id'?: number;
    datenaturalization?: string;
    primarysource?: string;
    secondarysource?: string;
}

export interface CitizenshipFullTextResult {
    id: number;
    signature: string | null;
    refNumber: string | null;
    queryResult: string;
}

export interface SearchCitizenshipFullText {
    query: string;
    page?: number;
    size?: number;
    sort?: string;
    exact?: boolean;
}

// PERSONS

/** Represents a DTO of a person object mentioned in the sources. */
export interface PersonDTO extends Auditable {
    firstName: string | null;
    lastName: string | null;
    fullName: string | null;
    altNames: string[] | [];
    sex: "männlich" | "weiblich" | null;
    associatedBuilding: BuildingDTO | null;
    isCitizen: boolean | null;
    origin: PersonOrigin;
    job: PersonJob;
    religion: PersonReligion;
    weapons: WeaponryDTO[] | [];
}

/** DTO projection of a {@link PersonDTO} object with only preview data */
export interface PersonPreviewDTO {
    id: number | null;
    fullName: string | null;
}

/** Represents an object describing the origin of a person. */
export interface PersonOrigin {
    places: PlaceDTO[] | [];
    originalText: string | null;
    certainty: OriginCertainty | null;
}

/** An `enum` describing the levels of certainty of identified origin. */
export enum OriginCertainty {
    IDENTIFIED = "geklärt",
    AMBIGUOUS = "unsicher",
    UNKNOWN = "unbekannt"
}

/** Represents an object describing the occupation of a person. */
export interface PersonJob {
    originalText: string | null;
    jobCategory: JobDTO | null;
}

/** An object describing the religion of a person. */
export interface PersonReligion {
    originalText: string | null;
    religionCategory: ReligionDTO | null;
}

/** Represents a persons' weapon mentioned in the sources. */
export interface WeaponryDTO {
    id: number | null;
    weapon: WeaponDTO | null;
    originalText: string | null;
}

/** Available filters for persons. */
export interface FilterPerson {
    page?: number;
    size?: number;
    sort?: string;
    name?: string;
    sex?: "männlich" | "weiblich";
    job?: string;
    'job-id'?: number;
    'associated-building'?: string;
    'associated-building-id'?: number;
    'is-citizen'?: boolean;
    'place-of-origin'?: string;
    'place-of-origin-id'?: number;
    'origin-certainty'?: OriginCertainty;
    religion?: string;
    'religion-id'?: number;
    weapon?: string;
    'weapon-id'?: number;
}

// OWNERSHIPS

/** Represents an ownership event written in the register. */
export interface Ownership extends Auditable {
    type: string | null;
    date: string | null;
    price: number | null;
    owner: PersonPreviewDTO;
    seller: PersonPreviewDTO;
    buildings: Feature[] | [];
    source: Source;
    entryText: string | null;
}

// PLACES

/** Represents a place as non-`geoJSON feature` */
export interface PlaceDTO {
    id: number;
    realName: string | null;
    altNames: string[] | [];
}

// SOURCES

/** Represents an item from the project's bibliography */
export interface Source extends Auditable {
    type: string | null;
    title: string | null;
    authors: string[] | [];
    signature: string | null;
    description: string | null;
    links: string[] | [];
}

/** DTO projection of a {@link Source} object */
export interface SourceDTO {
    id: number | null;
    title: string | null;
}

// STREETS

/** Represents a street */
export interface Street extends Auditable {
    name: string | null;
    altNames: string[] | [];
    description: string | null;
}

/** DTO projection of a {@link Street} object */
export interface StreetDTO {
    id: number | null;
    name: string | null;
}

// DISTRICTS

/** Represents a district */
export interface District extends Auditable {
    name: string | null;
    description: string | null;
}

/** DTO projection of a {@link District} object */
export interface DistrictDTO {
    id: number | null;
    name: string | null;
}

// QUARTERS

/** Represents a quarter */
export interface Quarter extends Auditable {
    name: string | null;
    description: string | null;
}

/** DTO projection of a {@link Quarter} object */
export interface QuarterDTO {
    id: number | null;
    name: string | null;
}

// JOBS

/** Represents an entity containing data about some job. */
export interface Job extends Auditable {
    name: string | null;
    altNames: string[] | [];
    description: string | null;
}

/** DTO representation of some job entity. */
export interface JobDTO {
    id: number | null;
    name: string | null;
    description: string | null;
}

// RELIGIONS

/** Represents an entity containing data about some religion. */
export interface Religion extends Auditable {
    name: string | null;
    description: string | null;
}

/** DTO representation of some religion entity. */
export interface ReligionDTO {
    id: number | null;
    name: string | null;
    description: string | null;
}

// WEAPONS

/**
 * Represents a weapon type, e.g. "Schwert" etc.
 */
export interface Weapon extends Auditable {
  name: string | null;
  description: string | null;
}

/**
 * Represents a DTO of a weapon.
 */
export interface WeaponDTO {
  id: number | null;
  name: string | null;
  description: string | null;
}

// TILES

/** Represents the response object from the `tileserver-gl` API */
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

export interface Page<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            empty: boolean;
            unsorted: boolean;
            sorted: boolean;
        };
        offset: number;
        unpaged: boolean;
        pages: boolean;
    };
    last: boolean;
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    sort: {
        empty: boolean;
        unsorted: boolean;
        sorted: boolean;
    };
    numberOfElements: number;
    first: boolean;
    empty: boolean;
}

/**
 * Data transfer object representing a stored file.
 * Contains file metadata exposed by the backend API.
 * The DTO does not include internal storage information such as
 * the filesystem path.
 */
export interface FileDTO extends Auditable {
    originalName: string | null;
    name: string | null;
    type: string | null;
    size: number | null;
}

/**
 * Parameters used for requesting paginated data.
 * Corresponds to Spring Data's {@code Pageable} parameters.
 */
export interface Pageable {
    page?: number;
    size?: number;
    sort?: string | string[];
}
