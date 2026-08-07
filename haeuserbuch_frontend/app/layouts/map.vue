<template>
  <div class="min-h-dvh">
    <div class="relative z-10 flex min-h-dvh flex-col overflow-clip bg-default">
      <AppHeader />
      <main class="w-full grow">
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>
