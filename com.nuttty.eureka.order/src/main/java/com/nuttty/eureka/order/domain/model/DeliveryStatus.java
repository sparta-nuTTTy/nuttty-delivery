package com.nuttty.eureka.order.domain.model;

/**
 * 배송 상태 나타내는 Enum <br>
 * 상태는 준비 중, 허브 상차, 허브 이동중, 허브 도착, 허브 하차, 배송지 이동중, 배송완료
 */
public enum DeliveryStatus {
    PREPARING("준비 중"),
    HUB_LOADING("허브 상차"),
    HUB_MOVING("허브 이동중"),
    HUB_ARRIVAL("허브 도착"),
    HUB_UNLOADING("허브 하차"),
    DELIVERY_MOVING("배송지 이동중"),
    DELIVERY_COMPLETE("배송완료");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
