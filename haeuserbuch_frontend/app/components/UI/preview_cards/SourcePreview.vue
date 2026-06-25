<script setup lang="ts">
import {title_shortener} from "~/utils/helpers";

const props = defineProps<{
  source: SourceDTO;
}>();
</script>

<template>
  <div class="flex flex-row items-center gap-2 flex-wrap">
    <NuxtLink
        :to="`/sources/${ props.source.id }`"
        class="rounded-md shadow-sm hover:shadow-md p-1.5 bg-gray-200 font-medium border border-gray-300"
    >
      {{ props.source.title ? title_shortener(props.source.title) : 'Ohne Titel' }}
    </NuxtLink>
  </div>
</template>

<style scoped>

</style>
