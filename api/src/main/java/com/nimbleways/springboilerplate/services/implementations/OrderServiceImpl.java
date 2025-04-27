package com.nimbleways.springboilerplate.services.implementations;


import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.service.OrderService;

import java.time.LocalDate;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final ProductRepository productRepository;


    public OrderServiceImpl(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    public void processProducts(Set<Product> products) {
        for (Product product : products) {
            processProduct(product);
        }
    }

    private void processProduct(Product product) {
        ProductType productType = product.getType();

        switch (productType) {
            case ProductType.NORMAL:
                processNormalProduct(product);
                break;
            case ProductType.SEASONAL:
                processSeasonalProduct(product);
                break;
            case ProductType.EXPIRABLE:
                processExpirableProduct(product);
                break;
            default:
                throw new IllegalArgumentException("Unknown product type: " + product.getType());
                break;
        }
    }

    private void processNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            updateProductAvailability(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                productService.notifyDelay(leadTime, product);
            }
        }
    }

    private void processSeasonalProduct(Product product) {
        LocalDate now = LocalDate.now();
        boolean isInSeason = now.isAfter(product.getSeasonStartDate()) &&
                now.isBefore(product.getSeasonEndDate());

        if (isInSeason && product.getAvailable() > 0) {
            updateProductAvailability(product);
        } else {
            productService.handleSeasonalProduct(product);
        }
    }

    private void processExpirableProduct(Product product) {
        productService.handleExpiredProduct(product);
    }

    private void updateProductAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
