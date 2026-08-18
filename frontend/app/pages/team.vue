<script setup lang="ts"></script>

<template>
  <div class="mx-auto w-full max-w-6xl space-y-10">
    <h1 class="text-3xl font-bold tracking-tight text-highlighted sm:text-4xl">
      Das Team
    </h1>
    <div class="space-y-4">
      <div>
        <h2 class="text-xl font-semibold tracking-tight text-highlighted">Projektleitung</h2>
      </div>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">
            Dr. Christian Naser
          </h3>
          <p class="mt-1 text-sm text-dimmed">Projektleitung</p>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
            Der akademische Direktor Christian Naser promovierte 1993 an der
            Universität Würzburg mit der Arbeit „Der geistliche Streit. Synoptischer
            Abdruck der Fassungen A, C, B und D. Kommentar und Motivgeschichte“.
            Seit 1987 arbeitet er als wissenschaftlicher Mitarbeiter am Institut für
            dt. Philologie, zunächst in der Sprachwissenschaft, dann in der
            Mediävistik und in der Neueren Abteilung. Sein Zuständigkeitsbereich
            sind die EDV-Belange in philologisch ausgerichteten Forschungsprojekten.
            So übernahm er 1987 die EDV-Betreuung von den Würzburger und Eichstätter
            Projekten des Sonderforschungsbereichs 226 (= Wissensorganisierende u.
            wissensvermittelnde Literatur im Mittelalter). Ab 1994 betreute er die
            fünf Einzelprojekte der interdisziplinären DFG-geförderten
            Forschergruppe „Das Bild des Krieges im Wandel vom späten Mittelalter
            zur frühen Neuzeit“. Seit 2005 betreut Christian Naser das von der DFG
            geförderte Langzeitprojekt der historisch-kritischen Jean- Paul-Ausgabe
            und seit 2022 im Anschluss das Projekt der Bayerischen Akademie der
            Wissenschaften „Sprachgitter digital: Die historisch-kritische
            Jean-Paul-Ausgabe“. Darüber hinaus leitet er ab Januar 2025 in
            Kooperation mit dem Staatsarchiv Würzburg das DFG-Projekt „Würzburg:
            Häuserbuch ohne Häuser Die sozialgeschichtliche und topographische
            Rekonstruktion einer untergegangenen Stadt durch Auswertung und
            Zusammenführung verschiedenartiger Archivaliengattungen des 17. bis 19.
            Jahrhunderts“.
          </p>
        </div>
        <footer class="border-t border-default pt-4 text-sm">
          <h4 class="mb-2 font-semibold text-highlighted">Kontakt</h4>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">E-Mail:</p>
            <NuxtLink
              to="mailto:christian.naser@uni-wuerzburg.de"
              target="_blank"
              class="text-primary hover:underline"
              >christian.naser@uni-wuerzburg.de</NuxtLink
            >
          </div>
        </footer>
      </article>
    </div>
    <div class="space-y-4">
      <div>
        <h2 class="text-xl font-semibold tracking-tight text-highlighted">Mitarbeitende</h2>
      </div>
      <div class="grid gap-6 md:grid-cols-2">
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Dr. Alexander Wolz</h3>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
          Dr. Alexander Wolz studierte Geschichte und Volkskunde an der
          Julius-Maximilians-Universität in Würzburg. Im Jahr 2012 wurde er mit
          einer Arbeit über die Rheinlandkrise 1936 promoviert. Nach einer
          kurzen Tätigkeit beim Stadtarchiv Lohr a. Main besuchte er von 2014
          bis 2016 die Bayerische Archivschule. Von 2016 bis 2021 leitete er das
          Staatsarchiv Coburg. Seit 2021 ist er der Leiter des Staatsarchivs
          Würzburg/Kitzingen. Wolz forscht zur fränkischen Landesgeschichte,
          insbesondere mit den Schwerpunkten im 19. und 20. Jahrhundert.
          </p>
        </div>
        <footer class="space-y-2 border-t border-default pt-4 text-sm">
          <h4 class="font-semibold text-highlighted">Kontakt</h4>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">E-Mail:</p>
            <NuxtLink
              to="mailto:alexander.wolz@stakt.bayern.de"
              target="_blank"
              class="text-primary hover:underline"
              >alexander.wolz@stakt.bayern.de</NuxtLink
            >
          </div>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">Dienststelle:</p>
            <p class="text-default">
            Staatsarchiv Kitzingen, Deuster-Park 1, 97318 Kitzingen
            </p>
          </div>
        </footer>
      </article>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Hans-Wolfgang Bergerhausen</h3>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
          Hans-Wolfgang Bergerhausen lehrte seit der Habilitation 1999 als
          Privatdozent und als außerplanmäßiger Professor bis 2018 Neuere
          Geschichte an der Universität Würzburg. In seinen Arbeiten verknüpft
          er frühneuzeitliche Reichsgeschichte mit vergleichender Landes- und
          Stadtgeschichte sowie der Konfessionalisierungsforschung. Aus seinen
          intensiven archivalischen Recherchen zur Geschichte Würzburgs in der
          Frühen Neuzeit ging 2014 eine umfangreiche Edition „Quellen zur
          Geschichte des Bürgerspitals Würzburg 1500 – 1650“ hervor. Ihr folgten
          weitere ganz aus den Quellen geschöpfte Arbeiten zum Fürsorgewesen in
          Würzburg und 2021 eine Biographie des Würzburger Bürgermeisters und
          Spitalpflegers Paulus von Worms (+ 1579). Zum Reformationsjubiläum
          verfasste er eine Studie über „Protestantisches Leben in Würzburg
          während des 16. Jahrhunderts“. Ferner wirkte er an der Edition der
          „Würzburger Ratsprotokolle der Riemenschneiderzeit“ mit, deren erster
          Band 2020 erschienen ist.
          </p>
        </div>
      </article>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Julia Merz, M.A.</h3>
          <p class="mt-1 text-sm text-dimmed">Verarbeitung geospatialer Daten</p>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <div class="space-y-3">
          <p>
            Julia Merz, M.A., ist seit Januar 2025 wissenschaftliche
            Mitarbeiterin am Institut für Deutsche Philologie der
            Julius-Maximilians-Universität Würzburg im von der Deutschen
            Forschungsgemeinschaft (DFG) geförderten Würzburger Häuserprojekt
            unter der Leitung von Dr. Christian Naser. Das Projekt verfolgt die
            Rekonstruktion der historischen Sozialtopographie Würzburgs sowie
            die Geschichte ihrer Häuser und Bewohnerinnen und Bewohner auf
            Grundlage bislang kaum erschlossener archivalischer Quellen.
          </p>
          <p>
            Nach ihrem Studium der Archäologie des Mittelalters und der Neuzeit,
            Bau- und Kunstdenkmalpflege sowie Restaurierungswissenschaften an
            der Otto-Friedrich-Universität Bamberg liegen ihre Schwerpunkte auf
            denkmalpflegerischen Erhebungen, kommunalen Denkmalkonzepten und
            historischen Ortsanalysen. Ein besonderer Fokus ihrer Arbeit liegt
            auf GIS-basierter Kartierung, 3D-Modellierung und Datenmodellierung
            im kulturellen Kontext.
          </p>
          <p>
            Im Würzburger Häuserprojekt ist Julia Merz verantwortlich für die
            räumliche Erfassung, georeferenzierte Auswertung und
            denkmalpflegerische Kontextualisierung der historischen
            Stadtstruktur. Ihre langjährige freiberufliche Erfahrung im Bereich
            der städtebaulichen Denkmalpflege fließt dabei maßgeblich in ihre
            Arbeit ein.
          </p>
          </div>
        </div>
        <footer class="border-t border-default pt-4 text-sm">
          <h4 class="mb-2 font-semibold text-highlighted">Kontakt</h4>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">E-Mail:</p>
            <NuxtLink
              to="mailto:julia.merz@uni-wuerzburg.de"
              target="_blank"
              class="text-primary hover:underline"
              >julia.merz@uni-wuerzburg.de</NuxtLink
            >
          </div>
        </footer>
      </article>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Dr. Renate Schindler</h3>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
            Studium der Geschichte in Marburg und Bonn, dort Magister Artium und
            Promotion an der Rheinischen Friedrich-Wilhelms-Universität. Zweites
            Staatsexamen für den Archivdienst. Tätigkeit als Archivarin in
            Koblenz, Wertheim und Würzburg.
          </p>
        </div>
        <footer class="border-t border-default pt-4 text-sm">
          <h4 class="mb-2 font-semibold text-highlighted">Kontakt</h4>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">ORCID:</p>
            <NuxtLink
              to="https://orcid.org/0009-0000-2430-5616"
              target="_blank"
              class="text-primary hover:underline"
              >0009-0000-2430-5616</NuxtLink
            >
          </div>
        </footer>
      </article>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Dr. Stephan Moser</h3>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
            Stephan Moser studierte Germanistik und Geschichte in Augsburg,
            Regensburg und Würzburg. 1987 bis 2024 arbeitete er am Würzburger
            Institut für deutsche Philologie in den Bereichen Sprachwissenschaft
            und Computerphilologie. 1994 promovierte er in der Deutschen
            Sprachwissenschaft mit einer Arbeit zur Herausbildung der deutschen
            Fachsprachen im Spätmittelalter.
          </p>
        </div>
      </article>
      </div>
    </div>
    <div class="space-y-4">
      <div>
        <h2 class="text-xl font-semibold tracking-tight text-highlighted">Technische Umsetzung</h2>
      </div>
      <div class="grid gap-6 md:grid-cols-2">
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Tomash Shtohryn, M.A.</h3>
          <p class="mt-1 text-sm text-dimmed">Full-Stack-Entwicklung</p>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
          Tomash Shtohryn hat sein Studium in Fächern Political and Social
          Studies und Digital Humanities als Nebenfach absolviert. Während des
          Masterstudiums machte er die Digital Humanities zu seinem Hauptfach
          und ist aktuell wissenschaftlicher Mitarbeiter am Zentrum für
          Philologie und Digitalität an der Universität Würzburg. Hier ist er an
          mehreren Forschungsprojekten beteiligt, deren Schwerpunkt auf der
          Erstellung und Kuratierung von Digitalen Editionen liegt. Auch die
          Entwicklung von vollwertigen interaktiven Ressourcen auf Basis einer
          Datenbank macht einen wesentlichen Bestandteil seiner Tätigkeit aus.
          </p>
        </div>
        <footer class="space-y-2 border-t border-default pt-4 text-sm">
          <h4 class="font-semibold text-highlighted">Kontakt</h4>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">E-Mail:</p>
            <NuxtLink
              to="mailto:tomash.shtohryn@uni-wuerzburg.de"
              target="_blank"
              class="text-primary hover:underline"
              >tomash.shtohryn@uni-wuerzburg.de</NuxtLink
            >
          </div>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">ORCID:</p>
            <NuxtLink
              to="https://orcid.org/0009-0000-4597-603X"
              target="_blank"
              class="text-primary hover:underline"
              >0009-0000-4597-603X</NuxtLink
            >
          </div>
        </footer>
      </article>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Florian Langhanki, M.A.</h3>
        </header>
        <footer class="mt-auto border-t border-default pt-4 text-sm">
          <h4 class="mb-2 font-semibold text-highlighted">Kontakt</h4>
          <div class="flex flex-wrap gap-x-3 gap-y-1">
            <p class="font-medium text-highlighted">E-Mail:</p>
            <NuxtLink
              to="mailto:florian.langhanki@uni-wuerzburg.de"
              target="_blank"
              class="text-primary hover:underline"
              >florian.langhanki@uni-wuerzburg.de</NuxtLink
            >
          </div>
        </footer>
      </article>
      <article class="flex flex-col gap-6 rounded-md border border-default bg-default p-6 shadow-sm">
        <header>
          <h3 class="text-2xl font-semibold tracking-tight text-highlighted">Natália Ratulovská, B.A.</h3>
          <p class="mt-1 text-sm text-dimmed">Datentransformation und -verarbeitung</p>
        </header>
        <div class="space-y-3 text-justify leading-7 text-default">
          <h4 class="text-sm font-semibold uppercase tracking-wide text-dimmed">Vita</h4>
          <p>
            Natália Ratulovská absolvierte ihr Bachelorstudium an der LMU München,
            wobei sie Germanistik als Hauptfach und Digital Humanities als
            Nebenfach belegte. Gegenwärtig studiert sie im Masterstudiengang
            Digital Humanities an der JMU Würzburg. Parallel zum Studium ist sie
            als Studentische Hilfskraft am Zentrum für Philologie und Digitalität
            der JMU tätig.
          </p>
        </div>
      </article>
      </div>
    </div>
  </div>
</template>
