package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.model.Product;

public interface ProductService {
    void deleteById(Long id);
    Product addProduct(String name,Boolean blocked);
    Product getProductById(Long id);
    Product renameProduct(Long id, String newName);
}
