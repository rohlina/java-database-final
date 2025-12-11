package com.project.code.Service;


import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository ;
    private final ProductRepository productRepository;

    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    private boolean validateInventory(Inventory inventory) {

        Inventory inv = inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(),inventory.getStore().getId());
        return inv == null;
    }

    private boolean validateProduct(Product product) {

        Product p = productRepository.findByName(product.getName());
        return p == null;
    }

    private boolean validateProductId(long id) {

        Product p = productRepository.findById(id);
        return p == null;
    }

    private Inventory getInventoryId(Inventory inventory) {

        return inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(),inventory.getStore().getId());
    }

}
