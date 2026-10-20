<script setup lang="ts">
import { searchEntityConfig, type GlobalSearchResult } from "~/utils/globalSearch";

const props = defineProps<{ result: GlobalSearchResult; query: string }>();
const matches = computed(() => (props.result.matches ?? [])
  .filter((match) => match.field !== "TITLE" || match.value !== props.result.title));
</script>

<template>
  <span>
    {{ searchEntityConfig[result.entityType].label }}
    <template v-if="result.subtitle">
      · <SearchHighlight :text="result.subtitle" :query="query" :enabled="result.matchedFields.includes('METADATA')" />
    </template>
    <span v-if="matches.length" class="mt-1 block space-y-0.5 text-xs leading-4">
      <span v-for="match in matches" :key="`${match.field}-${match.label}`" class="block break-words">
        <span class="font-medium">{{ match.label }}: </span>
        <SearchHighlight :text="match.value" :query="query" />
      </span>
    </span>
    <span
      v-if="result.excerpt && result.matchedFields.includes('FULL_TEXT')"
      class="command-search-excerpt search-match-excerpt"
      v-html="result.excerpt"
    />
  </span>
</template>
