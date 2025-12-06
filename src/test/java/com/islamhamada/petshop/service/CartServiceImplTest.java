package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.exception.CartServiceException;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.model.AddSessionCartRequest;
import com.islamhamada.petshop.repository.CartItemRepository;
import org.apache.commons.math.stat.descriptive.summary.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartServiceImplTest {

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    ProductService productService;

    @Spy
    @InjectMocks
    CartService cartService = new CartServiceImpl();

    @Nested
    @DisplayName("addCartItem")
    class addCartItem {

        @Test
        @DisplayName("success 1")
        void success_1() {
            AddCartItemRequest addCartItemRequest = getAddCartItemRequestMock();
            CartItem cartItem = getCartItemMock();
            int original_count = cartItem.getCount();

            when(cartItemRepository.findByUserIdAndProductId(anyLong(), anyLong()))
                    .thenReturn(Optional.of(cartItem));
            when(cartItemRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            long rv = cartService.addCartItem(addCartItemRequest);

            verify(cartItemRepository, times(1))
                    .findByUserIdAndProductId(anyLong(), anyLong());
            verify(cartItemRepository, times(1))
                    .save(any());

            assertEquals(rv, cartItem.getId());
            assertEquals(cartItem.getUserId(), addCartItemRequest.getBackend_id());
            assertEquals(cartItem.getProductId(), addCartItemRequest.getProduct_id());
            assertEquals(original_count + addCartItemRequest.getCount(), cartItem.getCount());
        }

        @Test
        @DisplayName("success 2")
        void success_2() {
            AddCartItemRequest addCartItemRequest = getAddCartItemRequestMock();
            CartItem[] cartItem = {null};

            when(cartItemRepository.findByUserIdAndProductId(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            when(cartItemRepository.save(any()))
                    .thenAnswer(invocation -> {
                        cartItem[0] = invocation.getArgument(0);
                        return cartItem[0];
                    });

            long rv = cartService.addCartItem(addCartItemRequest);

            verify(cartItemRepository, times(1))
                    .findByUserIdAndProductId(anyLong(), anyLong());
            verify(cartItemRepository, times(1))
                    .save(any());

            assertNotNull(cartItem[0]);
            assertEquals(rv, cartItem[0].getId());
            assertEquals(addCartItemRequest.getBackend_id(), cartItem[0].getUserId());
            assertEquals(addCartItemRequest.getProduct_id(), cartItem[0].getProductId());
            assertEquals(addCartItemRequest.getCount(), cartItem[0].getCount());
        }

        private AddCartItemRequest getAddCartItemRequestMock() {
            CartItem cartItem = getCartItemMock();
            return AddCartItemRequest.builder()
                    .count(2)
                    .backend_id(cartItem.getId())
                    .product_id(cartItem.getProductId())
                    .build();
        }
    }

    @Nested
    @DisplayName("getUserCart")
    class getUserCart {

        @Test
        @DisplayName("success")
        void success() {
            int user_id = 1;
            List<ProductDTO> productDTOList = getProductDTOListMock();
            List<CartItem> cartItems = getCartItemListMock(user_id);
            assertEquals(2, cartItems.size());
            assertEquals(2, productDTOList.size());


            when(cartItemRepository.findByUserId(anyLong()))
                    .thenReturn(cartItems);
            for(ProductDTO productDTO : productDTOList) {
                when(productService.getProductById(productDTO.getId()))
                        .thenReturn(new ResponseEntity<>(productDTO, HttpStatus.OK));
            }

            List<ElaborateCartItemDTO> elaborateCartItems = cartService.getUserCart(user_id);

            verify(cartItemRepository, times(1))
                    .findByUserId(anyLong());
            verify(productService, times(cartItems.size()))
                    .getProductById(anyLong());

            assertEquals(cartItems.size(), elaborateCartItems.size());
            for(int i = 0; i < elaborateCartItems.size(); i++) {
                ElaborateCartItemDTO elaborateCartItemDTO = elaborateCartItems.get(i);
                CartItem cartItem = cartItems.get(i);
                ProductDTO productDTO = productDTOList.get(i);

                assertEquals(cartItem.getId(), elaborateCartItemDTO.getCart_item_id());
                assertEquals(cartItem.getCount(), elaborateCartItemDTO.getCart_item_count());

                assertEquals(productDTO.getId(), elaborateCartItemDTO.getProduct_id());
                assertEquals(productDTO.getId(), cartItem.getProductId());

                assertEquals(productDTO.getPrice(), elaborateCartItemDTO.getProduct_price());
                assertEquals(productDTO.getName(), elaborateCartItemDTO.getProduct_name());
                assertEquals(productDTO.getImage(), elaborateCartItemDTO.getProduct_image());
            }
        }

        private List<CartItem> getCartItemListMock(int user_id) {
            List<CartItem> cartItems = new ArrayList<>();
            List<ProductDTO> productDTOList = getProductDTOListMock();
            for(int i = 0; i < productDTOList.size(); i++){
                cartItems.add(CartItem.builder()
                        .id(i + 1)
                        .count(1)
                        .productId(productDTOList.get(i).getId())
                        .userId(user_id)
                        .build());
            }
            return cartItems;
        }

        private ProductDTO getProductDTOMock1() {
            return ProductDTO.builder()
                    .id(1)
                    .name("name1")
                    .description("description1")
                    .price(1)
                    .image("image1")
                    .quantity(1)
                    .utility("utility1")
                    .for_animal("for_animal1")
                    .build();
        }

        private ProductDTO getProductDTOMock2() {
            return ProductDTO.builder()
                    .id(2)
                    .name("name2")
                    .description("description2")
                    .price(2)
                    .image("image2")
                    .quantity(2)
                    .utility("utility2")
                    .for_animal("for_animal2")
                    .build();
        }

        private List<ProductDTO> getProductDTOListMock() {
            ProductDTO productDTO1 = getProductDTOMock1();
            ProductDTO productDTO2 = getProductDTOMock2();
            return List.of(productDTO1, productDTO2);
        }
    }


    @Nested
    @DisplayName("updateCartItemCount")
    class updateCartItemCount {

        @Test
        @DisplayName("success")
        void success() {
            CartItem cartItem = getCartItemMock();
            int count = 5;

            when(cartItemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(cartItem));
            when(cartItemRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            int rv = cartService.updateCartItemCount(cartItem.getId(), count);

            verify(cartItemRepository, times(1))
                    .findById(anyLong());
            verify(cartItemRepository, times(1))
                    .save(any());

            assertEquals(count, rv);
        }

        @Test
        @DisplayName("failure")
        void failure() {
            int count = 5;
            CartItem cartItem = getCartItemMock();

            when(cartItemRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            CartServiceException exception = assertThrows(CartServiceException.class,
                    () -> cartService.updateCartItemCount(cartItem.getId(), count));

            verify(cartItemRepository, times(1))
                    .findById(anyLong());
            verify(cartItemRepository, never())
                    .save(any());

            assertEquals("No cart item with id: " + cartItem.getId(), exception.getMessage());
            assertEquals("CART_NOT_FOUND", exception.getError_code());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        }
    }

    @Nested
    @DisplayName("addSessionCart")
    class addSessionCart {

        @Test
        @DisplayName("success")
        void success() {
            AddSessionCartRequest addSessionCartRequest = getAddSessionCartRequestMock();
            int user_id = 1;

            cartService.addSessionCart(user_id, addSessionCartRequest);

            ArgumentCaptor<AddCartItemRequest> captor = ArgumentCaptor.forClass(AddCartItemRequest.class);

            verify(cartService, times(addSessionCartRequest.getCart_items().size()))
                    .addCartItem(captor.capture());
            List<AddCartItemRequest> requests = captor.getAllValues();
            for(int i = 0; i < requests.size(); i++) {
                AddCartItemRequest request = requests.get(i);
                AddSessionCartRequest.SessionCartItem sessionCartItem = addSessionCartRequest.getCart_items().get(i);
                assertEquals(sessionCartItem.getCount(), request.getCount());
                assertEquals(sessionCartItem.getProduct_id(), request.getProduct_id());
            }
        }

        AddSessionCartRequest getAddSessionCartRequestMock() {
            List<AddSessionCartRequest.SessionCartItem> sessionCartItems = new ArrayList<>();
            sessionCartItems.add(AddSessionCartRequest.SessionCartItem.builder()
                    .count(1)
                    .product_id(1)
                    .build());
            sessionCartItems.add(AddSessionCartRequest.SessionCartItem.builder()
                    .count(2)
                    .product_id(2)
                    .build());
            return AddSessionCartRequest.builder()
                    .cart_items(sessionCartItems)
                    .build();
        }
    }

    private CartItem getCartItemMock() {
        return CartItem.builder()
                .id(1)
                .count(1)
                .productId(1)
                .userId(1)
                .build();
    }
}