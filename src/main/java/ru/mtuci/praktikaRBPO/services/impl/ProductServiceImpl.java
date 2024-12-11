package ru.mtuci.praktikaRBPO.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.Product;
import ru.mtuci.praktikaRBPO.repository.ProductRepository;
import ru.mtuci.praktikaRBPO.services.ProductService;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Продукт с ID " + id + " не найден"));
    }

    @Override
    public Product addProduct(String name,Boolean blocked) {
        if (productRepository.existsByName(name)) {
            throw new IllegalArgumentException("Продукт с таким именем уже существует");
        }
        Product product = new Product();
        product.setName(name);
        product.setBlocked(blocked);
        return productRepository.save(product);
    }
}
