# =====================================
# Frontend: Nginx leggero per servire statico
# =====================================

# Base Nginx image ottimizzato per static files
FROM nginx:alpine

WORKDIR /usr/share/nginx/html

# Copia configurazione server
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

# Copia file HTML e JavaScript dal build frontend
COPY ./public/ .
COPY ./src/ .

# Configurazione Nginx per gzip compression
RUN sed -i 's/gzip off;/gzip on;/' /etc/nginx/conf.d/default.conf

# Exposizione porta
EXPOSE 8080

# Comando di avvio Nginx
CMD ["nginx", "-g", "daemon off;"]
