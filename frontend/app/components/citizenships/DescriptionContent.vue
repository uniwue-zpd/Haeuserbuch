<script setup lang="ts">
</script>
<template>
  <article class="citizenship-description space-y-7 text-base leading-7 text-highlighted sm:text-lg sm:leading-8">
    <header class="space-y-3 text-center">
      <p class="text-sm font-semibold uppercase tracking-[0.18em] text-muted">Quellenbeschreibung</p>
      <h1 class="font-bold text-2xl tracking-tight sm:text-3xl">Die Bürgermatrikel-Datenbank</h1>
      <h2 class="font-semibold text-lg text-muted">Stadtarchiv Würzburg, RB 207–209, 211–213, 215</h2>
    </header>
    <section class="space-y-5">
      <h2 class="mb-3 text-center text-xl font-semibold leading-tight text-highlighted sm:text-2xl">Projektbeschreibung und Editionsrichtlinien</h2>
      <div class="space-y-3 text-justify">
        <p>
          Die Bürgermatrikel-Datenbank erfasst auf Grundlage der Ratsbücher 207 bis 215 des
          Stadtarchivs Würzburg die Einbürgerungen der Jahre 1405 bis 1613. Sie ist konzeptionell
          so angelegt, dass sie in mehrfacher Hinsicht erweitert und optimiert werden kann.
        </p>
        <p>
          Bereits Georg Meyer-Erlachs Arbeit ging über den Bestand der Ratsbücher hinaus,
          indem er zusätzlich Ratsprotokolle dieses Zeitraums ausgewertet hatte, in denen
          ebenfalls Einbürgerungen verzeichnet sind. Auf diese Weise konnte er die Zahl der
          dokumentierten Einbürgerungen von 12.741 auf 12.987 erhöhen.
        </p>
        <p>
          Für den Zeitraum von 1613 bis 1737 liegen keine Bürgeraufnahmebücher vor; es ist
          bislang ungeklärt, ob diese verloren gingen oder nie angelegt wurden. Mit dem Ratsbuch
          217 setzen die Aufnahmematrikel im Jahr 1737 wieder ein und werden bis 1910
          fortgeführt (Ratsbuch 222).In einer kommenden Bewilligungsphase der DFG würden
          diese Matrikel in die Datenbank eingepflegt werden.
        </p>
        <p>
          Auch für die fast 125 Jahre umfassende Überlieferungslücke des Stadtarchivs bietet das
          DFG-Projekt „Häuserbuch ohne Häuser“ eine Lösung: Durch die Erschließung der ab
          1644 im Staatsarchiv Kitzingen bzw. im Stadtarchiv Würzburg erhaltenen Steuerbücher
          lässt sich diese Lücke deutlich verringern. So ergab die Analyse der Steuerbücher
          Rößnerbuch 1606 und 1608, die ab den Jahren 1651 und 1668 und somit in der Zeit der
          Überlieferungslücke angelegt wurden, dass dort explizit 102 bzw. 337 Einbürgerungen
          mit jeweiligem Aufnahmedatum verzeichnet sind. Diese Steuerbücher werden im
          laufenden Projekt bearbeitet und die darin enthaltenen Informationen zu
          Einbürgerungen in die Bürgermatrikel-Datenbank eingebunden.
        </p>
        <p>
          Anhand der Bürgermatrikel-Datenbank lässt sich somit der Synergieeffekt
          verdeutlichen, der aus der Verknüpfung verschiedener Würzburger Archivbestände
          resultiert. Eine weitere, künftig für die Matrikel-Datenbank zu erschließende und
          besonders ertragreiche Informationsquelle sind die Kirchenbücher des Würzburger
          Diözesanarchivs; bei Ehen und Taufen wird häufig die Bürgerschaft der beteiligten
          Trauzeugen und Taufpaten vermerkt. Die hier skizzierte Methode der Rekonstruktion der
          Bürgermatrikel bzw. der Schließung von Überlieferungslücken durch den
          Zusammenschluss mehrerer Archive wie auch verschiedenartiger Archivalien besitzt
          modellhaften Charakter.
        </p>
        <p>
          Das bisher Gesagte zeigt bereits eindrücklich, dass die Bürgermatrikel-Datenbank
          insgesamt als „project in progress“ konzipiert ist.
          Auch die bislang erfassten rund 13.000 Matrikel sind noch nicht als endgültig
          abgeschlossen zu betrachten, da die Einträge nicht im Sinne einer „traditionellen“
          Edition bearbeitet wurden. Vor- und Nachnamen, Ortsangaben sowie
          Berufsbezeichnungen wurden buchstabengetreu erfasst; die übrigen Bestandteile der
          Einträge, etwa Jahresangaben oder Angaben zu Bürgergeldzahlungen, dagegen lediglich
          wortgetreu wiedergegeben. Ergänzend zu den buchstabengetreuen Transkriptionen
          wurde jeweils eine normalisierte Form angelegt. So wird beispielsweise der Vorname
          „Hans“ sowohl in dieser heutzutage gebräuchlichen (normalisierten) Form als auch in
          seinen unterschiedlichen historischen Schreibweisen – etwa „Hanns“, „Hannß“ oder
          „Hanß“ – in der Datenbank gespeichert.
        </p>
        <p>
          Während die Bildung einer normalisierten Form bei Vornamen zunächst entbehrlich
          erscheinen mag, ist sie bei Berufsbezeichnungen sinnvoll ("smyt", "smit", "schmit" usw.
          für „Schmied“) und bei Ortsnamen unverzichtbar, um die erfassten Informationen
          weiterverarbeiten zu können. So werden die Schreibweisen „Onelspach“, „Onoltzpach“,
          „Onspach“ und „Anspach“ der mittelfränkischen Stadt Ansbach zugeordnet. Dadurch
          können sie etwa für Register oder kartografische Visualisierungen nutzbar gemacht
          werden (
          <NuxtLink
              to="/orte"
              class="inline-flex items-center gap-1 font-medium hover:underline"
          >
            Ortsregister
            <UIcon name="i-lucide-arrow-up-right" class="size-4 shrink-0" />
          </NuxtLink>
          ). Zu diesem Zweck wurden die
          Herkunftsorte außerdem ihrer heutigen Verwaltungszugehörigkeit – beispielsweise
          Randersacker, Landkreis Würzburg – zugeordnet und in der Datenbank verschiedenen
          Kategorien zugewiesen.
        </p>
        <p>
          Von den mehr als 8.700 erfassten Ortsbezeichnungen lassen sich rund 7.350
          vergleichsweise sicher bestimmen. Bei mehr als 1.000 unpräzisen Angaben – etwa
          „Neustadt“ – kommen hingegen mehrere mögliche Orte infrage, beispielsweise
          Neustadt an der Saale, Neustadt am Main oder Neustadt an der Aisch. In diesen Fällen
          kann die eindeutige Identifizierung nur durch weitere Forschungen geklärt werden, etwa
          anhand der Archivalien der jeweiligen Gemeinden oder ihrer Kirchenbücher.
        </p>
        <p>
          Auch die Personennamen werden, wie bereits erwähnt, sowohl buchstabengetreu als
          auch in normalisierter Form wiedergegeben. Eingriffe erfolgen dabei allerdings vorerst
          grundsätzlich nur bei flektierten Namensformen: In der normalisierten Fassung werden
          die Flexionsendungen entfernt, sodass etwa „Müllers“ zu „Müller“ und „Lutzen“ zu
          „Lutz“ wird.
        </p>
        <p>
          Die Bürgermatrikel-Datenbank ist somit sowohl hinsichtlich ihres Umfangs als auch
          ihrer inhaltlichen Präzision als „project in progress“ zu verstehen. Durch ihre
          wissenschaftliche Nutzung wird sie fortlaufend erweitert, präzisiert und optimiert
          werden. In diesem Sinne bildet sie gewissermaßen das lebendige Meldeamt der
          Vergangenheit, wobei ein „Treibstoff“ ihrer Weiterentwicklung die Korrektur- und
          Ergänzungsvorschläge ihrer Nutzerinnen und Nutzer sind.
        </p>
      </div>
    </section>
  </article>
</template>
