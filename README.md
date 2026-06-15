# Pet Store Backend

Pet Store rendeléskezelő backend egy multi-module Maven projekt, amely jelenleg egy rendeléskezelő
microservice-t (`petstore-order-service`) tartalmaz REST API-val, PostgreSQL adatbázissal és Liquibase sémakezeléssel.

## Felhasznált technológiák

- Java 25
- Spring Boot 4.1.0
- Maven 3.9+
- Docker + Docker Compose
- PostgreSQL 18
- Liquibase 5
- OpenAPI 3.1
- gRPC (Spring gRPC) + Protocol Buffers

## Projekt felépítés

```
petstore                                  # root POM
├── petstore-bom                          # központi függőség- és verziókezelés (dependencyManagement)
├── petstore-common                       # service-ek közti újrahasznosítható modulok
│   ├── petstore-common-core              #   core - exception hierarchia, ProblemType
│   ├── petstore-common-jpa               #   közös entitások (audit), Specification helperek
│   ├── petstore-common-web               #   közös REST kódok - exception handler, logging filter, autoconfig
│   └── petstore-common-grpc              #   közös gRPC kódok - exception handler, logging interceptor, autoconfig
├── petstore-api                          # API modulok
│   └── petstore-order-api                # Rendeléskezeléssel kapcsolatos API-k
│       ├── petstore-order-api-rest       #   Rendelkéskezeléssel kapcsolatos REST API - OpenAPI spec + generált REST interfész és DTO-k
│       └── petstore-order-api-grpc       #   Rendelkéskezeléssel kapcsolatos gRPC API
├── petstore-services                     # futtatható Spring Boot alkalmazások
│   └── petstore-order-service            #   rendeléskezelő service
├── petstore-it                           # integrációs tesztek
└── etc
    ├── docker                            # Dockerfile-ok és docker-compose a lokális futtatáshoz
    └── liquibase                         # adatbázis changelog-ok
```

## API dokumentáció

A Swagger UI és az OpenAPI YML csak a `dev` profillal engedélyezett (alapértelmezetten a `8080` porton):

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/openapi/order-api.yaml

## Előfeltételek

- JDK 25
- Maven 3.9+
- Docker + Docker Compose (lokális adatbázishoz / konténeres futtatáshoz)

## Build

A projekt fő könyvtárában:

```shell
mvn clean install
```

## Tesztek futtatása

Unit tesztek futtatása:

```shell
mvn test
```

Integrációs tesztek futtatása:

- futó alkalmazás szükséges hozzá
- petstore-it mappa alatt szükséges a parancsot kiadni

```shell
mvn verify -P integration-test
```

## Alkalmazás lokális futtatása

A Docker Compose fájlok az `etc/docker` könyvtárban vannak.
A környezeti változókat az `etc/docker/.env` tartalmazza (lokális, nem éles értékekkel).

A parancsokat az `etc/docker` könyvtárból add ki:

```shell
cd etc/docker
```

### 1. opció – Adatbázis konténerben, a service lokálisan (fejlesztéshez ajánlott)

Indítsd el a PostgreSQL-t és futtasd le a Liquibase migrációkat (dev seed adatokkal):

```shell
docker compose -f docker-compose.local.yml up -d
```

Ez elindítja:

- `petstore-postgresql` - PostgreSQL adatbázis (`localhost:5432`)
- `petstore-liquibase` - egyszer lefutó migráció, majd kilép

Ezután indítsd a service-t a `dev` profillal (IDEA, terminál).

### 2. opció – Minden konténerben

A teljes stack (adatbázis + migráció + service) indításához az `app` profil-t aktiválni kell:

```shell
docker compose -f docker-compose.local.yml --profile app up -d --build
```

Ez elindítja:

- `petstore-postgresql` - PostgreSQL adatbázis (`localhost:5432`)
- `petstore-liquibase` - egyszer lefutó migráció, majd kilép
- `petstore-order-service` - a `8080` porton

## Adatbázis-migráció

A Liquibase changelog-ok az `etc/liquibase/changelog` alatt vannak:

- `db.changelog-master.xml` – a belépési pont
- `0.1.0/` – verziózott séma-változások (táblák)
- `dev/` – csak `dev` context-ben futó seed adatok

A migrációt a `petstore-liquibase` konténer futtatja; a context-szűrőt az `.env`-ben a `LIQUIBASE_CONTEXT_FILTER` (alapból `dev`) vezérli.

## Konfiguráció

