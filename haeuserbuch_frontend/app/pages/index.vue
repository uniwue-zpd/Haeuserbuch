<script setup lang="ts">
import QuickNavigation from "~/components/homepage/QuickNavigation.vue";

definePageMeta({ layout: "landing" });

useHead(() => ({
  title: "Häuserbuch - Hauptseite",
}));
</script>

<template>
  <div class="flex flex-col gap-16 pb-10 sm:gap-20 lg:gap-24">
    <section
      class="hero relative flex min-h-[calc(100svh-6rem)] w-full overflow-hidden rounded-3xl bg-cover bg-center sm:min-h-[calc(100svh-6.5rem)] sm:rounded-4xl"
      aria-labelledby="hero-title"
    >
      <div class="absolute inset-0 bg-[#17261f]/70" />

      <div
        class="relative flex min-h-[calc(100svh-6rem)] w-full flex-col gap-12 px-6 py-10 text-white sm:min-h-[calc(100svh-6.5rem)] sm:px-10 sm:py-14 lg:px-14 lg:py-14 xl:px-20"
      >
        <div class="flex flex-1 flex-col justify-center">
          <p
            class="mb-4 text-sm font-semibold tracking-[0.18em] text-white/75 sm:text-xl"
          >
            Das Würzburger Häuserprojekt
          </p>
          <h1
            id="hero-title"
            class="max-w-6xl font-sans text-6xl font-semibold text-balance text-[#F4F3E8] sm:text-7xl lg:text-8xl xl:text-9xl"
          >
            <span class="block">Häuserbuch ohne Häuser</span>
          </h1>
          <p
            class="mt-7 max-w-3xl text-base font-medium leading-relaxed text-white/80 sm:text-lg lg:text-xl"
          >
            Die sozialgeschichtliche und topographische Rekonstruktion einer
            untergegangenen Stadt durch Auswertung und Zusammenführung
            verschiedenartiger Archivaliengattungen des 17. bis 19. Jahrhunderts
          </p>
        </div>

        <NuxtLink
          to="/buildings"
          class="group grid grid-cols-[minmax(0,1fr)_7rem] overflow-hidden rounded-2xl bg-white text-[#283036] shadow-2xl transition-transform duration-300 hover:-translate-y-1 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-white sm:grid-cols-[minmax(0,1fr)_9rem] lg:absolute lg:bottom-14 lg:right-14 lg:w-104 lg:grid-cols-[minmax(0,1fr)_10rem] xl:bottom-20 xl:right-20"
        >
          <div class="flex min-h-32 flex-col p-4 sm:min-h-44 sm:p-5">
            <div
              class="flex items-center justify-between gap-3 text-xs font-semibold uppercase tracking-[0.12em] text-[#5b675f]"
            >
              <span>Historische Karte</span>
              <UIcon name="i-lucide-map" class="size-4" />
            </div>
            <h2
              class="mt-auto font-sans text-lg font-semibold leading-tight sm:text-2xl"
            >
              Katasterplan interaktiv erkunden
            </h2>
            <div
              class="mt-2 flex items-center gap-2 text-xs font-semibold text-[#345b4d] sm:mt-3 sm:text-sm"
            >
              <span>Karte öffnen</span>
              <UIcon
                name="i-lucide-arrow-up-right"
                class="size-4 transition-transform duration-300 group-hover:-translate-y-0.5 group-hover:translate-x-0.5"
              />
            </div>
          </div>
          <img
            src="/cta_image.webp"
            alt="Historischer Katasterplan mit dreidimensionalen Gebäuden"
            class="h-full min-h-32 w-full object-cover object-[50%_20%] sm:min-h-44 lg:min-h-44"
          />
        </NuxtLink>

        <a
          href="#features"
          class="group mx-auto flex size-14 shrink-0 items-center justify-center rounded-full border border-white/70 text-white transition-colors hover:bg-white hover:text-[#283036] focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-white sm:size-16 lg:absolute lg:bottom-14 lg:left-1/2 lg:-translate-x-1/2 xl:bottom-20"
          aria-label="Zu den Recherchebereichen scrollen"
        >
          <UIcon
            name="i-lucide-arrow-down"
            class="size-6 transition-transform duration-300 group-hover:translate-y-1"
          />
        </a>
      </div>
    </section>

    <section
      id="features"
      class="mx-auto w-full max-w-360 scroll-mt-28 px-2 sm:px-3 lg:px-4"
    >
      <div class="mb-9 sm:mb-12">
        <h2
          class="font-sans text-3xl font-semibold tracking-[-0.03em] text-highlighted sm:text-4xl lg:text-5xl"
        >
          Recherche
        </h2>
      </div>

      <QuickNavigation />
    </section>
  </div>
</template>

<style scoped>
.hero {
  background-image: url("/hero_image.webp");
}
</style>
