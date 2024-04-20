package com.robosoft.foodApp.service;

import com.robosoft.foodApp.entity.*;

import com.robosoft.foodApp.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService, UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public String verifyNumber(MobileNumberVerification mobileNumberVerification) {
        String query = "insert into MobileNumberVerification(mobileNumber) values('" + mobileNumberVerification.getMobileNumber() + "')";
        jdbcTemplate.update(query);
        return "mobile number registered";
    }

    @Override
    public void update2FAProperties(String mobileNumber, String tfacode) {
        jdbcTemplate.update("update MobileNumberVerification set code=?, expiryTime=? where mobileNumber=?", new Object[]
                {
                        tfacode, (System.currentTimeMillis() / 1000) + 60, mobileNumber
                });
    }

    @Override
    public boolean checkCode(String mobileNumber, String code) {
        try {
            boolean store = jdbcTemplate.queryForObject("select count(*) from MobileNumberVerification where code=? and mobileNumber=? and expiryTime>=?", new Object[]{code, mobileNumber, System.currentTimeMillis() / 1000}, Integer.class) > 0;
            String update_query = "update MobileNumberVerification set verified=true where mobileNumber='" + mobileNumber + "'";
            jdbcTemplate.update(update_query);
            return store;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String userRegister(User user) {
        try {
            String mobNo = jdbcTemplate.queryForObject("select mobileNumber from MobileNumberVerification where mobileNumber='" + user.getMobileNumber() + "'", String.class);
            boolean verified = jdbcTemplate.queryForObject("select verified from MobileNumberVerification where mobileNumber='" + mobNo + "'", Boolean.class);
            if (verified) {
                String query = "insert into user(userName,mobileNumber,email,password) values(?,?,?,?)";
                jdbcTemplate.update(query, user.getUserName(), user.getMobileNumber(), user.getEmail(), user.getPassword());
                return "user registered successfully";
            }
        } catch (Exception e) {
            System.out.println(e);
            return "registration failed";
        }
        return "mobile number is not verified";
    }

    @Override
    public void forgotPassword(String mobileNumber, String tfacode) {
        jdbcTemplate.update("update user set code=?, expirytime=? where mobileNumber=?", new Object[]
                {
                        tfacode, (System.currentTimeMillis() / 1000) + 60, mobileNumber
                });
    }

    @Override
    public boolean updatePassword(String mobileNumber, String code, String password) {
        try {
            boolean store = jdbcTemplate.queryForObject("select count(*) from user where code=? and mobilenumber=? and expirytime>=?", new Object[]{code, mobileNumber, System.currentTimeMillis() / 1000}, Integer.class) > 0;
            String update_query = "update user set password='" + password + "' where mobileNumber='" + mobileNumber + "'";
            jdbcTemplate.update(update_query);
            return store;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            String emailId = jdbcTemplate.queryForObject("select email from user where email=?", String.class, new Object[]{email});
            String password = jdbcTemplate.queryForObject("select password from user where email=?", String.class, new Object[]{email});
            return new org.springframework.security.core.userdetails.User(emailId, password, new ArrayList<>());
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public String addRestaurants(Restaurant restaurant) {
        String fileName = StringUtils.cleanPath(restaurant.getImage().getOriginalFilename());
        String downloadUrl;
        try {
            if (fileName.contains("..")) {
                throw new Exception("file name is invalid" + fileName);
            }
            downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/food/restaurantImage/")
                    .path(restaurant.getImageUrl())
                    .toUriString();
            String finalDownloadUrl = downloadUrl;
            String query = "insert into restaurant(name,location,description,freedelivery,openTime,closetime,image,imageUrl,minCost,creditCard) values(?,?,?,?,?,?,?,?,?,?)";
            jdbcTemplate.update(query, restaurant.getName(), restaurant.getLocation(), restaurant.getDescription(), restaurant.isFreeDelivery(), restaurant.getOpenTime(), restaurant.getCloseTime(), restaurant.getImage().getBytes(), finalDownloadUrl, restaurant.getMinCost(), restaurant.isCreditCard());
            return "restaurant added successfully";
        } catch (Exception e) {

            System.out.println(e);
            return "already there";
        }
    }

    @Override
    public List<Restaurant> viewRestaurants() {
        String query = "select restaurantId,name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,imageUrl,minCost,creditCard from restaurant";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Restaurant.class));
    }

    @Override
    public List<Restaurant> viewRestaurantsByName(String name) {
        try {
            String query1 = "select count(name) from restaurant where name='" + name + "'";
            int count = jdbcTemplate.queryForObject(query1, Integer.class);
            if (count > 0) {
                String query = "select restaurantId,name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,imageUrl,minCost,creditCard from restaurant where name='" + name + "'";
                return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Restaurant.class));
            }
            return null;
        } catch (Exception e) {
            System.out.println("restaurant name does not found");
            return null;
        }

    }

    public String getUserNameFromToken() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    public int getUserIdFromEmail() {
        String email = getUserNameFromToken();
        int userId = jdbcTemplate.queryForObject("select userId from user where email=?", new Object[]{email}, Integer.class);
        return userId;
    }


    @Override
    public String addMenu(Menu menu) {
        String fileName = StringUtils.cleanPath(menu.getImage().getOriginalFilename());
        String downloadUrl;
        try {
            if (fileName.contains("..")) {
                throw new Exception("file name is invalid" + fileName);
            }
            downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/food/image/")
                    .path(menu.getName())
                    .toUriString();
            String finalDownloadUrl = downloadUrl;
            jdbcTemplate.update("insert into menu(restaurantId,dishId,name,price,image,imageUrl,dishType) values(?,?,?,?,?,?,?)", menu.getRestaurantId(), menu.getDishId(), menu.getName(), menu.getPrice(), menu.getImage().getBytes(), finalDownloadUrl, menu.getDishType());
            return "dish added";
        } catch (Exception e) {

            System.out.println(e);
            return "dish already exist";
        }
    }

    @Override
    public byte[] dishImage(String dishName) {
        String get_image = "select image from menu where name='" + dishName + "'";
        return jdbcTemplate.queryForObject(get_image, byte[].class);
    }


    @Override
    public String getImageUrl(String dishName) {

        try {
            String url = "select imageUrl from  menu where name='" + dishName + "'";
            return jdbcTemplate.queryForObject(url, String.class);
        } catch (Exception e) {
            return "dish does not exist";
        }

    }

    @Override
    public List<Menu> viewMenu() {
        String query = "select restaurantId,dishId,name,price,imageUrl,dishType from menu";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public List<Menu> viewMenuByRestaurantId(int id) {
        try {
            String query1 = "select count(restaurantId) from menu where restaurantId=" + id;
            int count = jdbcTemplate.queryForObject(query1, Integer.class);
            if (count > 0) {
                String query = "select restaurantId,dishId,name,price,imageUrl,dishType from menu where restaurantId=" + id;
                return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Menu.class));
            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    @Override
    public String getRestaurantImageUrl(int rId) {

        try {
            String url = "select imageUrl from  restaurant where restaurantId=" + rId;
            return jdbcTemplate.queryForObject(url, String.class);
        } catch (Exception e) {
            System.out.println(e);
            return "restaurant id does not exist";
        }

    }

    @Override
    public byte[] restaurantImage(int rId) {
        String get_image = "select image from restaurant where restaurantId=" + rId;
        return jdbcTemplate.queryForObject(get_image, byte[].class);
    }

    @Override
    public String addRatings(Ratings ratings) {
        int id = getUserIdFromEmail();
        if (ratings.getRating() <= 5 && ratings.getRating() > 0) {
            try {
                jdbcTemplate.update("insert into ratings(RestaurantId,UserId,Rating) values(?,?,?)", ratings.getRestaurantId(), id, ratings.getRating());
                String upquery = "update restaurant rt set rt.ratings=rt.ratings+1 , rt.overallratings=(select avg(r.rating) from ratings r where r.restaurantId=?) where rt.restaurantId=" + ratings.getRestaurantId();
                jdbcTemplate.update(upquery, ratings.getRestaurantId());
                return "rated successfully";
            } catch (Exception e) {
                System.out.println(e);
                return "you have already rated";
            }
        }
        return "Invalid Rating";
    }

    @Override
    public String addToFavourites(Favourites favourites) {
        int id = getUserIdFromEmail();
        try {

            jdbcTemplate.update("insert into favourites(RestaurantId,UserId) values(?,?)", favourites.getRestaurantId(), id);
            String upquery = "update restaurant set favourites=favourites+1  where restaurantId=" + favourites.getRestaurantId();
            jdbcTemplate.update(upquery);
            return "added to favourites";
        } catch (Exception e) {
            jdbcTemplate.update("delete from favourites where restaurantId = ? and UserId= ?", favourites.getRestaurantId(), id);
            String upquery = "update restaurant set favourites=favourites-1  where restaurantId=" + favourites.getRestaurantId();
            jdbcTemplate.update(upquery);
            return "Deleted from favourites";
        }
    }

    @Override
    public List<Restaurant> viewMyFavourites() {
        int id = getUserIdFromEmail();
        String query = "select  favourites.restaurantId,name,location,description,freedelivery,ratings,overallratings,opentime,closetime,imageUrl,minCost,creditCard from restaurant inner join favourites on restaurant.restaurantId=favourites.restaurantId where userId=" + id;
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Restaurant.class));
    }


    //    @Override
