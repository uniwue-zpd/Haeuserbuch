<script setup lang="ts">
defineProps<{
  mainClass: string;
}>();
</script>

<template>
  <div class="min-h-dvh">
    <div class="relative z-10 flex min-h-dvh flex-col overflow-clip bg-default">
      <AppHeader />
      <main :class="mainClass">
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>
