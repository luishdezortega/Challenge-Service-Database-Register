# Usa la imagen oficial de PostgreSQL
FROM postgres:15

# Configuración de las variables de entorno
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=admin
ENV POSTGRES_DB=CHALLENGE_DATABASE

# Copia el script SQL de inicialización
COPY init.sql /docker-entrypoint-initdb.d/

# Expone el puerto de PostgreSQL
EXPOSE 5432
