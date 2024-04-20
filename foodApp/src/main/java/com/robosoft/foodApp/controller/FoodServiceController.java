package com.robosoft.foodApp.controller;


import com.robosoft.foodApp.entity.*;

import com.robosoft.foodApp.response.CartItems;
import com.robosoft.foodApp.response.CategoryResponse;
import com.robosoft.foodApp.response.Filter;
import com.robosoft.foodApp.response.FilterResponse;
import com.robosoft.foodApp.service.FoodServiceImpl;
import com.robosoft.foodApp.service.SmsService;
import com.robosoft.foodApp.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/food")
public class FoodServiceController {
    @Autowired
    FoodServiceImpl foodService;
    @Autowired
    SmsService smsService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTUtility jwtUtility;

    @PostMapping("/verifyNumber")
    public ResponseEntity<?> verifyNumber(@RequestBody MobileNumberVerification mobileNumberVerification) {
        return new ResponseEntity<>(foodService.verifyNumber(mobileNumberVerification), HttpStatus.CREATED);
    }

    @PutMapping("/user/send2faCodeInSMS/{mobNo}")
    public ResponseEntity<Object> send2faCodeInSMS(@RequestBody String mobileNumber, @PathVariable String mobNo) {
        System.out.println(mobileNumber);
        String tfaCode = String.valueOf(new Random().nextInt(9999) + 1000);
        smsService.sendSms(mobileNumber, tfaCode);
        foodService.update2FAProperties(mobNo, tfaCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/verify")
    public ResponseEntity<Object> verify(@RequestParam String mobileNumber, @RequestParam String code) {
        boolean isValid = foodService.checkCode(mobileNumber, code);
        if (isValid) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/userRegister")
    public ResponseEntity<?> userRegister(@RequestBody User user) {
        String check = foodService.userRegister(user);
        if (check.equalsIgnoreCase("user registered successfully"))
            return new ResponseEntity<>(check, HttpStatus.CREATED);
        else if (check.equalsIgnoreCase("registration failed")) {
            return new ResponseEntity<>(check, HttpStatus.NOT_FOUND);
        } else
            return new ResponseEntity<>(check, HttpStatus.NOT_FOUND);
    }


    @PutMapping("/user/updatePassword/{phoneNumber}")
    public ResponseEntity<Object> updatePassword(@RequestBody String mobileNumber, @PathVariable String phoneNumber) {
        String tfaCode = String.valueOf(new Random().nextInt(9999) + 1000);
        smsService.sendSms(mobileNumber, tfaCode);
        foodService.forgotPassword(phoneNumber, tfaCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<Object> verify(@RequestParam String mobileNumber, @RequestParam String code, @RequestParam String password) {
        boolean isValid = foodService.updatePassword(mobileNumber, code, password);
        if (isValid) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    @PostMapping("/authenticate")
    public JWTResponse authenticate(@RequestBody JWTRequest jwtRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getEmail(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = foodService.loadUserByUsername(jwtRequest.getEmail());

        final String token = jwtUtility.generateToken(userDetails);

        return new JWTResponse(token);
    }

    @PostMapping("/addRestaurants")
    public ResponseEntity<?> addRestaurants(@ModelAttribute Restaurant restaurant) {
        try {
            return new ResponseEntity<>(foodService.addRestaurants(restaurant), HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("registration failed", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/viewRestaurants")
    public ResponseEntity<List<Restaurant>> viewRestaurants() {
        if (foodService.viewRestaurants().size() > 0) {
            return new ResponseEntity<>(foodService.viewRestaurants(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/viewRestaurantsByName/{name}")
    public ResponseEntity<List<Restaurant>> viewRestaurantsByName(@PathVariable String name) {
        if (foodService.viewRestaurantsByName(name) != null) {
            return new ResponseEntity<>(foodService.viewRestaurantsByName(name), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addMenu")
    public ResponseEntity<String> addMenu(@ModelAttribute Menu menu) {
        String check = foodService.addMenu(menu);
        if (check.equalsIgnoreCase("dish added")) {

            return new ResponseEntity<>(check, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(check, HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/viewMenus")
    public ResponseEntity<List<Menu>> viewMenu() {
        if (foodService.viewMenu() != null) {
            return new ResponseEntity<>(foodService.viewMenu(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

    }


    @GetMapping("/restaurantImageUrl/{rId}")
    public ResponseEntity<String> getRestaurantImageUrl(@PathVariable int rId) {
        if (foodService.getRestaurantImageUrl(rId) != null) {
            return new ResponseEntity<>(foodService.getRestaurantImageUrl(rId), HttpStatus.OK);
        }
        return new ResponseEntity<>("empty image", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/restaurantImage/{rId}")
    public ResponseEntity<Resource> getRestaurantImage(@PathVariable int rId) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png")).header("Content-Disposition", "filename=\"" + rId + ".png" + "\"").body(new ByteArrayResource(foodService.restaurantImage(rId)));
    }


    @GetMapping("/imageUrl")
    public ResponseEntity<String> getImageUrl(@RequestParam String dishName) {
        if (foodService.getImageUrl(dishName) != null) {
            return new ResponseEntity<>(foodService.getImageUrl(dishName), HttpStatus.OK);
        }
        return new ResponseEntity<>("empty image", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/image/{dishName}")
    public ResponseEntity<Resource> getImageLogo(@PathVariable String dishName) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png")).header("Content-Disposition", "filename=\"" + dishName + ".png" + "\"").body(new ByteArrayResource(foodService.dishImage(dishName)));
    }

    @PostMapping("/addRatings")
    public ResponseEntity<?> addRatings(@RequestBody Ratings ratings) {
        String query = foodService.addRatings(ratings);
        if (query.equalsIgnoreCase("rated successfully")) {
            return new ResponseEntity<>(query, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(query, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/viewMenuByRestaurantId/{id}")
    public ResponseEntity<List<Menu>> viewMenuByRestaurantId(@PathVariable int id) {
        if (foodService.viewMenuByRestaurantId(id) != null) {
            return new ResponseEntity<>(foodService.viewMenuByRestaurantId(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addToFavourites")
    public ResponseEntity<?> addToFavourites(@RequestBody Favourites favourites) {
//        UserDetails userDetails= (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        userDetails.
        String query = foodService.addToFavourites(favourites);
        if (query.equalsIgnoreCase("added to favourites")) {
            return new ResponseEntity<>(query, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(query, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/viewMyFavourites")
    public ResponseEntity<List<Restaurant>> viewMyFavourites() {
        if (foodService.viewMyFavourites().size() > 0) {
            return new ResponseEntity<>(foodService.viewMyFavourites(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

//    @GetMapping("/filterRestaurantByRating")
//    public ResponseEntity<DishTypeResponse> filterRestaurantByRating(@RequestParam String dishType) {
//        if (foodService.filterRestaurantByRating(dishType) != null) {
//            return new ResponseEntity<>(foodService.filterRestaurantByRating(dishType), HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }
//
//    @GetMapping("/filterRestaurantByHighCost")
//    public ResponseEntity<DishTypeResponse> filterRestaurantByHighCost(@RequestParam String dishType) {
//        if (foodService.filterRestaurantByHighCost(dishType) != null) {
//            return new ResponseEntity<>(foodService.filterRestaurantByHighCost(dishType), HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }
//
//    @GetMapping("/filterRestaurantByLowCost")
//    public ResponseEntity<DishTypeResponse> filterRestaurantByLowCost(@RequestParam String dishType) {
//        if (foodService.filterRestaurantByLowCost(dishType) != null) {
//            return new ResponseEntity<>(foodService.filterRestaurantByLowCost(dishType), HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }
    @GetMapping("/searchWithFilter")
    public ResponseEntity<FilterResponse> searchWithFilter(@RequestBody Filter filter) {
        if (foodService.searchWithFilter(filter) != null) {
            return new ResponseEntity<>(foodService.searchWithFilter(filter), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @GetMapping("/homePage")
    public ResponseEntity<List<Restaurant>> HomePage()
    {
        if (foodService.HomePage().size() > 0) {
            return new ResponseEntity<>(foodService.HomePage(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @PostMapping("/addAddress")

    public ResponseEntity<String> addAddresses(@RequestBody Address address)
    {
        String query = foodService.addAddresses(address);
        if (query.equalsIgnoreCase("address added")) {
            return new ResponseEntity<>(query, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(query, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/viewAddress")
    public ResponseEntity<List<Address>> viewAddress() {
        if (foodService.viewAddress().size() > 0) {
            return new ResponseEntity<>(foodService.viewAddress(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @PatchMapping("/updateAddress")
    public ResponseEntity<?> updateAddresses(@RequestParam String address)
    {
       return foodService.updateAddresses(address);
    }
    @PostMapping("/createCart")
    public  ResponseEntity<?> addToCart(@RequestBody Cart cart)
    {
        String query = foodService.addToCart(cart);
        if (query.equalsIgnoreCase("cart added successfully")) {
            return new ResponseEntity<>(query, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("failed", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/viewCart")
    public ResponseEntity<List<Cart>> viewCart()
    {
        if (foodService.viewCart().size() > 0) {
            return new ResponseEntity<>(foodService.viewCart(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @PostMapping("/addToOrder")
    public ResponseEntity<?> addToOrder(@RequestBody CartItems cartItems)
    {
        return new ResponseEntity<>(foodService.addToOrder(cartItems),HttpStatus.CREATED);
    }
    @PostMapping("/addCartItems/{cartId}")
    public ResponseEntity<?> addCartItems(@RequestBody List<Item> items,@PathVariable int cartId)
    {
        return new ResponseEntity<>(foodService.addCartItems(items,cartId),HttpStatus.CREATED);
    }
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody Order order)
    {
        String check=foodService.placeOrder(order);
        if (check.equalsIgnoreCase("order placed successfully")) {
            return new ResponseEntity<>(check, HttpStatus.OK);
        }
        return new ResponseEntity<>(check, HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/viewOrders")
    public ResponseEntity<List<OrderResponse>> viewOrders() {
        if (foodService.viewOrders().size() > 0) {
            return new ResponseEntity<>(foodService.viewOrders(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @GetMapping("/viewFoodBasedOnTopCategory")
    public ResponseEntity<?> viewFoodBasedOnTopCategory()
    {
        if (foodService.viewFoodBasedOnTopCategory()!=null ) {
            return new ResponseEntity<>(foodService.viewFoodBasedOnTopCategory(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @PostMapping("/addCard")
    public ResponseEntity<?> addCard(@RequestBody Card card)
    {
        String check=foodService.addCard(card);
        if (check.equalsIgnoreCase("card registered")) {
            return new ResponseEntity<>(check, HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/viewCard")
    public ResponseEntity<List<Card>> viewCard()
    {
        if (foodService.viewCard().size() > 0) {
            return new ResponseEntity<>(foodService.viewCard(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}








