<script setup lang="ts">
import { useVirtualizer } from "@tanstack/vue-virtual";
import { computed, nextTick, ref, watch } from "vue";
import type { BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";

const props = withDefaults(
  defineProps<{
    buildings: BuildingFeature[];
    buildingsPending?: boolean;
    selectedBuilding?: BuildingFeature | null;
    search: string;
    district: string;
    quarter: string;
    objectType: string;
    georeferenced: string;
    districtOptions: string[];
    quarterOptions: string[];
    objectTypeOptions: string[];
    associatedPeople?: PersonPreviewDTO[] | null;
    associatedPeoplePending?: boolean;
    associatedPeopleError?: boolean;
    hasPrevious?: boolean;
    hasNext?: boolean;
  }>(),
  {
    buildingsPending: false,
    selectedBuilding: null,
    associatedPeople: () => [],
    associatedPeoplePending: false,
    associatedPeopleError: false,
    hasPrevious: false,
    hasNext: false,
  },
);

const emit = defineEmits<{
  "update:search": [value: string];
  "update:district": [value: string];
  "update:quarter": [value: string];
  "update:objectType": [value: string];
  "update:georeferenced": [value: string];
  select: [id: number];
  deselect: [];
  previous: [];
  next: [];
  clearFilters: [];
  retryRelationships: [];
}>();

const listViewport = ref<HTMLElement | null>(null);
const itemHeight = 112;
const allOptionValue = "__all__";

const searchModel = computed({
  get: () => props.search,
  set: (value) => emit("update:search", value),
});
const districtModel = computed({
  get: () => props.district || allOptionValue,
  set: (value) =>
    emit("update:district", value === allOptionValue ? "" : value),
});
const quarterModel = computed({
  get: () => props.quarter || allOptionValue,
  set: (value) => emit("update:quarter", value === allOptionValue ? "" : value),
});
const objectTypeModel = computed({
  get: () => props.objectType || allOptionValue,
  set: (value) =>
    emit("update:objectType", value === allOptionValue ? "" : value),
});
const georeferencedModel = computed({
  get: () => props.georeferenced || allOptionValue,
  set: (value) =>
    emit("update:georeferenced", value === allOptionValue ? "" : value),
});
const districtSelectItems = computed(() => [
  { label: "Alle Distrikte", value: allOptionValue },
  ...props.districtOptions.map((value) => ({ label: value, value })),
]);
const quarterSelectItems = computed(() => [
  { label: "Alle Viertel", value: allOptionValue },
  ...props.quarterOptions.map((value) => ({ label: value, value })),
]);
const objectTypeSelectItems = computed(() => [
  { label: "Alle Objekttypen", value: allOptionValue },
  ...props.objectTypeOptions.map((value) => ({ label: value, value })),
]);
const georeferencedSelectItems = [
  { label: "Alle Gebäude", value: allOptionValue },
  { label: "Kartiert", value: "yes" },
  { label: "Ohne Geodaten", value: "no" },
];

const buildingVirtualizerOptions = computed(() => ({
  count: props.buildings.length,
  getScrollElement: () => listViewport.value,
  estimateSize: () => itemHeight,
  getItemKey: (index: number) => props.buildings[index]?.id ?? index,
  overscan: 5,
}));
const buildingVirtualizer = useVirtualizer(buildingVirtualizerOptions);
const activeFilters = computed(() =>
  Boolean(
    props.search ||
      props.district ||
      props.quarter ||
      props.objectType ||
      props.georeferenced,
  ),
);

function buildingTitle(building: BuildingFeature) {
  return (
    building.properties.districtPropertyNumber ||
    building.properties.names.find((name) => name.name)?.name ||
    building.properties.object ||
    `Gebäude ${building.id}`
  );
}

function primaryAddress(building: BuildingFeature) {
  const current = building.properties.addresses.find(
    (address) => String(address.fromDate ?? "") === "2025",
  );
  const address = current ?? building.properties.addresses[0];
  return address
    ? `${address.street?.name ?? "Unbekannte Straße"} ${address.houseNumber ?? ""}`.trim()
    : null;
}

watch(
  () => props.buildings,
  async () => {
    await nextTick();
    buildingVirtualizer.value.scrollToOffset(0);
  },
);
</script>

<template>
  <aside
    class="flex h-full min-h-0 flex-col overflow-hidden bg-default text-default"
    aria-label="Gebäudeverzeichnis"
    :aria-busy="buildingsPending"
  >
    <template v-if="buildingsPending">
      <div
        class="animate-pulse border-b border-muted bg-default px-4 pb-4 pt-5"
      >
        <div class="h-7 w-44 rounded bg-elevated" />
        <div class="mt-4 h-10 w-full rounded bg-elevated" />
        <div class="mt-3 grid grid-cols-2 gap-2 sm:grid-cols-4">
          <div
            v-for="index in 3"
            :key="index"
            class="h-11 rounded bg-elevated"
          />
        </div>
      </div>
      <div
        class="border-b border-muted bg-default px-4 py-2.5 text-sm font-semibold text-muted"
      >
        Gebäude werden geladen
      </div>
      <div
        class="min-h-0 flex-1 overflow-hidden bg-default px-4 py-3"
        role="status"
        aria-live="polite"
      >
        <div
          v-for="index in 5"
          :key="index"
          class="h-28 border-b border-muted py-3"
        >
          <div class="h-5 w-3/5 rounded bg-elevated" />
          <div class="mt-3 h-4 w-2/5 rounded bg-elevated" />
          <div class="mt-2 h-3 w-4/5 rounded bg-elevated" />
        </div>
        <span class="sr-only">Gebäude werden geladen</span>
      </div>
    </template>

    <template v-else-if="selectedBuilding">
      <div class="z-10 border-b border-muted bg-default px-4 py-3">
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0">
            <p class="text-xs font-medium text-muted">Ausgewähltes Gebäude</p>
            <h1 class="mt-0.5 truncate text-xl font-bold text-highlighted">
              {{ buildingTitle(selectedBuilding) }}
            </h1>
          </div>
          <UButton
            color="neutral"
            variant="ghost"
            icon="i-material-symbols-close-rounded"
            aria-label="Auswahl schließen"
            title="Auswahl schließen"
            class="shrink-0"
            @click="emit('deselect')"
          />
        </div>

        <div class="mt-2 flex items-center gap-1">
          <UButton
            color="neutral"
            variant="ghost"
            icon="i-material-symbols-arrow-back-rounded"
            :disabled="!hasPrevious"
            aria-label="Vorheriges Gebäude"
            label="Zurück"
            @click="emit('previous')"
          />
          <UButton
            color="neutral"
            variant="ghost"
            trailing-icon="i-material-symbols-arrow-forward-rounded"
            :disabled="!hasNext"
            aria-label="Nächstes Gebäude"
            label="Weiter"
            @click="emit('next')"
          />
          <UButton
            :to="`/buildings/${selectedBuilding.id}`"
            color="neutral"
            variant="link"
            trailing-icon="i-material-symbols-open-in-new-rounded"
            label="Vollständige Seite"
            class="ml-auto"
          />
        </div>
      </div>

      <div class="min-h-0 flex-1 overflow-y-auto bg-default px-4">
        <UAlert
          v-if="!selectedBuilding.geometry"
          color="warning"
          variant="subtle"
          icon="i-material-symbols-location-off-outline"
          description="Für dieses Gebäude sind bisher keine Geodaten hinterlegt."
          class="mt-4"
        />
        <BuildingsDetails
          :building="selectedBuilding"
          :associated-people="associatedPeople"
          :associated-people-pending="associatedPeoplePending"
          :associated-people-error="associatedPeopleError"
          compact
          @retry-relationships="emit('retryRelationships')"
        />
      </div>
    </template>

    <template v-else>
      <div class="z-10 border-b border-muted bg-default px-4 pb-4 pt-5">
        <div class="flex items-center justify-between gap-3">
          <h1 class="mt-0.5 text-2xl font-bold text-highlighted">
            Gebäude entdecken
          </h1>
          <UModal
            title="Die Würzburger Uraufnahme von 1832"
            scrollable
            :ui="{ content: 'max-w-7xl', body: 'p-5 sm:p-8' }"
          >
            <UButton
              color="neutral"
              variant="ghost"
              icon="i-lucide-info"
              label="Info"
              aria-label="Informationen zur Würzburger Uraufnahme"
              title="Informationen zur Würzburger Uraufnahme"
            />

            <template #body>
              <div class="space-y-8 text-base leading-7 text-highlighted sm:text-lg sm:leading-8">
                    <h2 class="text-center font-[Montserrat] text-2xl font-semibold text-highlighted sm:text-3xl">
                      Die Würzburger Uraufnahme von 1832
                    </h2>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                        Entstehung und Bedeutung
                      </h3>
                      <div class="space-y-5 text-justify">
                        <p>
                          Die Grundlage der hier dargestellten digitalisierten Gebäudegrundrisse im historischen Zentrum von Würzburg bildet die sogenannte Uraufnahme aus dem Jahr 1832. Als Uraufnahme wird die zwischen 1808 und 1864 durchgeführte erste flächendeckende, maßstäbliche und parzellengenaue Vermessung des Königreichs Bayern bezeichnet. Die dabei entstandenen handgezeichneten Katasterkarten dokumentieren Grundstücksgrenzen, Gebäude, Verkehrsflächen, Gewässer und verschiedene Formen der Bodennutzung. 23.000 Kartenblätter zeigen flächendeckend alle Orte, Flurstücke, Wege und Gewässer. Ca. 3.000 davon sind großmaßstäbliche Stadt- und Ortsblätter. Sie dienten vor allem der einheitlichen Erfassung und Besteuerung des Grundbesitzes und bildeten die Grundlage des späteren bayerischen Flurkartenwerks beziehungsweise der heutigen Flurkarte (vgl. Landesamt für Digitalisierung, Breitband und Vermessung Bayern, „Historische Flurkarten“, sowie Bayerische Vermessungsverwaltung, „Uraufnahme Würzburg 1832“, BayernAtlas).
                        </p>
                        <p>
                          Damit bildet die Würzburger Uraufnahme eine einzigartige Momentaufnahme des Stadtgefüges im Jahr 1832 und zugleich eine zentrale Grundlage für die Erforschung einer historischen Stadt, deren damaliger Gebäudebestand infolge der Zerstörungen des Zweiten Weltkriegs und späterer Veränderungen zu großen Teilen verloren gegangen ist. Sie ist damit das wichtigste Bindeglied zwischen den schriftlichen Quellen, dem historischen Stadtgrundriss und dem heutigen Bestand.
                        </p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                        Quellenkritische Einordnung
                      </h3>
                      <div class="space-y-5 text-justify">
                        <p>
                          Zugleich ist die Uraufnahme als historische Verwaltungsquelle quellenkritisch zu lesen: Sie gibt den zum Zeitpunkt der Vermessung amtlich erfassten Bestand wieder, ohne sämtliche baulichen Details oder Nutzungen gleichermaßen zu dokumentieren. Dies ist insbesondere von Bedeutung, weil Nebengebäude, Anbauten oder Funktionsänderungen unter Umständen nur eingeschränkt erkennbar sind.
                        </p>
                        <p>
                          Die zum Plan gehörigen Akten (sogenannte Grundsteuerkataster von 1837/40), die – nach Haus- und Besitznummern geordnet – detaillierte Beschreibungen der Gebäude und der jeweiligen Besitzverhältnisse sowie den Namen und in der Regel auch den Beruf des Eigentümers enthielten und somit das Kartenwerk ergänzten, sind für die Stadt Würzburg nicht mehr erhalten. Die Überlieferung dieser Grundbücher setzt erst 1897/1900 wieder ein und damit rund 65 Jahre nach der Entstehung der Uraufnahme. Wichtige Quellen zu den Häusern und ihren Bewohnern sind damit für Würzburg andere Quellen, wie etwa die Stadtsteuer- und Lagerbücher im Bestand Ratsbücher des Stadtarchivs Würzburg und die sogenannten Viertelbücher, die über die Amtsgerichte in das Staatsarchiv Kitzingen gelangt sind.
                        </p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                        Distrikte und Stadtviertel
                      </h3>
                      <div class="space-y-5 text-justify">
                        <p>
                          Zum Zeitpunkt der Entstehung der Uraufnahme um 1832 war die Stadt in fünf Distrikte eingeteilt, die in der Karte allerdings nicht ersichtlich sind. Als administrative Gliederung trat sie 1805 an die Stelle der bereits 1410 vorgenommenen Unterteilung der Kernstadt in vier innere und vier äußere Viertel.
                        </p>
                        <p>
                          Das Cresser, Dietricher, Gänheimer und Bastheimer Viertel bildeten die inneren Viertel innerhalb des sogenannten Bischofshuts, während das Sander, Pleichacher und Hauger Viertel und das linksmainische Mainviertel als äußere Viertel bezeichnet wurden. Letzteres wurde auch Viertel St. Burkard oder Viertel in der Kunbach genannt.
                        </p>
                        <p>
                          Der I. Distrikt umfasste das Pleichacher sowie das Hauger Viertel, der II. Distrikt das Dietricher und Cresser Viertel, der III. Distrikt das Bastheimer und Gänheimer Viertel, der IV. Distrikt das Sander Viertel und der V. Distrikt das Mainviertel.
                        </p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                        Besitznummern und Gebäudesignaturen
                      </h3>
                      <div class="space-y-5 text-justify">
                        <p>
                          In der Uraufnahme sind überwiegend nur die Hauptgebäude mit Haus- und Besitznummern versehen. Den Haus- und Besitznummern kommt dabei zusammen mit der jeweiligen Distriktnummer eine Schlüsselfunktion zu: Die Nummer kennzeichnete im Regelfall ein Haus oder Anwesen, den dazugehörigen Besitzkomplex und mittelbar den in den Schriftunterlagen geführten Besitzer. Zeitweise diente sie offenbar auch als administrative und möglicherweise postalische Adresse. Sie konnte deshalb auch auf weiter entfernten Grundstücksflächen (wie etwa landwirtschaftlichen Flächen oder Gärten) erscheinen, die zum selben Anwesen gehörten. Sie war damit keine geometrisch eindeutige Nummer jedes einzelnen Flurstücks, wie es die spätere Flurstücksnummer ist. Sie ist also nicht mit der heutigen Hausnummer gleichzusetzen, aber auch nicht vollständig von einer Hausnummer zu trennen. Als eindeutiges Ordnungsmerkmal ermöglicht sie vielfach bis heute die Zuordnung von Akten und Archivalien zu den in der Uraufnahme dargestellten Gebäuden.
                        </p>
                        <p>
                          Die Nummerierung wurde in Würzburg nicht wie ansonsten in den Uraufnahmen des Königreichs Bayern üblich straßenweise fortlaufend vergeben. Die Zählung begann in jedem Distrikt von Neuem. Die Haus- und Besitznummer muss also, um eindeutig zu sein, zusammen mit dem Distrikt gelesen werden, der jeweils in römischen Ziffern wiedergegeben ist (z. B. I/132, II/202, III/36 usw.). Im Folgenden werden diese Nummern verkürzt als Besitznummern bezeichnet. Im Jahr 1869 wurde das ältere, aus Distrikt- und fortlaufender Besitznummer bestehende Ordnungssystem durch eine straßenbezogene Adressierung ersetzt. Seither wurden die Gebäude durch die Verbindung von Straßenname und Hausnummer gekennzeichnet.
                        </p>
                        <p>
                          Die Hauptgebäude sind in der Karte in der Regel braun schraffiert dargestellt, während Nebengebäude eine gelbe Signatur tragen. Dies bedeutet jedoch nicht zwingend, dass diese Nebengebäude nicht auch Wohnzwecken dienen konnten. Überwiegend handelt es sich jedoch wohl um klassische Nebengebäude wie Werkstätten, Betriebsstätten, Lagergebäude und Scheunen. Im städtischen Kontext dienten sie seltener auch als Ställe. Die Plansignaturen orientieren sich weitgehend an den amtlichen bayerischen Zeichenvorschriften von 1808 und 1830, überliefert in der Zusammenstellung <em>II A Planzeichnung nach den Vorschriften von den Jahren 1808 und 1830</em>, veröffentlicht von der Bayerischen Landesvermessung.
                        </p>
                        <p>
                          Nur wenige Gebäude tragen eine Funktionsbezeichnung, wie etwa das ehemalige Polizeirevier an der heutigen Karmelitenstraße. Weitere Funktionen werden in der Legende – den jeweiligen Haus- und Besitznummern zugeteilt – aufgeschlüsselt. Flurstücksnummern sind in der Uraufnahme noch nicht enthalten. Sie wurden erst im Zuge der anschließenden Liquidation und der Aufstellung des Grundsteuerkatasters vergeben, in die aus der Uraufnahme hervorgegangenen Katasterpläne eingetragen und finden sich damit frühestens in den sogenannten Liquidationsplänen oder in den abschließenden Extraditionsplänen. Letztere bildeten die vollständigen, an die zuständigen Rentämter abgegebenen Planexemplare. Die in der Karte eingetragenen Straßenabschnitte wurden in der Uraufnahme von Würzburg, nach Distrikten sortiert, mit den Nummern 1–175 versehen und die Straßennamen im Planfuß angefügt.
                        </p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                        Befestigungsanlagen
                      </h3>
                      <div class="space-y-5 text-justify">
                        <p>
                          Eine weitere Orientierungsebene bilden die Befestigungsanlagen der Stadt. Die in der Karte eingetragenen Türme, Tore und Schneller – bewegliche Schlagbrücken vor einzelnen Toranlagen – geben den gegenwärtigen Forschungsstand wieder. Sie sind daher nicht als Rekonstruktion eines einzigen historischen Zustands zu verstehen: Die dargestellten Elemente haben nicht zwingend gleichzeitig bestanden, sondern können unterschiedlichen Ausbauphasen der Stadtbefestigung angehören. Die mittelalterliche Stadtbefestigung Würzburgs entstand seit dem 10. beziehungsweise frühen 11. Jahrhundert und wurde bis in das Spätmittelalter, insbesondere im 13. bis 15. Jahrhundert, mehrfach erweitert und ausgebaut.
                        </p>
                        <p>
                          Da sich aber Lage- und Straßenbezeichnungen, die auf ehemalige Befestigungselemente zurückgehen, häufig über lange Zeiträume erhalten haben, bilden insbesondere Tore und Türme wichtige Bezugspunkte für die räumliche Zuordnung historischer Quellen. Die mittelalterliche Befestigung umschloss die Stadt gleichsam wie ein räumliches Korsett und prägte deren Grundriss und Entwicklung über Jahrhunderte.
                        </p>
                        <p>
                          Grundlage der bisherigen Erfassung ist vor allem Franz Seberichs Untersuchung zur mittelalterlichen Stadtbefestigung Würzburgs:
                        </p>
                        <p class="italic">
                          Franz Seberich: Die Stadtbefestigung Würzburgs. Band 1: Die mittelalterliche Befestigung mit Mauern und Türmen, Mainfränkische Hefte 39, Würzburg 1962.
                        </p>
                        <p>
                          Die von Seberich beschriebenen Befestigungselemente wurden mit dem heute noch erhaltenen Bestand abgeglichen. Grundlage hierfür waren unter anderem die Denkmallayer des Bayerischen Landesamtes für Denkmalpflege. Auf diese Weise konnten bis heute erhaltene mittelalterliche Türme, Tore und weitere Befestigungselemente, die in der Uraufnahme nicht oder nur unvollständig verzeichnet sind, mit größtmöglicher Lagegenauigkeit verortet werden.
                        </p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">Ausblick</h3>
                      <div class="space-y-5 text-justify">
                        <p>
                          Die vorliegende Kartierung wird im weiteren Projektverlauf schrittweise ergänzt und vertieft. Vorgesehen sind insbesondere die weitere Verknüpfung der Gebäude mit schriftlichen und bildlichen Quellen, historischen Plänen und Forschungsliteratur sowie die fortlaufende Präzisierung der Objektbeschreibungen und historischen Adresszuordnungen. Darüber hinaus soll die bislang erfasste mittelalterliche Stadtbefestigung in einem weiteren Bearbeitungsschritt um die neuzeitliche Festungsanlage mit ihren Bastionen, Wällen und weiteren Befestigungswerken ergänzt werden.
                        </p>
                        <p>
                          Mit dem Fortschreiten der Erschließung soll so ein zunehmend differenziertes Bild der historischen Stadtstruktur und ihrer baulichen Entwicklung entstehen. Die digitale Kartierung ist daher nicht als abgeschlossen, sondern als fortlaufend erweiterbare Arbeits- und Forschungsgrundlage zu verstehen.
                        </p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">Benutzerhinweise</h3>
                      <h4 class="mb-3 text-center text-lg font-semibold text-highlighted">Digitale Bearbeitung</h4>
                      <div class="space-y-5 text-justify">
                        <p>
                          Für die vorliegende Anwendung wurde die bereits vom Landesamt für Digitalisierung, Breitband und Vermessung georeferenzierte Uraufnahme aus dem Jahr 1832 nochmals genauer georeferenziert, um eine höhere Deckungsgleichheit mit dem noch erhaltenen historischen Baubestand zu erreichen. Die Bearbeitung erfolgte im Geoinformationssystem QGIS; durch die Georeferenzierung werden die historischen Kartendaten mit einem heutigen räumlichen Koordinatensystem verknüpft, sodass sie lagebezogen mit aktuellen Karten- und Geodaten überlagert und verglichen werden können. Die in Form eines Rasterdatensatzes vorliegende Uraufnahme wurde anschließend hinsichtlich der darin dargestellten Gebäudegrundrisse digital ausgewertet. Diese wurden, soweit möglich, mit historischen und aktuellen Adress- und Flurstücksdaten aus der ALKIS-Flurkarte der Bayerischen Vermessungsverwaltung verknüpft.
                        </p>
                        <p>
                          Die Zuordnung der heutigen Adresse richtet sich nach dem aktuellen Gebäude beziehungsweise nach dem heutigen Straßennamen des Bereichs, in dem sich der größte Teil des in der Uraufnahme dargestellten Hauptgebäudes befindet oder ehemals befand. Infolge erheblicher Veränderungen der Stadtstruktur liegen die Standorte ganzer historischer Häuserzeilen heute teilweise im Straßenraum. Die Konkordanz mit dem um 1869 durch ein Gemeindeedikt eingeführten, auf Straßennamen und Hausnummer beruhenden Adresssystem wurde auf Grundlage einer im Stadtarchiv Würzburg als Hilfsmittel vorliegenden Konkordanz (leider ohne Herkunftsnachweis) erstellt. Die Angaben lassen sich jedoch durch das Adressbuch von 1868 verifizieren (<em>Adressbuch für die königlich bayrische Kreishaupt- und Universitäts-Stadt Würzburg. 1868</em>, Würzburg 1868).
                        </p>
                        <p>
                          In einem ersten Bearbeitungsschritt wurden lediglich die (braun schraffierten) Hauptgebäude der Uraufnahme von 1832 erfasst. Die (gelb schraffierten) Nebengebäude und Grundstücksgrenzen sollen im weiteren Projektverlauf ergänzt werden. Eine Erfassung erfolgt dabei insbesondere dann, wenn Hinweise in den überlieferten Quellen eindeutig einem Gebäude oder Anwesen zugeordnet werden können.
                        </p>
                      </div>

                      <h4 class="mb-3 mt-7 text-center text-lg font-semibold text-highlighted">Metadaten und Adressangaben</h4>
                      <div class="space-y-5 text-justify">
                        <p>
                          Durch Anklicken eines beliebigen Gebäudes können die jeweils zum Gebäude hinterlegten Metadaten und Verknüpfungen aufgerufen werden. Zur räumlichen Orientierung werden im Bereich „Adresse“ zunächst der zugehörige Distrikt sowie die ältere Bezeichnung des Stadtviertels angegeben.
                        </p>
                        <p>
                          Darüber hinaus finden sich dort die Haus- und Besitznummer aus der Uraufnahme von 1832, die 1869 eingeführte, aus Straßennamen und Hausnummer bestehende Adresse sowie die heutige Adresse und Flurstücksnummer. Die aktuellen Angaben basieren auf den ALKIS-Daten der Bayerischen Vermessungsverwaltung und entsprechen dem Stand von 2025.
                        </p>
                      </div>

                      <h4 class="mb-3 mt-7 text-center text-lg font-semibold text-highlighted">Objektbeschreibung, Quellen und Anmerkungen</h4>
                      <p class="text-justify">
                        Der Bereich „Objektbeschreibung“ schließlich enthält sämtliche bislang ermittelten Informationen zum jeweiligen Gebäude. Daran schließen sich die mit dem Objekt verknüpften Quellen- und Literaturangaben an. Den Abschluss der Metadaten bilden ergänzende Anmerkungen der Bearbeiterinnen und Bearbeiter.
                      </p>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">Quellen</h3>
                      <div class="space-y-3 text-justify">
                        <p>Bayerische Vermessungsverwaltung: Uraufnahme Würzburg 1832, georeferenzierte Fassung; Neureferenzierung und Bearbeitung: Julia Merz</p>
                        <p>Staatsarchiv Kitzingen: Nicht georeferenzierte Uraufnahme Würzburg mit Ergänzungen zu Straßennamen und Gebäudefunktionen</p>
                        <p>Bayerisches Landesvermessungsamt: II A Planzeichnung nach den Vorschriften von den Jahren 1808 und 1830, vier Tafeln, undatierte digitale Zusammenstellung auf Grundlage der Zeichenvorschriften von 1808 und 1830</p>
                        <p><em>Adressbuch für die königlich bayrische Kreishaupt- und Universitäts-Stadt Würzburg. 1868</em>, Würzburg 1868</p>
                      </div>
                    </section>

                    <section>
                      <h3 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">Literatur</h3>
                      <div class="space-y-3 text-justify">
                        <p>Amann, Joseph: <em>Die bayerische Landesvermessung in ihrer geschichtlichen Entwicklung. Band 1: Die Aufstellung des Landesvermessungswerkes 1808–1871</em>, München 1908</p>
                        <p>Bayerisches Staatsministerium der Finanzen (Hg.): <em>Es ist ein Maß in allen Dingen. 200 Jahre Bayerische Vermessungsverwaltung 1801–2001</em>, München 2001</p>
                        <p>Seeberger, Max/Holl, Frank: <em>Wie Bayern vermessen wurde</em>, Hefte zur Bayerischen Geschichte und Kultur 26, Augsburg 2001</p>
                        <p>Seberich, Franz: <em>Die Stadtbefestigung Würzburgs. Band 1: Die mittelalterliche Befestigung mit Mauern und Türmen</em>, Mainfränkische Hefte 39, Würzburg 1962</p>
                        <p>Ziegler, Theodor: <em>Vom Grenzstein zur Landkarte. Die bayerische Landesvermessung in Geschichte und Gegenwart</em>, 2. Auflage, Stuttgart 1989</p>
                      </div>
                    </section>

                    <p class="border-t border-default pt-4 text-sm text-dimmed">
                      Bearbeitung: JM (korr. RS), Stand 31.07.2026
                    </p>
                  </div>
            </template>
          </UModal>
        </div>

        <UInput
          v-model="searchModel"
          type="search"
          icon="i-material-symbols-search-rounded"
          size="lg"
          variant="outline"
          placeholder="Nummer, Name, Adresse …"
          aria-label="Gebäude durchsuchen"
          class="mt-4 w-full"
        />

        <div class="mt-3 grid grid-cols-3 gap-2">
          <UFormField
            label="Distrikt"
            :ui="{ label: 'text-[0.6875rem] font-semibold text-muted' }"
          >
            <USelect
              v-model="districtModel"
              :items="districtSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="Viertel"
            :ui="{ label: 'text-[0.6875rem] font-semibold text-muted' }"
          >
            <USelect
              v-model="quarterModel"
              :items="quarterSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="Objekttyp"
            :ui="{ label: 'text-[0.6875rem] font-semibold text-muted' }"
          >
            <USelect
              v-model="objectTypeModel"
              :items="objectTypeSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
          <UFormField
            label="Geodaten"
            :ui="{ label: 'text-[0.6875rem] font-semibold text-muted' }"
          >
            <USelect
              v-model="georeferencedModel"
              :items="georeferencedSelectItems"
              size="sm"
              class="w-full"
            />
          </UFormField>
        </div>

        <UButton
          v-if="activeFilters"
          color="neutral"
          variant="link"
          icon="i-material-symbols-filter-alt-off-outline-rounded"
          label="Filter zurücksetzen"
          class="mt-2 px-0"
          @click="emit('clearFilters')"
        />
      </div>

      <div
        class="border-b border-muted bg-default px-4 py-2.5 text-sm font-semibold text-muted"
        aria-live="polite"
      >
        Gebäude ({{ buildings.length }})
      </div>

      <div
        ref="listViewport"
        class="min-h-0 flex-1 overflow-y-auto overscroll-contain bg-default"
        role="listbox"
        aria-label="Gefilterte Gebäude"
      >
        <div
          v-if="buildings.length"
          class="relative w-full"
          :style="{ height: `${buildingVirtualizer.getTotalSize()}px` }"
        >
          <div
            class="absolute inset-x-0 top-0"
            :style="{
              transform: `translateY(${buildingVirtualizer.getVirtualItems()[0]?.start ?? 0}px)`,
            }"
          >
            <template
              v-for="virtualRow in buildingVirtualizer.getVirtualItems()"
              :key="virtualRow.key"
            >
              <div v-if="buildings[virtualRow.index]" class="h-28">
                <button
                  type="button"
                  role="option"
                  :aria-selected="false"
                  class="group flex h-full w-full flex-col justify-center border-b border-muted px-6 py-3 text-left transition-colors hover:bg-elevated focus-visible:bg-elevated focus-visible:outline-none"
                  @click="emit('select', buildings[virtualRow.index]!.id)"
                >
                  <div class="flex items-start justify-between gap-2">
                    <h2 class="line-clamp-1 font-bold text-highlighted">
                      {{ buildingTitle(buildings[virtualRow.index]!) }}
                    </h2>
                    <UBadge
                      :color="
                        buildings[virtualRow.index]!.geometry
                          ? 'success'
                          : 'neutral'
                      "
                      :icon="
                        buildings[virtualRow.index]!.geometry
                          ? 'i-material-symbols-location-on-outline-rounded'
                          : 'i-material-symbols-location-off-outline-rounded'
                      "
                      :label="
                        buildings[virtualRow.index]!.geometry
                          ? 'Kartiert'
                          : 'Ohne Geodaten'
                      "
                      variant="subtle"
                      size="sm"
                    />
                  </div>

                  <p
                    v-if="primaryAddress(buildings[virtualRow.index]!)"
                    class="mt-1.5 line-clamp-1 text-sm text-default"
                  >
                    {{ primaryAddress(buildings[virtualRow.index]!) }}
                  </p>
                  <p v-else class="mt-1.5 text-sm italic text-muted">
                    Keine Adresse hinterlegt
                  </p>

                  <p class="mt-1 line-clamp-1 pr-6 text-xs text-muted">
                    Distrikt
                    {{
                      buildings[virtualRow.index]!.properties.district?.name ??
                      "–"
                    }}
                    <template
                      v-if="
                        buildings[virtualRow.index]!.properties.quarter?.name
                      "
                    >
                      ·
                      {{
                        buildings[virtualRow.index]!.properties.quarter?.name
                      }}</template
                    >
                    <template
                      v-if="
                        buildings[virtualRow.index]!.properties.object ||
                        buildings[virtualRow.index]!.properties.partType
                      "
                    >
                      ·
                      {{
                        buildings[virtualRow.index]!.properties.object ||
                        buildings[virtualRow.index]!.properties.partType
                      }}
                    </template>
                  </p>
                </button>
              </div>
            </template>
          </div>
        </div>

        <div
          v-else
          class="flex h-full min-h-48 flex-col items-center justify-center px-8 text-center text-slate-600"
        >
          <UIcon
            name="i-material-symbols-search-off-rounded"
            class="text-5xl text-muted"
          />
          <h2 class="mt-3 text-lg font-bold text-highlighted">
            Keine Gebäude gefunden
          </h2>
          <p class="mt-1 text-sm">
            Ändern Sie die Suche oder setzen Sie die Filter zurück.
          </p>
          <UButton
            v-if="activeFilters"
            color="neutral"
            variant="outline"
            label="Filter zurücksetzen"
            class="mt-4"
            @click="emit('clearFilters')"
          />
        </div>
      </div>
    </template>
  </aside>
</template>

<style scoped>
button[role="option"]:focus-visible {
  box-shadow: inset 0 0 0 2px var(--ui-color-neutral-400);
}
</style>
