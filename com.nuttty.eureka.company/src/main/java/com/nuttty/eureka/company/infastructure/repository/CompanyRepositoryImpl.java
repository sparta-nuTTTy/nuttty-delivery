package com.nuttty.eureka.company.infastructure.repository;

import com.nuttty.eureka.company.application.dto.CompanyDto;
import com.nuttty.eureka.company.application.dto.QCompanyDto;
import com.nuttty.eureka.company.domain.repository.CompanyRepositoryCustom;
import com.nuttty.eureka.company.presentation.request.CompanySearchRequestDto;
import com.nuttty.eureka.company.presentation.response.CompanySearchResponseDto;
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

import static com.nuttty.eureka.company.domain.model.QCompany.company;
import static org.springframework.util.StringUtils.hasText;

public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CompanyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CompanySearchResponseDto> findAllCompany(Pageable pageable, CompanySearchRequestDto condition) {
        JPAQuery<CompanyDto> query = queryFactory
                .select(new QCompanyDto(
                        company.id,
                        company.userId,
                        company.hubId,
                        company.name,
                        company.type.stringValue(),
                        company.address,
                        company.createdAt,
                        company.createdBy,
                        company.updatedAt,
                        company.updatedBy
                ))
                .from(company)
                .where(companyIdEq(condition.getCompany_id()),
                        userIdEq(condition.getUser_id()),
                        hubIdEq(condition.getHub_id()),
                        nameContains(condition.getName()),
                        typeContains(condition.getType()),
                        addressContains(condition.getAddress()));

        if (pageable.getSort().isSorted()) {
            query.orderBy(company.createdAt.asc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? company.createdAt.asc() : company.createdAt.desc());
                }else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? company.updatedAt.asc() : company.updatedAt.desc());
                }
            });
        }

        List<CompanyDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(company.count())
                .from(company)
                .where(companyIdEq(condition.getCompany_id()),
                        userIdEq(condition.getUser_id()),
                        hubIdEq(condition.getHub_id()),
                        nameContains(condition.getName()),
                        typeContains(condition.getType()),
                        addressContains(condition.getAddress()));

        CompanySearchResponseDto companySearchResponse = new CompanySearchResponseDto(
                HttpStatus.OK.value(),
                "company found",
                content
        );

        return PageableExecutionUtils.getPage(Collections.singletonList(companySearchResponse), pageable, countQuery::fetchOne);
    }

    private BooleanExpression addressContains(String address) {
        return hasText(address) ? company.address.contains(address) : null;
    }

    private BooleanExpression typeContains(String type) {
        return hasText(type) ? company.type.stringValue().contains(type) : null;
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? company.name.contains(name) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? company.hubId.eq(hubId) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? company.userId.eq(userId) : null;
    }

    private BooleanExpression companyIdEq(UUID companyId) {
        return companyId != null ? company.id.eq(companyId) : null;
    }
}