//    public DishTypeResponse filterRestaurantByRating(String dishType)
//    {
//        String query="select restaurant.restaurantId,menu.dishType,restaurant.name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,restaurant.imageUrl,minCost,creditCard from restaurant inner join menu on restaurant.restaurantId=menu.restaurantId where dishType='"+dishType+"'  order by overallratings desc";
//        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(DishTypeResponse.class));
//    }
//
//    @Override
//    public DishTypeResponse filterRestaurantByHighCost(String dishType)
//    {
//        String query="select restaurant.restaurantId,menu.dishType,restaurant.name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,restaurant.imageUrl,minCost from restaurant inner join menu on restaurant.restaurantId=menu.restaurantId where dishType='"+dishType+"' order by  minCost desc";
//        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(DishTypeResponse.class));
//    }
//
//    @Override
//    public DishTypeResponse filterRestaurantByLowCost(String dishType)
//    {
//        String query="select restaurant.restaurantId,menu.dishType,restaurant.name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,restaurant.imageUrl,minCost from restaurant inner join menu on restaurant.restaurantId=menu.restaurantId where dishType='"+dishType+"' order by  minCost desc";
//        return jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(DishTypeResponse.class));
//    }
//    @Override
//
//    public DishTypeResponse cuisines(String dishType) {
//        String query = "select restaurant.restaurantId,menu.dishType,restaurant.name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,restaurant.imageUrl,minCost from restaurant inner join menu on restaurant.restaurantId=menu.restaurantId where dishType='" + dishType + "' order by overallratings desc ";
//        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(DishTypeResponse.class));
//    }

    @Override
    public FilterResponse searchWithFilter(Filter filter) {
        String query = this.applyFilter(filter);

        FilterResponse filterResponse = new FilterResponse();
        filterResponse.setDishType(filter.getDishType());
        List<RestaurantResponse> restaurantResponses = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(RestaurantResponse.class));

        filterResponse.setRestaurants(restaurantResponses);
        filterResponse.setCount(0);
        if (restaurantResponses != null)
            filterResponse.setCount(restaurantResponses.size());

        return filterResponse;
    }

    public String applyFilter(Filter filter) {
        String query = "select restaurant.restaurantId,menu.dishType,restaurant.name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,restaurant.imageUrl,minCost,creditCard from restaurant inner join menu on restaurant.restaurantId=menu.restaurantId where ";

        //dish type
        if (filter.getDishType() != null) {
            query = query + " menu.dishType like '%" + filter.getDishType() + "%' ";
        } else {
            query = query + " dishType like '%' ";
        }

        //filter
        if (filter.isOpenNow()) {
            Time now = Time.valueOf(LocalTime.now());

            query = query + "and restaurant.openTime<'" + now + "' and restaurant.closeTime>'" + now + "' ";
        }

        if (filter.isCreditCard()) {
            query = query + "and restaurant.creditCard=true ";
        }

        if (filter.isFreeDelivery()) {
            query = query + "and restaurant.freeDelivery=true ";
        }

        //price
        if (filter.getPrice() > 0) {
            query = query + "and restaurant.minCost<=" + filter.getPrice();
        }


        //order by
        if (filter.getSortBy() != null) {
            if (filter.getSortBy().equalsIgnoreCase("TOP RATED")) {
                query = query + " order by restaurant.overallRatings desc";
            }

            if (filter.getSortBy().equalsIgnoreCase("COST DESC")) {
                query = query + " order by restaurant.minCost desc";
            }

            if (filter.getSortBy().equalsIgnoreCase("COST ASC")) {
                query = query + " order by restaurant.minCost asc";
            }
        }


        System.out.println(query);

        return query;


    }

    @Override
    public List<Restaurant> HomePage() {
        String query = "select restaurantId,name,location,description,freedelivery,ratings,favourites,overallratings,opentime,closetime,imageUrl,minCost,creditCard from restaurant";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Restaurant.class));
    }

    @Override
    public String addAddresses(Address address) {
        int id = getUserIdFromEmail();
        jdbcTemplate.update("insert into address(userId,address) values(?,?)", id, address.getAddress());
        return "address added";
    }

    @Override
    public List<Address> viewAddress() {
        int id = getUserIdFromEmail();
        String query = "select * from address where userId=" + id;
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Address.class));
    }

    @Override
    public ResponseEntity<?> updateAddresses(String address) {
        int id = getUserIdFromEmail();
        try {
            String select = jdbcTemplate.queryForObject("select address from address where userId=" + id, String.class);
            if (select.equalsIgnoreCase(address)) {
                return new ResponseEntity<>("previous and new addresses are same", HttpStatus.EXPECTATION_FAILED);
            }
            jdbcTemplate.update("update address set address='" + address + "' where userId=" + id);
            return ResponseEntity.ok("Address updated successfully..");
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("enter valid userId", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public String addToCart(Cart cart) {
        String query = "insert into cart(userId,restaurantId,totalprice) values(?,?,?)";
        jdbcTemplate.update(query, cart.getUserId(), cart.getRestaurantId(), cart.getTotalPrice());
        return "cart added successfully";
    }

    @Override
    public List<Cart> viewCart() {
        int id = getUserIdFromEmail();
        String query = "select * from cart where userId=" + id;
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Cart.class));
    }

    public int getRecentlyCreatedCartId(int id) {
        return jdbcTemplate.queryForObject("select max(cartId) from cart where userId=" + id, Integer.class);
    }

    @Override
    public String addCartItems(List<Item> items, int cartId) {
        String query = "insert into item values(?,?,?)";
        for (Item item : items) {
            jdbcTemplate.update(query, item.getDishId(), cartId, item.getCount());
        }
        return "items added successfully";
    }

    @Override
    public int addToOrder(CartItems cartItems) {
        int id = getUserIdFromEmail();
        Cart cart = new Cart();
        cart.setUserId(id);
        cart.setRestaurantId(cartItems.getRestaurantId());
        cart.setTotalPrice(cartItems.getTotalPrice());
        addToCart(cart);

        int cartId = this.getRecentlyCreatedCartId(id);

        this.addCartItems(cartItems.getItems(), cartId);

        return cartId;

    }

    @Override
    public String placeOrder(Order order) {
        int id = getUserIdFromEmail();
        try {

            //check if card number given belongs to the user
            try {
                int card = jdbcTemplate.queryForObject("select cardNo from card where userId=" + id + " and cardNo=" + order.getCardNo(), Integer.class);
            } catch (Exception e) {
                e.printStackTrace();
                return "invalid Card number..";
            }

            //check if address belong to the user
            try {
                String address = jdbcTemplate.queryForObject("select address from address where address='" + order.getAddress() + "' and userId=" + id, String.class);
            } catch (Exception e) {
                e.printStackTrace();
                return "invalid address..";
            }

            //check if cart belongs to the user
            try {
                int cartId = jdbcTemplate.queryForObject("select cartId from cart where cartId=" + order.getCartId() + " and userId=" + id, Integer.class);
            } catch (Exception e) {
                e.printStackTrace();
                return "invalid cart number..";
            }

            //insert into orders table
            String query = "insert into orders(cartId,cardNo,address,status) values(?,?,?,?)";
            jdbcTemplate.update(query, order.getCartId(), order.getCardNo(), order.getAddress(), "successful");
            return "order placed successfully";


        } catch (Exception e) {
            System.out.println(e);
            return "order canceled";
        }

    }

    @Override
    public List<OrderResponse> viewOrders() {
        int id = getUserIdFromEmail();
        String query = "select o.orderId,o.cartId,o.cardNo,o.address,o.status,c.restaurantId,c.totalPrice from orders o inner join cart c on o.cartId=c.cartId where c.userId=" + id;
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(OrderResponse.class));

    }

    @Override
    public List<CategoryResponse> viewFoodBasedOnTopCategory() {
        String query = "SELECT name,imageUrl,COUNT(name) as count FROM menu GROUP BY  name  ORDER BY COUNT(name) DESC";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(CategoryResponse.class));
    }

    @Override
    public String addCard(Card card) {
        try {
            jdbcTemplate.update("insert into card(userId,cardNo) values(?,?)", card.getUserId(), card.getCardNo());
            return "card registered";
        } catch (Exception e) {
            System.out.println(e);
            return "duplicate card ";
        }

    }

    @Override
    public List<Card> viewCard() {
        int id = getUserIdFromEmail();
        return jdbcTemplate.query("select cardNo from card where userId=" + id, new BeanPropertyRowMapper<>(Card.class));
    }


}
