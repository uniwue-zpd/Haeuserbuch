<script setup lang="ts">
import { researchNavigation } from "~/utils/researchNavigation";

const route = useRoute();
const { user, loggedIn, clear } = useUserSession();
const mobileMenuOpen = ref(false);
const searchOpen = ref(false);
const isScrolled = ref(false);
const projectNavigation = [
  {
    label: "Das Projekt",
    description: "Ziele, Hintergründe und Aufbau des Würzburger Häuserbuchs.",
    icon: "i-lucide-landmark",
    to: "/projekt",
  },
  {
    label: "Team",
    description: "Lernen Sie das Team hinter dem Häuserbuch kennen.",
    icon: "i-lucide-users-round",
    to: "/team",
  },
];
const commandGroups = computed(() => [
  {
    id: "research",
    label: "Recherche",
    items: researchNavigation
      .filter((item) => item.to)
      .map((item) => ({
        ...item,
        onSelect: () => {
          searchOpen.value = false;
        },
      })),
  },
  {
    id: "project",
    label: "Projekt",
    items: projectNavigation.map((item) => ({
      ...item,
      onSelect: () => {
        searchOpen.value = false;
      },
    })),
  },
  {
    id: "general",
    label: "Allgemein",
    items: [
      {
        label: "Startseite",
        icon: "i-lucide-house",
        to: "/",
        onSelect: () => {
          searchOpen.value = false;
        },
      },
      {
        label: "Kontakt",
        icon: "i-lucide-mail",
        to: "/kontakt",
        onSelect: () => {
          searchOpen.value = false;
        },
      },
    ],
  },
]);

const headerUi = computed(() => ({
  root: "top-0 z-50 h-auto border-0 bg-default !backdrop-blur-none sticky",
  container: [
    "w-full min-h-20 max-w-none rounded-none bg-default px-4 shadow-none transition-colors duration-200 sm:px-6",
    isScrolled.value ? "border-b border-default" : "border-b border-transparent",
  ].join(" "),
  center: "hidden items-center gap-1 lg:flex",
  right: "gap-1 sm:gap-2",
  title: "py-1.5",
  content: "bg-default",
  header: "border-b border-transparent",
  body: "space-y-6",
  toggle: "text-highlighted hover:bg-elevated hover:text-highlighted",
}));

const researchIsActive = computed(() =>
  researchNavigation.some((item) => item.to && isActive(item.to)),
);
const projectIsActive = computed(() =>
  projectNavigation.some((item) => isActive(item.to)),
);

function isActive(to: string) {
  return route.path === to || route.path.startsWith(`${to}/`);
}

function updateHeaderState() {
  isScrolled.value = window.scrollY > 0;
}

async function logout() {
  await $fetch("/auth/logout", { method: "POST" });
  await clear();
}

onMounted(() => {
  updateHeaderState();
  window.addEventListener("scroll", updateHeaderState, { passive: true });
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", updateHeaderState);
});

defineShortcuts({
  meta_k: () => {
    searchOpen.value = true;
  },
});
</script>

