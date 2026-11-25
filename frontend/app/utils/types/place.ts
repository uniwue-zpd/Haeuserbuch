/**
 * Represents a place as non-`geoJSON feature`
 */
export interface PlaceDTO {
    id: number;
    realName: string | null;
    altNames: string[] | [];
}
