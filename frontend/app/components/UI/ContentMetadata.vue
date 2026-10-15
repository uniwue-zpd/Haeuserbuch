<script setup lang="ts">
const props = defineProps<{
  createdDate?: string | number | null;
  createdBy?: string | null;
  lastModifiedDate?: string | number | null;
  lastModifiedBy?: string | null;
}>();

const { loggedIn } = useUserSession();

const hasMetadata = computed(() => Boolean(props.createdDate || props.lastModifiedDate));

function formatDate(value: string | number | null | undefined) {
  if (!value) return null;
  return new Intl.DateTimeFormat("de-DE").format(new Date(value));
}
</script>

<template>
  <div
    v-if="hasMetadata"
    class="mt-auto flex flex-wrap items-start gap-x-8 gap-y-1 text-sm text-muted"
  >
    <div v-if="formatDate(createdDate)" class="flex flex-col gap-0.5">
      <span class="font-semibold">Erstellt am</span>
      <time :datetime="String(createdDate)">{{ formatDate(createdDate) }}</time>
      <span v-if="loggedIn && createdBy">von {{ createdBy }}</span>
    </div>
    <div v-if="formatDate(lastModifiedDate)" class="flex flex-col gap-0.5">
      <span class="font-semibold">Zuletzt aktualisiert am</span>
      <time :datetime="String(lastModifiedDate)">{{ formatDate(lastModifiedDate) }}</time>
      <span v-if="loggedIn && lastModifiedBy">von {{ lastModifiedBy }}</span>
    </div>
  </div>
</template>
