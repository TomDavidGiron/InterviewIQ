# ── Build stage ──────────────────────────────────────────────────────────────
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Baked at build time — browser talks directly to port 8080 on the host
ARG REACT_APP_API_BASE_URL=http://localhost:8080
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL

RUN npm run build

# ── Serve stage ───────────────────────────────────────────────────────────────
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
