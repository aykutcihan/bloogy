# 📱 Bloogy - Full Stack Blog Platform

A modern, responsive blog platform built with **Flutter Web** and **Spring Boot**, featuring user authentication via Google OAuth and comprehensive article management.

## ✨ Features

- 🔐 **Google OAuth 2.0 Authentication**
- 📝 **Article CRUD Operations** (Create, Read, Update, Delete)
- 🎯 **Pagination Support** for articles
- 🎨 **Responsive UI** with Flutter Web
- 🔗 **RESTful API** with OpenAPI documentation
- 🐳 **Docker & Docker Compose** for easy deployment
- 🗄️ **GCP Integration** for secure credential management

## 🏗️ Architecture

- **Frontend**: Flutter Web + Nginx (Port 18080)
- **Backend**: Spring Boot REST API (Port 18081)
- **Authentication**: Google OAuth 2.0
- **Containerization**: Docker & Docker Compose

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose installed
- Google Cloud Console credentials (.env file)

### Quick Start

**Run everything with Docker Compose:**

```bash
docker-compose up --build
```

Then open: http://localhost:18080

## ⚙️ Configuration

### Environment Variables

Create `.env` file in `bloogy_backend/bloogy_backend/`:

```env
GOOGLE_CLIENT_ID=your_client_id
GOOGLE_CLIENT_SECRET=your_client_secret
GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
GOOGLE_OAUTH_REDIRECT_URI=http://localhost:18080/login/oauth2/code/google
```

### Google Cloud Setup

1. Create a GCP project
2. Enable OAuth 2.0
3. Add authorized redirect URI:
   - Local: `http://localhost:18080/login/oauth2/code/google`
   - Production: `https://your-domain.com/login/oauth2/code/google`

## 📂 Project Structure

```
bloogy/
├── docker-compose.yml                    # Main orchestration
├── bloogy_backend/
│   └── bloogy_backend/
│       ├── Dockerfile
│       ├── pom.xml                      # Maven dependencies
│       ├── src/main/java/               # Spring Boot application
│       ├── .env                         # Configuration
│       └── openapi.yaml                 # API documentation
└── bloogy_frontend/
    ├── Dockerfile
    ├── nginx.conf                       # Web server config
    ├── pubspec.yaml                     # Flutter dependencies
    └── lib/                             # Flutter source code
```

## 🔌 API Endpoints

Base URL: `http://localhost:18081/api/v1`

**Articles:**
- `GET /articles/pagination?pageSize=5` - Get articles with pagination
- `GET /articles/{id}` - Get single article
- `POST /articles` - Create article
- `PUT /articles/{id}` - Update article
- `DELETE /articles/{id}` - Delete article

**Authentication:**
- `GET /login/oauth2/authorization/google` - Google OAuth login

See `openapi.yaml` for full API documentation.

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Frontend** | Flutter Web, Dart |
| **Backend** | Spring Boot, Java |
| **Authentication** | Google OAuth 2.0 |
| **Containerization** | Docker, Docker Compose |
| **Cloud** | Google Cloud Platform |
| **Web Server** | Nginx |
| **Build Tool** | Maven |

## 📚 Development

### Running Individual Services

**Backend only:**
```bash
cd bloogy_backend/bloogy_backend
docker-compose up --build
```
API: http://localhost:18081/api/v1/articles/pagination?pageSize=5

**Frontend only (if backend is running):**
```bash
cd bloogy_frontend
docker build -t bloogy-frontend .
docker run -p 18080:80 --network bloogy_bloogy-network bloogy-frontend
```

## 📖 Notes

- Initial Flutter build may take 5-10 minutes on first run
- Google Cloud credentials required for authentication
- Both services communicate via internal Docker network in production
- OpenAPI/Swagger documentation available at `/api/v1/swagger-ui.html`

## 📄 License

MIT License - Feel free to use this project for learning purposes.

---

**Author:** Aykut Cihan Demir  
**GitHub:** https://github.com/aykutcihan/bloogy
