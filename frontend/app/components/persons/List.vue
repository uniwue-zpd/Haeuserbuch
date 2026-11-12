<script setup lang="ts">
import type { PersonPreviewDTO } from "~/utils/types";

/**
 * Titled, scrollable list of links to persons. The parent decides whether to render it (e.g. `v-if="persons.length > 0"`).
 * Content passed to the default slot (e.g. a loading or error state) replaces the list.
 */
defineProps<{
  title: string;
  persons: PersonPreviewDTO[];
}>();
</script>

<template>
  <div class="flex min-w-0 flex-col gap-2">
    <div class="flex items-center gap-2">
      <p class="text-sm font-semibold text-muted">{{ title }}</p>
      <UBadge
        v-if="!$slots.default"
        :label="String(persons.length)"
        color="neutral"
        variant="subtle"
        size="sm"
      />
    </div>
    <slot>
      <ul
        class="max-h-80 divide-y divide-muted overflow-y-auto rounded-md border border-muted"
      >
        <li
          v-for="person in persons"
          :key="person.id ?? person.fullName ?? 'person'"
        >
          <NuxtLink
            :to="`/personen/${person.id}`"
            class="group flex items-center gap-2 px-3 py-2 text-sm text-highlighted transition-colors hover:bg-elevated hover:text-primary focus-visible:bg-elevated focus-visible:text-primary focus-visible:outline-none"
          >
            <UIcon name="i-lucide-arrow-up-right" class="size-4 shrink-0 text-muted group-hover:text-primary" />
            <span class="truncate">{{ person.fullName || `Person mit ID ${person.id}` }}</span>
          </NuxtLink>
        </li>
      </ul>
    </slot>
  </div>
</template>
