<script setup lang="ts">
import {title_shortener} from "~/utils/helpers";

const props = defineProps<{
  source: SourceDTO;
}>();
</script>

<template>
  <div>
    <NuxtLink
        :to="`/sources/${ props.source.id }`"
        class="rounded-lg shadow-sm hover:shadow-md p-1.5 font-medium border-2 border-gray-300"
    >
      {{ props.source.title ? title_shortener(props.source.title) : 'Ohne Titel' }}
    </NuxtLink>
  </div>
</template>

<style scoped>

</style>