A főbb környezeti változók (lásd `etc/docker/.env`):

| Változó                     | Leírás                                 | Lokális érték                                         |
|-----------------------------|----------------------------------------|-------------------------------------------------------|
| `POSTGRES_DB`               | Adatbázis neve                         | `petstore`                                            |
| `POSTGRES_USER`             | Adatbázis felhasználó                  | `root`                                                |
| `POSTGRES_PASSWORD`         | Adatbázis jelszó                       | `admin`                                               |
| `JDBC_URL`                  | JDBC kapcsolat URL                     | `jdbc:postgresql://petstore-postgresql:5432/petstore` |
| `LIQUIBASE_CONTEXT_FILTER`  | Liquibase context-szűrő                | `dev`                                                 |
| `PETSTORE_SECURITY_API_KEY` | A `x-api-key` headerben elvárt API key | `petstore-local-api-key`                              |

## REST API végpontok

A rendeléskezelő végpontok (mind `x-api-key` header-t igényelnek):

| Metódus | Útvonal                      | Leírás                                    |
|---------|------------------------------|-------------------------------------------|
| POST    | `/api/store/order`           | Új rendelés létrehozása                   |
| GET     | `/api/store/order`           | Rendelések listázása (`from`/`to` szűrő)  |
| GET     | `/api/store/order/{orderId}` | Rendelés lekérdezése ID alapján           |
| PATCH   | `/api/store/order/{orderId}` | Részleges módosítás (RFC 6902 JSON Patch) |
| DELETE  | `/api/store/order/{orderId}` | Rendelés törlése                          |

## gRPC API

A rendeléskezelő a REST mellett gRPC-n is elérhető néhány funkció.
A gRPC szerver alapértelmezetten a `9090` porton figyel.

| gRPC metódus                                | Leírás                                                                  |
|---------------------------------------------|-------------------------------------------------------------------------|
| `petstore.order.v1.OrderService/GetOrder`   | Rendelés lekérdezése ID alapján                                         |
| `petstore.order.v1.OrderService/ListOrders` | Rendelések listázása (opcionális `ship_date_from`/`ship_date_to` szűrő) |

Az [`order.proto`](petstore-api/petstore-order-api/petstore-order-api-grpc/src/main/proto/order.proto) leíró fájból generálódnak a Java források.

### Védelem

A REST-teé azonos `x-api-key` alapú védelem érvényes a gRPC végpontokra is: minden hívásnál meg kell adni az API kulcsot metadataként.
Hiányzó vagy hibás kulcs esetén a válasz `UNAUTHENTICATED`.

A domain hibák gRPC státuszra képződnek (a REST hibakezeléssel azonos szemantikával):
`NOT_FOUND` (rendelés nem található), `INVALID_ARGUMENT` (hibás UUID / dátumformátum / dátumtartomány),
`FAILED_PRECONDITION` (üzleti szabálysértés).

### Kipróbálás grpcurl-lel

A példákhoz a [grpcurl](https://github.com/fullstorydev/grpcurl) eszköz szükséges.
A szerver-reflection engedélyezett, így nincs szükség a `.proto` fájl megadására.
A példák a `dev` profil API kulcsát (`petstore-local-api-key`) használják.

Elérhető service-ek listázása (reflection):

```shell
grpcurl -plaintext -H "x-api-key: petstore-local-api-key" localhost:9090 list
```

Az `OrderService` leírása:

```shell
grpcurl -plaintext -H "x-api-key: petstore-local-api-key" localhost:9090 describe petstore.order.v1.OrderService
```

`GetOrder` – rendelés lekérdezése ID alapján (az `<orderId>` helyére egy létező rendelés UUID-ja kerül;
rendelést REST-en, a `POST /api/store/order` végponton lehet létrehozni):

```shell
grpcurl -plaintext -H "x-api-key: petstore-local-api-key" -d '{"orderId": "55555555-5555-5555-5555-555555555555"}' localhost:9090 petstore.order.v1.OrderService/GetOrder
```

`ListOrders` – összes rendelés:

```shell
grpcurl -plaintext -H "x-api-key: petstore-local-api-key" -d '{}' localhost:9090 petstore.order.v1.OrderService/ListOrders
```

`ListOrders` – szállítási dátum tartományra szűrve:

```shell
grpcurl -plaintext -H "x-api-key: petstore-local-api-key" -d '{"ship_date_from": "2026-01-01", "ship_date_to": "2026-12-31"}' localhost:9090 petstore.order.v1.OrderService/ListOrders
```
