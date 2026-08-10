<script setup lang="ts">
const props = defineProps<{
  weapons: WeaponryDTO[];
}>();
</script>

<template>
  <div class="flex w-full flex-wrap gap-3">
    <template v-for="(weaponItem, weaponIndex) in props.weapons">
      <NuxtLink
        v-if="weaponItem.weapon"
        :key="weaponItem.id ?? `weapon-${weaponIndex}`"
        :to="`/weapons/${weaponItem.weapon.id}`"
        class="flex min-w-52 flex-col gap-3 rounded-lg border border-default p-3 text-highlighted transition-colors hover:border-accented hover:text-primary focus-visible:border-accented focus-visible:outline-none"
      >
        <span class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0">
          <span class="font-semibold text-muted">Überlieferte Bezeichnung</span>
          <span class="italic">{{ weaponItem.originalText || "unbekannt" }}</span>
        </span>
        <span class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm">
          <span class="font-semibold text-muted">Normalisierte Bezeichnung</span>
          <span class="font-semibold">{{ weaponItem.weapon.name }}</span>
        </span>
      </NuxtLink>
    </template>
  </div>
</template>
