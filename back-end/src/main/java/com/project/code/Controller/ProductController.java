package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ServiceClass serviceClass; //= new ServiceClass(inventoryRepository,productRepository);

    @PostMapping
    public Map<String, String> addProduct (@RequestBody Product product) {
        Map<String, String> message = new HashMap<String, String>();

        if(!serviceClass.validateProduct(product)) {
            message.put("message", "The product is already in the database");
        }

        try {
            productRepository.save(product);
            message.put("message", "Product added successfully");
        } catch (DataIntegrityViolationException e) {
            message.put("message", "SKU must be unique");
        }

        return message;
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId (@PathVariable long id) {
        Map<String, Object> result = new HashMap<>();
        Product products = productRepository.findById(id);
        result.put("products", products);
        return result;
    }

    @PutMapping("/updateProduct")
    public Map<String, String> updateProduct (@RequestBody Product product) {
        Map<String, String> message = new HashMap<String, String>();
        if(serviceClass.validateProductId(product.getId())) {
            message.put("message", "No data available");
        }
        try {
            productRepository.save(product);
            message.put("message", "Product updated successfully");
        } catch (Error e) {
            message.put("message", e.getMessage());
        }

        return message;
    }


    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct (@PathVariable String name, @PathVariable String category) {
        Map<String, Object> result = new HashMap<>();

        List<Product> products;
        if (category == null && name == null) {
            products = productRepository.findAll();
            
        } else if (name == null)  {
            products = productRepository.findByCategory(category);
        } else if (category == null) {
            products = productRepository.findProductBySubName(name);
        } else{
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        result.put("products", products);
        return result;
    }

    @GetMapping("/")
    public Map<String, Object> listProduct () {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Product> products = productRepository.findAll();
        result.put("products", products);
        return result;
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId (@PathVariable String category, @PathVariable long storeId) {
        Map<String, Object> result = new HashMap<String, Object>();

        List<Product> products = productRepository.findProductByCategory(category, storeId);

        result.put("products", products);
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct (@PathVariable long id) {
        Map<String, String> message = new HashMap<String, String>();
        if(serviceClass.validateProductId(id)) {
            message.put("message", "Product was not found in the Database");
        } else {
            inventoryRepository.deleteByProductId(id);

            productRepository.deleteById(id);
            message.put("message", "The product has been removed");
        }
        return message;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct (@PathVariable String name, @PathVariable long storeId) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Product> products = productRepository.findProductBySubName(name);
        result.put("product", products);
        return result;
    }

}
