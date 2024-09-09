package com.nuttty.eureka.auth.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    MASTER(Authority.MASTER),
    HUB_MANAGER(Authority.HUB_MANAGER),
    HUB_DELIVERY_PERSON(Authority.HUB_DELIVERY_PERSON),
    HUB_COMPANY(Authority.HUB_COMPANY);

    private final String authority;

    public static class Authority {
        public static final String MASTER = "MASTER";
        public static final String HUB_MANAGER = "HUB_MANAGER";
        public static final String HUB_DELIVERY_PERSON = "HUB_DELIVERY_PERSON";
        public static final String HUB_COMPANY = "HUB_COMPANY";
    }
}
