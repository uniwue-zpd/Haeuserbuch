// https://nuxt.com/docs/api/configuration/nuxt-config
import Aura from '@primeuix/themes/aura';
import { definePreset } from "@primeuix/themes";

const HaeuserbuchPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50:  '#EFF1F2',
      100: '#E3E7E9',
      200: '#CDD4D8',
      300: '#B7BFC4',
      400: '#9EA8B0',
      500: '#6F7981',
      600: '#647076',
      700: '#596570',
      800: '#4E545F',
      900: '#42454A',
      950: '#2C3238',
    }
  }
})

export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  runtimeConfig: {
    apiBaseUrl: 'http://localhost:8080',
    tileserverApiUrl: 'http://localhost:8081'
  },
  modules: [
    '@primevue/nuxt-module',
    '@formkit/nuxt',
    '@pinia/nuxt',
    '@nuxt/icon',
  ],
  primevue: {
    options: {
      theme: {
        preset: HaeuserbuchPreset
      }
    },
    autoImport: true
  },
  formkit: {
    configFile: './formkit.config.ts',
    autoImport: true
  },
  icon: {
    clientBundle: {
      scan: true,
    }
  },
  css: ['~/assets/css/main.css'],
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
})
