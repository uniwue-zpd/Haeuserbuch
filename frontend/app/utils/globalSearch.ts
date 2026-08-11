export const SEARCH_FIELDS = ["TITLE", "METADATA", "FULL_TEXT"] as const;
export type SearchField = (typeof SEARCH_FIELDS)[number];

export const SEARCH_ENTITY_TYPES = [
  "BUILDING",
  "PERSON",
  "PLACE",
  "CITIZENSHIP",
  "SOURCE",
  "STREET",
  "DISTRICT",
  "QUARTER",
] as const;
export type SearchEntityType = (typeof SEARCH_ENTITY_TYPES)[number];

export interface GlobalSearchResult {
  entityType: SearchEntityType;
  entityId: number;
  title: string;
  subtitle: string | null;
  matchedFields: SearchField[];
  excerpt: string | null;
}

export interface SearchFacets {
  entityTypes: Record<SearchEntityType, number>;
  fields: Record<SearchField, number>;
}

export interface GlobalSearchResponse {
  content: GlobalSearchResult[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  facets: SearchFacets | null;
}

export interface GlobalSearchParams {
  query: string;
  fields?: SearchField[];
  types?: SearchEntityType[];
  exactFullText?: boolean;
  page?: number;
  size?: number;
  includeFacets?: boolean;
}

export const searchFieldConfig: Record<SearchField, { label: string; description: string }> = {
  TITLE: { label: "Titel und Namen", description: "Bezeichnungen, Namen und Alternativnamen" },
  METADATA: { label: "Metadaten", description: "Kennungen, Datierungen und verknüpfte Angaben" },
  FULL_TEXT: { label: "Volltext", description: "Eintragstexte der Bürgermatrikel" },
};

export const searchEntityConfig: Record<
  SearchEntityType,
  { label: string; pluralLabel: string; icon: string; to: (id: number) => string }
> = {
  BUILDING: { label: "Gebäude", pluralLabel: "Gebäude", icon: "i-lucide-building-2", to: (id) => `/katasterplan/${id}` },
  PERSON: { label: "Person", pluralLabel: "Personen", icon: "i-lucide-user-round", to: (id) => `/personen/${id}` },
  PLACE: { label: "Ort", pluralLabel: "Orte", icon: "i-lucide-map-pin", to: (id) => `/orte/${id}` },
  CITIZENSHIP: { label: "Bürgermatrikel", pluralLabel: "Bürgermatrikel", icon: "i-lucide-id-card", to: (id) => `/buergermatrikel/${id}` },
  SOURCE: { label: "Quelle", pluralLabel: "Quellen", icon: "i-lucide-library", to: (id) => `/quellen/${id}` },
  STREET: { label: "Straße", pluralLabel: "Straßen", icon: "i-lucide-signpost", to: (id) => `/streets/${id}` },
  DISTRICT: { label: "Distrikt", pluralLabel: "Distrikte", icon: "i-lucide-grid-2x2", to: (id) => `/districts/${id}` },
  QUARTER: { label: "Viertel", pluralLabel: "Viertel", icon: "i-lucide-map", to: (id) => `/quarters/${id}` },
};

export function parseSearchFields(value: unknown): SearchField[] {
  const parts = String(Array.isArray(value) ? value[0] ?? "" : value ?? "")
    .split(",")
    .map((part) => part.trim().toUpperCase())
    .filter((part): part is SearchField => SEARCH_FIELDS.includes(part as SearchField));
  return parts.length ? [...new Set(parts)] : [...SEARCH_FIELDS];
}

export function parseSearchEntityTypes(value: unknown): SearchEntityType[] {
  const parts = String(Array.isArray(value) ? value[0] ?? "" : value ?? "")
    .split(",")
    .map((part) => part.trim().toUpperCase())
    .filter((part): part is SearchEntityType => SEARCH_ENTITY_TYPES.includes(part as SearchEntityType));
  return parts.length ? [...new Set(parts)] : [...SEARCH_ENTITY_TYPES];
}
