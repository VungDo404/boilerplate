<div align="center">

  <div>
     <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white"
alt="springboot" />
<img src="https://img.shields.io/badge/Angular-DD0031?style=flat-square&logo=angular&logoColor=white"
alt="angular" />
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=fff" alt="mysql" />
<img src="https://img.shields.io/badge/Redis-%23DD0031.svg?style=flat-square&logo=redis&logoColor=white" alt="redis" />
  </div>

<h3 align="center">Full-Stack Starter Project</h3>

</div>

## 📋 <a name="table">Table of Contents</a>

1. 🤖 [Introduction](#introduction)
2. ⚙️ [Tech Stack](#tech-stack)
3. 🔋 [Features](#features)
4. 🤸 [Quick Start](#quick-start)


## <a name="introduction">🤖 Introduction</a>

This project was developed both as a learning experience to become familiar with Spring and as a reusable boilerplate
for new projects.
After working on several projects, I noticed that starting from scratch often requires re-implementing common
functionalities such as user management, authentication, authorization, logging,caching and so on.

This boilerplate aims
to speed up development by providing these features out of the box, allowing developers to focus on the business logic.

## <a name="tech-stack">⚙️ Tech Stack</a>

- Angular 19 (Angular2)
- PrimeNG
- TypeScript
- Spring Framework
- Java 17

## <a name="features">🔋 Features</a>

🛡️ **Secure Authentication:** Signup, Login, Logout, Spring Security, JWT sessions, Verify Email, Reset Password.

🌐 **Internationalization (i18n):**  Reach a global audience with built-in support for multiple languages. Easily adapt
your application for different locales, with initial support for Vietnamese and English.

🎨 **Modern & Responsive UI:**  Enjoy a clean and minimalist user interface designed for optimal usability across all
devices. The responsive design ensures a consistent and professional experience on desktops, tablets, and smartphones.

📚 **API Documentation with Swagger:**  Generate interactive and up-to-date API documentation automatically with
Swagger. Simplify API exploration and integration for both frontend developers and external consumers.

🔄 **Comprehensive Audit Trail:**  Entity history tracking using Spring Envers for data revision history.

🗄️ **Database Migrations with Liquibase:**  Streamline database schema management. Liquibase enables you to manage
database changes in a controlled and versioned manner, ensuring smooth deployments and updates.

🔒 **Fine-grained Authorization with Spring ACL:** Implement granular access control and secure your application
resources. Spring ACL (Access Control List) provides a powerful framework for defining and enforcing authorization
rules.

## <a name="quick-start">🤸 Quick Start</a>

Follow these steps to set up the project locally on your machine.

**Prerequisites**

Make sure you have the following installed on your machine:

- [Git](https://git-scm.com/)
- [Node 20](https://nodejs.org/en/blog/release/v20.16.0)
- [npm](https://www.npmjs.com/) (Node Package Manager)

**Cloning the Repository**

```bash
git clone https://github.com/VungDo404/boilerplate.git
cd boilerplate
```

**Installation**

Install the project dependencies using npm:

```bash
npm install
mvn clean install
docker-compose up -d
```

**Set Up Environment Variables**

1. **Create Certificates Folder:**
    - Navigate to `/boilerplate/src/main/resources/`
    - Create a folder named `certs`

2. **Generate Keys (inside `certs` folder):**
    - Change to the `certs` folder:
      ```bash
      cd certs
      ```
    - **Private Key:**
      ```bash
      openssl genrsa -out private.pem 2048
      ```
    - **Public Key:**
      ```bash
      openssl rsa -in private.pem -pubout -out public.pem
      ```

3. **Update Environment Variables:**
    - Edit the file `application-dev.properties` located in `/boilerplate/src/main/resources/`
