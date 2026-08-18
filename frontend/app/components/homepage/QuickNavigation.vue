<script setup lang="ts">
import { researchNavigation } from "~/utils/researchNavigation";
</script>

<template>
  <div class="grid border-l border-t border-default sm:grid-cols-2 lg:grid-cols-3">
    <template v-for="item in researchNavigation" :key="item.label">
      <div
          v-if="item.to"
          class="group flex min-h-56 flex-col border-b border-r border-default bg-default p-6 transition-colors duration-200 hover:bg-elevated sm:p-7"
      >
        <NuxtLink
            :to="item.to"
            prefetch
            class="flex flex-1 flex-col justify-between focus-visible:z-10 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
        >
          <div class="flex items-start justify-between gap-4">
            <UIcon
                :name="item.icon"
                class="size-6 text-muted transition-colors group-hover:text-highlighted"
            />
            <UIcon
                name="i-lucide-arrow-up-right"
                class="size-5 text-dimmed transition-transform duration-200 group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-highlighted"
            />
          </div>
          <div class="mt-12">
            <h3 class="font-sans text-xl font-semibold tracking-tight text-highlighted sm:text-2xl">
              {{ item.label }}
            </h3>
            <p class="mt-2 max-w-md text-sm leading-6">
              {{ item.description }}
            </p>
          </div>
        </NuxtLink>
        <div
            v-if="item.descriptionTo"
            class="mt-4"
        >
          <NuxtLink
              :to="item.descriptionTo"
              class="inline-flex items-center gap-1 text-sm font-medium text-muted underline decoration-default underline-offset-3 transition-colors hover:text-highlighted"
          >
            {{ item.descriptionToLabel }}
            <UIcon
                name="i-lucide-arrow-up-right"
                class="size-3.5 shrink-0"
            />
          </NuxtLink>
        </div>
      </div>
      <div
          v-else
          class="flex min-h-56 flex-col justify-between border-b border-r border-default bg-muted/30 p-6 sm:p-7"
          aria-disabled="true"
      >
        <div class="flex items-start justify-between gap-3">
          <UIcon :name="item.icon" class="size-6 text-dimmed" />
          <span class="text-xs font-medium text-dimmed">
            In Vorbereitung
          </span>
        </div>
        <div class="mt-12">
          <h3 class="font-sans text-xl font-semibold tracking-tight text-muted sm:text-2xl">
            {{ item.label }}
          </h3>
          <p class="mt-2 max-w-xs text-sm leading-6 text-dimmed">
            {{ item.description }}
          </p>
        </div>
      </div>
    </template>
  </div>
</template>
