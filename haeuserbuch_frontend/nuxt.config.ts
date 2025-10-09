// https://nuxt.com/docs/api/configuration/nuxt-config
import Aura from '@primeuix/themes/aura';

export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  runtimeConfig: {
    apiBaseUrl: 'http://localhost:8080',
  },
  modules: [
      '@primevue/nuxt-module',
      '@formkit/nuxt',
      '@pinia/nuxt'
  ],
  primevue: {
    options: {
      theme: {
        preset: Aura
      }
    },
    autoImport: true
  },
  formkit: {
    configFile: './formkit.config.ts',
    autoImport: true
  },
  css: ['~/assets/css/main.css'],
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
})
