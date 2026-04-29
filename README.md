# 🚲 Plataforma de Alquiler de Bicicletas

Aplicación web full-stack para la gestión de alquiler de bicicletas y accesorios con reservas por días, gestión de clientes y envío automático de confirmaciones por email.

---

## 🧠 Sobre el proyecto

Este proyecto ha sido desarrollado de forma **100% autodidacta** como demostración práctica de conocimientos en desarrollo web full-stack.

Incluye tanto el **frontend como el backend completamente funcionales**, con integración real con base de datos, sistema de reservas y comunicación por email.

El objetivo principal ha sido construir una aplicación realista, modular y escalable que simule un entorno profesional.

---

## ⚙️ Tecnologías utilizadas

### 🔧 Backend

* Spring Boot
* Java (Java 17)
* Hibernate / JPA
* Flyway (migraciones de base de datos)
* PostgreSQL
* Seguridad con Spring Security
* Generación de PDFs (contratos)
* Envío de emails (SMTP Gmail)

---

### 🎨 Frontend

* Angular
* TypeScript
* Bootstrap
* Tailwind CSS

---

### 🛠️ Herramientas

* Git
* GitHub
* Visual Studio Code
* Postman

---

## 🚀 Funcionalidades principales

* 📅 Reserva de bicicletas por días
* 👤 Gestión de clientes
* 📧 Envío automático de emails de confirmación
* 🧾 Generación de contratos en PDF
* 🛠️ Panel administrativo (lazy loading en Angular)
* 🔗 API REST completa
* 🗄️ Persistencia en base de datos relacional
* 🔄 Migraciones automáticas con Flyway

---

## 🏗️ Arquitectura

Proyecto estructurado como **monorepo**:

```
📦 proyecto_app_alquiler_bicicletas
 ┣ 📂 alquilerBicicletasBackend   → API REST (Spring Boot)
 ┗ 📂 alquilerBicicletasFrontend  → Aplicación Angular
```

---

## 🔌 Comunicación frontend-backend

* Backend: `http://localhost:8085`
* Frontend: `http://localhost:4200`
* Comunicación mediante HTTP REST (JSON)

---

## 🧪 Ejemplo de endpoint

```http
GET /api/clientes/{id}
```

---

## ⚙️ Configuración del entorno

### 🔐 Seguridad de credenciales

Las credenciales sensibles (base de datos, email, etc.) **no están incluidas en el repositorio**.

Se gestionan mediante:

* `application-secrets.properties` (no versionado)
* Variables de entorno

---

### ▶️ Backend

```bash
cd alquilerBicicletasBackend/alquiler-bicicletas
mvn spring-boot:run
```

---

### ▶️ Frontend

```bash
cd alquilerBicicletasFrontend/alquilerBicicletasFronted
npm install
npm start
```

---

## 📦 Estado del proyecto

✔ Aplicación funcional end-to-end
✔ Integración completa frontend + backend
✔ Persistencia real en base de datos
✔ Envío de emails operativo
✔ Estructura preparada para producción

---

## 🎯 Objetivo profesional

Este proyecto ha sido desarrollado como demostración de:

* Capacidad de aprendizaje autónomo
* Diseño de aplicaciones completas
* Integración de múltiples tecnologías
* Resolución de problemas reales

---

## 👩‍💻 Autora

Desarrollado íntegramente de forma autodidacta como proyecto personal.

---

## 📩 Contacto

Disponible para oportunidades como desarrolladora junior / full-stack.

---
