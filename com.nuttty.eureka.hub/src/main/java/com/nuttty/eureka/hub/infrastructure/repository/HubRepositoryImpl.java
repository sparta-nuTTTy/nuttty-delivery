package com.nuttty.eureka.hub.infrastructure.repository;

import com.nuttty.eureka.hub.application.dto.HubDto;
import com.nuttty.eureka.hub.application.dto.QHubDto;
import com.nuttty.eureka.hub.domain.repository.HubRepositoryCustom;
import com.nuttty.eureka.hub.presestation.request.HubSearchRequestDto;
import com.nuttty.eureka.hub.presestation.response.HubSearchResponseDto;
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

import static com.nuttty.eureka.hub.domain.model.QHub.hub;
import static org.springframework.util.StringUtils.hasText;

public class HubRepositoryImpl implements HubRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public HubRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<HubSearchResponseDto> findAllHub(Pageable pageable, HubSearchRequestDto condition) {

        JPAQuery<HubDto> query = queryFactory
                .select(new QHubDto(
                        hub.id,
                        hub.userId,
                        hub.name,
                        hub.address,
                        hub.latitude,
                        hub.longitude,
                        hub.createdAt,
                        hub.createdBy,
                        hub.updatedAt,
                        hub.updatedBy
                ))
                .from(hub)
                .where(hubIdEq(condition.getHub_id()),
                        userIdEq(condition.getUser_id()),
                        nameContains(condition.getName()),
                        addressContains(condition.getAddress()),
                        hub.isDelete.eq(false));

        if (pageable.getSort().isEmpty()) {
            query.orderBy(hub.createdAt.asc());
        }else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? hub.createdAt.asc() : hub.createdAt.desc());
                } else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? hub.updatedAt.asc() : hub.updatedAt.desc());
                }
            });
        }

        List<HubDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hub.count())
                .from(hub)
                .where(hubIdEq(condition.getHub_id()),
                        userIdEq(condition.getUser_id()),
                        nameContains(condition.getName()),
                        addressContains(condition.getAddress()),
                        hub.isDelete.eq(false));

        HubSearchResponseDto hubSearchResponse = new HubSearchResponseDto(
                HttpStatus.OK.value(),
                "hub found",
                content
        );

        return PageableExecutionUtils.getPage(Collections.singletonList(hubSearchResponse), pageable, countQuery::fetchOne);
    }

    private BooleanExpression addressContains(String address) {
        return hasText(address) ? hub.address.contains(address) : null;
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? hub.name.contains(name) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? hub.userId.eq(userId) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? hub.id.eq(hubId) : null;
    }
}
