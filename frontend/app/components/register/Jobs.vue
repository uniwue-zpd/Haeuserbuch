<script setup lang="ts">
import EntityRegister from "~/components/register/EntityRegister.vue";

/**
 * Occupation register with the tabs "Personen" and "Bürgermatrikel", embedded in the register page.
 */
type RegisterTab = "personen" | "buergermatrikel";

const route = useRoute();
const router = useRouter();

useHead({ title: "Register der Berufe" });

const tabs = [
  { label: "Personen", value: "personen", slot: "persons" as const, icon: "i-lucide-users" },
  { label: "Bürgermatrikel", value: "buergermatrikel", slot: "citizenship_entries" as const, icon: "i-lucide-id-card" },
];

/** Active tab, kept in the URL as `?tab=` */
const activeTab = computed<RegisterTab>({
  get: () => (route.query.tab === "buergermatrikel" ? "buergermatrikel" : "personen"),
  set: (tab) => router.replace({ query: { ...route.query, tab } }),
});
</script>

<template>
  <div class="flex flex-col gap-6">
    <div class="flex flex-col gap-2">
      <h1 class="text-3xl font-bold montserrat-headline">Register der Berufe</h1>
      <p class="text-muted">
        Liste aller Berufe, die für Personen der Bürgermatrikeln erfasst wurden.
        Das Register erlaubt die Auflistung der Personen mit dem jeweiligen Beruf und die Einsicht in die
        Bürgermatrikel-Einträge, in denen eine solche Person als Bürger, Bürge oder erwähnte Person vorkommt.
      </p>
    </div>
    <UTabs v-model="activeTab" :items="tabs" variant="link" class="w-full">
      <template #persons>
        <EntityRegister register="occupations" type="persons" class="pt-4" />
      </template>
      <template #citizenship_entries>
        <EntityRegister register="occupations" type="citizenships" class="pt-4" />
      </template>
    </UTabs>
  </div>
</template>
