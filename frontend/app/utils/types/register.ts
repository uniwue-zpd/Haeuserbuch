/**
 * A group of a register (e.g. a place or an occupation) with its related entries (persons or citizenship entries)
 */
export interface RegisterEntry<T> {
    id: number;
    name: string | null;
    entries: T[];
}

/**
 * Query parameters of a register
 */
export interface FilterRegister extends Pageable {
    type: "persons" | "citizenships";
    name?: string;
}
