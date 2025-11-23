# Docker Setup for VSL Platform Backend

## Prerequisites
- Docker Engine 20.10+
- Docker Compose 2.0+
- Ubuntu Linux (or any Linux distribution)

## Quick Start

### 1. Build and Start Services
```bash
docker-compose up -d --build
```

### 2. Check Service Status
```bash
docker-compose ps
```

### 3. View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f postgres
docker-compose logs -f elasticsearch
```

### 4. Stop Services
```bash
docker-compose down
```

### 5. Stop and Remove Volumes (Clean Slate)
```bash
docker-compose down -v
```

## Services

### PostgreSQL (Port 5432)
- Database: `vsl_db` (auto-created)
- User: `postgres`
- Password: `password`
- Timezone: `Asia/Ho_Chi_Minh`
- Data persisted in: `./postgres-data/`

### Elasticsearch (Port 9200)
- Security: Disabled (development)
- Memory: 512MB (limited)
- Timezone: `Asia/Ho_Chi_Minh`
- Data persisted in: `./elasticsearch-data/`

### Backend (Port 8080)
- Spring Boot 3.3 (Java 21)
- Timezone: `Asia/Ho_Chi_Minh`
- Connects to PostgreSQL and Elasticsearch
- Can access Python services on host via `host.docker.internal`

## Python Service Integration

The backend can access Python services running on the Ubuntu host:
- Gesture Recognition: `http://host.docker.internal:5000/predict`
- NLP Service: `http://host.docker.internal:5001/add-accents`

**Important**: Ensure Python services are running on the host before starting the backend.

## Environment Variables

All configuration is set in `docker-compose.yml`. To override:

1. Create `.env` file:
```bash
POSTGRES_PASSWORD=your_password
JWT_SECRET=your_secret_key
```

2. Or modify `docker-compose.yml` directly.

## Troubleshooting

### Backend can't connect to PostgreSQL
- Wait for PostgreSQL healthcheck to pass (10-30 seconds)
- Check logs: `docker-compose logs postgres`

### Backend can't connect to Elasticsearch
- Wait for Elasticsearch healthcheck to pass (60+ seconds)
- Check logs: `docker-compose logs elasticsearch`

### Backend can't reach Python service on host
- Verify Python service is running: `curl http://localhost:5000/predict`
- Check `extra_hosts` configuration in docker-compose.yml
- On Linux, `host.docker.internal` is mapped to `host-gateway`

### Timezone Issues
- All services use `Asia/Ho_Chi_Minh` timezone
- Logs and database timestamps will be in Vietnam time

## Production Considerations

1. **Change default passwords** in `docker-compose.yml`
2. **Enable Elasticsearch security** (xpack.security.enabled=true)
3. **Use secrets management** for sensitive data
4. **Configure proper resource limits** for production workloads
5. **Set up backup strategy** for PostgreSQL and Elasticsearch data
6. **Use reverse proxy** (nginx/traefik) for SSL termination

