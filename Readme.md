# News Application

A full-stack news application with user authentication, article management, and commenting system.

## Prerequisites

Before running the application, make sure you have the following installed:

- Java 17 or higher
- Maven
- PostgreSQL
- Node.js and npm (for frontend)

## Database Setup

1. Create a PostgreSQL database named `newsapp`
2. Update the database configuration in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/newsapp
spring.datasource.username=postgres
spring.datasource.password=your_password
```

## Email Configuration

The application uses Gmail SMTP for sending emails. Update the email configuration in `src/main/resources/application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

Note: For Gmail, you need to use an App Password. You can generate one in your Google Account settings under Security > 2-Step Verification > App passwords.

## Backend Setup

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
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

### Public Endpoints (No Authentication Required)
- `GET /api/categories/**` - Get all categories
- `GET /api/tags/**` - Get all tags
- `GET /api/articles/published/**` - Get published articles
- `GET /api/articles/{id}` - Get article by ID
- `GET /api/comments/article/**` - Get article comments

### Authentication Endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/2fa/verify-reset-password` - Reset password with verification code

### Protected Endpoints (Authentication Required)
- All other endpoints require a valid JWT token

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

## Troubleshooting

### Common Issues

1. Database Connection Issues
   - Verify PostgreSQL is running
   - Check database credentials in application.properties
   - Ensure database 'newsapp' exists

2. Email Sending Issues
   - Verify Gmail credentials
   - Check if App Password is correctly set
   - Ensure email configuration in application.properties is correct

3. Authentication Issues
   - Check JWT token expiration
   - Verify user credentials
   - Ensure proper role assignments

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.