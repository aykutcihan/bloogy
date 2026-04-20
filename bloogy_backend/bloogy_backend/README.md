# Bloogy Project

## Overview
Bloogy is a blog management system built using Spring Boot for the backend and Flutter for the frontend. The project integrates Google Cloud Platform (GCP) services like Firestore and Pub/Sub for seamless data storage and messaging.

## Features
- Create, read, update, and delete (CRUD) operations for articles.
- Feedback management for articles.
- Google OAuth authentication.
- Integration with GCP services such as Firestore and Pub/Sub.

## Technologies Used
- **Backend**: Spring Boot (2.7.5)
- **Frontend**: Flutter
- **Database**: Google Firestore
- **Authentication**: Google OAuth
- **Messaging**: Google Pub/Sub
- **Build Tools**: Maven

## Project Structure
### Backend
The backend is a Spring Boot application with a modular structure:
- `controller`: Handles HTTP requests.
- `service`: Contains business logic.
- `repository`: Manages database operations.
- `payload`: Defines request and response DTOs.
- `model`: Represents database entities.

### Frontend
The frontend is built with Flutter and follows a simple modular approach:
- **Pages**: Separate files for different screens such as login, article list, article details, and feedback management.
- **Services**: Handles API calls to the backend.

## Setup Instructions
### Backend
1. Clone the repository.
2. Navigate to the backend directory.
3. Add your GCP credentials JSON file to the project and configure `application.properties`:
    ```properties
    spring.cloud.gcp.firestore.enabled=true
    spring.cloud.gcp.project-id=your-project-id
    spring.cloud.gcp.credentials.location=file:/path-to-json-file
    ```
4. Build and run the application:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
5. Access the backend API at `http://localhost:8080`.

### Frontend
1. Install Flutter by following the official [Flutter installation guide](https://flutter.dev/docs/get-started/install).
2. Navigate to the frontend directory.
3. Run the following commands:
    ```bash
    flutter pub get
    flutter run
    ```
4. Use the app on an emulator or physical device.

## Challenges and Solutions
### Firestore Integration
- **Issue**: Difficulty in connecting Spring Boot with Firestore.
- **Solution**: Used a custom repository implementation instead of `FirestoreReactiveRepository` and configured GCP credentials correctly.

### Port Conflicts
- **Issue**: Encountered port conflicts while running the application.
- **Solution**: Identified and terminated conflicting processes, cleaned Maven cache, and invalidated IntelliJ caches.

### Firebase Emulator Issues
- **Issue**: Some annotations did not work while using Firebase Emulator.
- **Solution**: Shifted to a custom repository implementation for Firestore.

### Google OAuth and JWT
- **Issue**: Challenges with managing Google OAuth and securing endpoints.
- **Solution**: Made APIs public temporarily for testing and resolved compatibility issues by downgrading Spring Boot to 2.7.5.

### Deployment Challenges
- **Issue**: Dependency conflicts during deployment.
- **Solutions Tried**:
    - Cleared corrupted dependencies using `mvn dependency:purge-local-repository`.
    - Updated and manually added `google-cloud-dependencies`.
    - Forced Maven to redownload dependencies with `mvn clean install -U`.

## Future Improvements
- Add role-based access control for users.
- Enhance error handling and logging.
- Deploy the application to a cloud environment (e.g., GCP App Engine or AWS).

## Contact
If you have questions or suggestions, feel free to reach out.

---
**Happy coding!** 🎉
