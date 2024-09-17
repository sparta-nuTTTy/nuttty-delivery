package com.nuttty.eureka.ai.domain.service;

import com.nuttty.eureka.ai.application.client.CompanyClient;
import com.nuttty.eureka.ai.application.client.OrderClient;
import com.nuttty.eureka.ai.application.dto.company.CompanyDto;
import com.nuttty.eureka.ai.application.dto.order.OrderDto;
import com.nuttty.eureka.ai.application.dto.order.OrderRequestDto;
import com.nuttty.eureka.ai.exception.exceptionsdefined.ClientException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
public class HubMappingOrderService {

    private final OrderClient orderClient;
    private final CompanyClient companyClient;

    /**
     * 허브 별 주문 정보 매핑
     */
    @Transactional
    public String getOrder() {

        // 전일 오전 8시에서 현재 오전 7시 59분
        LocalDateTime startTime = LocalDateTime.now().minusDays(2).withHour(8).withMinute(0).withSecond(0);
        LocalDateTime endTime = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        // 전일 오전 8시에서 현재 오전 7시 59분 주문 10만 건 조회
        OrderRequestDto findAllOrder = null;
        try {
            findAllOrder = orderClient.findAllOrder(startTime, endTime, 100000);

        } catch (FeignException e) {
            int statusCode = e.status();
            String errorMessage = e.getMessage();
            log.error("Error occurred with status code: " + statusCode + ", message: " + e.getMessage());
            throw new ClientException(errorMessage, statusCode);

        } catch (Exception e) {
            log.error("Error occurred with exception: " + e.getMessage());
            throw new ClientException(e.getMessage());
        }

        // 조회된 주문 없을 시 null 반환
        if (findAllOrder == null) {
            return null;
        }
        Page<OrderDto> orderList = findAllOrder.getData();

        // 주문을 공급 업체 supplierId 로 매핑
        Map<UUID, List<OrderDto>> supplierIdMapping = new HashMap<>();

        // 주문을 supplierId 로 매핑
        for (OrderDto order : orderList) {
            UUID supplierId = order.getSupplierId();

            // supplierId 가 있을 경우 그대로 반환, 없을 경우 새로운 리스트 생성
            supplierIdMapping.computeIfAbsent(supplierId, k -> new ArrayList<>());

            supplierIdMapping.get(supplierId).add(order);
        }

        // 맵에서 공급업체 키 값만 추출
        Set<UUID> supplierKeySet = supplierIdMapping.keySet();
        List<UUID> supplierKeyList = new ArrayList<>(supplierKeySet);

        // 공급업체 키 값으로 업체 전체 조회
        List<CompanyDto> findAllCompany = null;
        try {
            findAllCompany = companyClient.findAllSupplierId(supplierKeyList);

        }catch (FeignException e) {
            int statusCode = e.status();
            String errorMessage = e.getMessage();
            log.error("Error occurred with status code: " + statusCode + ", message: " + e.getMessage());
            throw new ClientException(errorMessage, statusCode);

        } catch (Exception e) {
            log.error("Error occurred with exception: " + e.getMessage());
            throw new ClientException(e.getMessage());
        }

        // 허브 ID 별로 주문을 매핑
        Map<UUID, List<OrderDto>> hubOrdersMap = new HashMap<>();

        // 허브 ID 별로 주문 매핑
        for (int i = 0; i < findAllCompany.size(); i++) {
            UUID hubId = findAllCompany.get(i).getHubId();
            UUID companyId = findAllCompany.get(i).getCompanyId();

            List<OrderDto> orderDtos = supplierIdMapping.get(companyId);
            hubOrdersMap.computeIfAbsent(hubId, k -> new ArrayList<>());

            if (orderDtos != null) {
                hubOrdersMap.get(hubId).addAll(orderDtos);
            }
        }
        StringBuilder sb = new StringBuilder();
        // 출력 코드
        for (Map.Entry<UUID, List<OrderDto>> entry : hubOrdersMap.entrySet()) {
            UUID key = entry.getKey();
            List<OrderDto> orders = entry.getValue();

            System.out.println("허브 ID: " + key);
            sb.append("허브 ID = " + key);
            for (OrderDto order : orders) {
                System.out.println(order);
                sb.append("주문 정보 = " + order);
            }
        }
        return sb.toString();
    }
}
