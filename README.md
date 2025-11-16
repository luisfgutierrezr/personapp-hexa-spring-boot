# PersonApp - Aplicación Hexagonal Spring Boot

Aplicación de gestión de personas desarrollada con arquitectura hexagonal (Clean Architecture) en Spring Boot, que soporta múltiples bases de datos (MariaDB y MongoDB).

## Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
  - [Instalación con Docker (Recomendado)](#instalación-con-docker-recomendado)
- [Configuración](#configuración)
- [Despliegue](#despliegue)
- [Documentación de API](#documentación-de-api)
- [Endpoints REST](#endpoints-rest)
- [Uso de Docker Compose](#uso-de-docker-compose)
- [Estructura del Proyecto](#estructura-del-proyecto)

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 11** o superior
- **Maven 3.6+**
- **Docker** 20.10+ y **Docker Compose** 2.0+ (para uso con Docker)
- **Git** (para clonar el repositorio)
- **Lombok** configurado en tu IDE (necesario para desarrollo)

### Verificar Instalación

```bash
java -version
mvn -version
docker --version
docker-compose --version
```

## Instalación

### Instalación con Docker (Recomendado)

Esta es la forma más sencilla de ejecutar la aplicación, ya que incluye todas las dependencias (bases de datos) en contenedores.

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/andres-sanchez-m/personapp-hexa-spring-boot.git
cd personapp-hexa-spring-boot
```

#### 2. Ejecutar con Docker Compose

```bash
docker-compose up -d
```

Este comando:
- Construye la imagen de la aplicación Spring Boot
- Inicia los contenedores de MariaDB y MongoDB
- Inicia el contenedor de la aplicación REST API
- Ejecuta los scripts de inicialización de las bases de datos

#### 3. Verificar que los servicios están corriendo

```bash
docker-compose ps
```

Deberías ver tres servicios en estado "Up":
- `personapp-mariadb` (puerto 3307)
- `personapp-mongodb` (puerto 27017)
- `personapp-rest-api` (puerto 3000)

#### 4. Ver logs de la aplicación

```bash
docker-compose logs -f personapp-rest
```

## Configuración

### Variables de Entorno (Docker)

Cuando usas Docker Compose, las siguientes variables están configuradas automáticamente:

- `SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/persona_db`
- `SPRING_DATASOURCE_USERNAME=persona_db`
- `SPRING_DATASOURCE_PASSWORD=persona_db`
- `SPRING_DATA_MONGODB_HOST=mongodb`
- `SPRING_DATA_MONGODB_PORT=27017`
- `SPRING_DATA_MONGODB_DATABASE=persona_db`
- `SPRING_DATA_MONGODB_USERNAME=persona_db`
- `SPRING_DATA_MONGODB_PASSWORD=persona_db`

## Despliegue

### Despliegue con Docker

#### Detener los servicios

```bash
docker-compose down
```

#### Detener y eliminar volúmenes (elimina los datos)

```bash
docker-compose down -v
```

#### Reconstruir la imagen de la aplicación

```bash
docker-compose build personapp-rest
docker-compose up -d
```

## Documentación de API

La aplicación utiliza **Swagger/OpenAPI 3** para documentación interactiva de la API.

### Acceder a Swagger UI

Una vez que la aplicación esté corriendo, accede a:

```
http://localhost:3000/swagger-ui.html
```

O alternativamente:

```
http://localhost:3000/swagger-ui/index.html
```

### Documentación JSON/OpenAPI

La especificación OpenAPI en formato JSON está disponible en:

```
http://localhost:3000/api-docs
```

## Endpoints REST

La aplicación expone una API REST bajo el prefijo `/api/v1/`.

### Base URL

```
http://localhost:3000/api/v1
```

### Parámetro de Base de Datos

La mayoría de los endpoints aceptan un parámetro `{database}` que puede ser:
- `MARIA` - Para usar MariaDB
- `MONGO` - Para usar MongoDB

### Checklist de Pruebas en Swagger UI

Accede a la interfaz de Swagger UI en `http://localhost:3000/swagger-ui.html` para realizar las siguientes pruebas:

#### 1. Persona

- [ ] POST - Crear persona
- [ ] GET - Obtener persona por ID (funciona)
- [ ] GET - Listar todas las personas (GET /api/v1/persona/MARIA)
- [ ] GET - Obtener persona que NO existe (debería devolver mensaje de error, no 500)
- [ ] PUT - Actualizar persona existente
- [ ] DELETE - Eliminar persona

#### 2. Teléfono

- [ ] POST - Crear teléfono
- [ ] GET - Listar todos los teléfonos (GET /api/v1/telefono/MARIA)
- [ ] GET - Obtener teléfono por número (GET /api/v1/telefono/MARIA/3001234567)
- [ ] PUT - Actualizar teléfono
- [ ] DELETE - Eliminar teléfono

#### 3. Profesión

- [ ] POST - Crear profesión
- [ ] GET - Listar todas las profesiones (GET /api/v1/profesion/MARIA)
- [ ] GET - Obtener profesión por ID (GET /api/v1/profesion/MARIA/1)
- [ ] PUT - Actualizar profesión
- [ ] DELETE - Eliminar profesión

#### 4. Estudios

- [ ] POST - Crear estudios (antes daba error 500, probar de nuevo)
- [ ] GET - Listar todos los estudios (GET /api/v1/estudios/MARIA)
- [ ] GET - Obtener estudios por persona y profesión (GET /api/v1/estudios/MARIA/1234567890/1)
- [ ] PUT - Actualizar estudios
- [ ] DELETE - Eliminar estudios

#### 5. Casos especiales

- [ ] GET de persona inexistente (debería devolver error, no 500)
- [ ] POST de estudios con persona/profesión inexistentes (debería devolver error)
- [ ] Probar con base de datos MONGO (si aplica)

### Orden sugerido de pruebas

1. GET - Listar todas las personas (verificar que aparece la que creaste)
2. GET - Obtener persona que NO existe (ej: ID 9999999999) - debería devolver mensaje de error
3. PUT - Actualizar la persona existente (cambiar nombre, edad, etc.)
4. GET - Listar teléfonos (verificar que aparece el que creaste)
5. GET - Listar profesiones (verificar que aparece la que creaste)
6. POST - Crear estudios de nuevo (verificar que ya no da error 500)
7. GET - Listar estudios (verificar que aparece el que creaste)
8. DELETE - Eliminar estudios
9. DELETE - Eliminar teléfono
10. DELETE - Eliminar profesión
11. DELETE - Eliminar persona

## Uso de Docker Compose

### Comandos Útiles

#### Iniciar todos los servicios

```bash
docker-compose up -d
```

#### Ver logs en tiempo real

```bash
docker-compose logs -f
```

#### Ver logs de un servicio específico

```bash
docker-compose logs -f personapp-rest
docker-compose logs -f mariadb
docker-compose logs -f mongodb
```

#### Detener todos los servicios

```bash
docker-compose stop
```

#### Iniciar servicios detenidos

```bash
docker-compose start
```

#### Reiniciar un servicio específico

```bash
docker-compose restart personapp-rest
```

#### Ejecutar comandos dentro de un contenedor

```bash
# Acceder a la base de datos MariaDB
docker-compose exec mariadb mariadb -u persona_db -ppersona_db persona_db

# Acceder a MongoDB
docker-compose exec mongodb mongosh -u persona_db -p persona_db --authenticationDatabase admin persona_db
```

#### Ver el estado de los servicios

```bash
docker-compose ps
```

#### Eliminar todo (contenedores, redes y volúmenes)

```bash
docker-compose down -v
```

**Advertencia**: `docker-compose down -v` eliminará todos los datos almacenados en las bases de datos.

## Estructura del Proyecto

```
personapp-hexa-spring-boot/
├── application/              # Casos de uso (reglas de negocio)
├── common/                   # Utilidades y clases comunes
├── domain/                   # Entidades del dominio
├── rest-input-adapter/       # Adaptador REST (entrada)
├── cli-input-adapter/        # Adaptador CLI (entrada)
├── maria-output-adapter/     # Adaptador MariaDB (salida)
├── mongo-output-adapter/     # Adaptador MongoDB (salida)
├── docker/                   # Scripts de inicialización Docker
│   ├── init/
│   │   ├── mariadb/         # Scripts SQL para MariaDB
│   │   └── mongodb/         # Scripts JS para MongoDB
├── Dockerfile               # Configuración de imagen Docker
├── docker-compose.yml       # Configuración de servicios Docker
└── pom.xml                  # Configuración Maven del proyecto
```

## Configuración de Swagger

La aplicación está configurada con **springdoc-openapi-ui** (Swagger 3). La configuración está en `rest-input-adapter/src/main/resources/application.properties`:

```properties
springdoc.api-docs.path=/api-docs
```

Los endpoints están documentados automáticamente usando anotaciones Spring. Accede a la interfaz de Swagger UI en:

```
http://localhost:3000/swagger-ui.html
```

## Notas Adicionales

- El adaptador REST corre en el puerto **3000**
- Son dos adaptadores de entrada: REST y CLI (cada uno es una SpringApplication diferente)
- Debes configurar **Lombok** en tu IDE para desarrollo
- Puedes hacer Fork de este repo, no editar este repositorio directamente

## Autores

Repositorio original: https://github.com/andres-sanchez-m/personapp-hexa-spring-boot
- **Andres Sanchez** - asanchez-m@javeriana.edu.co
- Pontificia Universidad Javeriana

Autores del fork:

- **Luis Felipe Gutiérrez** - gutierrez-lfelipe@javeriana.edu.co
- Pontificia Universidad Javeriana

- **Daniel Perez** - perezpdaniel@javeriana.edu.co
- Pontificia Universidad Javeriana

- **Maria Paula Rodríguez** - mpaularodriguezm@javeriana.edu.co
- Pontificia Universidad Javeriana