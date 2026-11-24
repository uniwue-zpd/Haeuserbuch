<script setup lang="ts">
import type { NavigationMenuItem } from "@nuxt/ui";

useHead({ title: "Register" });

type RegisterKey = "occupations" | "places";

const route = useRoute();

const registers: { key: RegisterKey; slug: string; label: string; icon: string }[] = [
  { key: "occupations", slug: "berufe", label: "Register der Berufe", icon: "i-lucide-briefcase" },
  { key: "places", slug: "orte", label: "Register der Orte", icon: "i-lucide-map-pin" },
];

const activeRegister = computed(() =>
  registers.find((register) => register.slug === route.query.type) ?? registers[0]!,
);

watch(
  () => route.query.type,
  (type) => {
    if (type !== activeRegister.value.slug) {
      navigateTo({ query: { ...route.query, type: activeRegister.value.slug } }, { replace: true });
    }
  },
  { immediate: true },
);

const menuItems = computed<NavigationMenuItem[]>(() =>
  registers.map((register) => ({
    label: register.label,
    icon: register.icon,
    to: { query: { type: register.slug } },
    active: register.key === activeRegister.value.key,
  })),
);
</script>

<template>
  <div class="grid grid-cols-1 gap-6 lg:grid-cols-12">
    <aside class="lg:col-span-3">
      <div class="flex flex-col gap-2 rounded-md border border-default bg-default p-3 shadow-md lg:sticky lg:top-24">
        <p class="px-2 text-sm font-semibold text-muted">Register</p>
        <UNavigationMenu :items="menuItems" orientation="vertical" />
      </div>
    </aside>
    <div class="min-w-0 lg:col-span-9">
      <RegisterJobs v-if="activeRegister.key === 'occupations'" />
      <RegisterPlaces v-if="activeRegister.key === 'places'" />
    </div>
  </div>
</template>
