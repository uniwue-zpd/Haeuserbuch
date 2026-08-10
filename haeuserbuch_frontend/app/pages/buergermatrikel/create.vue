<script setup lang="ts">
import CitizenshipForm from "~/components/forms/CitizenshipForm.vue";
import PlaceForm from "~/components/forms/PlaceForm.vue";
import PersonForm from "~/components/forms/PersonForm.vue";

definePageMeta({
  middleware: 'auth',
});
</script>

<template>
  <div class="flex flex-col gap-4">
    <div class="flex flex-col gap-2">
      <h1 class="font-bold text-3xl">Neue Bürgermatrikel erstellen</h1>
      <p class="font-bold">Hinweise:</p>
      <ul class="list-disc list-inside">
        <li>
          Erstellen Sie zuerst den Herkunftsort der Person, falls noch keiner in
          der Datenbank existiert
        </li>
        <li>
          Erstellen Sie danach die Person selbst, falls diese noch nicht erfasst
          wurde.
        </li>
        <li>
          Abschließend können Sie die Bürgermatrikel der Person hinzufügen.
        </li>
      </ul>
    </div>
    <Stepper value="1">
      <StepItem value="1">
        <Step>Herkunftsorte</Step>
        <StepPanel v-slot="{ activateCallback }">
          <div class="flex flex-col gap-2">
            <PlaceForm header="Neuen Ort erstellen" action="create" />
            <div class="flex flex-row justify-center">
              <button
                @click="activateCallback('2')"
                class="flex flex-row space-x-1 text-lg items-center bg-gray-700 hover:bg-gray-800 text-white font-semibold p-1.5 rounded-md shadow-md"
              >
                <span>Weiter</span>
                <Icon name="material-symbols:arrow-forward" />
              </button>
            </div>
          </div>
        </StepPanel>
      </StepItem>
      <StepItem value="2">
        <Step>Personen</Step>
        <StepPanel v-slot="{ activateCallback }">
          <div class="flex flex-col gap-2">
            <PersonForm header="Neue Person erstellen" action="create" />
            <div class="flex flex-row space-x-5 justify-center">
              <button
                @click="activateCallback('1')"
                class="flex flex-row space-x-1 text-lg items-center bg-gray-300 hover:bg-gray-400 font-semibold p-1.5 rounded-md shadow-md"
              >
                <Icon name="material-symbols:arrow-back" />
                <span>Zurück</span>
              </button>
              <button
                @click="activateCallback('3')"
                class="flex flex-row space-x-1 text-lg items-center bg-gray-700 hover:bg-gray-800 text-white font-semibold p-1.5 rounded-md shadow-md"
              >
                <span>Weiter</span>
                <Icon name="material-symbols:arrow-forward" />
              </button>
            </div>
          </div>
        </StepPanel>
      </StepItem>
      <StepItem value="3">
        <Step>Bürgermatrikel</Step>
        <StepPanel v-slot="{ activateCallback }">
          <div class="flex flex-col gap-2">
            <CitizenshipForm
              header="Neue Bürgermatrikel hinzufügen"
              action="create"
            />
            <div class="flex flex-row space-x-5 justify-center">
              <button
                @click="activateCallback('2')"
                class="flex flex-row space-x-1 text-lg items-center bg-gray-300 hover:bg-gray-400 font-semibold p-2 rounded-md shadow-md"
              >
                <Icon
                  name="material-symbols:arrow-back"
                  class="inline-block mr-1"
                />
                <span>Zurück</span>
              </button>
            </div>
          </div>
        </StepPanel>
      </StepItem>
    </Stepper>
  </div>
</template>
