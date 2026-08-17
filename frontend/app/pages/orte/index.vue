<script setup lang="ts">
import { h, onMounted, resolveComponent, watch } from "vue";
import maplibregl, {
  LngLat,
  type RasterLayerSpecification,
  type RasterSourceSpecification,
} from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import type { TableColumn } from "@nuxt/ui";
import { getPaginationRowModel } from "@tanstack/vue-table";
import { initMap } from "~/service/map_init";
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";
import ErrorComponent from "~/components/UI/FetchError.vue";
import type {
  Feature,
  FeatureCollection,
  PlaceProperties,
  Point,
} from "~/utils/GeoJsonTypes";

const placeStore = usePlaceStore();
const tile_store = useTileStore();
const UButton = resolveComponent("UButton");
const {
  data: places,
  pending: loadingData,
  error: hasError,
} = await useAsyncData("places", () => placeStore.getPlaces());

const globalFilter = ref("");

type PlaceTableRow = {
  id?: number;
  realName: string | null;
  altNames: string[];
  georeferenced: boolean;
};

const places_table_data = computed(() =>
  (places.value?.features ?? []).map<PlaceTableRow>((p) => {
    const props = p.properties as PlaceProperties;
    return {
      id: p.id,
      realName: props.realName,
      altNames: props.altNames ?? [],
      georeferenced: Boolean(p.geometry),
    };
  }),
);

const filteredPlaces = computed<FeatureCollection>(() => {
  const query = globalFilter.value.toLowerCase();
  const features = places.value?.features ?? [];

  if (!query) {
    return { type: "FeatureCollection", features };
  }

  return {
    type: "FeatureCollection",
    features: features.filter((place: Feature) => {
      const properties = place.properties as PlaceProperties | null;
      const names = [properties?.realName, ...(properties?.altNames ?? [])];

      return names.some((name) =>
        name?.toLowerCase().includes(query),
      );
    }),
  };
});

const placeColumns: TableColumn<PlaceTableRow>[] = [
  {
    id: "actions",
    header: "",
    enableSorting: false,
    enableGlobalFilter: false,
    meta: { class: { th: "w-12", td: "w-12" } },
    cell: ({ row }) =>
      h(UButton, {
        to: `/orte/${row.original.id}`,
        color: "neutral",
        variant: "ghost",
        icon: "i-lucide-arrow-up-right",
        "aria-label": `${row.original.realName ?? "Ort"} öffnen`,
        title: "Ort öffnen",
      }),
  },
  {
    accessorKey: "realName",
    header: "Name",
    enableSorting: true,
  },
  {
    accessorKey: "altNames",
    header: "Namensvarianten",
    enableSorting: true,
  },
  {
    accessorKey: "georeferenced",
    header: "Georeferenziert",
    enableSorting: true,
    enableGlobalFilter: false,
  },
];

const table = useTemplateRef("table");
const sorting = ref<{ id: string; desc: boolean }[]>([]);
const pagination = ref({
  pageIndex: 0,
  pageSize: 10,
});
const sources = computed(() => tile_store.baseSources);
const layers = computed(() => tile_store.baseLayers);
let map: maplibregl.Map | null = null;

function updateMapPlaces() {
  const source = map?.getSource("places") as maplibregl.GeoJSONSource | undefined;
  source?.setData(
    filteredPlaces.value as Parameters<maplibregl.GeoJSONSource["setData"]>[0],
  );
}

watch(filteredPlaces, updateMapPlaces);

useHead(() => ({
  title: "Orte - Orteverzeichnis",
}));

