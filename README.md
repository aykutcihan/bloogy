# Bloogy - Calistirma Rehberi

## Port Yapisi
- **localhost:18080** → Frontend (Flutter + nginx)
- **localhost:18081** → Backend (Spring Boot) - direkt erisim

---

## Nasil Calistirilir?

### Yontem 1: FE + BE birlikte (TAVSIYE EDILEN)
```bash
# Bu klasorde (bloogy/) calistir:
docker-compose up --build
```
Tarayicida: http://localhost:18080

---

### Yontem 2: Sadece BE calistir (test icin)
```bash
cd bloogy_backend/bloogy_backend
docker-compose up --build
```
BE direkt: http://localhost:18081/api/v1/articles/pagination?pageSize=5

---

### Yontem 3: Sadece FE calistir (BE zaten calisiyorsa)
```bash
cd bloogy_frontend
docker build -t bloogy-frontend .
docker run -p 18080:80 --network bloogy_bloogy-network bloogy-frontend
```

---

## Onemli Notlar

1. **Google Cloud Console** - Authorized redirect URIs listesinde su olduğunu kontrol et:
   ```
   http://localhost:18080/login/oauth2/code/google
   ```

2. **BE tek basina calisinca** FE acilmaz (artik static klasor bos), 
   sadece API endpoint'leri cevap verir.

3. **Ilk calistirmada** Flutter build uzun surebilir (5-10 dk), bu normal.

---

## Klasor Yapisi
```
bloogy/
├── docker-compose.yml          ← FE+BE birlikte calistirmak icin
├── bloogy_backend/
│   └── bloogy_backend/
│       ├── docker-compose.yml  ← Sadece BE icin
│       ├── .env                ← Google OAuth ve GCP ayarlari
│       └── src/...
└── bloogy_frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── lib/...
```
