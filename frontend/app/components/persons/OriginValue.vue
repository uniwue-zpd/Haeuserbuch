<script setup lang="ts">
/** Original origin text, optionally followed by the certainty marker and identified places */
const props = defineProps<{
  origin: PersonOrigin | null | undefined;
  showNormalized: boolean;
}>();

const hasNormalized = computed(
  () => props.showNormalized && !!(props.origin?.certainty || props.origin?.places?.length),
);
</script>

<template>
  <div v-if="origin?.originalText || hasNormalized" class="flex flex-col">
    <span v-if="origin?.originalText">{{ origin.originalText }}</span>
    <div v-if="origin && hasNormalized" class="flex items-baseline gap-x-1 text-xs">
      <span
        v-if="origin.certainty"
        :class="`size-2 shrink-0 self-center rounded-full border border-gray-400 ${ORIGIN_CERTAINTY[origin.certainty].color}`"
        :title="`Lokalisierbarkeit: ${ORIGIN_CERTAINTY[origin.certainty].label}`"
        :aria-label="`Lokalisierbarkeit: ${ORIGIN_CERTAINTY[origin.certainty].label}`"
      />
      <span v-if="origin.places?.length" class="min-w-0">
        <template v-for="(place, index) in origin.places" :key="place.id">
          <NuxtLink
            :to="`/orte/${place.id}`"
            :title="`${place.realName}`"
            class="whitespace-nowrap text-highlighted underline decoration-muted underline-offset-4 hover:decoration-current"
          >
            {{ place.realName ? place.realName.split(',')[0] : 'Ort' }}
          </NuxtLink>
          <span v-if="index < origin.places.length - 1" class="text-muted">, </span>
        </template>
      </span>
    </div>
  </div>
  <slot v-else name="empty" />
</template>
