<script setup lang="ts">

</script>

<template>
  <div class="text-3xl font-bold montserrat-headline pb-2">Das Team</div>
  <div class="flex flex-col gap-4 md:grid md:grid-cols-2 md:gap-3">
    <Card>
      <template #title>
        <div class="flex flex-row justify-between space-x-5">
          <div class="flex flex-col">
            <div class="text-2xl text-black font-bold montserrat-headline">Person 1</div>
            <div class="text-black font-medium montserrat-headline italic">Projektleiter</div>
          </div>
          <Avatar label="P1" size="xlarge" shape="circle"/>
        </div>
      </template>
      <template #content>
        <Divider/>
        <div class="flex flex-col gap-3">
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Vita</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Aufgaben</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Kontakt</div>
            <div class="flex flex-row space-x-3 roboto-plain">
              <p>E-Mail:</p>
              <a href="mailto:" class="text-blue-950">@uni-wuerzburg.de</a>
            </div>
          </div>
        </div>
      </template>
    </Card>
    <Card>
      <template #title>
        <div class="flex flex-row justify-between space-x-5">
          <div class="flex flex-col">
            <div class="text-2xl text-black font-bold montserrat-headline">Person 2</div>
            <div class="text-black font-medium montserrat-headline italic">Technische Assistenz</div>
          </div>
          <Avatar label="P2" size="xlarge" shape="circle"/>
        </div>
      </template>
      <template #content>
        <Divider/>
        <div class="flex flex-col gap-3">
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Vita</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Aufgaben</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Kontakt</div>
            <div class="flex flex-row space-x-3 roboto-plain">
              <p>E-Mail:</p>
              <a href="mailto:" class="text-blue-950">@uni-wuerzburg.de</a>
            </div>
          </div>
        </div>
      </template>
    </Card>
    <Card>
      <template #title>
        <div class="flex flex-row justify-between space-x-5">
          <div class="flex flex-col">
            <div class="text-2xl text-black font-bold montserrat-headline">Person 3</div>
            <div class="text-black font-medium montserrat-headline italic">Transkription</div>
          </div>
          <Avatar label="P3" size="xlarge" shape="circle"/>
        </div>
      </template>
      <template #content>
        <Divider/>
        <div class="flex flex-col gap-3">
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Vita</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Aufgaben</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Kontakt</div>
            <div class="flex flex-row space-x-3 roboto-plain">
              <p>E-Mail:</p>
              <a href="mailto:" class="text-blue-950">@uni-wuerzburg.de</a>
            </div>
          </div>
        </div>
      </template>
    </Card>
    <Card>
      <template #title>
        <div class="flex flex-row justify-between space-x-5">
          <div class="flex flex-col">
            <div class="text-2xl text-black font-bold montserrat-headline">Person 4</div>
            <div class="text-black font-medium montserrat-headline italic">Verarbeitung geospatialer Daten</div>
          </div>
          <Avatar label="P4" size="xlarge" shape="circle"/>
        </div>
      </template>
      <template #content>
        <Divider/>
        <div class="flex flex-col gap-3">
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Vita</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Aufgaben</div>
          </div>
          <div class="flex flex-col">
            <div class="text-black roboto-plain montserrat-headline font-bold">Kontakt</div>
            <div class="flex flex-row space-x-3 roboto-plain">
              <p>E-Mail:</p>
              <a href="mailto:" class="text-blue-950">@uni-wuerzburg.de</a>
            </div>
          </div>
        </div>
      </template>
    </Card>
  </div>
</template>

<style scoped>

</style>
