# Développement d'une architecture micro-services
### **📁 Structure du Projet Créée**
```
ecom-sdia-app/
├── customer-service/          
├── inventory-service/        
├── discovery-service/         
└── gateway-service/          
```

### ** Détails Techniques Implémentés**

#### **1. Service de Découverte (Eureka)**
**Fichier :** `discovery-service/src/main/resources/application.properties`
```properties
spring.application.name=discovery-server
server.port=8761
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
```

**Ce que ça fait :**
- Lance un serveur Eureka sur le port **8761**
- Sert d'annuaire pour tous vos microservices
- Les autres services viennent s'y enregistrer automatiquement

#### **2. Customer Service**
**Structure créée :**
```java
// Entité Customer
public class Customer {
    private Long id;
    private String name;
    private String email;
}

// Repository Spring Data
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

// Configuration REST
@Configuration
public class RestRepositoryConfig {
    // Exposition automatique des APIs REST
}
```

#### **3. Inventory Service**  
**Structure similaire :**
```java
// Entité Product  
public class Product {
    private Long id;
    private String name;
    private double price;
    private int quantity;
}
```

#### **4. Gateway Service**
**Configuration principale :**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: r1
          uri: lb://CUSTOMER-SERVICE
          predicates:
            - Path=/api/customers/**
        - id: r2
          uri: lb://INVENTORY-SERVICE  
          predicates:
            - Path=/api/products/**
```

## 🚀 **Comment Tester **

### **Démarrage Séquentiel :**
1. **Démarrer Eureka :**
   ```bash
   cd discovery-service
   mvn spring-boot:run
   ```
   → Vérifiez sur http://localhost:8761

   <img width="887" height="817" alt="image" src="https://github.com/user-attachments/assets/c3825905-32a9-4cfe-9591-04a3c681c0b6" />


3. **Démarrer Customer Service :**
   ```bash
   cd customer-service
   mvn spring-boot:run
   ```
   → S'enregistre automatiquement dans Eureka

4. **Démarrer Inventory Service :**
   ```bash
   cd inventory-service  
   mvn spring-boot:run
   ```

5. **Démarrer Gateway :**
   ```bash
   cd gateway-service
   mvn spring-boot:run
   ```

### **Tests des APIs :**
```bash
# Via la Gateway
GET http://localhost:8888/api/customers
GET http://localhost:8888/api/products

# Créer un client
POST http://localhost:8888/api/customers
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

## 🔄 **Flux des Requêtes Dans Votre Architecture**

### **Exemple : Accéder aux Clients**
1. **Requête utilisateur :**
   ```
   GET http://localhost:8888/api/customers
   ```

2. **Traitement par la Gateway :**
   - Reçoit la requête sur le port 8888
   - Reconnaît le pattern `/api/customers/**`
   - Consulte Eureka : "Où est CUSTOMER-SERVICE ?"
  
<img width="857" height="752" alt="image" src="https://github.com/user-attachments/assets/32c3f94f-c499-4481-affc-61b45006f308" />


3. **Eureka répond :**
   - "CUSTOMER-SERVICE est sur http://192.168.1.x:8081"

4. **Gateway route vers :**
   - `http://192.168.1.x:8081/customers`
   - Renvoie la réponse au client

