export interface ResearchNavigationItem {
  label: string
  description: string
  icon: string
  to?: string
}

export const researchNavigation: readonly ResearchNavigationItem[] = [
  {
    label: 'Globale Suche',
    description: 'Namen, Metadaten und Texte durchsuchen.',
    icon: 'i-lucide-search',
    to: '/suche',
  },
  {
    label: 'Katasterplan',
    description: 'Gebäude im historischen Stadtplan entdecken.',
    icon: 'i-lucide-map',
    to: '/katasterplan',
  },
  {
    label: 'Orte',
    description: 'Orte und historische Adressen durchsuchen.',
    icon: 'i-lucide-map-pin',
    to: '/orte',
  },
  {
    label: 'Personen',
    description: 'Biografische Einträge und Verknüpfungen finden.',
    icon: 'i-lucide-users',
    to: '/personen',
  },
  {
    label: 'Bürgermatrikel',
    description: 'Bürgeraufnahmen und Zugehörigkeiten recherchieren.',
    icon: 'i-lucide-id-card',
    to: '/buergermatrikel',
  },
  {
    label: 'Quellenverzeichnis',
    description: 'Verwendete Quellen und Literatur durchsuchen.',
    icon: 'i-lucide-library',
    to: '/quellen',
  },
  {
    label: 'Eigentumsverhältnisse',
    description: 'Historische Besitzfolgen – in Vorbereitung.',
    icon: 'i-lucide-book-open',
  },
  {
    label: 'Steuerbucheinträge',
    description: 'Historische Steuerdaten – in Vorbereitung.',
    icon: 'i-lucide-landmark',
  },
]
