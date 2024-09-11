package com.nuttty.eureka.company.presentation.controller;

import com.nuttty.eureka.company.application.service.ProductService;
import com.nuttty.eureka.company.presentation.request.ProductRequestDto;
import com.nuttty.eureka.company.presentation.request.ProductSearchRequestDto;
import com.nuttty.eureka.company.presentation.request.ProductUpdateRequestDto;
import com.nuttty.eureka.company.presentation.response.ProductDelResponseDto;
import com.nuttty.eureka.company.presentation.response.ProductResponseDto;
import com.nuttty.eureka.company.presentation.response.ProductSearchResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param request
     * @param role
     * @param userId
     * @return
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDto request,
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @RequestHeader(value = "X-User-Id") Long userId) {
        validateExcludeRoleDeliveryPerson(role);
        log.info("상품 등록 시도 중 | request: {}, userId: {}, role: {}", request, userId, role);

        ProductResponseDto response = productService.createProduct(request, userId, role);

        log.info("상품 등록 완료 | response: {}, userId: {}, role: {}", response, userId, role);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 수정 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param productId
     * @param request
     * @param role
     * @param userId
     * @return
     */
    @PatchMapping("/products/{product_id}")
    public ResponseEntity<?> updateProduct(@PathVariable("product_id") UUID productId,
                                           @Valid @RequestBody ProductUpdateRequestDto request,
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @RequestHeader(value = "X-User-Id") Long userId) {
        validateExcludeRoleDeliveryPerson(role);
        log.info("상품 수정 시도 중 | request: {}, userId: {}, role: {}, productId: {}", request, userId, role, productId);

        ProductResponseDto response = productService.updateProduct(productId, request, role, userId);
        log.info("상품 수정 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 삭제 | 마스터 허용
     * @param productId
     * @param role
     * @param email
     * @return
     */
    @DeleteMapping("/products/{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("product_id") UUID productId,
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @RequestHeader(value = "X-User-Email") String email) {
        validateRoleMaster(role);
        log.info("상품 삭제 시도 중 | productId: {}, role: {}, email: {}", productId, role, email);

        ProductDelResponseDto response = productService.deleteProduct(productId, email);
        log.info("상품 삭제 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 재고 수량 추가 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param productId
     * @param quantity
     * @param role
     * @param userId
     * @return
     */
    @PatchMapping("/products/add_stock/{product_id}")
    public ResponseEntity<?> addStock(@PathVariable("product_id") UUID productId,
                                      @RequestParam("quantity") Integer quantity,
                                      @RequestHeader(value = "X-User-Role") String role,
                                      @RequestHeader(value = "X-User-Id") Long userId) {
        validateExcludeRoleDeliveryPerson(role);
        log.info("재고 수량 추가 시도 중 | productId: {}, quantity: {}, role: {}", productId, quantity, role);

        ProductResponseDto response = productService.addStock(productId, quantity, role, userId);
        log.info("재고 수량 추가 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 재고 수량 감소 | 모두 허용
     * @param productId
     * @param quantity
     * @return
     */
    @PatchMapping("/products/remove_stock/{product_id}")
    public Integer removeStock(@PathVariable("product_id") UUID productId,
                               @RequestParam("quantity") Integer quantity) {
        log.info("재고 수량 감소 시도 중 | productId: {}, quantity: {}", productId, quantity);

        Integer restStock = productService.removeStock(productId, quantity);
        log.info("재고 수량 감소 완료 | resStock: {}", restStock);
        return restStock;
    }

    /**
     * 재고 수량 복원 | 모두 허용
     * @param productId
     * @param quantity
     * @return
     */
    @PatchMapping("/products/cancel_stock/{product_id}")
    public Integer cancelStock(@PathVariable("product_id") UUID productId,
                               @RequestParam("quantity") Integer quantity) {
        log.info("재고 수량 복원 시도 중 | productId: {}, quantity: {}", productId, quantity);

        Integer restStock = productService.cancelStock(productId, quantity);
        log.info("재고 수량 복원 완료 | resStock: {}", restStock);
        return restStock;
    }

    /**
     * 상품 단건 조회 | 모두 허용
     * @param productId
     * @return
     */
    @GetMapping("/products/{product_id}")
    public ProductResponseDto findOneProduct(@PathVariable("product_id") UUID productId) {

        log.info("상품 단건 조회 시도 중 | productId: {}", productId);

        ProductResponseDto response = productService.findOneProduct(productId);

        log.info("상품 단건 조회 완료 | response: {}", response);
        return response;
    }

    /**
     * 상품 페이지 조회 | 모두 허용
     * @param pageable
     * @param condition
     * @return
     */
    @GetMapping("/products")
    public Page<ProductSearchResponseDto> findAll(@PageableDefault(size = 10) Pageable pageable,
                                                  ProductSearchRequestDto condition) {
        log.info("상품 페이지 조회 시도 중 | pageable: {}, condition: {}", pageable, condition);

        Page<ProductSearchResponseDto> response = productService.findAllProduct(pageable, condition);

        log.info("상품 페이지 조회 완료 | response: {}", response);
        return response;
    }

    /**
     * 마스터 권한 확인
     * @param role
     * @return
     */
    private boolean validateRoleMaster(String role) {
        if (!role.equals("MASTER")) {
            log.error("마스터만 접근 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "마스터만 접근 가능합니다.");
        }else {
            return true;
        }
    }

    /**
     * 배송 담당자 제외 검증
     * @param role
     * @return
     */
    private boolean validateExcludeRoleDeliveryPerson(String role) {
        if (role.equals("HUB_DELIVERY_PERSON")) {
            log.error("배송 담당자는 접근 불가능 합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "배송 담당자는 접근 불가능합니다.");
        }else {
            return true;
        }
    }
}