onMounted(async () => {
  if (hasError.value) return;
  map = initMap(
    "map",
    DEFAULT_MAP_CENTER,
    5,
    sources.value as Record<string, RasterSourceSpecification>,
    // @ts-ignore
    layers.value as RasterLayerSpecification[],
  );
  map.on("load", () => {
    if (!places.value) return;
    map!.addSource("places", {
      type: "geojson",
      // @ts-ignore
      data: filteredPlaces.value as FeatureCollection,
      cluster: true,
      clusterRadius: 50,
    });
    map!.addLayer({
      id: "places_clusters",
      type: "circle",
      source: "places",
      filter: ["has", "point_count"],

      paint: {
        "circle-color": [
          "step",
          ["get", "point_count"],
          "#8FB3C9",
          25,
          "#7398B2",
          100,
          "#5C7F9B",
        ],
        "circle-radius": ["step", ["get", "point_count"], 16, 25, 22, 100, 30],
        "circle-stroke-color": "#F8F4EA",
        "circle-stroke-width": 2,
        "circle-opacity": 0.85,
      },
    });
    map!.addLayer({
      id: "places_clusters_count",
      type: "symbol",
      source: "places",
      filter: ["has", "point_count"],
      layout: {
        "text-field": "{point_count_abbreviated}",
        "text-size": 12,
        "text-font": ["Noto Sans Regular"],
      },
    });
    map!.addLayer({
      id: "places",
      type: "circle",
      source: "places",
      filter: ["!", ["has", "point_count"]],
      paint: {
        "circle-radius": 8,
        "circle-color": "#5C7F9B",
        "circle-opacity": 0.8,
        "circle-stroke-color": "#F8F4EA",
        "circle-stroke-width": 2,
      },
    });
    updateMapPlaces();
  });
  map.on("click", "places_clusters", async (e) => {
    const feature = e.features?.[0];
    if (!feature) return;
    const clusterId = feature.properties?.cluster_id;
    if (typeof clusterId !== "number") return;
    const source = map!.getSource("places") as maplibregl.GeoJSONSource;
    const zoom = await source.getClusterExpansionZoom(clusterId);
    if (feature.geometry.type !== "Point") return;
    const coords = feature.geometry.coordinates as [number, number];
    map!.easeTo({
      center: coords,
      zoom,
    });
  });
  map.on("click", "places", (e) => {
    const feature = e.features?.[0];
    if (!feature) return;
    const geometry = feature.geometry as Point;
    const coordinates = new LngLat(
      geometry.coordinates[0],
      geometry.coordinates[1],
    );
    const popUpLink = document.createElement("div");
    popUpLink.innerHTML = feature.properties?.realName ?? "Unbekannter Ort";
    popUpLink.setAttribute("class", "cursor-pointer font-bold ");
    const id = feature.id;
    popUpLink.addEventListener("click", () => {
      navigateTo(`/orte/${id}`);
    });
    new maplibregl.Popup()
      .setLngLat(coordinates)
      .setDOMContent(popUpLink)
      .addTo(map!);
    map!.flyTo({
      center: coordinates,
      zoom: 14,
    });
  });
  map.on("mouseenter", ["places", "places_clusters"], () => {
    map!.getCanvas().style.cursor = "pointer";
  });
  map.on("mouseleave", ["places", "places_clusters"], () => {
    map!.getCanvas().style.cursor = "";
  });
});

onBeforeUnmount(() => {
  if (map) {
    map.remove();
    map = null;
  }
});
</script>

