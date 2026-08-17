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
    description: 'Verzeichnis und Visualisierung der Herkunftsorte der von 1405 bis 1613 aufgenommenen Würzburger Bürger.',
    icon: 'i-lucide-map-pin',
    to: '/orte',
  },
  {
    label: 'Personen',
    description: 'Die aufgenommenen Würzburger Bürger in alphabetischer Anordnung.',
    icon: 'i-lucide-users',
    to: '/personen',
  },
  {
    label: 'Bürgermatrikel',
    description: 'Verzeichnis der von 1405 bis 1613 aufgenommenen Würzburger Bürger.',
    icon: 'i-lucide-id-card',
    to: '/buergermatrikel',
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
