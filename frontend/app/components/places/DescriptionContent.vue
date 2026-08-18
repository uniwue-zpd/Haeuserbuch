<script setup lang="ts">

</script>

<template>
  <div class="space-y-7 text-base leading-7 text-highlighted sm:text-lg sm:leading-8">
    <section>
      <h2 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
        Methodik
      </h2>
      <div class="space-y-5 text-justify">
        <p>
          Die dargestellten Herkunftsorte beruhen auf einer projektintern erstellten Ortsliste, die aus den in den Würzburger Bürgermatrikeln der Jahre 1405 bis 1613 überlieferten Ortsangaben abgeleitet wurde (Stadtarchiv Würzburg, RB 207–209, 211–213, 215). Ausgangspunkt der Geokodierung war eine Ortsliste, in der die historischen Ortsbezeichnungen in eine heutige normierte Schreibweise überführt wurden. Diese normierten Ortsnamen bildeten die Grundlage für den automatisierten Abgleich mit verschiedenen Geokodierungsdiensten und Referenzdatensätzen. Die historischen Ortsbezeichnungen bleiben dabei als Quelleninformation erhalten und werden durch die normierten Ortsnamen nicht ersetzt.
        </p>
        <p>
          Da historische Ortsbezeichnungen häufig unterschiedliche Schreibweisen, Verwaltungszusätze oder mehrdeutige Ortsnamen aufweisen, erfolgte die Geokodierung in einem mehrstufigen Verfahren. Zunächst wurden die erfassten Ortsnamen für die automatisierte Verarbeitung aufbereitet. Anschließend wurden verschiedene Geokodierungsverfahren und Referenzdatensätze – darunter ArcGIS, OpenStreetMap/Nominatim und GeoNames – in RStudio sowie im Geoinformationssystem (GIS) getestet und hinsichtlich ihrer Trefferqualität miteinander verglichen. Dabei zeigte sich, dass die Kombination aus Ortsname, Ortszusatz (Landkreis) und Land die zuverlässigsten Ergebnisse liefert. Die automatisiert ermittelten Koordinaten wurden anschließend durch Plausibilitätsprüfungen, Distanzvergleiche zwischen den Geokodierungsdiensten sowie manuelle Nachrecherchen überprüft und bei Bedarf korrigiert.
        </p>
        <p>
          Soweit mehrere Orte mit identischem oder ähnlichem Namen existieren, erfolgte die Zuordnung anhand der in den Quellen überlieferten Zusatzinformationen sowie weiterer historischer und geographischer Kriterien. Nicht eindeutig identifizierbare Ortsangaben werden fortlaufend überprüft und gegebenenfalls neu bewertet.
        </p>
      </div>
    </section>
    <section>
      <h2 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
        Bearbeitungsstand und Statistik
      </h2>
      <div class="space-y-5 text-justify">
        <p>
          Die Ortsliste befindet sich weiterhin im Aufbau. Grundlage der bisherigen Bearbeitung sind 2.216 Herkunftsortdatensätze, die aus den Würzburger Bürgermatrikeln erfasst wurden. Nach dem aktuellen Bearbeitungsstand konnten 2.089 Ortsangaben (94,3 %) automatisiert georeferenziert werden; 127 Ortsangaben (5,7 %) bedürfen weiterhin einer manuellen Überprüfung.
        </p>
        <p>
          Ein Teil der bislang nicht automatisch georeferenzierbaren Ortsangaben betrifft historische Siedlungen, Wüstungen, Klöster, Burgen, Herrschaften oder Landschaftsbezeichnungen, die in modernen Gazetteers nicht oder nur eingeschränkt nachgewiesen sind. Hinzu kommen mehrdeutige Ortsnamen sowie historische Schreibvarianten, deren eindeutige Identifizierung eine quellenkritische Einzelfallprüfung erfordert.
        </p>
        <p>
          Mit der fortschreitenden Erschließung weiterer Quellenbestände werden darüber hinaus sowohl neue historische Schreibweisen als auch bislang nicht erfasste Herkunftsorte in die Ortsliste aufgenommen. Dadurch verändern sich die Gesamtzahl der erfassten Ortsnamen, die statistischen Auswertungen sowie die kartographischen Darstellungen fortlaufend. Sämtliche Angaben stellen daher den aktuellen Bearbeitungsstand des Projekts dar. Die Georeferenzierung wird kontinuierlich überprüft und entsprechend neuer Erkenntnisse fortgeschrieben.
        </p>
      </div>
    </section>
    <section>
      <h2 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
        Hinweise zur Kartendarstellung
      </h2>
      <div class="space-y-5 text-justify">
        <p>
          Die dargestellten Punkte repräsentieren die im Rahmen des Geokodierungsprozesses ermittelten Koordinaten. Diese beziehen sich – abhängig vom jeweiligen Referenzdatensatz – auf den Mittelpunkt einer heutigen Siedlung, Gemarkung oder Verwaltungseinheit und stimmen daher nicht zwangsläufig mit der Lage des historischen Ortskerns überein. Ebenso kann die heutige administrative Zuordnung eines Ortes von den historischen Herrschafts- oder Verwaltungsverhältnissen zum Zeitpunkt der Eintragung in die Bürgermatrikel abweichen.
        </p>
        <p>
          Die Karten dienen in erster Linie der räumlichen Veranschaulichung der Herkunftsorte im überregionalen Maßstab und nicht der exakten Lokalisierung historischer Siedlungen. Eine Feinjustierung einzelner Ortslagen bleibt aufgrund der Datenmenge einer späteren Projektphase vorbehalten. Jeder kartierte Herkunftsort ist mit den zugehörigen Datensätzen der Bürgermatrikel verknüpft und ermöglicht so den direkten Zugang zu den überlieferten Quelleninformationen.
        </p>
      </div>
    </section>
    <p class="border-t border-default pt-4 text-sm text-dimmed">
      Bearbeitung: JM, Stand 31.07.2026
    </p>
  </div>
</template>

<style scoped>

</style>
