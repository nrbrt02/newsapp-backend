## Database Setup

1. Create a PostgreSQL database named `newsapp`



## Backend Setup

```bash
mvn clean install
```
4. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

## Default Admin Account

On first run, the application automatically creates an admin account:
- Username: `admin`
- Password: `Pa$$w0rd!`
- Email: `nrbrt2002@gmail.com`

## API Endpoints

## Security Features

- JWT-based authentication
- Two-factor authentication for login
- Password reset via email verification
- Role-based access control (ADMIN, WRITER, READER)

## Development

### Backend Development
- The application uses Spring Boot 3.2.3
- JPA/Hibernate for database operations
- Spring Security for authentication and authorization
- JWT for token-based authentication

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods small and focused

