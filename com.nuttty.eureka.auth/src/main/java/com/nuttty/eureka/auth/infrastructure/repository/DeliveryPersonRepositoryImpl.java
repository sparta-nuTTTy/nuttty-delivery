package com.nuttty.eureka.auth.infrastructure.repository;

import com.nuttty.eureka.auth.application.dto.DeliveryPersonInfoDto;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonSearchResponseDto;
import com.nuttty.eureka.auth.application.dto.QDeliveryPersonInfoDto;
import com.nuttty.eureka.auth.domain.model.DeliveryPersonTypeEnum;
import com.nuttty.eureka.auth.domain.repository.DeliveryPersonRepositoryCustom;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonSearchRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.nuttty.eureka.auth.domain.model.QDeliveryPerson.deliveryPerson;

@Repository
public class DeliveryPersonRepositoryImpl implements DeliveryPersonRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DeliveryPersonRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<DeliveryPersonSearchResponseDto> findAllDeliveryPerson(Pageable pageable, DeliveryPersonSearchRequestDto condition) {

        // 기본 쿼리 구성
        JPAQuery<DeliveryPersonInfoDto> query = queryFactory
                .select(new QDeliveryPersonInfoDto(
                        deliveryPerson.deliveryPersonId,
                        deliveryPerson.userId,
                        deliveryPerson.hubId,
                        deliveryPerson.deliveryPersonType,
                        deliveryPerson.slackId,
                        deliveryPerson.createdAt,
                        deliveryPerson.createdBy,
                        deliveryPerson.updatedAt,
                        deliveryPerson.updatedBy
                ))
                .from(deliveryPerson)
                .where(userIdEq(condition.getUser_id()),
                        deliveryPersonTypeEq(condition.getType()),
                        hubIdEq(condition.getHub_id()),
                        slackIdEq(condition.getSlack_id()),
                        deliveryPerson.isDelete.eq(false));


        // 권한 == 허브 관리자일 경우, 자신의 허브에 소속된 배송 담당자만 조회 - 추가 필요

        // 페이징 및 정렬
        if (pageable.getSort().isEmpty()) {
            query.orderBy(deliveryPerson.createdAt.asc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? deliveryPerson.createdAt.asc() : deliveryPerson.createdAt.desc());
                } else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? deliveryPerson.updatedAt.asc() : deliveryPerson.updatedAt.desc());
                }
            });
        }

        // 결과 조회 및 페이징 처리
        List<DeliveryPersonInfoDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(deliveryPerson.count())
                .from(deliveryPerson)
                .where(userIdEq(condition.getUser_id()),
                        deliveryPersonTypeEq(condition.getType()),
                        hubIdEq(condition.getHub_id()),
                        slackIdEq(condition.getSlack_id()),
                        deliveryPerson.isDelete.eq(false));

        DeliveryPersonSearchResponseDto deliveryPersonSearchResponse = new DeliveryPersonSearchResponseDto(content);

        return PageableExecutionUtils.getPage(Collections.singletonList(deliveryPersonSearchResponse), pageable, countQuery::fetchOne);
    }


    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? deliveryPerson.userId.eq(userId) : null;
    }

    private BooleanExpression deliveryPersonTypeEq(String type) {
        return type != null ? deliveryPerson.deliveryPersonType.eq(DeliveryPersonTypeEnum.valueOf(type)) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? deliveryPerson.hubId.eq(hubId) : null;
    }

    private BooleanExpression slackIdEq(String slackId) {
        return slackId != null ? deliveryPerson.slackId.eq(slackId) : null;
    }
}
