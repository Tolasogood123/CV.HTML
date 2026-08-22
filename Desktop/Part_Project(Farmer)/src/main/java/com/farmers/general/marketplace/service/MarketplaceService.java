package com.farmers.general.marketplace.service;

import com.farmers.general.exception.BadRequestException;
import com.farmers.general.exception.ResourceNotFoundException;
import com.farmers.general.exception.UnauthorizedException;
import com.farmers.general.marketplace.dto.*;
import com.farmers.general.marketplace.entity.Order;
import com.farmers.general.marketplace.entity.OrderItem;
import com.farmers.general.marketplace.entity.Product;
import com.farmers.general.marketplace.enums.OrderStatus;
import com.farmers.general.marketplace.enums.ProductStatus;
import com.farmers.general.marketplace.repository.OrderRepository;
import com.farmers.general.marketplace.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // TODO: inject AuditLogService to log APPROVE_PRODUCT, REJECT_PRODUCT
    // TODO: inject NotificationService to notify sellers on order status changes

    // --- Products ---

    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findByDeletedFalseAndStatus(ProductStatus.ACTIVE).stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toProductResponse(product);
    }

    public List<ProductResponse> getSellerProducts(Long sellerId) {
        return productRepository.findBySellerIdAndDeletedFalse(sellerId).stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(Long sellerId, ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .status(ProductStatus.PENDING)
                .sellerId(sellerId)
                .farmId(request.getFarmId())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .build();

        product = productRepository.save(product);
        return toProductResponse(product);
    }

    public ProductResponse updateProduct(Long id, Long sellerId, ProductRequest request) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You can only modify your own products");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setFarmId(request.getFarmId());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());

        product = productRepository.save(product);
        return toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You can only delete your own products");
        }

        product.setDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public ProductResponse approveProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setStatus(ProductStatus.ACTIVE);
        product = productRepository.save(product);

        // TODO: call auditLogService.log("APPROVE_PRODUCT", "MARKETPLACE", "Product approved: " + product.getId())
        // TODO: notify seller that product was approved

        return toProductResponse(product);
    }

    @Transactional
    public ProductResponse rejectProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setStatus(ProductStatus.REJECTED);
        product = productRepository.save(product);

        // TODO: call auditLogService.log("REJECT_PRODUCT", "MARKETPLACE", "Product rejected: " + product.getId())
        // TODO: notify seller that product was rejected

        return toProductResponse(product);
    }

    // --- Orders ---

    @Transactional
    public OrderResponse createOrder(Long buyerId, OrderRequest request) {
        Order order = Order.builder()
                .buyerId(buyerId)
                .sellerId(request.getSellerId())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .filter(p -> !p.getDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + product.getStock());
            }

            if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
                throw new BadRequestException("Product is not available for purchase: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(itemTotal)
                    .build();

            order.addItem(item);
            totalAmount = totalAmount.add(itemTotal);

            // Reduce product stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            if (product.getStock() == 0) {
                product.setStatus(ProductStatus.SOLD_OUT);
            }
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // TODO: notify seller of new order

        return toOrderResponse(order);
    }

    public List<OrderResponse> getBuyerOrders(Long buyerId) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId).stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getSellerOrders(Long sellerId) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId).stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Cannot transition order from " + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        // TODO: notify buyer of order status change

        return toOrderResponse(order);
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED;
            case PROCESSING -> to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED -> to == OrderStatus.DELIVERED;
            case DELIVERED -> false;
            case CANCELLED -> false;
        };
    }

    private ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setStatus(product.getStatus());
        response.setSellerId(product.getSellerId());
        response.setFarmId(product.getFarmId());
        response.setImageUrl(product.getImageUrl());
        response.setCategory(product.getCategory());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setBuyerId(order.getBuyerId());
        response.setSellerId(order.getSellerId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse ir = new OrderResponse.OrderItemResponse();
                    ir.setId(item.getId());
                    ir.setProductId(item.getProductId());
                    ir.setProductName(item.getProductName());
                    ir.setQuantity(item.getQuantity());
                    ir.setUnitPrice(item.getUnitPrice());
                    ir.setTotalPrice(item.getTotalPrice());
                    return ir;
                })
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        return response;
    }
}
