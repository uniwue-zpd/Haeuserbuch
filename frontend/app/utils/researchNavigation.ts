export interface ResearchNavigationItem {
  label: string
  description: string
  icon: string
  to?: string
  descriptionTo?: string
}

export const researchNavigation: readonly ResearchNavigationItem[] = [
  {
    label: 'Suche',
    description: 'Namen, Metadaten und Texte durchsuchen.',
    icon: 'i-lucide-search',
    to: '/suche',
  },
  {
    label: 'Urkatasterplan von 1832',
    description: 'Gebäudezuordnung und Beschreibung auf der Grundlage des Urkatasterplans von 1832.',
    icon: 'i-lucide-map',
    to: '/katasterplan',
  },
  {
    label: 'Herkunftsorte',
    description: 'Verzeichnis und Visualisierung der Herkunftsorte der von 1405 bis 1613 aufgenommenen Würzburger Bürger.',
    icon: 'i-lucide-map-pin',
    to: '/orte',
  },
  {
    label: 'Bürger',
    description: 'Die aufgenommenen Würzburger Bürger in alphabetischer Anordnung.',
    icon: 'i-lucide-users',
    to: '/personen',
  },
  {
    label: 'Bürgermatrikel',
    description: 'Verzeichnis der von 1405 bis 1613 aufgenommenen Würzburger Bürger.',
    icon: 'i-lucide-id-card',
    to: '/buergermatrikel',
    descriptionTo: '/buergermatrikel/description'
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
