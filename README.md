

```markdown
# 🌾 Hinga Backend


> A robust backend system for managing smart agriculture operations — covering everything from weather forecasts to crop, livestock, farmer, and product management.

---

## 📌 Features

✅ Farm Management System 
✅ Real-time Weather Forecasts (Multi-location)  
✅ Crop & Livestock Management  
✅ Farmer & Finance Tracking  
✅ Secure Role-Based Access  
✅ Modular & Scalable Spring Boot Structure  
✅ RESTful API Design
✅ Spring Security and Mvc

---

## 📂 Project Structure


src/
├── config/               # Security and general app config
├── controllers/          # REST API controllers
├── Constants/            # Enum and Constants
├── dto/                  # Data transfer objects
├── filter/               # Security filters
├── models/               # JPA entities (Crops, Livestock, Farmers, etc.)
├── repository/           # Data access layer
├── services/             # Business logic layer
└── HingaBackendApplication.java
uploads/                  #it Contains uploaded images to the server   
```

---

## 📦 Modules

| Module      | Description                                  |
|-------------|----------------------------------------------|
| 🌿 **Crops**     | Manage crop types, records, and details     |
| 🐄 **Livestock** | Track animals, breed data, and health logs  |
| 👨‍🌾 **Farmers**   | Create & manage farmer accounts & profiles |
| 📦 **Products**  | Register tools, fertilizers, seeds, etc.    |
| 🌦️ **Weather**   | Multi-location forecasts using external API |
| 🧺 **Farm**      | Create & Manage Farms adding crops and more |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 | 20
- Maven 3.8+
- PostgreSQL
- Weather API Key (e.g., OpenWeatherMap)
- Google Developer Console OAuth2 credentials

### Run Locally

```bash
git clone https://github.com/aine1100/Hinga_Backend.git
cd farmMis
```

### Setup

1. Configure `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hinga_db
spring.datasource.username=your_username
spring.datasource.password=yourpassword
weather.api.key=your-weather-api-key
```

2. Run the app:

```bash
./mvnw spring-boot:run
```

---

## 🌍 API Reference

Once running, access your endpoints via:
```
http://localhost:8080/api/
```

API Docs (Swagger coming soon!)

---

## 🛡 Security

- Role-based access control via Spring Security
- JWT (if implemented) for session management

---



## 📃 License

This project is licensed under the **MIT License**.

---

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you'd like to change.

---

## 🙌 Acknowledgements

- Spring Boot Docs  
- OpenWeatherMap API   
- Google Auth

---

### 🔗 Related Projects

- [Hinga Frontend (React)](https://github.com/aine1100/agri-navigator-platform)


Made by Dushimire Aine ❤️
