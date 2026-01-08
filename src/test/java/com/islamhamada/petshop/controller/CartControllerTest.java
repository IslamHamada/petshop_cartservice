package com.islamhamada.petshop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.islamhamada.petshop.CartServiceConfig;
import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.model.AddSessionCartRequest;
import com.islamhamada.petshop.model.UpdateCartItemCountRequest;
import com.islamhamada.petshop.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest({"server.port=0",
        "product-service-svc.url=http://localhost:9090"})
@AutoConfigureMockMvc
@EnableConfigurationProperties
@ContextConfiguration(classes = {CartServiceConfig.class})
class CartControllerTest {

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer =
            WireMockExtension.newInstance()
                    .options(WireMockConfiguration
                            .wireMockConfig()
                            .port(9090))
                    .build();

    private ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SimpleGrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_Admin");
    SimpleGrantedAuthority customerRole = new SimpleGrantedAuthority("ROLE_Customer");

    SimpleGrantedAuthority neededRole = customerRole;
    SimpleGrantedAuthority notNeededRole = adminRole;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        cartItemRepository.deleteAll();
        getProductById();
    }

    private void getProductById() throws IOException {
        wireMockServer.stubFor(WireMock.get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(StreamUtils.copyToString(
                                CartControllerTest.class.getClassLoader()
                                        .getResourceAsStream("mock/GetProduct1.json"),
                                Charset.defaultCharset()
                        ))));
        wireMockServer.stubFor(WireMock.get("/product/2")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(StreamUtils.copyToString(
                                CartControllerTest.class.getClassLoader()
                                        .getResourceAsStream("mock/GetProduct2.json"),
                                Charset.defaultCharset()
                        ))));
        wireMockServer.stubFor(WireMock.get("/product/999")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value()))
        );
    }

    @Nested
    public class addCartItem {
        MockHttpServletRequestBuilder httpRequest = post("/cart");

        @Test
        public void success_item_exists() throws Exception {
            AddCartItemRequest requestBody = getMockAddCartItemRequest();
            CartItem cartItem = getMockCartItem();
            cartItemRepository.save(cartItem);
            MvcResult mvcResult = mockMvc.perform(httpRequest
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();

            assertEquals(cartItem.getProductId(), requestBody.getProduct_id());
            assertEquals(cartItem.getUserId(), requestBody.getBackend_id());

            assertEquals(response, cartItem.getId() + "");

            CartItem updatedCartItem = cartItemRepository.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId()).get();
            assertEquals(cartItem.getCount() + requestBody.getCount(), updatedCartItem.getCount());
        }

        @Test
        public void success_item_new() throws Exception {
            AddCartItemRequest requestBody = getMockAddCartItemRequest();
            MvcResult mvcResult = mockMvc.perform(httpRequest
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            CartItem cartItem = cartItemRepository.findByUserIdAndProductId(requestBody.getBackend_id(), requestBody.getProduct_id()).get();
            assertEquals(cartItem.getId() + "", response);
            assertEquals(requestBody.getBackend_id(), cartItem.getUserId());
            assertEquals(requestBody.getProduct_id(), cartItem.getProductId());
            assertEquals(requestBody.getCount(), cartItem.getCount());
        }

        @Test
        public void failure_no_permission() throws Exception {
            AddCartItemRequest request = getMockAddCartItemRequest();
            MvcResult mvcResult = mockMvc.perform(httpRequest
                            .with(jwt().authorities(notNeededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(AddCartItemRequest request) throws Exception {
            MvcResult mvcResult = mockMvc.perform(httpRequest
                            .with(jwt().authorities(notNeededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        public static List<AddCartItemRequest> bad_input() {
            List<AddCartItemRequest> list = new ArrayList<>();
            list.add(AddCartItemRequest.builder()
                    .backend_id(-1)
                    .product_id(2)
                    .count(5)
                    .build());
            list.add(AddCartItemRequest.builder()
                    .backend_id(1)
                    .product_id(-1)
                    .count(5)
                    .build());
            list.add(AddCartItemRequest.builder()
                    .backend_id(1)
                    .product_id(2)
                    .count(0)
                    .build());
            list.add(AddCartItemRequest.builder()
                    .backend_id(1)
                    .product_id(2)
                    .count(-1)
                    .build());
            return list;
        }

        private AddCartItemRequest getMockAddCartItemRequest() {
            return AddCartItemRequest.builder()
                    .product_id(2)
                    .backend_id(1)
                    .count(3)
                    .build();
        }
    }

    @Nested
    public class getCartByUser {

        @Test
        public void success() throws Exception {
            long user_id = 1;
            CartItem cartItem = cartItemRepository.save(CartItem.builder()
                    .userId(user_id)
                    .productId(1)
                    .count(2)
                    .build());
            CartItem cartItem2 = cartItemRepository.save(CartItem.builder()
                    .userId(user_id)
                    .productId(2)
                    .count(3)
                    .build());
            MvcResult mvcResult = mockMvc.perform(
                            get("/cart/user/" + user_id)
                                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            List<ElaborateCartItemDTO> elaborateCartItemDTOS = objectMapper.readValue(response, new TypeReference<List<ElaborateCartItemDTO>>(){});
            assertEquals(2, elaborateCartItemDTOS.size());
            assertEquals(cartItem.getId(), elaborateCartItemDTOS.get(0).getCart_item_id());
            assertEquals(cartItem.getCount(), elaborateCartItemDTOS.get(0).getCart_item_count());
            assertEquals(cartItem2.getId(), elaborateCartItemDTOS.get(1).getCart_item_id());
            assertEquals(cartItem2.getCount(), elaborateCartItemDTOS.get(1).getCart_item_count());
        }

        @Test
        public void failure_product_not_found() throws Exception {
            long user_id = 1;
            CartItem cartItem = cartItemRepository.save(CartItem.builder()
                    .userId(user_id)
                    .productId(1)
                    .count(2)
                    .build());
            CartItem cartItem2 = cartItemRepository.save(CartItem.builder()
                    .userId(user_id)
                    .productId(999)
                    .count(3)
                    .build());
            MvcResult mvcResult = mockMvc.perform(
                            get("/cart/user/" + user_id)
                                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isInternalServerError())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            RestExceptionResponse restExceptionResponse = objectMapper.readValue(response, RestExceptionResponse.class);
            assertEquals("INTERNAL_SERVER_ERROR", restExceptionResponse.getError_code());
            assertEquals("Internal Server Error", restExceptionResponse.getError_message());
        }

        @Test
        public void failure_bad_input() throws Exception {
            long user_id = -1;
            MvcResult mvcResult = mockMvc.perform(
                            get("/cart/user/" + user_id)
                                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        public void failure_no_permission() throws Exception {
            long user_id = 1;
            MvcResult mvcResult = mockMvc.perform(
                    get("/cart/user/" + user_id)
                            .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }
    }

    @Nested
    public class emptyCartOfUser {

        @Test
        public void success() throws Exception {
            long user_id = 1;
            cartItemRepository.save(CartItem.builder()
                    .productId(1)
                    .userId(user_id)
                    .count(2)
                    .build());
            cartItemRepository.save(CartItem.builder()
                    .productId(2)
                    .userId(user_id)
                    .count(4)
                    .build());
            MvcResult mvcResult = mockMvc.perform(delete("/cart/user/" + user_id)
                            .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(2 + "", response);
            assertEquals(0, cartItemRepository.findByUserId(user_id).size());
        }

        @Test
        public void failure_bad_input() throws Exception {
            long user_id = -1;
            MvcResult mvcResult = mockMvc.perform(delete("/cart/user/" + user_id)
                                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        public void failure_no_permission() throws Exception {
            long user_id = 1;
            MvcResult mvcResult = mockMvc.perform(
                            get("/cart/user/" + user_id)
                                    .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }
    }

    @Nested
    public class updateCartItemCount {
        @Test
        public void success() throws Exception {
            CartItem cartItem = getMockCartItem();
            cartItem = cartItemRepository.save(cartItem);
            UpdateCartItemCountRequest request = new UpdateCartItemCountRequest(10);
            MvcResult mvcResult = mockMvc.perform(put("/cart/cart_item/" + cartItem.getId())
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(request.getCount() + "", response);
            CartItem cartItem2 = cartItemRepository.findById(cartItem.getId()).get();
            assertEquals(request.getCount(), cartItem2.getCount());
            assertEquals(cartItem.getProductId(), cartItem2.getProductId());
            assertEquals(cartItem.getUserId(), cartItem2.getUserId());
            assertEquals(cartItem.getId(), cartItem2.getId());
        }

        @Test
        public void failure_no_item() throws Exception {
            long cart_item_id = 1;
            UpdateCartItemCountRequest request = new UpdateCartItemCountRequest(5);
            MvcResult mvcResult = mockMvc.perform(put("/cart/cart_item/" + cart_item_id)
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            RestExceptionResponse exceptionResponse = objectMapper.readValue(response, RestExceptionResponse.class);
            assertEquals("CART_ITEM_NOT_FOUND", exceptionResponse.getError_code());
            assertEquals("No cart item with id: " + cart_item_id, exceptionResponse.getError_message());
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(long cart_item_id, UpdateCartItemCountRequest request) throws Exception {
            MvcResult mvcResult = mockMvc.perform(put("/cart/cart_item/" + cart_item_id)
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        public static List<Arguments> bad_input() {
            List<Arguments> list = new ArrayList<>();
            list.add(Arguments.of(-1, new UpdateCartItemCountRequest(3)));
            list.add(Arguments.of(1, new UpdateCartItemCountRequest(0)));
            return list;
        }

        @Test
        public void failure_no_permission() throws Exception {
            long cart_item_id = 1;
            UpdateCartItemCountRequest request = getMockUpdateCartItemCountRequest();
            MvcResult mvcResult = mockMvc.perform(
                            put("/cart/cart_item/" + cart_item_id)
                                    .with(jwt().authorities(notNeededRole))
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        public UpdateCartItemCountRequest getMockUpdateCartItemCountRequest() {
            return new UpdateCartItemCountRequest(5);
        }
    }

    @Nested
    public class deleteCartItem {

        @Test
        public void success() throws Exception {
            CartItem cartItem = getMockCartItem();
            cartItem = cartItemRepository.save(cartItem);
            MvcResult mvcResult = mockMvc.perform(delete("/cart/cart_item/" + cartItem.getId())
                            .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            assertEquals(false, cartItemRepository.findById(cartItem.getId()).isPresent());
        }

        @Test
        public void failure_bad_input() throws Exception {
            long cart_item_id = -1;
            MvcResult mvcResult = mockMvc.perform(delete("/cart/cart_item/" + cart_item_id)
                            .with(jwt().authorities(neededRole)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        public void failure_no_permission() throws Exception {
            long cart_item_id = 1;
            MvcResult mvcResult = mockMvc.perform(
                            delete("/cart/cart_item/" + cart_item_id)
                                    .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }
    }

    @Nested
    public class getCartItemCount {

        @Test
        public void success() throws Exception{
            CartItem item1 = getMockCartItem();
            cartItemRepository.save(item1);
            int count2 = 3;
            cartItemRepository.save(CartItem.builder()
                    .count(count2)
                    .userId(item1.getUserId())
                    .productId(3)
                    .build());
            int total_count = item1.getCount() + count2;
            MvcResult mvcResult = mockMvc.perform(get("/cart/user/item_count/" + item1.getUserId())
                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(total_count + "", response);
        }

        @Test
        public void failure_bad_input() throws Exception {
            long user_id = -1;
            MvcResult mvcResult = mockMvc.perform(get("/cart/user/item_count/" + user_id)
                            .with(jwt().authorities(neededRole)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        public void failure_no_permission() throws Exception {
            long user_id = 1;
            MvcResult mvcResult = mockMvc.perform(
                            get("/cart/user/item_count/" + user_id)
                                    .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }
    }

    @Nested
    public class addSessionCart {

        @Test
        public void success() throws Exception {
            long user_id = 1;
            CartItem cartItem = getMockCartItem();
            CartItem cartItem2 = CartItem.builder()
                    .userId(cartItem.getUserId())
                    .productId(3)
                    .count(3)
                    .build();
            AddSessionCartRequest.SessionCartItem sessionCartItem = new AddSessionCartRequest.SessionCartItem(cartItem.getProductId(), cartItem.getCount());
            AddSessionCartRequest.SessionCartItem sessionCartItem2 = new AddSessionCartRequest.SessionCartItem(cartItem2.getProductId(), cartItem2.getCount());

            AddSessionCartRequest request = AddSessionCartRequest.builder()
                    .cart_items(List.of(sessionCartItem, sessionCartItem2))
                    .build();

            MvcResult mvcResult = mockMvc.perform(post("/cart/login_to_checkout/" + user_id)
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = mvcResult.getResponse().getContentAsString();
            List<CartItem> cartItems = cartItemRepository.findByUserId(user_id);
            for(CartItem c : cartItems)
                assertEquals(user_id, c.getUserId());
            assertEquals(cartItem.getCount(), cartItems.get(0).getCount());
            assertEquals(cartItem.getProductId(), cartItems.get(0).getProductId());

            assertEquals(cartItem2.getCount(), cartItems.get(1).getCount());
            assertEquals(cartItem2.getProductId(), cartItems.get(1).getProductId());
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(int user_id, AddSessionCartRequest request) throws Exception {
            MvcResult mvcResult = mockMvc.perform(post("/cart/login_to_checkout/" + user_id)
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        public static List<Arguments> bad_input () {
            List<Arguments> list = new ArrayList<>();

            AddSessionCartRequest addSessionCartRequest = getMockAddSessionCartRequest();
            list.add(Arguments.of(-1, addSessionCartRequest));

            List<AddSessionCartRequest.SessionCartItem> sessionCartItems = getMockAddSessionCartRequest().getCart_items();
            sessionCartItems.get(1).setProduct_id(-1);
            list.add(Arguments.of(1, new AddSessionCartRequest(sessionCartItems)));

            List<AddSessionCartRequest.SessionCartItem> sessionCartItems2 = getMockAddSessionCartRequest().getCart_items();
            sessionCartItems2.get(1).setCount(0);
            list.add(Arguments.of(1, new AddSessionCartRequest(sessionCartItems2)));

            list.add(Arguments.of(1, new AddSessionCartRequest(List.of())));
            list.add(Arguments.of(1, null));

            return list;
        }

        @Test
        public void failure_no_permission() throws Exception {
            AddSessionCartRequest request = getMockAddSessionCartRequest();
            long user_id = 1;
            MvcResult mvcResult = mockMvc.perform(
                            post("/cart/login_to_checkout/" + user_id)
                                    .with(jwt().authorities(notNeededRole))
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        public static AddSessionCartRequest getMockAddSessionCartRequest() {
            CartItem cartItem = getMockCartItem();
            CartItem cartItem2 = CartItem.builder()
                    .userId(cartItem.getUserId())
                    .productId(3)
                    .count(3)
                    .build();
            AddSessionCartRequest.SessionCartItem sessionCartItem = AddSessionCartRequest.SessionCartItem.builder()
                    .product_id(cartItem.getProductId())
                    .count(cartItem.getCount())
                    .build();
            AddSessionCartRequest.SessionCartItem sessionCartItem2 = AddSessionCartRequest.SessionCartItem.builder()
                    .product_id(cartItem2.getProductId())
                    .count(cartItem2.getCount())
                    .build();
            return AddSessionCartRequest.builder()
                    .cart_items(List.of(sessionCartItem, sessionCartItem2))
                    .build();
        }
    }

    public static CartItem getMockCartItem() {
        CartItem cartItem = CartItem.builder()
                .userId(1)
                .productId(2)
                .count(5)
                .build();
        return cartItem;
    }
}