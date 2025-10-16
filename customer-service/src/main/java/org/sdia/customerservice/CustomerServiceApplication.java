package org.sdia.customerservice;

import org.sdia.customerservice.entities.Customer;
import org.sdia.customerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
		return args -> {
			customerRepository.save(new Customer(null, "mohamed", "Zt6yH@example.com"));
			customerRepository.save(new Customer(null, "mohamed", "Zt6yH@example.com"));
			customerRepository.save(new Customer(null, "mohamed", "Zt6yH@example.com"));
			customerRepository.save(new Customer(null, "mohamed", "Zt6yH@example.com"));
			customerRepository.save(new Customer(null, "mohamed", "Zt6yH@example.com"));
};}}
