package org.sdia.billingservice.feign;

import org.sdia.billingservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name="CUSTOMER-SERVICE")
public interface CustomerRestClient {
    @GetMapping("api/customers/{id}")
    Customer findCustomerById(@PathVariable Long id);
    @GetMapping("api/customers")
    PagedModel<Customer> getAllCustomers();


}
