package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.exception.EmptyCheckoutException;
import org.yearup.exception.NotFoundException;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;
import org.yearup.repository.ProfileRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class OrderService {
    private final ShoppingCartService shoppingCartService;
    private final ProfileRepository profileRepository;
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;

    public OrderService(
            ShoppingCartService shoppingCartService,
            ProfileRepository profileRepository,
            OrderRepository orderRepository,
            OrderLineItemRepository orderLineItemRepository
    ) {
        this.shoppingCartService = shoppingCartService;
        this.profileRepository = profileRepository;
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    // TODO: this should return a model of the full order, with items
    public Order placeOrderFromUserCart(int userId) {
        // 1. Retrieve the current cart
        // 2. Create order
        // 3. Copy cart items into order_line_items
        ShoppingCart cart = shoppingCartService.getByUserId(userId);
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCheckoutException();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDate.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        // Free shipping :)
        order.setShippingAmount(BigDecimal.ZERO);
        order = orderRepository.save(order);
        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            OrderLineItem item = new OrderLineItem();
            item.setOrderId(order.getOrderId());
            item.setProductId(cartItem.getProductId());
            item.setSalesPrice(new BigDecimal(cartItem.getLineTotal()));
            item.setQuantity(cartItem.getQuantity());
            item.setDiscount(new BigDecimal(cartItem.getDiscountPercent()));
            orderLineItemRepository.save(item);
        }
        return order;
    }
}
