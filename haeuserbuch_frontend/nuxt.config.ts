// https://nuxt.com/docs/api/configuration/nuxt-config
import Aura from '@primeuix/themes/aura';
import { definePreset } from "@primeuix/themes";
import tailwindcss from "@tailwindcss/vite";

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
  app: {
    head: {
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/wue_haeuserbuch_logo_skyline_bright.svg' },
      ],
      // Prevent the maps being blocked by the OSM tile server due to missing referrer information
      meta: [{
        name: 'referrer',
        content: 'strict-origin-when-cross-origin'
      }]
    }
  },
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
  css: ['./app/assets/css/main.css'],
  vite: {
    plugins: [tailwindcss()],
    ssr: {
      // Bundle Terradraw + MapLibre for SSR to avoid named-export interop issues.
      noExternal: ['@watergis/maplibre-gl-terradraw', 'maplibre-gl']
    }
  }
})
