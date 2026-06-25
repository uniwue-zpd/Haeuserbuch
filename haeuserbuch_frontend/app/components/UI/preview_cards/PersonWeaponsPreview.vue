<script setup lang="ts">
const props = defineProps<{
  weapons: WeaponryDTO[];
}>();
</script>

<template>
  <div class="inline-block">
    <div class="rounded-md shadow-md p-3 bg-gray-200 border border-gray-300">
      <div class="flex flex-wrap gap-2">
        <div v-for="weaponItem in props.weapons">
          <div v-if="weaponItem.weapon">
            <NuxtLink
                :to="`/weapons/${weaponItem.weapon?.id}`"
                class="p-2 flex flex-col gap-1 text-sm rounded-md shadow-sm bg-gray-300 hover:shadow-md"
            >
              <span class="italic">{{ weaponItem.originalText }}</span>
              <hr class="border-black"/>
              <span class="font-bold">{{ weaponItem.weapon.name }}</span>
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
