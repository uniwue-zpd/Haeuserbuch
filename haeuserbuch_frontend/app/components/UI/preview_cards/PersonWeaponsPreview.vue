<script setup lang="ts">
const props = defineProps<{
  weapons: WeaponryDTO[];
}>();
</script>

<template>
  <div class="inline-block">
    <div class="flex flex-wrap gap-2">
      <div v-for="weaponItem in props.weapons">
        <div v-if="weaponItem.weapon">
          <NuxtLink
              :to="`/weapons/${weaponItem.weapon?.id}`"
              class="flex flex-col gap-2 p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
          >
            <span class="italic">{{ weaponItem.originalText }}</span>
            <hr class="border-black"/>
            <span class="font-bold">{{ weaponItem.weapon.name }}</span>
          </NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
