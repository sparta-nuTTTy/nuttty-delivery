package com.nuttty.eureka.company.presentation.controller;

import com.nuttty.eureka.company.application.service.ProductService;
import com.nuttty.eureka.company.presentation.request.ProductRequestDto;
import com.nuttty.eureka.company.presentation.request.ProductSearchRequestDto;
import com.nuttty.eureka.company.presentation.request.ProductUpdateRequestDto;
import com.nuttty.eureka.company.presentation.response.ProductDelResponseDto;
import com.nuttty.eureka.company.presentation.response.ProductResponseDto;
import com.nuttty.eureka.company.presentation.response.ProductSearchResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Tag(name = "상품", description = "상품 등록, 조회, 수정, 삭제 API")
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
    @Operation(summary = "상품 생성", description = "상품 생성 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품이 생성되었습니다."),
            @ApiResponse(responseCode = "403", description = "상품 생성 권한이 없습니다."),
    })
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDto request,

                                           @Parameter(hidden = true)
                                           @RequestHeader(value = "X-User-Role") String role,

                                           @Parameter(hidden = true)
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
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품이 수정되었습니다."),
            @ApiResponse(responseCode = "403", description = "상품 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "상품가 존재하지 않습니다.")
    })
    @PatchMapping("/products/{product_id}")
    public ResponseEntity<?> updateProduct(@Parameter(description = "수정할 상품 ID") @PathVariable("product_id") UUID productId,
                                           @Valid @RequestBody ProductUpdateRequestDto request,

                                           @Parameter(hidden = true)
                                           @RequestHeader(value = "X-User-Role") String role,

                                           @Parameter(hidden = true)
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
    @Operation(summary = "상품 삭제", description = "상품를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품이 삭제되었습니다."),
            @ApiResponse(responseCode = "403", description = "상품 삭제 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "상품이 존재하지 않습니다.")
    })
    @DeleteMapping("/products/{product_id}")
    public ResponseEntity<?> deleteProduct(@Parameter(description = "삭제할 상품 ID") @PathVariable("product_id") UUID productId,

                                           @Parameter(hidden = true)
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @Parameter(hidden = true)
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
    @Operation(summary = "상품 수량 추가", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품이 수정되었습니다."),
            @ApiResponse(responseCode = "403", description = "상품 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "상품가 존재하지 않습니다.")
    })
    @PatchMapping("/products/add_stock/{product_id}")
    public ResponseEntity<?> addStock(@Parameter(description = "수량 추가할 상품 ID") @PathVariable("product_id") UUID productId,
                                      @Parameter(description = "추가할 수량") @RequestParam("quantity") Integer quantity,

                                      @Parameter(hidden = true)
                                      @RequestHeader(value = "X-User-Role") String role,
                                      @Parameter(hidden = true)
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
    @Operation(summary = "상품 수량 감소", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품이 수정되었습니다."),
            @ApiResponse(responseCode = "403", description = "상품 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "상품가 존재하지 않습니다.")
    })
    @PatchMapping("/products/remove_stock/{product_id}")
    public Integer removeStock(@Parameter(description = "수량 감소할 상품 ID") @PathVariable("product_id") UUID productId,
                               @Parameter(description = "감소할 수량") @RequestParam("quantity") Integer quantity) {
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
    @Operation(summary = "상품 수량 복원", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품이 수정되었습니다."),
            @ApiResponse(responseCode = "404", description = "상품가 존재하지 않습니다.")
    })
    @PatchMapping("/products/cancel_stock/{product_id}")
    public Integer cancelStock(@Parameter(description = "수량 복원할 상품 ID") @PathVariable("product_id") UUID productId,
                               @Parameter(description = "복원할 수량") @RequestParam("quantity") Integer quantity) {
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
    @Operation(summary = "상품 단건 조회", description = "상품 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품이 존재하지 않습니다.")
    })
    @GetMapping("/products/{product_id}")
    public ProductResponseDto findOneProduct(@Parameter(description = "조회할 상품 ID") @PathVariable("product_id") UUID productId) {

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
    @Operation(
            summary = "상품 전체 조회",
            description = "모든 상품을 페이징하여 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "product_id",
                            description = "상품의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "company_id",
                            description = "업체의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "hub_id",
                            description = "허브의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "product_name",
                            description = "상품의 이름",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업체 전체 조회 성공")
    })
    @GetMapping("/products")
    public Page<ProductSearchResponseDto> findAll(@ParameterObject @PageableDefault(size = 10) Pageable pageable,
                                                  @Parameter(hidden = true) ProductSearchRequestDto condition) {
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
