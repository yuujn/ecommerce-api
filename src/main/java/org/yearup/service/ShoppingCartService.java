package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId)
    {
        // load the user's cart rows, look up each product, and build the ShoppingCart
        List<CartItem> items = shoppingCartRepository.findByUserId(userId);
        ShoppingCart cart = new ShoppingCart();
        // TODO: resolve that this is a big N+1 query loop
        for (CartItem item : items) {
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            Product product = productService.getById(item.getProductId());
            shoppingCartItem.setProduct(product);
            shoppingCartItem.setQuantity(item.getQuantity());
            // TODO: I don't see anywhere to get discounts from.
            shoppingCartItem.setDiscountPercent(0.);
            cart.add(shoppingCartItem);
        }
        return cart;
    }

    // add additional methods here
    public void addCartItem(int userId, int productId) {
        CartItem found = shoppingCartRepository.findByUserIdAndProductId(userId, productId);
        if (found != null) {
            int newQuantity = found.getQuantity() + 1;
            found.setQuantity(newQuantity);
            shoppingCartRepository.save(found);
        } else {
            CartItem created = new CartItem();
            created.setUserId(userId);
            created.setProductId(productId);
            shoppingCartRepository.save(created);
        }
    }

    public void updateCartItemQuantity(int userId, int productId, int quantity) {
        CartItem found = shoppingCartRepository.findByUserIdAndProductId(userId, productId);
        found.setQuantity(quantity);
        shoppingCartRepository.save(found);
    }

    @Transactional
    public void deleteCart(int userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }
}
