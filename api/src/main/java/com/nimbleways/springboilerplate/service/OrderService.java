package com.nimbleways.springboilerplate.service;

import com.nimbleways.springboilerplate.entities.Product;

import java.util.Set;

public interface OrderService {
    void processProducts(Set<Product> products);
}
