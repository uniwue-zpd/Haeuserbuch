import type { WeaponDTO } from "~/utils/types/weapon";
import type { ReligionDTO } from "~/utils/types/religion";
import type { JobDTO } from "~/utils/types/job";
import type { Pageable } from "~/utils/types/data_fetching";

/**
 * Represents a DTO of a person object mentioned in the sources.
 */
export interface PersonDTO extends Auditable {
    firstName: string | null;
    lastName: string | null;
    fullName: string | null;
    altNames: string[] | [];
    sex: "männlich" | "weiblich" | null;
    associatedBuilding: BuildingDTO | null;
    isCitizen: boolean | null;
    origin: PersonOrigin;
    emigration: PersonEmigration;
    job: PersonJob;
    religion: PersonReligion;
    weapons: WeaponryDTO[] | [];
}

/**
 * DTO projection of a {@link PersonDTO} object with only preview data
 */
export interface PersonPreviewDTO {
    id: number | null;
    fullName: string | null;
}

/**
 * Represents an object describing the origin of a person.
 */
export interface PersonOrigin {
    places: PlaceDTO[] | [];
    originalText: string | null;
    certainty: OriginCertainty | null;
}

/**
 * An `enum` describing the levels of certainty of identified origin.
 * Values match the backend {@code PlaceCertainty} enum.
 */
export enum OriginCertainty {
    IDENTIFIED = "IDENTIFIED",
    AMBIGUOUS = "AMBIGUOUS",
    UNKNOWN = "UNKNOWN"
}

/**
 * Display label and traffic light color for each {@link OriginCertainty}.
 */
export const ORIGIN_CERTAINTY: Record<OriginCertainty, { label: string; color: string }> = {
    IDENTIFIED: { label: "Identifiziert", color: "bg-green-400" },
    AMBIGUOUS: { label: "Mehrdeutig", color: "bg-yellow-400" },
    UNKNOWN: { label: "Unbekannt", color: "bg-red-400" },
};

/**
 * Represents an object describing the place a person emigrated to.
 */
export interface PersonEmigration {
    originalText: string | null;
    place: PlaceDTO | null;
}

/**
 * Represents an object describing the occupation of a person.
 */
export interface PersonJob {
    originalText: string | null;
    jobCategory: JobDTO | null;
}

/**
 * An object describing the religion of a person.
 */
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

/**
 * Available filters for persons.
 */
export interface FilterPerson extends Pageable {
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
    'place-of-emigration'?: string;
    'place-of-emigration-id'?: number;
    religion?: string;
    'religion-id'?: number;
    weapon?: string;
    'weapon-id'?: number;
}
