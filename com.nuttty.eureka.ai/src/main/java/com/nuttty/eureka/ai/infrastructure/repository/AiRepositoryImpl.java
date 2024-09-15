package com.nuttty.eureka.ai.infrastructure.repository;

import com.nuttty.eureka.ai.application.dto.ai.AiDto;
import com.nuttty.eureka.ai.application.dto.ai.AiSearchRequestDto;
import com.nuttty.eureka.ai.application.dto.ai.QAiDto;
import com.nuttty.eureka.ai.domain.repository.AiRepositoryCustom;
import com.nuttty.eureka.ai.presentation.response.AiResponseDto;
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

import static com.nuttty.eureka.ai.domain.model.QAi.ai;
import static org.springframework.util.StringUtils.hasText;

public class AiRepositoryImpl implements AiRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AiRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AiResponseDto> findAllAi(Pageable pageable, AiSearchRequestDto condition) {

        JPAQuery<AiDto> query = queryFactory
                .select(new QAiDto(
                        ai.id.as("aiId"),
                        ai.deliveryManagerId.as("deliveryManagerId"),
                        ai.question,
                        ai.answer
                ))
                .from(ai)
                .where(aiIdEq(condition.getAiId()),
                        deliveryManagerIdEq(condition.getDeliveryManagerId()),
                        questionContains(condition.getQuestion()),
                        answerContains(condition.getAnswer()));

        if (pageable.getSort().isEmpty()) {
            query.orderBy(ai.createdAt.asc());
        }else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {

                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? ai.createdAt.asc() : ai.createdAt.desc());
                } else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? ai.updatedAt.asc() : ai.updatedAt.desc());
                }
            });
        }

        List<AiDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ai.count())
                .from(ai)
                .where(aiIdEq(condition.getAiId()),
                        deliveryManagerIdEq(condition.getDeliveryManagerId()),
                        questionContains(condition.getQuestion()),
                        answerContains(condition.getAnswer()));

        AiResponseDto aiFound = new AiResponseDto(
                HttpStatus.OK.value(),
                "ai found",
                content
        );

        return PageableExecutionUtils.getPage(Collections.singletonList(aiFound), pageable, countQuery::fetchOne);
    }

    private BooleanExpression answerContains(String answer) {
        return hasText(answer) ? ai.answer.contains(answer) : null;
    }

    private BooleanExpression questionContains(String question) {
        return hasText(question) ? ai.question.contains(question) : null;
    }

    private BooleanExpression deliveryManagerIdEq(UUID deliveryManagerId) {
        return deliveryManagerId != null ? ai.deliveryManagerId.eq(deliveryManagerId) : null;
    }

    private BooleanExpression aiIdEq(UUID aiId) {
        return aiId != null ? ai.id.eq(aiId) : null;
    }
}
