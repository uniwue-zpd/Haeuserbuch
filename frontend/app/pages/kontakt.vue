<script setup lang="ts">
useHead(() => ({
  title: "Kontakt - Würzburger Häuserprojekt",
}));
</script>

<template>
  <div class="mx-auto w-full max-w-6xl py-4 sm:py-8 lg:py-12">
    <div class="grid gap-10 lg:grid-cols-[minmax(0,0.8fr)_minmax(0,1.2fr)] lg:gap-16">
      <section class="flex flex-col justify-between gap-10" aria-labelledby="kontakt-title">
        <div>
          <h1
            id="kontakt-title"
            class="max-w-xl font-sans text-4xl font-semibold tracking-tight text-highlighted sm:text-5xl"
          >
            Nehmen Sie Kontakt auf.
          </h1>
          <p class="mt-6 max-w-lg text-base leading-7 text-muted sm:text-lg">
            Sie haben Fragen zum Projekt, zu den Quellen oder zur Recherche im
            Häuserbuch? Schreiben Sie uns. Wir freuen uns über Ihre Nachricht.
          </p>
        </div>

        <div class="border-t border-default pt-6 text-sm leading-6 text-muted">
          <p class="font-semibold text-highlighted">Direkter Kontakt</p>
          <a
            href="mailto:jmu-haeuserprojekt@uni-wuerzburg.de"
            class="mt-2 inline-flex items-center gap-2 text-primary transition-colors hover:underline"
          >
            <UIcon name="i-lucide-mail" class="size-4" />
            jmu-haeuserprojekt@uni-wuerzburg.de
          </a>
        </div>
      </section>

      <section class="rounded-3xl border border-default bg-elevated/50 p-5 shadow-sm sm:p-8" aria-labelledby="formular-title">
        <div class="mb-7">
          <h2 id="formular-title" class="font-sans text-2xl font-semibold tracking-tight text-highlighted">
            Kontaktformular
          </h2>
        </div>

        <div class="flex items-start gap-3 rounded-2xl bg-primary/10 px-4 py-4 text-sm leading-6 text-muted" role="status">
          <UIcon name="i-lucide-info" class="mt-1 size-4 shrink-0 text-primary" />
          <p>
            Das Kontaktformular wird derzeit noch nicht unterstützt. Wir arbeiten
            daran und werden es zu einem späteren Zeitpunkt bereitstellen.
          </p>
        </div>
      </section>
    </div>
  </div>
</template>
