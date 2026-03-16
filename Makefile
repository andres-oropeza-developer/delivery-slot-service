.PHONY: run test docker stop clean

## Levanta con H2 embebido (cero configuración)
run:
	./mvnw spring-boot:run

## Corre los tests unitarios
test:
	./mvnw test

## Levanta app + PostgreSQL con Docker
docker:
	docker-compose up --build

## Detiene los contenedores
stop:
	docker-compose down

## Limpia el build
clean:
	./mvnw clean
