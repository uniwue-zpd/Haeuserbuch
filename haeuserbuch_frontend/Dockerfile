FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json /app

RUN npm ci && npm cache clean --force

COPY . .

RUN npm run build

FROM node:20-alpine

WORKDIR /app

COPY --from=build /app/.output /app/.output

ENTRYPOINT ["node", ".output/server/index.mjs"]
