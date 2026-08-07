<script setup lang="ts">
import { ref } from "vue";
import type { TableColumn } from "@nuxt/ui";
import { getPaginationRowModel } from "@tanstack/vue-table";
import type { Street } from "~/utils/types";

const street_store = useStreetStore();

const { data: streets, pending } = useAsyncData("streets", () =>
  street_store.fetchStreets(),
);

const globalFilter = ref("");
const pagination = ref({ pageIndex: 0, pageSize: 10 });
const sorting = ref<{ id: string; desc: boolean }[]>([]);

const columns: TableColumn<Street>[] = [
  {
    accessorKey: "name",
    header: "Name",
  },
  {
    accessorKey: "altNames",
    header: "Andere Namen",
  },
];

useHead(() => ({
  title: "Straßen - Verzeichnis der Straßen",
}));
</script>

<template>
  <Card>
    <template #title>
      <h1 class="text-3xl font-bold text-black">Straßen</h1>
    </template>
    <template #content>
      <div class="grid gap-6 lg:grid-cols-[18rem_minmax(0,1fr)]">
        <aside class="h-fit overflow-hidden rounded-lg border border-default bg-default">
          <div class="border-b border-default px-4 py-4">
            <h2 class="text-xl font-semibold">Filter</h2>
          </div>
          <details open class="p-4">
            <summary class="cursor-pointer list-none text-base font-semibold">
              <span class="flex items-center justify-between">
                Suchen
                <UIcon name="i-lucide-chevron-down" class="size-4" />
              </span>
            </summary>
            <UInput
              v-model="globalFilter"
              class="mt-3 w-full"
              placeholder="Nach Namen suchen"
              icon="i-lucide-search"
            />
          </details>
        </aside>

        <div class="min-w-0">
          <UTable
            v-model:global-filter="globalFilter"
            v-model:sorting="sorting"
            v-model:pagination="pagination"
            :data="streets ?? []"
            :columns="columns"
            :loading="pending"
            :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
            class="w-full"
          >
            <template #name-cell="{ row }">
              <NuxtLink :to="`/streets/${row.original.id}`" prefetch>
                {{ row.original.name }}
              </NuxtLink>
            </template>
            <template #altNames-cell="{ row }">
              <ul v-if="row.original.altNames?.length" class="list-inside list-disc">
                <li v-for="(altName, index) in row.original.altNames" :key="index">
                  {{ altName }}
                </li>
              </ul>
              <span v-else class="rounded-md bg-red-100 p-2 italic">unbekannt</span>
            </template>
          </UTable>
          <div class="flex justify-end border-t border-default pt-4">
            <UPagination
              :page="pagination.pageIndex + 1"
              :items-per-page="pagination.pageSize"
              :total="streets?.length ?? 0"
              @update:page="(nextPage) => (pagination.pageIndex = nextPage - 1)"
            />
          </div>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped></style>
