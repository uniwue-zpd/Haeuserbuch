/*
    Represents administrative data about each database item
*/
export interface Auditable {
    id: number;
    createdDate: string;
    createdBy: string | null;
    lastModifiedDate: string;
    lastModifiedBy: string | null;
    internalNotes: string | null;
    generalNotes: string | null;
}

/*
    Represents a naturalisation event listed in the citizen register
*/
export interface Citizenship extends Auditable {
    person: Person;
    source: Source;
    place: Feature | null;
    number: number | null;
    date: string | null;
    entryText: string | null;
    addendum: string | null;
}

/*
    Represents an ownership event written in the register
*/
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

/*
    Represents a person
*/
export interface Person extends Auditable {
    firstName: string | null;
    lastName: string | null;
    topographicSurname: string | null;
    fullName: string | null;
    sex: "männlich" | "weiblich" | null;
    occupation: string | null;
    occupationCategory: string | null;
    isCitizen: boolean | null;
    confession: string | null;
}

/*
    Represents an item from the project's bibliography
*/
export interface Source extends Auditable {
    type: string | null;
    title: string | null;
    signature: string | null;
    description: string | null;
}

/*
    Represents the response object from the tileserver-gl API
*/
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
