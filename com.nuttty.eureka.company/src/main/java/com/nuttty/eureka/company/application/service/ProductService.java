package com.nuttty.eureka.company.application.service;

import com.nuttty.eureka.company.application.dto.ProductDto;
import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.domain.model.Product;
import com.nuttty.eureka.company.exception.exceptionsdefined.MissmatchException;
import com.nuttty.eureka.company.infastructure.repository.CompanyRepository;
import com.nuttty.eureka.company.infastructure.repository.ProductRepository;
import com.nuttty.eureka.company.presentation.request.ProductRequestDto;
import com.nuttty.eureka.company.presentation.request.ProductSearchRequestDto;
import com.nuttty.eureka.company.presentation.request.ProductUpdateRequestDto;
import com.nuttty.eureka.company.presentation.response.ProductDelResponseDto;
import com.nuttty.eureka.company.presentation.response.ProductResponseDto;
import com.nuttty.eureka.company.presentation.response.ProductSearchResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    /**
     * 상품 등록 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param request
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request, Long userId, String role) {

        if (role.equals("MASTER") || role.equals("HUB_MANAGER")) {
            // 업체 ID, 허브 ID 업체에서 조회 v
            Company findCompany = companyRepository.findByCompanyIdAndHubId(request.getCompany_id(), request.getHub_id()).orElseThrow(() ->
                    new EntityNotFoundException("company_id: " + request.getCompany_id() + ", hub_id: " + request.getHub_id() + " 일치하는 Company 없음"));

            // 같은 업체 내 상품 이름 중복 확인 v
            if (productRepository.existsByNameAndCompanyIdAndHubId(request.getProduct_name(), request.getCompany_id(), request.getHub_id())) {
                log.error(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
                throw new DataIntegrityViolationException(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
            }

            Product savedProduct = productRepository.save(request.toEntity(findCompany));
            return new ProductResponseDto(HttpStatus.CREATED.value(), "product created", ProductDto.toDto(savedProduct));

        }else { // HUB_COMPANY

            // 업체 ID, 허브 ID, user ID (본인 업체 확인) v
            Company findCompany = companyRepository.findByCompanyIdAndHubIdAndUserId(request.getCompany_id(), request.getHub_id(), userId).orElseThrow(() ->
                    new EntityNotFoundException("본인 가게의 업체 ID, HUB ID 가 일치하여야 등록 가능합니다."));

            // 상품 이름 중복 확인 v
            if (productRepository.existsByNameAndCompanyIdAndHubId(request.getProduct_name(), request.getCompany_id(), request.getHub_id())) {
                log.error(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
                throw new DataIntegrityViolationException(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
            }

            Product savedProduct = productRepository.save(request.toEntity(findCompany));
            return new ProductResponseDto(HttpStatus.CREATED.value(), "product created", ProductDto.toDto(savedProduct));
        }
    }

    /**
     * 상품 수정 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param productId
     * @param request
     * @param role
     * @param userId
     * @return
     */
    @Transactional
    public ProductResponseDto updateProduct(UUID productId, ProductUpdateRequestDto request, String role, Long userId) {

        if (role.equals("MASTER") || role.equals("HUB_MANAGER")) {

            // 상품 이름 중복 확인 v
            if (productRepository.existsByProductNameExcludeId(request.getProduct_name(), productId)) {
                log.error(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
                throw new DataIntegrityViolationException(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
            }

            Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                    new EntityNotFoundException("not found product with id: " + productId));

            Product updatedProduct = findProduct.update(request);
            return new ProductResponseDto(HttpStatus.OK.value(), "product updated", ProductDto.toDto(updatedProduct));

        } else { // HUB_COMPANY

            // 본인 업체 상품 변경인지 확인 (본인 여부)
            Company findCompany = companyRepository.findByUserId(userId).orElseThrow(() ->
                    new EntityNotFoundException("not found company with userId: " + userId));

            Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                    new EntityNotFoundException("not found product with id: " + productId));

            if (!findCompany.getId().equals(findProduct.getCompany().getId())) {
                log.error("본인 업체의 상품만 수정 가능합니다.");
                throw new MissmatchException("본인 업체의 상품만 수정 가능합니다.");
            }

            // 상품 이름 중복 확인 v
            if (productRepository.existsByProductNameExcludeId(request.getProduct_name(), productId)) {
                log.error(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
                throw new DataIntegrityViolationException(request.getProduct_name() + " 이미 존재하는 상품 이름입니다.");
            }

            Product updatedProduct = findProduct.update(request);
            return new ProductResponseDto(HttpStatus.OK.value(), "product updated", ProductDto.toDto(updatedProduct));
        }
    }

    /**
     * 상품 삭제 | 마스터 허용
     * @param productId
     * @param email
     * @return
     */
    @Transactional
    public ProductDelResponseDto deleteProduct(UUID productId, String email) {
        Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                new EntityNotFoundException("not found product with id: " + productId));

        findProduct.delete(email);
        return new ProductDelResponseDto(HttpStatus.OK.value(), "product deleted", productId + " deleted");
    }

    /**
     * 재고 수량 추가 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param productId
     * @param quantity
     * @param role
     * @param userId
     * @return
     */
    @Transactional
    public ProductResponseDto addStock(UUID productId, Integer quantity, String role, Long userId) {

        if (role.equals("MASTER") || role.equals("HUB_MANAGER")) {
            Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                    new EntityNotFoundException("not found product with id: " + productId));

            findProduct.addStock(quantity);
            return new ProductResponseDto(HttpStatus.OK.value(), "product added", ProductDto.toDto(findProduct));

        }else { // HUB_COMPANY

            // 본인 업체 상품 변경인지 확인 (본인 여부)
            Company findCompany = companyRepository.findByUserId(userId).orElseThrow(() ->
                    new EntityNotFoundException("not found company with userId: " + userId));

            Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                    new EntityNotFoundException("not found product with id: " + productId));

            if (!findCompany.getId().equals(findProduct.getCompany().getId())) {
                log.error("본인 업체의 상품만 수정 가능합니다.");
                throw new MissmatchException("본인 업체의 상품만 수정 가능합니다.");
            }

            findProduct.addStock(quantity);
            return new ProductResponseDto(HttpStatus.OK.value(), "product added", ProductDto.toDto(findProduct));
        }
    }

    /**
     * 재고 수량 감소 | 모두 허용
     * @param productId
     * @param quantity
     * @return
     */
    @Transactional
    public Integer removeStock(UUID productId, Integer quantity) {

        Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                new EntityNotFoundException("not found product with id: " + productId));

        return findProduct.removeStock(quantity);
    }

    /**
     * 수량 복원 | 모두 허용
     * @param productId
     * @param quantity
     * @return
     */
    @Transactional
    public Integer cancelStock(UUID productId, Integer quantity) {

        Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                new EntityNotFoundException("not found product with id: " + productId));

        return findProduct.cancelStock(quantity);
    }

    /**
     * 상품 단건 조회 | 모두 허용
     * @param productId
     * @return
     */
    public ProductResponseDto findOneProduct(UUID productId) {

        Product findProduct = productRepository.findById(productId).orElseThrow(() ->
                new EntityNotFoundException("not found product with id: " + productId));

        return new ProductResponseDto(HttpStatus.OK.value(), "product found", ProductDto.toDto(findProduct));
    }

    /**
     * 상품 페이지 조회 | 모두 허용
     * @param pageable
     * @param condition
     * @return
     */
    public Page<ProductSearchResponseDto> findAllProduct(Pageable pageable, ProductSearchRequestDto condition) {
        return productRepository.findAllProduct(pageable, condition);
    }
}
