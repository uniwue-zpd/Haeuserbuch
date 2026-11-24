<script setup lang="ts">
import EntityRegister from "~/components/register/EntityRegister.vue";

/**
 * Place register with the tabs "Personen" and "Bürgermatrikel", embedded in the register page.
 */
type RegisterTab = "personen" | "buergermatrikel";

const route = useRoute();
const router = useRouter();

useHead({ title: "Ortsregister" });

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
      <h1 class="text-3xl font-bold montserrat-headline">Ortsregister</h1>
      <p class="text-muted">
        Liste aller Orte, die als Herkunftsort oder Wegzugsort in den Bürgermatrikeln erfasst wurden.
        Das Register erlaubt die Auflistung der zugehörigen Personen und die Einsicht in die Bürgermatrikel-Einträge, in denen der Ort vorkommt.
      </p>
    </div>
    <UTabs v-model="activeTab" :items="tabs" variant="link" class="w-full">
      <template #persons>
        <EntityRegister register="places" type="persons" class="pt-4" />
      </template>
      <template #citizenship_entries>
        <EntityRegister register="places" type="citizenships" class="pt-4" />
      </template>
    </UTabs>
  </div>
</template>
