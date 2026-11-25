/**
 * Represents a naturalization event listed in the citizen register
 */
export interface CitizenshipDTO extends Auditable {
    signature: string | null;
    person: PersonDTO | null;
    mentionedPersons: PersonPreviewDTO[] | [];
    guarantors: PersonPreviewDTO[] | [];
    primarySource: SourceDTO;
    secondarySource: SourceDTO | null;
    refNumber: string | null;
    dateNaturalization: string | null;
    yearNaturalization: number | null;
    dateMisc: string | null;
    entryText: string | null;
    addendum: string | null;
}

/**
 * Represents a preview DTO of a citizenship entry
 */
export interface CitizenshipPreviewDTO {
    id: number;
    signature: string | null;
    refNumber: string | null;
}

/**
 *  Represents an object describing available citizenship filtering options
 */
export interface FilterCitizenship extends Pageable {
    refnumber?: string;
    signature?: string;
    naturalizedperson?: string;
    'naturalizedperson-id'?: number;
    guarantor?: string;
    'guarantor-id'?: number;
    yearnaturalization?: number;
    'year-from'?: number;
    'year-to'?: number;
    primarysource?: string;
    secondarysource?: string;
    'naturalizedperson-job'?: string;
    'naturalizedperson-origin'?: string;
    'guarantor-job'?: string;
    'guarantor-origin'?: string;
}

/**
 * Represents a full-text search result for a citizenship entry
 */
export interface CitizenshipFullTextResult {
    id: number;
    signature: string | null;
    refNumber: string | null;
    queryResult: string;
}

/**
 * Represents an object describing available citizenship full-text search options
 */
export interface SearchCitizenshipFullText extends Pageable {
    query: string;
    fuzzy?: boolean;
    exact?: boolean;
}
