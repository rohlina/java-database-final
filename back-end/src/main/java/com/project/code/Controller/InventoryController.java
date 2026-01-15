package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ServiceClass serviceClass = new ServiceClass(inventoryRepository,productRepository);

    @PutMapping("/updateInventory")
    public Map<String, String> updateInventory (CombinedRequest combinedRequest) {
        if(!serviceClass.validateProductId(combinedRequest.getProduct().getId())) {
           throw new DataIntegrityViolationException("No data available");
        }
        productRepository.save(combinedRequest.getProduct());

        Map<String, String> message = new HashMap<String, String>();
        if (serviceClass.validateInventory(combinedRequest.getInventory())) {
            inventoryRepository.save(combinedRequest.getInventory());
            message.put("message", "Product updated successfully");
        } else {
            message.put("message", "No data available");
        }

        return message;
    }

    @PostMapping("/saveInventory")
    public Map<String, String> saveInventory (Inventory inventory) {
        Map<String, String> message = new HashMap<String, String>();
        if(!serviceClass.validateInventory(inventory)) {
            message.put("message", "Inventory is already exist");
        } else {
            inventoryRepository.save(inventory);
            message.put("message", "Inventory saved successfully");
        }
        return message;
    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts (@PathVariable long storeId) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Product> products = productRepository.findProductsByStoreId(storeId);
        result.put("products", products);
        return result;
    }


    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName (@PathVariable String category, @PathVariable String name, @PathVariable long storeId) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Product> products;

        if (category == null) {
            products = productRepository.findByNameLike(storeId, name);
        } else if (name == null)  {
            products = productRepository.findByCategoryAndStoreId(storeId, category);
        } else {
            products = productRepository.findByNameAndCategory(storeId, name, category);
        }

        result.put("product", products);
        return result;
    }

    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct (@PathVariable String name, @PathVariable long storeId) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Product> products = productRepository.findByNameLike(storeId, name);
        result.put("product", products);
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct (@PathVariable long id) {
        Map<String, String> message = new HashMap<String, String>();
        if(serviceClass.validateProductId(id)) {
            message.put("message", "Product was not found in the Database");
        } else {
            inventoryRepository.deleteByProductId(id);
            message.put("message", "The product has been removed");
        }
        return message;
    }


    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity (@PathVariable long quantity, @PathVariable long storeId, @PathVariable long productId) {

        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);

        return inventory.getStockLevel() >= quantity;
    }

}
