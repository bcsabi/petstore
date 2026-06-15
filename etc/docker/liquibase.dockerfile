FROM liquibase/liquibase:5.0.3

# A PostgreSQL JDBC-driver nincs az alap-image-ben.
# A Spring Boot által menedzselt pontos verziót telepítjük lpm-mel a classpath-ra.
RUN lpm add postgresql@42.7.11 --global
