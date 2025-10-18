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
  








### **5. Billing Service**
**Port:** 8083  
**Responsabilité:** Gestion des factures et des éléments de facturation

**Structure:**
```
billing-service/
├── src/main/java/org/sdia/billingservice/
│   ├── entities/
│   │   ├── Bill.java
│   │   └── ProductItem.java
│   ├── feign/
│   │   ├── CustomerRestClient.java
│   │   └── ProductRestClient.java
│   ├── model/
│   │   ├── Customer.java
│   │   └── Product.java
│   ├── repository/
│   │   ├── BillRepository.java
│   │   └── ProductItemRepository.java
│   ├── web/
│   │   └── BillRestController.java
│   └── BillingServiceApplication.java
└── resources/
    └── application.properties
```

## ⚙️ **Configuration du Billing Service**

**Fichier :** `application.properties`
```properties
spring.application.name=billing-service
server.port=8083
spring.datasource.url=jdbc:h2:mem:bills-db
spring.h2.console.enabled=true
spring.cloud.discovery.enabled=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
spring.cloud.config.enabled=false
spring.data.rest.base-path=/api
management.endpoints.web.exposure.exclude=*
```

##  **Communication Entre Microservices**

### **Feign Clients Implémentés**

#### **ProductRestClient :**
```java
@FeignClient(name="INVENTORY-SERVICE")
public interface ProductRestClient {
    @GetMapping("/api/products/{id}")
    public Product getProductById(@PathVariable String id);
    
    @GetMapping("/api/products")
    PagedModel<Product> getAllProducts();
}
```

#### **CustomerRestClient :**
```java
@FeignClient(name="CUSTOMER-SERVICE") 
public interface CustomerRestClient {
    @GetMapping("/api/customers/{id}")
    public Customer findCustomerById(@PathVariable Long id);
}
```

### **Controller Principal**

**BillRestController :**
```java
@RestController
public class BillRestController {
    @Autowired
    private BillRepository billRepository;
    
    @GetMapping("/bills/{id}")
    public Bill getBill(Long id){
        Bill bill = billRepository.findById(id).get();
        // Appel aux autres microservices
        bill.setCustomer(customerRestClient.findCustomerById(bill.getCustomerId()));
        bill.getProductItems().forEach(productItem -> {
            productItem.setProduct(productRestClient.getProductById(productItem.getProductId()));
        });
        return bill;
    }
}
```

## 🗃️ **Modèles de Données**

### **Entité Bill (Facture) :**
```java
@Entity
public class Bill {
    private Long id;
    private Date billingDate;
    private Long customerId;
    private Collection<ProductItem> productItems;
}
```

### **Entité ProductItem (Ligne de facture) :**
```java
@Entity
public class ProductItem {
    private Long id;
    private Long productId;
    private double price;
    private int quantity;
    private Bill bill;
}
```





### **Exemple : Récupération d'une Facture**
1. **GET** `/api/bills/1` via Gateway (8888)

<img width="915" height="400" alt="image" src="https://github.com/user-attachments/assets/59a2f384-f4f6-45c6-a329-93c570521810" />

3. **Billing Service** reçoit la requête
4. **Appel à Customer Service** pour les infos client
5. **Appel à Inventory Service** pour les détails produits
6. **Aggrégation** des données dans la réponse

<img width="905" height="805" alt="image" src="https://github.com/user-attachments/assets/7c71518b-5958-4641-91b5-742c5654b6a2" />


### **Avec le Billing Service :**
-  **Gestion complète des factures**
-  **Communication inter-services** avec Feign
-  **Aggrégation de données** depuis multiples sources
-  **API REST** pour les opérations de facturation

##  **Technologies Additionnelles**
- **HATEOAS** : Hypermedia pour les APIs



