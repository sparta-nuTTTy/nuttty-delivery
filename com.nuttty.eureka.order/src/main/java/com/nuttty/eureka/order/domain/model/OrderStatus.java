package com.nuttty.eureka.order.domain.model;

/**
 * 주문 상태 나타내는 Enum <br>
 * 상태는 주문완료, 주문취소
 */
public enum OrderStatus {
    ORDERED("주문완료"),
    CANCELED("주문취소");

    private final String status;

    OrderStatus(String status) {
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
