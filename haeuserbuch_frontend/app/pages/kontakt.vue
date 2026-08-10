<script setup lang="ts">
const form = reactive({
  name: "",
  email: "",
  subject: "",
  message: "",
});

const submitted = ref(false);

useHead(() => ({
  title: "Kontakt - Würzburger Häuserprojekt",
}));

function submitForm() {
  submitted.value = true;
}
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
            href="mailto:christian.naser@uni-wuerzburg.de"
            class="mt-2 inline-flex items-center gap-2 text-primary transition-colors hover:underline"
          >
            <UIcon name="i-lucide-mail" class="size-4" />
            christian.naser@uni-wuerzburg.de
          </a>
        </div>
      </section>

      <section class="rounded-3xl border border-default bg-elevated/50 p-5 shadow-sm sm:p-8" aria-labelledby="formular-title">
        <div class="mb-7">
          <h2 id="formular-title" class="font-sans text-2xl font-semibold tracking-tight text-highlighted">
            Kontaktformular
          </h2>
          <p class="mt-2 text-sm leading-6 text-muted">
            Füllen Sie bitte die Felder aus. Die mit * markierten Angaben sind erforderlich.
          </p>
        </div>

        <form class="space-y-5" @submit.prevent="submitForm">
          <div class="grid gap-5 sm:grid-cols-2">
            <UFormField label="Name" name="name" required>
              <UInput
                v-model="form.name"
                class="w-full"
                placeholder="Ihr Name"
                autocomplete="name"
                required
              />
            </UFormField>

            <UFormField label="E-Mail-Adresse" name="email" required>
              <UInput
                v-model="form.email"
                class="w-full"
                type="email"
                placeholder="name@beispiel.de"
                autocomplete="email"
                required
              />
            </UFormField>
          </div>

          <UFormField label="Betreff" name="subject" required>
            <UInput
              v-model="form.subject"
              class="w-full"
              placeholder="Worum geht es?"
              required
            />
          </UFormField>

          <UFormField label="Nachricht" name="message" required>
            <UTextarea
              v-model="form.message"
              class="w-full"
              :rows="7"
              placeholder="Ihre Nachricht an das Projektteam"
              required
            />
          </UFormField>

          <div class="flex flex-col gap-4 border-t border-default pt-5 sm:flex-row sm:items-center sm:justify-between">
            <p class="text-xs leading-5 text-dimmed">
              Mit dem Absenden stimmen Sie der Verarbeitung Ihrer Angaben zur
              Bearbeitung Ihrer Anfrage zu.
            </p>
            <UButton
              type="submit"
              label="Nachricht vorbereiten"
              trailing-icon="i-lucide-arrow-right"
              color="primary"
              class="shrink-0"
            />
          </div>

          <p
            v-if="submitted"
            class="flex items-center gap-2 rounded-xl bg-success/10 px-4 py-3 text-sm text-success"
            role="status"
          >
            <UIcon name="i-lucide-circle-check" class="size-4" />
            Vielen Dank. Ihre Nachricht ist vorbereitet.
          </p>
        </form>
      </section>
    </div>
  </div>
</template>
