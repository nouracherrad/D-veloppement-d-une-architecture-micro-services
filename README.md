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










# Création du service de configuration  

### 1. Repository de Configuration Git
**URL :** `https://github.com/nouracherrad/config-ecom-app`

**Fichiers créés :**
```
config-ecom-app/
├── application.properties           (Configuration globale)
├── billing-service.properties       (Configuration spécifique billing)
├── customer-service.properties      (Configuration spécifique customer)  
├── inventory-service.properties     (Configuration spécifique inventory)
├── billing-service-dev.properties   (Configuration dev billing)
├── billing-service-prod.properties  (Configuration prod billing)
├── customer-service-dev.properties  (Configuration dev customer)
├── customer-service-prod.properties (Configuration prod customer)
├── inventory-service-dev.properties (Configuration dev inventory)
└── inventory-service-prod.properties (Configuration prod inventory)
```

### 2. Microservice Config-Service

**Structure :**
```
config-service/
├── src/main/java/org/sdia/configservice/
│   └── ConfigServiceApplication.java
└── src/main/resources/
    └── application.properties
```

**Configuration principale :** `config-service/src/main/resources/application.properties`
```properties
spring.application.name=config-service
server.port=9999
spring.cloud.config.server.git.uri=https://github.com/nouracherrad/config-ecom-app
```

**Code Java :** `ConfigServiceApplication.java`
```java
package org.sdia.configservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
```

## 🔄 Comment configurer vos microservices clients


### Contenu des fichiers de configuration

**Fichier global :** `config-repo/application.properties`
```properties
global.params.p1=555
global.params.p2=777
spring.h2.console.enabled=true
spring.cloud.discovery.enabled= true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
management.endpoints.web.exposure.exclude=*

```

# Sécurité et actualisation 
management.endpoints.web.exposure.include=health,info , refresh 
```
pour que le configuration sera à jour aprés chaque modification

## 🚀 Démarrage et Test


###  Vérifier que le service fonctionne
```bash
# dans le navigateur
 http://localhost:9999/billing-service/default

```
<img width="1407" height="236" alt="image" src="https://github.com/user-attachments/assets/9bf16f21-77b1-4bec-8d3f-31f0d8100038" />


```
Fetching config from server at : http://localhost:9999
Located environment: name=billing-service, profiles=[dev]
```
<img width="887" height="867" alt="image" src="https://github.com/user-attachments/assets/9b70b24a-54d4-415f-9872-49d049a731cd" />


test sur le microservice  : qu'il fonctionne bien
```
http://www.localhost:8888/CUSTOMER-SERVICE/api/customers
```

<img width="897" height="873" alt="image" src="https://github.com/user-attachments/assets/087c6ee1-7d78-473a-a5e4-7f24c9fb181b" />

##  Endpoints du Config Service


**Exemples :**
```
http://localhost:9999/billing-service/dev
http://localhost:9999/customer-service/prod
http://localhost:9999/inventory-service/dev
http://localhost:9999/application/default
```


##  Avantages Obtenus

-  **Configuration centralisée** : Tous les paramètres au même endroit
-  **Versioning** : Historique complet via Git
-  **Environnements multiples** : Dev, Prod, Test
-  **Déploiement flexible** : Changement de config sans rebuild
-  **Sécurité** : Configuration sensible externalisée
-  **Consistance** : Tous les services utilisent la même source



