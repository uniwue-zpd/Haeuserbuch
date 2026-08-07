<template>
  <div class="min-h-dvh">
    <div class="relative z-10 flex min-h-dvh flex-col overflow-clip bg-default">
      <AppHeader />
      <main class="mx-auto w-full grow max-w-[120rem] px-4 py-8 sm:px-6 lg:px-8 lg:py-10">
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>
