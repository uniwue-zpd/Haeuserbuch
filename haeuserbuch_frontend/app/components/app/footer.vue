<script setup lang="ts">
const year = new Date().getFullYear();

const partners = [
  {
    href: "https://www.dfg.de/de",
    title: "Deutsche Forschungsgemeinschaft",
    image: "/dfg.png",
    alt: "DFG-Logo",
    monochrome: true,
  },
  {
    href: "https://www.uni-wuerzburg.de/",
    title: "Universität Würzburg",
    image: "/uniwue.jpg",
    alt: "Logo der Universität Würzburg",
  },
  {
    href: "https://www.gda.bayern.de/de/archives/staatsarchiv-kitzingen/",
    title: "Staatsarchiv Kitzingen",
    image: "/sta.jpg",
    alt: "Logo des Staatsarchivs Kitzingen",
  },
  {
    href: "https://www.blfd.bayern.de/",
    title: "Bayerisches Landesamt für Denkmalpflege",
    image: "/blfd.jpg",
    alt: "Logo des Bayerischen Landesamts für Denkmalpflege",
  },
  {
    href: "https://www.ldbv.bayern.de/",
    title: "Landesamt für Digitalisierung, Breitband und Vermessung",
    image: "/ldbv.svg",
    alt: "Logo des Landesamts für Digitalisierung, Breitband und Vermessung",
  },
];
</script>

<template>
  <footer
    class="bg-[#ececec] text-[#283036] transition-colors dark:bg-[#242525] dark:text-[#f1f0ec]"
  >
    <div class="mx-auto max-w-375 px-5 pb-5 pt-12 sm:px-8 sm:pt-16 lg:px-10">
      <div
        class="grid items-start gap-10 border-b border-black/10 pb-10 dark:border-white/10 lg:grid-cols-[1.5fr_0.7fr_0.9fr] lg:gap-16"
      >
        <div>
          <NuxtLink to="/" aria-label="Zur Startseite" class="inline-block">
            <img
              src="/wue_haeuserbuch_logo_skyline_bright.svg"
              alt="Würzburger Häuserprojekt"
              class="h-24 w-auto brightness-0 dark:hidden sm:h-28"
            />
            <img
              src="/wue_haeuserbuch_logo_skyline_white.svg"
              alt=""
              aria-hidden="true"
              class="hidden h-24 w-auto dark:block sm:h-28"
            />
          </NuxtLink>
        </div>

        <nav aria-label="Footer-Navigation">
          <p
            class="font-sans text-sm font-semibold uppercase tracking-[0.16em] text-[#283036]/50 dark:text-white/45"
          >
            Navigation
          </p>
          <div class="mt-4 grid gap-3 text-sm sm:grid-cols-2 lg:grid-cols-1">
            <NuxtLink
              to="/projekt"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >Projekt</NuxtLink
            >
            <NuxtLink
              to="/faq"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >FAQ</NuxtLink
            >
            <NuxtLink
              to="/team"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >Über uns</NuxtLink
            >
            <NuxtLink
              to="/buildings"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >Katasterplan</NuxtLink
            >
            <NuxtLink
              to="/places"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >Orte</NuxtLink
            >
            <NuxtLink
              to="/persons"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >Personen</NuxtLink
            >
            <NuxtLink
              to="/citizenships"
              class="w-fit text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
              >Bürgermatrikel</NuxtLink
            >
          </div>
        </nav>

        <div>
          <p
            class="font-sans text-sm font-semibold uppercase tracking-[0.16em] text-[#283036]/50 dark:text-white/45"
          >
            Kontakt
          </p>
          <NuxtLink
            to="/kontakt"
            class="mt-4 block w-fit text-sm text-[#283036]/75 transition-colors hover:text-[#283036] dark:text-white/70 dark:hover:text-white"
          >
            Kontakt
          </NuxtLink>
        </div>
      </div>

      <div class="py-8">
        <p
          class="font-sans text-xs font-semibold uppercase tracking-[0.16em] text-[#283036]/45 dark:text-white/40"
        >
          Gefördert und unterstützt von
        </p>
        <div
          class="mt-5 grid grid-cols-2 items-center justify-items-center gap-x-8 gap-y-8 sm:grid-cols-3 lg:grid-cols-5 lg:gap-x-12"
        >
          <a
            v-for="partner in partners"
            :key="partner.href"
            :href="partner.href"
            target="_blank"
            rel="noopener noreferrer"
            :title="partner.title"
            class="flex h-20 w-full items-center justify-center transition"
          >
            <img
              :src="partner.image"
              :alt="partner.alt"
              :class="[
                'max-h-18 max-w-[18rem] object-contain',
                partner.monochrome && 'brightness-0 dark:invert',
              ]"
            />
          </a>
        </div>
      </div>

      <div
        class="flex flex-col gap-3 border-t border-black/10 pt-5 text-xs text-[#283036]/50 dark:border-white/10 dark:text-white/45 sm:flex-row sm:items-center sm:justify-between"
      >
        <p>&copy; {{ year }} Würzburger Häuserprojekt</p>
        <div class="flex items-center gap-4">
          <NuxtLink
            to="/impressum"
            class="transition-colors hover:text-[#283036] dark:hover:text-white"
            >Impressum</NuxtLink
          >
          <NuxtLink
            to="/data-protection"
            class="transition-colors hover:text-[#283036] dark:hover:text-white"
            >Datenschutz</NuxtLink
          >
          <NuxtLink
            to="/login"
            class="transition-colors hover:text-[#283036] dark:hover:text-white"
            >Login</NuxtLink
          >
        </div>
      </div>
    </div>
  </footer>
</template>
