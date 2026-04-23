# OTP Protection Service

Backend-сервис на Java для генерации и проверки временных кодов (OTP) с отправкой через Email, SMS (SMPP эмулятор), Telegram и сохранением в файл. Сервис предназначен для защиты действий пользователей с помощью одноразовых паролей (OTP).

---

## Основные функции

- **Регистрация и аутентификация пользователей** с ролями: `ADMIN` и `USER`
- **Генерация и отправка OTP-кодов**:
    - Email (JavaMail)
    - SMS (SMPP-эмулятор)
    - Telegram Bot API
    - Сохранение OTP в файл
- **Проверка OTP-кодов** с учетом статусов: `ACTIVE`, `USED`, `EXPIRED`
- **Администрирование** (настройка TTL и длины OTP, управление пользователями)
- **Токенная авторизация** с проверкой ролей
- **Логирование** всех ключевых операций через SLF4J/Logback

---

## Технологии

- **Java 17**
- **PostgreSQL 17 + JDBC** (без Hibernate)
- **Maven** (система сборки)
- **JavaMail** для отправки Email
- **SMPP** (OpenSMPP-core, эмулятор SMPPsim)
- **Telegram Bot API** (Apache HttpClient)
- **HttpServer** (встроенный com.sun.net.httpserver)
- **SLF4J/Logback** для логирования

---

## Установка и запуск

### 1. Подготовка

- Убедитесь, что установлены:
    - Java 17
    - PostgreSQL 17
    - Maven

Создайте базу данных `otp_service`:

```sql
CREATE DATABASE otp_service;
```

### 2. Настройка

Клонируйте репозиторий:

```bash
git clone https://github.com/amasovich/otp-protection-service.git
cd otp-protection-service
```

Заполните конфигурационные файлы в `src/main/resources`:

- `application.properties` (параметры БД)
- `email.properties` (SMTP сервер)
- `sms.properties` (SMPP эмулятор)
- `telegram.properties` (токен и chatId)

Пример `application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/otp_service
db.user=postgres
db.password=ваш_пароль
```

### 3. Сборка и запуск

Соберите проект и запустите приложение:

```bash
mvn clean package
java -jar target/otp-backend.jar
```

---

## Структура проекта

```
otp-backend/
├── src/                      # Исходный код и ресурсы
│   └── main/
│       ├── java/             # Java-код
│       │   ├── api/      # HTTP-контроллеры (API-слой)
│       │   ├── config/   # Конфигурация приложения (загрузка конфигураций)
│       │   ├── dao/      # Доступ к базе данных (JDBC-реализация)
│       │   ├── model/    # Модели данных (DTO и сущности)
│       │   ├── service/  # Бизнес-логика и сервисы
│       │   └── util/     # Вспомогательные классы и утилиты
│       └── resources/        # Конфигурационные файлы и ресурсы
│           ├── application.properties  # Общие настройки приложения
│           ├── email.properties        # Настройки Email
│           ├── logback.xml             # Конфигурация логирования
│           ├── sms.properties          # Настройки SMS
│           └── telegram.properties     # Настройки Telegram
├── pom.xml                   # Конфигурация Maven
└── README.md                 # Описание проекта
```

---

## Роли и авторизация

- **ADMIN**: полные права управления
    - настройка OTP
    - просмотр и удаление пользователей
- **USER**: ограниченные права
    - генерация и валидация OTP

### Токены

- Генерируются при логине, имеют ограниченный TTL
- Передаются в заголовке:

```http
Authorization: Bearer <token>
```

---

## Примеры API-запросов

### Регистрация пользователя

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123","role":"USER"}'
```

### Вход (получение токена)

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Генерация OTP

```bash
curl -X POST http://localhost:8080/otp/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"operationId":"op123","channel":"EMAIL"}'
```

### Проверка OTP

```bash
curl -X POST http://localhost:8080/otp/validate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"code":"123456"}'
```

### Действия администратора

```bash
# Изменение параметров OTP
curl -X PATCH http://localhost:8080/admin/config \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{"length":6,"ttlSeconds":300}'

# Просмотр пользователей
curl -X GET http://localhost:8080/admin/users \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Удаление пользователя
curl -X DELETE http://localhost:8080/admin/users/2 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## Тестирование

Используйте **Postman** или **curl** для проверки API. Убедитесь, что работают:

- регистрация и аутентификация
- генерация и отправка OTP
- проверка OTP-кодов
- админ-функции