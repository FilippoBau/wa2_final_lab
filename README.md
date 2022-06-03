# Group 22: Reactive - Kafka (Lab 5)


1. [Getting Started](#getting-started)
   - [Database credentials and settings](#database-credentials-and-settings)
   - [Services port overview](#services-port-overview)
2. [Setting up Postgres Databases](#setting-up-postgres-databases)
   - [TravelerService](#travelerservice)
   - [PaymentService](#paymentservice)
   - [TicketCatalogueService](#ticketcatalogueservice)
3. [Example Token and Payloads for API Tests](#example-token-and-payloads-for-api-tests)
   - [TOKEN [ROLE USER]: user1](#token-role-user-user1)
   - [TOKEN [ROLE USER]: user2](#token-role-user-user2)
   - [TOKEN [ROLE ADMIN]: user3](#token-role-admin-user3)
   - [Example Payloads](#example-payloads)

# Getting Started

- Move to `/docker` directory:
- For each sub folder (`/db` and `/kafka`) run: 
```
docker-compose up
```

#### Database credentials and settings

| Database               | Port  | Username | Password | DB Name                |
|------------------------|-------|----------|----------|------------------------|
| LoginService           | 54321 | postgres | group22  | login_service          |
| TravelerService        | 54322 |     =    |     =    | traveler_service       |
| TicketCatalogueService | 54323 |     =    |     =    | ticket_catalog_service |
| PaymentService         | 54323 |     =    |     =    | payment_service        |

#### Services port overview

| Service name           | Port |
|------------------------|------|
| LoginService           | 8080 |
| PaymentService         | 8081 |
| TicketCatalogueService | 8082 |
| TravelerService        | 8083 |
| FakeBankService        | 8084 |

# Setting up Postgres Databases

#### TravelerService

Populate Table:

```postgres-sql
INSERT INTO user_details (username, address, date_of_birth, name, telephone_number) VALUES ('user1', 'Corso Duca degli Abruzzi 24', '1993-01-01', 'Utente Prova 1', '333 4455666');
INSERT INTO user_details (username, address, date_of_birth, name, telephone_number) VALUES ('user2', 'Corso Duca degli Abruzzi 24', '2000-01-01', 'Utente Prova 2', '333 4455666');
```

#### PaymentService

Create the table:

```postgres-sql
CREATE TABLE payment(
    paymentid SERIAL PRIMARY KEY,
    orderid INT NOT NULL,
    userid VARCHAR(255) NOT NULL,
    status INT
);
```

#### TicketCatalogueService

Create and populate the table:
```
BEGIN;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    price FLOAT NOT NULL,
    type VARCHAR,
    max_age INT,
    min_age INT
);

CREATE TABLE users (
    username VARCHAR(250) PRIMARY KEY,
    email VARCHAR(250) UNIQUE NOT NULL
);

CREATE TABLE orders(
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    ticketId INT REFERENCES tickets(id),
    username VARCHAR(255) REFERENCES users(username),
    status VARCHAR(255) NOT NULL
);

INSERT INTO users (username, email) VALUES ('user1', 'user1@email.it');
INSERT INTO users (username, email) VALUES ('user2', 'user2@email.it');
INSERT INTO users (username, email) VALUES ('user3', 'user3@email.it');
INSERT INTO tickets (price, type, max_age, min_age) VALUES (123, 'students', 25, NULL);
INSERT INTO tickets (price, type, max_age, min_age) VALUES (123, 'elders', NULL, 65);


COMMIT;
```

# Example Token and Payloads for API Tests

## TOKEN [ROLE USER]: user1 


```token
Bearer
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxNjY2MjM5MDIyLCJyb2xlcyI6WyJVU0VSIl19.GZe-2ACp4oEPsozbXvjt_dVww46ynIyp3aHqsWqpuUU```
```

#### HEADER
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
#### PAYLOAD
```json
{
   "sub": "user1",
   "iat": 1616239022,
   "exp": 1666239022,
   "roles":["USER"]
}
```
#### VERIFY SIGNATURE
```json
{
  ...
}
```

## TOKEN [ROLE USER]: user2

```token
Bearer
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMiIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxNjY2MjM5MDIyLCJyb2xlcyI6WyJVU0VSIl19.xtjwBeWf3O9mTinU5WHhUj-dxEes2VzjiJdK-NzjpUk
```

#### HEADER
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
#### PAYLOAD
```json
{
   "sub": "user2",
   "iat": 1616239022,
   "exp": 1666239022,
   "roles":["USER"]
}
```
#### VERIFY SIGNATURE
```json
{
  ...
}
```

## TOKEN [ROLE ADMIN]: user3

```token
Bearer
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMyIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxNjY2MjM5MDIyLCJyb2xlcyI6WyJBRE1JTiJdfQ.u2C4OaSAeddWUs05aXMdx_KhUE6xmnnJm7PJH5Gs1OI
```

#### HEADER
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
#### PAYLOAD
```json
{
   "sub": "user3",
   "iat": 1616239022,
   "exp": 1666239022,
   "roles":["ADMIN"]
}
```
#### VERIFY SIGNATURE
```json
{
  ...
}
```
## Example Payloads

#### POST `/shop/{ticket-id}`

```json
{
  "amount": 1,
  "creditCardNumber": "3666666666666666",
  "cvv": "123",
  "expirationDate": "2026-10-10"
}
```

#### POST `/admin/tickets`

```json
{  
  "price": 2.5,
  "type": "promo",
  "max_age": null,
  "min_age": null
}
```
