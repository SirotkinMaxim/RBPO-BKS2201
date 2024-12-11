package ru.mtuci.praktikaRBPO.controller;

import ru.mtuci.praktikaRBPO.dto.ProductAddRequest;
import ru.mtuci.praktikaRBPO.model.Product;
import ru.mtuci.praktikaRBPO.repository.ProductRepository;
import ru.mtuci.praktikaRBPO.services.LicenseService;
import ru.mtuci.praktikaRBPO.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final LicenseService licenseService;
    private final ProductRepository productRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody ProductAddRequest productAddRequest) {
        try {
            Product createdProduct = productService.addProduct(productAddRequest.getName(),productAddRequest.getBlocked());
            return ResponseEntity.ok("Product создан с ID: " + createdProduct.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeProduct(@PathVariable Long id) {
        try {
            if (licenseService.existsByProductId(id)) {
                return ResponseEntity.badRequest().body("Невозможно удалить Product.");
            }
            productService.deleteById(id);
            return ResponseEntity.ok("Product удалён.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
