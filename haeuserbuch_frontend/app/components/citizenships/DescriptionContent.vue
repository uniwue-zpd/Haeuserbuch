<template>
  <article class="citizenship-description flex flex-col gap-4 text-base leading-7 text-highlighted sm:text-lg sm:leading-8">
    <header class="flex flex-col gap-3 rounded-2xl border border-gray-300 bg-default p-6 text-center shadow-md sm:p-8">
      <p class="text-sm font-semibold uppercase tracking-[0.18em] text-muted">Quellenbeschreibung</p>
      <h1 class="font-bold text-2xl tracking-tight sm:text-3xl">
          Die Bürgermatrikel der Jahre 1405 bis 1613 des Würzburger Stadtarchivs
      </h1>
      <h2 class="font-semibold text-lg text-muted">
          Stadtarchiv Würzburg, RB 207–209, 211–213, 215
      </h2>
      <p class="text-sm text-dimmed">Stand: 15.6.2026 / RS / CN</p>
    </header>

    <div class="grid grid-cols-1 gap-4 lg:grid-cols-12 lg:items-start">
      <section class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-7">
        <h2 class="text-xl font-semibold leading-tight text-highlighted">Bestand und Erschließung</h2>
        <div class="space-y-5 text-justify">
        <p>
          Die vorliegende Datenbank basiert auf sieben Amtsbüchern, die im
          Stadtarchiv Würzburg unter den Signaturen Ratsbuch (RB) 207–209,
          211–213 sowie 215 aufbewahrt werden. Diese enthalten die Namen der
          zwischen 1405 und 1613 neu aufgenommenen Würzburger Bürger, insgesamt
          12987 Personen.
        </p>
        <p>
          Für den Zeitraum von 1613 bis 1738 sind keine Bürgeraufnahmebücher
          vorhanden; mit RB 217 setzen die Matrikel im Jahr 1737 wieder ein und
          sind bis 1910 (RB 222) fortgeführt worden.
        </p>
        <p>
          Voraussetzungen für das Bürgerrecht in Würzburg waren im Alten Reich
          die eheliche Geburt (nachgewiesen durch einen Geburtsbrief der
          Heimatgemeinde), die Freiheit von Leibeigenschaft und ein guter
          Leumund.
        </p>
        <p>
          Die Aufnahme neuer Bürger erfolgte zweimal jährlich, jeweils um
          Pfingsten sowie im Herbst vor Beginn der Weinlese – im Zusammenhang
          mit der Erbhuldigung. Dabei hatten die Neubürger einen Eid abzulegen,
          in dem sie dem Rat und dem Bischof Gehorsam zusicherten und sich
          verpflichteten, das Wohl der Stadt zu fördern. Zudem war ein
          Bürgergeld zu entrichten, von dem die Bürgersöhne allerdings befreit
          waren.
        </p>
        <p>
          Zu den bürgerlichen Pflichten zählte die solidarische Beteiligung an
          städtischen Lasten. Diese bestanden sowohl in finanziellen Abgaben –
          etwa Wach-, Wochen- und Brunnengeldern – als auch in der Leistung von
          Wach- und Frondiensten. Im Gegenzug erhielten die Bürger das
          Aufenthaltsrecht sowie die uneingeschränkte Ausübung ihres Gewerbes.
          Darüber hinaus gewährte die Stadt umfassenden Rechtsschutz, etwa bei
          Vorladungen vor auswärtige Gerichte oder bei Forderungen auswärtiger
          Kaufleute. Sie unterstützte ihre Bürger sowohl beim Eintreiben von
          Schulden gegenüber Auswärtigen als auch, im Falle einer Gefangennahme,
          beim Freikauf.
        </p>
        <p>
          Die Bürgermatrikel wurden erst durch Georg Meyer-Erlach (1877–1961) in
          den 1950er Jahren durch eine alphabetisch geordnete „Bürgerkartei“
          nutzbar gemacht. Auf Karteikarten wurden der Tag der Aufnahme, die
          Namen der Neubürger und oft auch ihre Herkunftsorte sowie Berufe
          erfasst. Durch die von Meyer-Erlach in den Ratsbüchern mit Bleistift
          vorgenommene fortlaufende Nummerierung (1–12741) ist eine eindeutige
          Zuordnung der Einträge gewährleistet.
        </p>
        <p>
          Zusätzlich hat Meyer-Erlach Ratsprotokolle ausgewertet, in denen
          Einbürgerungen verzeichnet sind. Diese Quellen wurden in die
          vorhandene „Bürgerkartei“ durch eine spezielle Nummerierung unter
          Beibehaltung der vorhandenen Zählung am Ende eines
          Einbürgerungszeitraums integriert. So geschehen mit den Nummern 604/1
          bis 604/239 (StadtAW_Ratsprotokoll 1432-34) und 1293/1 bis 1293/8
          (StadtAW_Ratsprotokoll 1452-62), so dass nun insgesamt 12.987
          Einbürgerungen verzeichnet sind.
        </p>
        <p>
          Ab 1571 (RB 215, fol.35r; Eintrag Nr. 9123 bei Meyer-Erlach) werden
          insgesamt in 3642 Fällen zusätzlich die Namen von Bürgen verzeichnet.
          Ab 1582 (RB 215, fol. 96r; Eintrag Nr. 10239 bei Meyer-Erlach) wird
          außerdem bei insgesamt 2434 Einbürgerungen die jeweils zu stellende
          Bewaffnung bzw. Ausrüstung vermerkt.
        </p>
        <p>
          Die von Meyer-Erlach angelegte „Bürgerkartei“ ist bislang
          unveröffentlicht, kann jedoch im Stadtarchiv Würzburg eingesehen
          werden.
        </p>
        <p>
          Im Rahmen des Würzburger Häuserprojekts wurden die genannten
          Bürgeraufnahmebücher inhaltlich erschlossen und in eine Datenbank
          überführt, die Recherchen nach Personennamen, Herkunftsorte, Berufe
          und Bewaffnung ermöglicht. Diese Kategorien sind in der Datenbank
          sowohl buchstabengetreu als auch in normalisierter Form gespeichert.
          So ist z.B. die Stadt Ansbach nicht nur in den historischen
          Schreibweisen (Anoltzbach, Onspach, Onelspach, Onoltzpach Anoltzbach),
          sondern auch in der heutigen Schreibart erfasst.
        </p>
        <ul class="list-disc list-inside font-semibold">
          <li>
            <code>&lt;ORT1 hier=1&gt;Onelspach {Ansbach}&lt;/ORT&gt;</code>
          </li>
          <li>
            <code>&lt;ORT1 hier=1&gt;Anspach {Ansbach}&lt;/ORT&gt;</code>
          </li>
          <li>
            <code>&lt;ORT1 hier=1&gt;Onoltzpach {Ansbach}&lt;/ORT&gt;</code>
          </li>
          <li>
            <code>&lt;ORT1 hier=1&gt;Onspach {Ansbach}&lt;/ORT&gt;</code>
          </li>
        </ul>
        <p>
          Genauso wurde bei Personennamen, Berufen und der Bewaffnung verfahren.
        </p>
        </div>
      </section>

      <section id="persons" class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-5">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">1. Personennamen</h2>
          <NuxtLink to="#persons" aria-label="Zu Personennamen verlinken" class="text-muted hover:text-highlighted">
            <Icon name="material-symbols-link-2" />
          </NuxtLink>
        </div>
        <div class="space-y-5 text-justify">
        <p>
          Da außer den in Ratsbüchern und Ratsprotokollen verzeichneten 12.987
          Neubürgern auch die Namen der Zeugen und Bürgen erfasst wurden, sind
          insgesamt 17.343 Personen namentlich nachgewiesen.
        </p>
        <p>
          Auch die Personennamen werden buchstabengetreu und normiert
          wiedergegeben. Ansonsten wurde nur bei flektierten Formen in der Art
          eingegriffen, dass die Endungen entfallen („Müllers“ wird zu „Müller“,
          „Lutzen“ zu „Lutz“). Die weibliche Namensform auf -in („Barbara
          Linhartin“) wird in dieser Version übernommen.
        </p>
        </div>
      </section>

      <section id="places" class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-7">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">2. Orte</h2>
          <NuxtLink to="#places" aria-label="Zu Orte verlinken" class="text-muted hover:text-highlighted">
            <Icon name="material-symbols-link-2" />
          </NuxtLink>
        </div>
        <div class="space-y-5 text-justify">
        <p>
          Für die Datenbank wurden die genannten Orte nach der heutigen
          Verwaltungszugehörigkeit (Randersacker, LK Würzburg) aufgenommen und
          in der Datenbank fünf Kategorien zugeordnet.
        </p>
        <ul class="list-decimal list-inside">
          <li>
            Unter die Kategorie eins fallen Orte, die eindeutig zugeordnet
            werden können (siehe oben „Ansbach“). Bei Ortsnamen wie „Hausen“
            wird davon ausgegangen, dass nicht das Hausen bei Dillingen a.d.
            Donau gemeint ist, sondern Hausen bei Würzburg.
          </li>
          <li>
            In der zweiten Kategorie sind Orte erfasst, die nicht eindeutig
            zugeordnet werden können. Bei unpräzisen Angaben - wie z.B.
            „Neustadt“ - werden deshalb die wahrscheinlichsten, möglichst nah an
            Würzburg liegenden Ortschaften vorgeschlagen (Neustadt/Saale;
            Neustadt a. Main; Neustadt/Aisch).
          </li>
          <li>
            Eine dritte Kategorie bilden die Fälle, deren Namensformen bislang
            nur eine spekulative Zuordnung erlauben. So könnte z.B. mit „Nutzing
            bey Lichtenfels“ Uetzing, ein Gemeindeteil der Stadt Bad
            Staffelstein im Landkreis Lichtenfels gemeint sein oder „Burgasgrün
            bey Hoff“ könnte Bobengrün, ein Gemeindeteil von Markt Bad Steben im
            Landkreis Hof sein. in. Ist selbst eine derartige wage Zuordnung
            nicht möglich, wird der Namen mit einem Asterisk versehen, wie
            „Sackich Mainzischen gebiets“ oder „Capel bey Burckay“
          </li>
          <li>
            In der vierten Kategorie finden sich Orte bzw. Regionen, in die
            Würzburger Bürger fortgezogen sind.
          </li>
          <li>
            Die letzte Kategorie (0) umfasst Ortsangaben innerhalb der Stadt
            Würzburg, vor allem die inneren (Cresser, Dietricher, Gänheimer,
            Bastheimer Viertel) und äußeren Viertel (Pleicher, Hauger, Sander
            und Mainser Viertel).
          </li>
        </ul>
        <p>
          Die 8717 Ortsangaben verteilen sich dabei folgendermaßen: Kategorie 1
          (7351 Orte), Kategorie 2 (1053), Kategorie 3 (97, davon 16 mit
          Asterisk), Kategorie 4 (12) und Kategorie 5 (204).
        </p>
        </div>
      </section>

      <section id="occupations" class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-5">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">3. Berufe</h2>
          <NuxtLink to="#occupations" aria-label="Zu Berufen verlinken" class="text-muted hover:text-highlighted">
            <Icon name="material-symbols-link-2" />
          </NuxtLink>
        </div>
        <div class="space-y-5 text-justify">
        <p>
          Die 8819 verzeichneten Berufsangaben wurden auf eine heute geläufige
          Form zurückgeführt (Beckh → Bäcker), wobei auch Unterkategorien
          gebildet wurden (Schmied / Kupferschmied oder Gerber / Rotgerber oder
          Lober bzw. Löber [= Gerber/Lohgerber]).
        </p>
        <p>
          Bei Bediensteten, wird – sofern bekannt – auch der Name des
          Dienstherrn angeben (Hoflakai = Lakai/fürstbischöflicher Hof;
          Syndicus/Domkapitel; Koch/Kloster St. Stephan).
        </p>
        </div>
      </section>

      <section id="weapons" class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-5">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">4. Bewaffnung</h2>
          <NuxtLink to="#weapons" aria-label="Zu Bewaffnung verlinken" class="text-muted hover:text-highlighted">
            <Icon name="material-symbols-link-2" />
          </NuxtLink>
        </div>
        <div class="space-y-5 text-justify">
        <p>
          Wie bei den bisher behandelten Kategorien wird bei der Bewaffung (2434
          Einträge) außer der Schreibform auch eine normalisierte angeboten
          („sturmbhutt“ ist „Sturmhut“ oder „hellbarten“ ist „Hellebarde“ usw.)
        </p>
        </div>
      </section>

      <section id="bibliography" class="flex min-w-0 flex-col gap-5 rounded-2xl border border-gray-300 bg-default p-5 shadow-md lg:col-span-7">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold leading-tight text-highlighted">Literatur</h2>
          <NuxtLink to="#bibliography" aria-label="Zu Literatur verlinken" class="text-muted hover:text-highlighted">
            <Icon name="material-symbols-link-2" />
          </NuxtLink>
        </div>
        <div class="space-y-5 text-justify">
        <ul class="list-disc list-inside">
          <li>
            Bergerhausen, Hans-Wolfgang: Ratseide. Einleitung zu Anhang 2, in:
            Würzburger Ratsprotokolle der Riemenschneiderzeit, Teil 1:
            1504–1513, bearbeitet von Uwe Schreiber und Renate Schindler unter
            Mitwirkung von Hans- Wolfgang Bergerhausen (Fontes Herbipolenses X),
            Würzburg 2020, S. 657–663.
          </li>
          <li>
            Götz, Hannelore: Würzburg im 16. Jahrhundert. Bürgerliche Vermögen
            und städtische Führungsschichten zwischen Bauernkrieg und
            fürstbischöflichem Absolutismus (Veröffentlichungen des Stadtarchivs
            Würzburg 2), Würzburg 1986.
          </li>
          <li>
            Schott, Herbert: Fürstlicher Absolutismus und barocke Stadt, in:
            Geschichte der Stadt Würzburg 2, Stuttgart 2004, S. 130–202 (hier S.
            166ff.).
          </li>
          <li>
            Soder von Güldenstubbe, Erik: Sozialgeschichte der Stadt Würzburg
            1500–1815, in: SGII, Stuttgart 2004, S. 464–488 (hier S. 467f.).
          </li>
          <li>
            Sprandel, Rolf: Das Würzburger Ratsprotokoll des 15. Jahrhunderts:
            eine historisch- systematische Analyse (Veröffentlichungen des
            Stadtarchivs Würzburg 11), Würzburg 2003.
          </li>
        </ul>
        </div>
      </section>
    </div>
  </article>
</template>
