/*
    Represents administrative data about each database item
*/
export interface Auditable {
    id: number;
    created_date: string;
    created_by: string;
    last_modified_date: string;
    last_modified_by: string;
}

/*
    Represents a naturalisation event listed in the citizen register
*/
export interface Citizenship extends Auditable {
    person: Person;
    source: Source;
    place?: Feature;
    number?: number;
    date?: string;
    entry_text?: string;
    addendum?: string;
    notes?: string;
}

/*
    Represents an ownership event written in the register
*/
export interface Ownership extends Auditable {
    type?: string;
    date?: string;
    price?: number;
    owner: Person;
    seller: Person;
    buildings: Feature[] | [];
    source: Source;
    entry_text?: string;
    notes?: string;
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
    isCitizen: boolean | null;
    confession: string | null;
    notes: string | null;
}

/*
    Represents an item from the project's bibliography
*/
export interface Source extends Auditable {
    type?: string;
    title?: string;
    signature?: string;
    description?: string;
    notes?: string;
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
