<script setup lang="ts">
const props = withDefaults(defineProps<{ text: string; query: string; enabled?: boolean }>(), { enabled: true });

const parts = computed(() => {
  if (!props.enabled || !props.query.trim()) return [props.text];
  const escaped = props.query.trim().replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  return props.text.split(new RegExp(`(${escaped})`, "gi"));
});
</script>

<template>
  <span class="search-match-excerpt"><template v-for="(part, index) in parts" :key="index"><mark v-if="index % 2">{{ part }}</mark><template v-else>{{ part }}</template></template></span>
</template>
