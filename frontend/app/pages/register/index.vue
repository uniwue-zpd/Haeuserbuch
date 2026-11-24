<script setup lang="ts">
import type { NavigationMenuItem } from "@nuxt/ui";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";

useHead({ title: "Register" });

type RegisterKey = "berufe" | "orte";

const route = useRoute();

/** Available registers, shown in the side menu; add new registers here and render their component below */
const registers: { key: RegisterKey; label: string; icon: string }[] = [
  { key: "berufe", label: "Register der Berufe", icon: "i-lucide-briefcase" },
  { key: "orte", label: "Register der Orte", icon: "i-lucide-map-pin" },
];

/** Selected register from `?type=`; the first register by default */
const activeRegister = computed<RegisterKey>(() =>
  registers.find((register) => register.key === route.query.type)?.key ?? registers[0]!.key,
);

/** Without a valid `?type=` (e.g. plain `/register`), write the selected register into the URL */
watch(
  () => route.query.type,
  (type) => {
    if (type !== activeRegister.value) {
      navigateTo({ query: { ...route.query, type: activeRegister.value } }, { replace: true });
    }
  },
  { immediate: true },
);

const menuItems = computed<NavigationMenuItem[]>(() =>
  registers.map((register) => ({
    label: register.label,
    icon: register.icon,
    to: { query: { type: register.key } },
    active: register.key === activeRegister.value,
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
      <UniversalSkeleton v-if="activeRegister === 'berufe'" />
      <RegisterPlaces v-if="activeRegister === 'orte'" />
    </div>
  </div>
</template>