<template>
  <UniversalSkeleton v-if="loadingData" />
  <ErrorComponent :error="hasError" v-else-if="hasError" />
  <div v-else class="flex flex-col gap-2">
    <div class="flex items-center justify-between gap-3">
      <h1 class="text-3xl font-bold">Verzeichnis und Visualisierung der Herkunftsorte der von 1405 bis 1613 aufgenommenen Würzburger Bürger</h1>
      <UModal
        title="Geokodierung der Herkunftsorte"
        scrollable
        :ui="{ content: 'max-w-7xl', body: 'p-5 sm:p-8' }"
      >
        <UButton
          color="neutral"
          variant="outline"
          icon="i-lucide-book-open"
          label="Beschreibung"
          aria-label="Beschreibung zur Geokodierung der Herkunftsorte"
          title="Beschreibung zur Geokodierung der Herkunftsorte"
        />

        <template #body>
          <div class="space-y-7 text-base leading-7 text-highlighted sm:text-lg sm:leading-8">
            <section>
              <h2 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                Methodik
              </h2>
              <div class="space-y-5 text-justify">
                <p>
                  Die dargestellten Herkunftsorte beruhen auf einer projektintern erstellten Ortsliste, die aus den in den Würzburger Bürgermatrikeln der Jahre 1405 bis 1613 überlieferten Ortsangaben abgeleitet wurde (Stadtarchiv Würzburg, RB 207–209, 211–213, 215). Ausgangspunkt der Geokodierung war eine Ortsliste, in der die historischen Ortsbezeichnungen in eine heutige normierte Schreibweise überführt wurden. Diese normierten Ortsnamen bildeten die Grundlage für den automatisierten Abgleich mit verschiedenen Geokodierungsdiensten und Referenzdatensätzen. Die historischen Ortsbezeichnungen bleiben dabei als Quelleninformation erhalten und werden durch die normierten Ortsnamen nicht ersetzt.
                </p>
                <p>
                  Da historische Ortsbezeichnungen häufig unterschiedliche Schreibweisen, Verwaltungszusätze oder mehrdeutige Ortsnamen aufweisen, erfolgte die Geokodierung in einem mehrstufigen Verfahren. Zunächst wurden die erfassten Ortsnamen für die automatisierte Verarbeitung aufbereitet. Anschließend wurden verschiedene Geokodierungsverfahren und Referenzdatensätze – darunter ArcGIS, OpenStreetMap/Nominatim und GeoNames – in RStudio sowie im Geoinformationssystem (GIS) getestet und hinsichtlich ihrer Trefferqualität miteinander verglichen. Dabei zeigte sich, dass die Kombination aus Ortsname, Ortszusatz (Landkreis) und Land die zuverlässigsten Ergebnisse liefert. Die automatisiert ermittelten Koordinaten wurden anschließend durch Plausibilitätsprüfungen, Distanzvergleiche zwischen den Geokodierungsdiensten sowie manuelle Nachrecherchen überprüft und bei Bedarf korrigiert.
                </p>
                <p>
                  Soweit mehrere Orte mit identischem oder ähnlichem Namen existieren, erfolgte die Zuordnung anhand der in den Quellen überlieferten Zusatzinformationen sowie weiterer historischer und geographischer Kriterien. Nicht eindeutig identifizierbare Ortsangaben werden fortlaufend überprüft und gegebenenfalls neu bewertet.
                </p>
              </div>
            </section>

            <section>
              <h2 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                Bearbeitungsstand und Statistik
              </h2>
              <div class="space-y-5 text-justify">
                <p>
                  Die Ortsliste befindet sich weiterhin im Aufbau. Grundlage der bisherigen Bearbeitung sind 2.216 Herkunftsortdatensätze, die aus den Würzburger Bürgermatrikeln erfasst wurden. Nach dem aktuellen Bearbeitungsstand konnten 2.089 Ortsangaben (94,3 %) automatisiert georeferenziert werden; 127 Ortsangaben (5,7 %) bedürfen weiterhin einer manuellen Überprüfung.
                </p>
                <p>
                  Ein Teil der bislang nicht automatisch georeferenzierbaren Ortsangaben betrifft historische Siedlungen, Wüstungen, Klöster, Burgen, Herrschaften oder Landschaftsbezeichnungen, die in modernen Gazetteers nicht oder nur eingeschränkt nachgewiesen sind. Hinzu kommen mehrdeutige Ortsnamen sowie historische Schreibvarianten, deren eindeutige Identifizierung eine quellenkritische Einzelfallprüfung erfordert.
                </p>
                <p>
                  Mit der fortschreitenden Erschließung weiterer Quellenbestände werden darüber hinaus sowohl neue historische Schreibweisen als auch bislang nicht erfasste Herkunftsorte in die Ortsliste aufgenommen. Dadurch verändern sich die Gesamtzahl der erfassten Ortsnamen, die statistischen Auswertungen sowie die kartographischen Darstellungen fortlaufend. Sämtliche Angaben stellen daher den aktuellen Bearbeitungsstand des Projekts dar. Die Georeferenzierung wird kontinuierlich überprüft und entsprechend neuer Erkenntnisse fortgeschrieben.
                </p>
              </div>
            </section>

            <section>
              <h2 class="mb-3 text-center text-xl font-semibold text-highlighted sm:text-2xl">
                Hinweise zur Kartendarstellung
              </h2>
              <div class="space-y-5 text-justify">
                <p>
                  Die dargestellten Punkte repräsentieren die im Rahmen des Geokodierungsprozesses ermittelten Koordinaten. Diese beziehen sich – abhängig vom jeweiligen Referenzdatensatz – auf den Mittelpunkt einer heutigen Siedlung, Gemarkung oder Verwaltungseinheit und stimmen daher nicht zwangsläufig mit der Lage des historischen Ortskerns überein. Ebenso kann die heutige administrative Zuordnung eines Ortes von den historischen Herrschafts- oder Verwaltungsverhältnissen zum Zeitpunkt der Eintragung in die Bürgermatrikel abweichen.
                </p>
                <p>
                  Die Karten dienen in erster Linie der räumlichen Veranschaulichung der Herkunftsorte im überregionalen Maßstab und nicht der exakten Lokalisierung historischer Siedlungen. Eine Feinjustierung einzelner Ortslagen bleibt aufgrund der Datenmenge einer späteren Projektphase vorbehalten. Jeder kartierte Herkunftsort ist mit den zugehörigen Datensätzen der Bürgermatrikel verknüpft und ermöglicht so den direkten Zugang zu den überlieferten Quelleninformationen.
                </p>
              </div>
            </section>

            <p class="border-t border-default pt-4 text-sm text-dimmed">
              Bearbeitung: JM, Stand 31.07.2026
            </p>
          </div>
        </template>
      </UModal>
    </div>
    <div id="map" class="h-125 w-full rounded-md" />
    <div class="grid gap-4 pb-4 lg:grid-cols-[minmax(16rem,18rem)_minmax(0,1fr)]">
      <aside class="overview-filter-panel h-fit">
        <div class="border-b border-muted p-5">
          <h2 class="text-xl font-semibold text-highlighted">Filter</h2>
        </div>
        <details open class="border-b border-default p-4 last:border-b-0">
          <summary class="cursor-pointer list-none text-base font-semibold">
            <span class="flex items-center justify-between">
              Suchen
              <UIcon name="i-lucide-chevron-down" class="size-4" />
            </span>
          </summary>
          <UInput
            v-model="globalFilter"
            class="mt-3 w-full"
            placeholder="Suchen"
            icon="i-lucide-search"
          />
        </details>
      </aside>
      <section class="overview-table-panel min-w-0 p-5">
        <UTable
          ref="table"
          v-model:global-filter="globalFilter"
          v-model:sorting="sorting"
          v-model:pagination="pagination"
          :data="places_table_data"
          :columns="placeColumns"
          :global-filter-options="{
            getColumnCanGlobalFilter: (column) =>
              column.id === 'realName' || column.id === 'altNames',
          }"
          :pagination-options="{
            getPaginationRowModel: getPaginationRowModel(),
          }"
          :ui="{ td: 'text-default' }"
          class="w-full"
        >
        <template #realName-header="{ column }">
          <UButton
            color="neutral"
            variant="ghost"
            label="Name"
            :icon="
              column.getIsSorted() === 'asc'
                ? 'i-lucide-arrow-up-narrow-wide'
                : column.getIsSorted() === 'desc'
                  ? 'i-lucide-arrow-down-wide-narrow'
                  : 'i-lucide-arrow-up-down'
            "
            class="-mx-2.5"
            @click="column.toggleSorting(column.getIsSorted() === 'asc')"
          />
        </template>
        <template #realName-cell="{ row }">
          <NuxtLink
            :to="`/orte/${row.original.id}`"
            prefetch
          >
            {{ row.original.realName }}
          </NuxtLink>
        </template>
        <template #altNames-header="{ column }">
          <UButton
            color="neutral"
            variant="ghost"
            label="Namensvarianten"
            :icon="
              column.getIsSorted() === 'asc'
                ? 'i-lucide-arrow-up-narrow-wide'
                : column.getIsSorted() === 'desc'
                  ? 'i-lucide-arrow-down-wide-narrow'
                  : 'i-lucide-arrow-up-down'
            "
            class="-mx-2.5"
            @click="column.toggleSorting(column.getIsSorted() === 'asc')"
          />
        </template>
        <template #altNames-cell="{ row }">
          <div v-if="row.original.altNames.length > 0">
            <ul class="list-disc list-inside">
              <li
                v-for="(name, index) in row.original.altNames"
                :key="index"
              >
                {{ name }}
              </li>
            </ul>
          </div>
          <span v-else class="italic p-2 bg-red-100 rounded-md">unbekannt</span>
        </template>
        <template #georeferenced-header="{ column }">
          <UButton
            color="neutral"
            variant="ghost"
            label="Georeferenziert"
            :icon="
              column.getIsSorted() === 'asc'
                ? 'i-lucide-arrow-up-narrow-wide'
                : column.getIsSorted() === 'desc'
                  ? 'i-lucide-arrow-down-wide-narrow'
                  : 'i-lucide-arrow-up-down'
            "
            class="-mx-2.5"
            @click="column.toggleSorting(column.getIsSorted() === 'asc')"
          />
        </template>
        <template #georeferenced-cell="{ row }">
          <UIcon
            :name="row.original.georeferenced ? 'i-lucide-check' : 'i-lucide-x'"
            :class="row.original.georeferenced ? 'text-green-500' : 'text-red-500'"
            :aria-label="row.original.georeferenced ? 'Ja' : 'Nein'"
          />
        </template>
        </UTable>
        <div class="flex justify-end border-t border-default pt-4">
          <UPagination
            :page="(table?.tableApi?.getState().pagination.pageIndex ?? 0) + 1"
            :items-per-page="table?.tableApi?.getState().pagination.pageSize ?? 10"
            :total="table?.tableApi?.getFilteredRowModel().rows.length ?? 0"
            @update:page="(page) => table?.tableApi?.setPageIndex(page - 1)"
          />
        </div>
      </section>
    </div>
  </div>
</template>
