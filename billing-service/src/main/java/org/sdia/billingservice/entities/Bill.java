package org.sdia.billingservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.sdia.billingservice.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
@Entity
@NoArgsConstructor
@AllArgsConstructor @Getter @Setter
@Builder
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date billingDate;
    private Long customerId;
    @OneToMany(mappedBy = "bill")
    private List<ProductItem> productItems=new ArrayList<>();
    @Transient
    Customer customer;
}
