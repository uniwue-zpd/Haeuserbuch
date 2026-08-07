export interface ResearchNavigationItem {
  label: string
  description: string
  icon: string
  to?: string
}

export const researchNavigation: readonly ResearchNavigationItem[] = [
  {
    label: 'Katasterplan',
    description: 'Gebäude im historischen Stadtplan entdecken.',
    icon: 'i-lucide-map',
    to: '/buildings',
  },
  {
    label: 'Orte',
    description: 'Orte und historische Adressen durchsuchen.',
    icon: 'i-lucide-map-pin',
    to: '/places',
  },
  {
    label: 'Personen',
    description: 'Biografische Einträge und Verknüpfungen finden.',
    icon: 'i-lucide-users',
    to: '/persons',
  },
  {
    label: 'Bürgermatrikel',
    description: 'Bürgeraufnahmen und Zugehörigkeiten recherchieren.',
    icon: 'i-lucide-id-card',
    to: '/citizenships',
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
