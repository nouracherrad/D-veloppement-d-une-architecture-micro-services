package org.sdia.customerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.apache.naming.SelectorContext.prefix;

@ConfigurationProperties(prefix="customer.params")
public record CustomerConfigParams(int x , int y) {
}
