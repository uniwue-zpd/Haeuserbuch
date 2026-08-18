<script setup lang="ts">
import { researchNavigation } from "~/utils/researchNavigation";

const year = new Date().getFullYear();
const { loggedIn } = useUserSession();

const footerGroups = [
  {
    label: "Recherche",
    links: researchNavigation
      .filter((item) => item.to)
      .map((item) => ({ label: item.label, to: item.to as string })),
  },
  {
    label: "Projekt",
    links: [
      { label: "Das Projekt", to: "/projekt" },
      { label: "Team", to: "/team" },
    ],
  },
  {
    label: "Allgemein",
    links: [
      { label: "Startseite", to: "/" },
      { label: "Kontakt", to: "/kontakt" },
    ],
  },
];

const partners = [
  {
    href: "https://www.dfg.de/de",
    title: "Deutsche Forschungsgemeinschaft",
    image: "/funding/dfg.webp",
    alt: "DFG-Logo",
    monochrome: true,
  },
  {
    href: "https://www.uni-wuerzburg.de/",
    title: "Universität Würzburg",
    image: "/funding/uniwue.webp",
    alt: "Logo der Universität Würzburg",
  },
  {
    href: "https://www.gda.bayern.de/de/archives/staatsarchiv-kitzingen/",
    title: "Staatsarchiv Kitzingen",
    image: "/funding/sta.webp",
    alt: "Logo des Staatsarchivs Kitzingen",
  },
  {
    href: "https://www.blfd.bayern.de/",
    title: "Bayerisches Landesamt für Denkmalpflege",
    image: "/funding/blfd.webp",
    alt: "Logo des Bayerischen Landesamts für Denkmalpflege",
  },
  {
    href: "https://www.ldbv.bayern.de/",
    title: "Landesamt für Digitalisierung, Breitband und Vermessung",
    image: "/funding/ldbv.webp",
    alt: "Logo des Landesamts für Digitalisierung, Breitband und Vermessung",
  },
  {
    href: "https://www.wuerzburg.de/themen/kultur-bildung-kulturangebot/stadtarchiv",
    title: "Stadtarchiv Würzburg",
    image: "/funding/stadtarchiv.webp",
    alt: "Logo des Stadtarchiv Würzburg",
  },
  {
    href: "https://www.wuerzburg.de/",
    title: "Stadt Würzburg",
    image: "/funding/stadt_wue.webp",
    alt: "Logo der Stadt Würzburg",
  },
];
</script>

<template>
  <footer
    class="bg-gray-200 text-slate-800 transition-colors dark:bg-zinc-800 dark:text-stone-100"
  >
    <div class="mx-auto max-w-375 px-5 pb-5 pt-12 sm:px-8 sm:pt-16 lg:px-10">
      <div
        class="grid items-start gap-10 border-b border-black/10 pb-10 dark:border-white/10 lg:grid-cols-3 lg:gap-16"
      >
        <nav
          v-for="group in footerGroups"
          :key="group.label"
          :aria-label="`${group.label} im Footer`"
        >
          <p
            class="font-sans text-sm font-semibold uppercase tracking-widest text-slate-800/50 dark:text-white/45"
          >
            {{ group.label }}
          </p>
          <div class="mt-4 grid gap-3 text-sm">
            <NuxtLink
              v-for="link in group.links"
              :key="link.to"
              :to="link.to"
              class="w-fit text-slate-800/75 transition-colors hover:text-slate-800 dark:text-white/70 dark:hover:text-white"
            >
              {{ link.label }}
            </NuxtLink>
          </div>
        </nav>
      </div>

      <div class="py-8">
        <p
          class="font-sans text-xs font-semibold uppercase tracking-widest text-slate-800/45 dark:text-white/40"
        >
          Gefördert und unterstützt von
        </p>
        <div
          class="mt-5 flex flex-wrap items-center justify-start gap-x-6 gap-y-8 sm:gap-x-10"
        >
          <a
            v-for="partner in partners"
            :key="partner.href"
            :href="partner.href"
            target="_blank"
            rel="noopener noreferrer"
            :title="partner.title"
            class="flex h-20 w-fit max-w-full shrink-0 items-center justify-start transition"
          >
            <img
              :src="partner.image"
              :alt="partner.alt"
              :class="[
                'max-h-18 max-w-[calc(100vw-2.5rem)] object-contain sm:max-w-72',
                partner.monochrome && 'brightness-0 dark:invert',
              ]"
            />
          </a>
        </div>
      </div>

      <div
        class="flex flex-col gap-3 border-t border-black/10 pt-5 text-xs text-slate-800/50 dark:border-white/10 dark:text-white/45 sm:flex-row sm:items-center sm:justify-between"
      >
        <p>&copy; {{ year }} Würzburger Häuserprojekt</p>
        <div class="flex items-center gap-4">
          <NuxtLink
            to="/impressum"
            class="transition-colors hover:text-slate-800 dark:hover:text-white"
            >Impressum</NuxtLink
          >
          <NuxtLink
            to="/data-protection"
            class="transition-colors hover:text-slate-800 dark:hover:text-white"
            >Datenschutz</NuxtLink
          >
          <a
            v-if="!loggedIn"
            href="/auth/login"
            class="transition-colors hover:text-slate-800 dark:hover:text-white"
            >Login</a
          >
        </div>
      </div>
    </div>
  </footer>
</template>