<template>
  <UHeader
    v-model:open="mobileMenuOpen"
    title="Würzburger Häuserprojekt"
    to="/"
    mode="slideover"
    :menu="{ title: 'Navigation', description: 'Seitennavigation' }"
    :ui="headerUi"
  >
    <template #title>
      <img
        src="/wue_haeuserbuch_logo_skyline_bright.svg"
        alt="Würzburger Häuserprojekt"
        :class="[
          'h-10 w-auto brightness-0 sm:h-12',
          'dark:hidden',
        ]"
      />
      <img
        src="/wue_haeuserbuch_logo_skyline_white.svg"
        alt=""
        aria-hidden="true"
        :class="[
          'h-10 w-auto sm:h-12',
          'hidden dark:block',
        ]"
      />
    </template>

    <UPopover
      mode="click"
      :content="{
        align: 'start',
        side: 'bottom',
        sideOffset: 14,
        collisionPadding: 16,
      }"
      :ui="{
        content:
          'w-full max-w-7xl overflow-hidden rounded-3xl border border-default bg-default p-0 shadow-2xl ring-0',
      }"
    >
      <template #default="{ open }">
        <UButton
          label="Recherche"
          color="neutral"
          variant="ghost"
          :trailing-icon="
            open ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'
          "
          :class="[
            'rounded-xl px-4 text-sm font-semibold',
            'text-highlighted hover:bg-elevated hover:text-highlighted',
            researchIsActive && 'bg-elevated',
          ]"
          aria-label="Recherchebereiche öffnen"
        />
      </template>

      <template #content="{ close }">
        <div class="p-5 sm:p-7">
          <div class="mb-5 flex items-end justify-between gap-4">
            <div>
              <p
                class="text-xs font-semibold uppercase tracking-widest text-muted"
              >
                Forschungsdaten
              </p>
              <h2 class="mt-1 font-sans text-xl font-bold text-highlighted">
                Im Häuserbuch recherchieren
              </h2>
            </div>
          </div>

          <div class="grid gap-3 md:grid-cols-2 lg:grid-cols-3">
            <NuxtLink
              v-for="item in researchNavigation.filter((entry) => entry.to)"
              :key="item.label"
              :to="item.to"
              class="group flex min-h-32 flex-col justify-between rounded-2xl border border-default bg-elevated/60 p-5 transition duration-200 hover:-translate-y-0.5 hover:border-primary/40 hover:bg-accented focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
              @click="close"
            >
              <UIcon
                :name="item.icon"
                class="size-5 text-muted transition-colors group-hover:text-primary"
              />
              <div class="mt-7">
                <div class="flex items-center justify-between gap-3">
                  <h3 class="font-sans font-semibold text-highlighted">
                    {{ item.label }}
                  </h3>
                  <UIcon
                    name="i-lucide-arrow-up-right"
                    class="size-4 text-dimmed transition group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary"
                  />
                </div>
                <p class="mt-1 text-sm leading-5 text-muted">
                  {{ item.description }}
                </p>
              </div>
            </NuxtLink>

            <div
              v-for="item in researchNavigation.filter((entry) => !entry.to)"
              :key="item.label"
              class="flex min-h-32 flex-col justify-between rounded-2xl border border-dashed border-default bg-muted/40 p-5"
              aria-disabled="true"
            >
              <div class="flex items-start justify-between gap-3">
                <UIcon :name="item.icon" class="size-5 text-dimmed" />
                <span
                  class="rounded-full bg-accented px-2.5 py-1 text-xs font-semibold uppercase tracking-wider text-muted"
                >
                  Demnächst
                </span>
              </div>
              <div class="mt-7">
                <h3 class="font-sans font-semibold text-muted">
                  {{ item.label }}
                </h3>
                <p class="mt-1 text-sm leading-5 text-dimmed">
                  {{ item.description }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </template>
    </UPopover>

    <UPopover
      mode="click"
      :content="{
        align: 'start',
        side: 'bottom',
        sideOffset: 14,
        collisionPadding: 16,
      }"
      :ui="{
        content:
          'w-full max-w-7xl overflow-hidden rounded-3xl border border-default bg-default p-0 shadow-2xl ring-0',
      }"
    >
      <template #default="{ open }">
        <UButton
          label="Projektinformationen"
          color="neutral"
          variant="ghost"
          :trailing-icon="
            open ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'
          "
          :class="[
            'rounded-xl px-4 text-sm font-semibold',
            'text-highlighted hover:bg-elevated hover:text-highlighted',
            projectIsActive && 'bg-elevated',
          ]"
          aria-label="Projektbereiche öffnen"
        />
      </template>

      <template #content="{ close }">
        <div class="p-5 sm:p-7">
          <p
            class="text-xs font-semibold uppercase tracking-widest text-muted"
          >
            Würzburger Häuserbuch
          </p>
          <h2 class="mt-1 font-sans text-xl font-bold text-highlighted">
            Projektinformationen
          </h2>

          <div class="mt-5 grid gap-3 sm:grid-cols-3">
            <NuxtLink
              v-for="item in projectNavigation"
              :key="item.label"
              :to="item.to"
              class="group flex min-h-32 flex-col justify-between rounded-2xl border border-default bg-elevated/60 p-5 transition duration-200 hover:-translate-y-0.5 hover:border-primary/40 hover:bg-accented focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
              @click="close"
            >
              <UIcon
                :name="item.icon"
                class="size-5 text-muted transition-colors group-hover:text-primary"
              />
              <div class="mt-7">
                <div class="flex items-center justify-between gap-3">
                  <h3 class="font-sans font-semibold text-highlighted">
                    {{ item.label }}
                  </h3>
                  <UIcon
                    name="i-lucide-arrow-up-right"
                    class="size-4 text-dimmed transition group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary"
                  />
                </div>
                <p class="mt-1 text-sm leading-5 text-muted">
                  {{ item.description }}
                </p>
              </div>
            </NuxtLink>
          </div>
        </div>
      </template>
    </UPopover>
    <UButton
      to="/kontakt"
      label="Kontakt"
      color="neutral"
      variant="ghost"
      :class="[
        'rounded-xl px-4 text-sm font-semibold',
        'text-highlighted hover:bg-elevated hover:text-highlighted',
        isActive('/kontakt') && 'bg-elevated',
      ]"
    />

    <template #right>
      <UModal
        v-model:open="searchOpen"
        :ui="{
          content:
            'w-full max-w-xl overflow-hidden rounded-2xl p-0',
        }"
      >
        <UButton
          icon="i-lucide-search"
          size="lg"
          color="neutral"
          variant="ghost"
          aria-label="Suche öffnen"
          :class="[
            'rounded-xl',
            'text-highlighted hover:bg-elevated hover:text-highlighted',
          ]"
        />

        <template #content>
          <UCommandPalette
            :groups="commandGroups"
            placeholder="Suche nach einer Seite..."
          />
        </template>
      </UModal>

      <UColorModeButton
        size="lg"
        color="neutral"
        variant="ghost"
        :class="[
          'rounded-xl',
          'text-highlighted hover:bg-elevated hover:text-highlighted',
        ]"
      />

      <UPopover
        mode="click"
        :content="{
          align: 'end',
          side: 'bottom',
          sideOffset: 10,
          collisionPadding: 16,
        }"
      >
        <UButton
          :label="loggedIn ? user?.name || 'Konto' : 'Anmelden'"
          icon="i-lucide-user-round"
          size="lg"
          color="neutral"
          variant="ghost"
          aria-label="Benutzerkonto öffnen"
          :class="[
            'max-w-48 rounded-xl',
            'text-highlighted hover:bg-elevated hover:text-highlighted',
          ]"
        />

        <template #content>
          <div class="w-72 space-y-4 p-4">
            <template v-if="loggedIn">
              <div class="flex items-center gap-3">
                <UAvatar :alt="user?.name || 'Häuserbuch-Mitglied'" />
                <div class="min-w-0">
                  <p class="truncate font-semibold text-highlighted">
                    {{ user?.name || "Häuserbuch-Mitglied" }}
                  </p>
                  <p class="text-sm text-muted">Angemeldet</p>
                </div>
              </div>
              <UButton
                label="Abmelden"
                icon="i-lucide-log-out"
                color="error"
                variant="soft"
                block
                @click="logout"
              />
            </template>
            <template v-else>
              <div>
                <p class="font-semibold text-highlighted">Willkommen</p>
                <p class="mt-1 text-sm leading-5 text-muted">
                  Melden Sie sich an, um erweiterten Zugriff auf das Projekt zu
                  erhalten.
                </p>
              </div>
              <a href="/auth/login">
                <UButton
                    label="Anmelden"
                    icon="i-lucide-log-in"
                    color="primary"
                    block
                />
              </a>
            </template>
          </div>
        </template>
      </UPopover>
    </template>

    <template #body>
      <nav
        aria-label="Mobile Navigation"
        class="space-y-10 px-1 pb-4 sm:px-2"
      >
        <section aria-labelledby="mobile-research-heading">
          <h2
            id="mobile-research-heading"
            class="mb-4 text-xs font-semibold uppercase tracking-widest text-muted"
          >
            Recherche
          </h2>
          <div class="grid gap-1">
            <NuxtLink
              v-for="item in researchNavigation.filter((entry) => entry.to)"
              :key="item.label"
              :to="item.to"
              class="block rounded-lg py-1 text-2xl font-medium tracking-tight text-highlighted transition-colors hover:text-primary focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-primary sm:text-3xl"
              @click="mobileMenuOpen = false"
            >
              {{ item.label }}
            </NuxtLink>
            <span
              v-for="item in researchNavigation.filter((entry) => !entry.to)"
              :key="item.label"
              class="block py-1 text-2xl font-medium tracking-tight text-dimmed sm:text-3xl"
              aria-disabled="true"
            >
              {{ item.label }}
            </span>
          </div>
        </section>

        <section aria-labelledby="mobile-project-heading">
          <h2
            id="mobile-project-heading"
            class="mb-4 text-xs font-semibold uppercase tracking-widest text-muted"
          >
            Projekt
          </h2>
          <div class="grid gap-1">
            <NuxtLink
              v-for="item in projectNavigation"
              :key="item.label"
              :to="item.to"
              class="block rounded-lg py-1 text-2xl font-medium tracking-tight text-highlighted transition-colors hover:text-primary focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-primary sm:text-3xl"
              @click="mobileMenuOpen = false"
            >
              {{ item.label }}
            </NuxtLink>
            <NuxtLink
              to="/kontakt"
              class="block rounded-lg py-1 text-2xl font-medium tracking-tight text-highlighted transition-colors hover:text-primary focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-primary sm:text-3xl"
              @click="mobileMenuOpen = false"
            >
              Kontakt
            </NuxtLink>
          </div>
        </section>
      </nav>
    </template>
  </UHeader>
</template>
