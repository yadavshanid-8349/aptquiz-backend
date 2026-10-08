# AptiQuiz Backend - Render

This service is a Spring Boot + WebSocket backend.

## Render
- Runtime: Docker
- Dockerfile: `Dockerfile`
- Region: Singapore (recommended for India)
- Web service port: Render supplies `PORT`

Required environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`

The frontend must use the final Render backend URL in `aptquiz-frontend/script.js`.
