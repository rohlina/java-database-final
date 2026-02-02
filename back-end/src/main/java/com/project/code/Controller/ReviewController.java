package com.project.code.Controller;

import com.project.code.Model.Customer;
import com.project.code.Model.Product;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews (@PathVariable long storeId, @PathVariable long productId) {
        Map<String, Object> result = new HashMap<String, Object>();

        List<Review> allReviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
        List<Map<String, Object>> reviews = new ArrayList<>();
        for (Review review : allReviews) {
            Customer customer = customerRepository.findById(review.getCustomerId().longValue());
            String name;
            if (customer != null) {
                name = customer.getName();
            } else {
                name = "Unknown";
            }

            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("review", review.getComment());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("customerName",name);

            reviews.add(reviewMap);
        }

        result.put("reviews", reviews);
        return result;
    }

    @GetMapping
    public Map<String,Object> getAllReviews()
    {
        Map<String,Object> map=new HashMap<>();
        map.put("reviews",reviewRepository.findAll());
        return map;
    }
}
