package com.project.code.Controller;

import com.project.code.Model.*;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    OrderService orderService;

    @PostMapping
    public Map<String, String> addStore (@RequestBody Store store) {
        Map<String, String> message = new HashMap<String, String>();
        try{
            Store savedStore = storeRepository.save(store);
            message.put("message", "Store added successfully id: " + savedStore.getId());
        } catch (DataIntegrityViolationException e) {
            message.put("message", e.getMessage());
        }

        return message;
    }

    @GetMapping("validate/{storeId}")
    public boolean validateStore (@PathVariable long storeId) {

        Store store = storeRepository.findById(storeId);

        return store != null;
    }

    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder (@RequestBody  PlaceOrderRequestDTO placeOrderRequestDTO) {

        Map<String, String> message = new HashMap<String, String>();
        try{
            orderService.saveOrder(placeOrderRequestDTO);
            message.put("message", "order is successfully placed");
        } catch (Exception e) {
            message.put("error", e.getMessage());
        }
        return message;
    }

}
