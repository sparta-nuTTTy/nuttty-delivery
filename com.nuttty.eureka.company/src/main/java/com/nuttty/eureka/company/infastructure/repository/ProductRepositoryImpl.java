package com.nuttty.eureka.company.infastructure.repository;

import com.nuttty.eureka.company.application.dto.ProductDto;
import com.nuttty.eureka.company.application.dto.QProductDto;
import com.nuttty.eureka.company.domain.repository.ProductRepositoryCustom;
import com.nuttty.eureka.company.presentation.request.ProductSearchRequestDto;
import com.nuttty.eureka.company.presentation.response.ProductSearchResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.nuttty.eureka.company.domain.model.QProduct.product;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductSearchResponseDto> findAllProduct(Pageable pageable, ProductSearchRequestDto condition) {

        JPAQuery<ProductDto> query = queryFactory
                .select(new QProductDto(
                        product.id,
                        product.company.id,
                        product.hubId,
                        product.productName,
                        product.productPrice,
                        product.productQuantity,
                        product.createdAt,
                        product.createdBy,
                        product.updatedAt,
                        product.updatedBy
                ))
                .from(product)
                .where(productIdEq(condition.getProduct_id()),
                        companyIdEq(condition.getCompany_id()),
                        hubIdEq(condition.getHub_id()),
                        productNameContains(condition.getProduct_name()));

        if (pageable.getSort().isEmpty()) {
            query.orderBy(product.createdAt.asc());
        }else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {

                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? product.createdAt.asc() : product.createdAt.desc());
                } else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? product.updatedAt.asc() : product.updatedAt.desc());

                }
            });
        }

        List<ProductDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(productIdEq(condition.getProduct_id()),
                        companyIdEq(condition.getCompany_id()),
                        hubIdEq(condition.getHub_id()),
                        productNameContains(condition.getProduct_name()));

        ProductSearchResponseDto productFound = new ProductSearchResponseDto(
                HttpStatus.OK.value(),
                "product found",
                content
        );

        return PageableExecutionUtils.getPage(Collections.singletonList(productFound), pageable, countQuery::fetchOne);
    }

    private BooleanExpression productNameContains(String productName) {
        return hasText(productName) ? product.productName.contains(productName) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? product.hubId.eq(hubId) : null;
    }

    private BooleanExpression companyIdEq(UUID companyId) {
        return companyId != null ? product.company.id.eq(companyId) : null;
    }

    private BooleanExpression productIdEq(UUID productId) {
        return productId != null ? product.id.eq(productId) : null;
    }
}
