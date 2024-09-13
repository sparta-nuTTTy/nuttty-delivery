package com.nuttty.eureka.auth.infrastructure.repository;

import com.nuttty.eureka.auth.application.dto.QUserInfoDto;
import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.dto.UserSearchResponseDto;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.domain.repository.UserRepositoryCustom;
import com.nuttty.eureka.auth.presentation.request.UserSearchRequestDto;
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

import static com.nuttty.eureka.auth.domain.model.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<UserSearchResponseDto> findAllUser(Pageable pageable, UserSearchRequestDto condition) {

        JPAQuery<UserInfoDto> query = queryFactory
                .select(new QUserInfoDto(
                        user.userId,
                        user.username,
                        user.email,
                        user.role,
                        user.createdAt,
                        user.createdBy,
                        user.updatedAt,
                        user.updatedBy
                ))
                .from(user)
                .where(userIdEq(condition.getUser_id()),
                        userRoleEq(condition.getRole()),
                        usernameContains(condition.getUsername()),
                        emailContains(condition.getEmail()),
                        user.isDelete.eq(false));

        if (pageable.getSort().isEmpty()) {
            query.orderBy(user.createdAt.asc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? user.createdAt.asc() : user.createdAt.desc());
                } else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? user.updatedAt.asc() : user.updatedAt.desc());
                }
            });
        }

        List<UserInfoDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(userIdEq(condition.getUser_id()),
                        userRoleEq(condition.getRole()),
                        usernameContains(condition.getUsername()),
                        emailContains(condition.getEmail()),
                        user.isDelete.eq(false));

        UserSearchResponseDto userSearchResponse = new UserSearchResponseDto(content);

        return PageableExecutionUtils.getPage(Collections.singletonList(userSearchResponse), pageable, countQuery::fetchOne);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.userId.eq(userId) : null;
    }

    private BooleanExpression userRoleEq(String role) {
        return role != null ? user.role.eq(UserRoleEnum.valueOf(role)) : null;
    }

    private BooleanExpression usernameContains(String username) {
        return hasText(username) ? user.username.contains(username) : null;
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? user.email.contains(email) : null;
    }
}
