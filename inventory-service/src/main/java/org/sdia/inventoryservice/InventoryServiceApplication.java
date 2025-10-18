package org.sdia.inventoryservice;

import org.sdia.inventoryservice.Repository.ProductRepository;
import org.sdia.inventoryservice.entities.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(ProductRepository productRepository) {
		return args -> {
		productRepository.save(Product.builder()
				.id(UUID.randomUUID().toString())
				.name("computer")
				.price(3200)
				.quantity(50)
				.build());
		productRepository.save(Product.builder()
				.id(UUID.randomUUID().toString())
				.name("tv")
				.price(320)
				.quantity(5)
				.build());
	productRepository.save(Product.builder()
			.id(UUID.randomUUID().toString())
			.name("telephone")
				.price(1100)
				.quantity(10)
				.build());

		};

	}
}
