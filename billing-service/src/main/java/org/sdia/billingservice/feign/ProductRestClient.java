package org.sdia.billingservice.feign;

import org.sdia.billingservice.model.Customer;
import org.sdia.billingservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="INVENTORY-SERVICE")
public interface ProductRestClient {
    @GetMapping("api/products/{id}")
    public Product getProductById(@PathVariable String id);
    @GetMapping("api/products")
    PagedModel<Product> getAllProducts();

}
