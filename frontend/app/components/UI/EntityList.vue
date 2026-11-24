<script setup lang="ts">
/**
 * Titled, scrollable list of links to persons or citizenship entries, depending on `type`.
 * The parent decides whether to render it (e.g. `v-if="entries.length > 0"`).
 * Content passed to the default slot (e.g. a loading or error state) replaces the list.
 */
const props = defineProps<{
  type: "persons" | "citizenships";
  title: string;
  entries: PersonPreviewDTO[] | CitizenshipPreviewDTO[];
}>();

const items = computed(() => {
  switch (props.type) {
    case "persons":
      return (props.entries as PersonPreviewDTO[]).map((person) => ({
        key: person.id ?? person.fullName ?? "person",
        to: `/personen/${person.id}`,
        label: person.fullName || `Person mit ID ${person.id}`,
        meta: undefined,
      }));
    case "citizenships":
      return (props.entries as CitizenshipPreviewDTO[]).map((citizenship) => ({
        key: citizenship.id,
        to: `/buergermatrikel/${citizenship.id}`,
        label: citizenship.signature || `Eintrag mit ID ${citizenship.id}`,
        meta: citizenship.refNumber ? `Nr. ${citizenship.refNumber}` : undefined,
      }));
  }
});
</script>

<template>
  <div class="flex min-w-0 flex-col gap-2">
    <p class="text-sm font-semibold text-muted">{{ title }}</p>
    <slot>
      <ul
        class="max-h-80 divide-y divide-muted overflow-y-auto rounded-md border border-muted"
      >
        <li v-for="item in items" :key="item.key">
          <NuxtLink
            :to="item.to"
            class="group flex items-center gap-2 px-3 py-2 text-sm text-highlighted transition-colors hover:bg-elevated hover:text-primary focus-visible:bg-elevated focus-visible:text-primary focus-visible:outline-none"
          >
            <UIcon name="i-lucide-arrow-up-right" class="size-4 shrink-0 text-muted group-hover:text-primary" />
            <span class="truncate">{{ item.label }}</span>
            <span v-if="item.meta" class="ml-auto shrink-0 text-muted">{{ item.meta }}</span>
          </NuxtLink>
        </li>
      </ul>
    </slot>
  </div>
</template>
