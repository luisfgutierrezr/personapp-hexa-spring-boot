# PersonApp - Aplicación Hexagonal Spring Boot

Aplicación de gestión de personas desarrollada con arquitectura hexagonal (Clean Architecture) en Spring Boot, que soporta múltiples bases de datos (MariaDB y MongoDB).

## Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
  - [Instalación con Docker (Recomendado)](#instalación-con-docker-recomendado)
  - [Instalación Manual](#instalación-manual)
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

### Instalación Manual

Si prefieres ejecutar la aplicación sin Docker, sigue estos pasos:

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/andres-sanchez-m/personapp-hexa-spring-boot.git
cd personapp-hexa-spring-boot
```

#### 2. Instalar y Configurar MariaDB

- Instala MariaDB en tu sistema
- Configúrala para que escuche en el puerto **3307**
- Crea la base de datos `persona_db` y el usuario `persona_db` con contraseña `persona_db`
- Ejecuta los scripts de inicialización ubicados en `docker/init/mariadb/`:
  - `01-init.sql` - Estructura de la base de datos
  - `02-data.sql` - Datos iniciales

#### 3. Instalar y Configurar MongoDB

- Instala MongoDB en tu sistema
- Configúralo para que escuche en el puerto **27017**
- Crea la base de datos `persona_db` con usuario `persona_db` y contraseña `persona_db`
- Ejecuta los scripts de inicialización ubicados en `docker/init/mongodb/`:
  - `01-init-user.js` - Configuración de usuarios
  - `02-init-data.js` - Datos iniciales

#### 4. Compilar el Proyecto

```bash
mvn clean install
```

#### 5. Ejecutar la Aplicación

```bash
cd rest-input-adapter
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:3000`

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

### Configuración Local (Sin Docker)

Si ejecutas la aplicación localmente, las variables por defecto en `application.properties` son:

- MariaDB: `localhost:3307`
- MongoDB: `localhost:27017`

Puedes sobrescribir estas configuraciones usando variables de entorno o modificando `rest-input-adapter/src/main/resources/application.properties`.

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

### Despliegue Manual

1. Compila el proyecto: `mvn clean package`
2. Encuentra el JAR en `rest-input-adapter/target/rest-input-adapter-*.jar`
3. Ejecuta: `java -jar rest-input-adapter/target/rest-input-adapter-*.jar`

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

### Endpoints de Persona

#### 1. Obtener todas las personas

```http
GET /api/v1/persona/{database}
```

**Ejemplo de solicitud:**
```bash
curl -X GET http://localhost:3000/api/v1/persona/MARIA
```

**Respuesta de ejemplo:**
```json
[
  {
    "dni": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "age": "30",
    "sex": "M",
    "database": "MARIA",
    "status": "success"
  }
]
```

#### 2. Crear una nueva persona

```http
POST /api/v1/persona
Content-Type: application/json
```

**Ejemplo de solicitud:**
```bash
curl -X POST http://localhost:3000/api/v1/persona \
  -H "Content-Type: application/json" \
  -d '{
    "dni": "9876543210",
    "firstName": "María",
    "lastName": "García",
    "age": "25",
    "sex": "F",
    "database": "MARIA"
  }'
```

**Cuerpo de la solicitud (JSON):**
```json
{
  "dni": "9876543210",
  "firstName": "María",
  "lastName": "García",
  "age": "25",
  "sex": "M",
  "database": "MARIA"
}
```

**Respuesta de ejemplo:**
```json
{
  "dni": "9876543210",
  "firstName": "María",
  "lastName": "García",
  "age": "25",
  "sex": "F",
  "database": "MARIA",
  "status": "created"
}
```

**Campos del Request:**
- `dni` (String, requerido): Documento Nacional de Identidad
- `firstName` (String, requerido): Nombre
- `lastName` (String, requerido): Apellido
- `age` (String, requerido): Edad
- `sex` (String, requerido): Sexo (M o F)
- `database` (String, requerido): Base de datos a usar (MARIA o MONGO)

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

## Solución de Problemas

### La aplicación no se conecta a las bases de datos

- Verifica que los contenedores de MariaDB y MongoDB estén corriendo: `docker-compose ps`
- Revisa los logs: `docker-compose logs mariadb` y `docker-compose logs mongodb`
- Asegúrate de que los healthchecks pasen antes de que la app intente conectarse

### Puerto 3000 ya está en uso

- Cambia el puerto en `docker-compose.yml` y `application.properties`
- O detén el servicio que está usando el puerto 3000

### Los datos no persisten después de reiniciar

- Verifica que los volúmenes de Docker estén configurados correctamente
- No uses `docker-compose down -v` si quieres mantener los datos

## Licencia

Este proyecto está bajo la Licencia Apache 2.0. Ver el archivo `LICENSE` para más detalles.

## Autores

- **Andres Sanchez** - asanchez-m@javeriana.edu.co
- Pontificia Universidad Javeriana
