package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.yearup.models.Order;
import org.yearup.models.User;
import org.yearup.service.OrderService;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrdersController {
    private final UserService userService;
    private final OrderService orderService;
    private final ShoppingCartService shoppingCartService;
    public OrdersController(UserService userService, OrderService orderService, ShoppingCartService shoppingCartService) {
        this.userService = userService;
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
    }
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Order> placeOrderFromCart(Principal principal) {
        User user = userService.getLoggedInUser(principal);
        System.out.println("Hey, user ID: " + user.getId());
        Order order = orderService.placeOrderFromUserCart(user.getId());
        shoppingCartService.deleteCart(user.getId());
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/orders/{orderId}")
                        .buildAndExpand(order.getOrderId())
                        .toUri()
        ).body(order);
    }
}
