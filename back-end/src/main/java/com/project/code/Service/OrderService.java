package com.project.code.Service;


import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;

    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest){
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        Store store = storeRepository.findById(placeOrderRequest.getStoreId().longValue());
        if (store == null) {
            throw new RuntimeException("no store");
        }

        Double totalPrice = placeOrderRequest.getTotalPrice();
        LocalDateTime date = LocalDateTime.now();

        OrderDetails orderDetails = new OrderDetails(customer, store, totalPrice, date);
        List<OrderItem> orderItems = new ArrayList<OrderItem>();

        for (PurchaseProductDTO purchaseProduct : placeOrderRequest.getPurchaseProduct()) {
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(
                    purchaseProduct.getId(),
                    placeOrderRequest.getStoreId());
            inventory.setStockLevel(inventory.getStockLevel() - purchaseProduct.getQuantity());
            inventoryRepository.save(inventory);

            Product product = productRepository.findById(purchaseProduct.getId().longValue());
            OrderItem orderItem = new OrderItem(
                    orderDetails,
                    product,
                    purchaseProduct.getQuantity(),
                    purchaseProduct.getPrice());
            orderItem = orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

        orderDetails.setOrderItems(orderItems);
        orderDetailsRepository.save(orderDetails);
    }

}
