package com.vish.pms.service.serviceimpl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vish.pms.entity.Product;
import com.vish.pms.exception.ProductNotFoundException;
import com.vish.pms.repository.ProductRepository;
import com.vish.pms.service.CrudService;

@Service
public class ProductService implements CrudService<Product, UUID> {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(Product entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return productRepository.save(entity);
    }

    @Override
    public void deleteById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product getByID(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product update(UUID id, Product entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // update fields
        existing.setName(entity.getName());
        existing.setPrice(entity.getPrice());
        existing.setDescription(entity.getDescription());

        return productRepository.save(existing);
    }

    public List<Product> paginatedSorted(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(pageable).getContent();
    }

    public List<Product> createAll(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty");
        }
        return productRepository.saveAll(products);
    }
}
