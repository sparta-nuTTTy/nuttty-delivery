package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.client.HubClient;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonInfoDto;
import com.nuttty.eureka.auth.domain.model.DeliveryPerson;
import com.nuttty.eureka.auth.domain.model.DeliveryPersonTypeEnum;
import com.nuttty.eureka.auth.exception.custom.DeliveryPersonAlreadyExistsException;
import com.nuttty.eureka.auth.infrastructure.repository.DeliveryPersonRepository;
import com.nuttty.eureka.auth.infrastructure.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonCreateDto;
import com.nuttty.eureka.auth.presentation.request.HubRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final UserRepository userRepository;
    private final HubClient hubClient;


    @Transactional
    @CachePut(cacheNames = "deliveryPersonInfoCache", key = "#result.user_id")
    public DeliveryPersonInfoDto createDeliveryPerson(DeliveryPersonCreateDto createDto) {
        // 회원 가입 유무 검사
        if (!userRepository.existsById(createDto.getUserId())) {
            throw new EntityNotFoundException("등록되지 않는 유저입니다.");
        }

        // 배송담당자 타입이 업체 배송 담당자인 경우, 소속 허브ID가 존재하는 허브인지 확인
        if (DeliveryPersonTypeEnum.valueOf(createDto.getDeliveryPersonType())
                .equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
            HubRequestDto hubInfo = hubClient.findOneHub(createDto.getHubId()).getBody();
            if (hubInfo == null) {
                throw new EntityNotFoundException("등록되지 않은 허브입니다.");
            }
        }

        // 배송 담당자로 등록된 회원인지 검사
        if (deliveryPersonRepository.existsById(createDto.getUserId())) {
            throw new DeliveryPersonAlreadyExistsException("이미 배송 담당자로 등록된 유저입니다.");
        }

        // 배송 담당자 등록
        DeliveryPerson deliveryPerson = DeliveryPerson
                .create(createDto.getUserId(),
                        createDto.getHubId(),
                        DeliveryPersonTypeEnum.valueOf(createDto.getDeliveryPersonType()),
                        createDto.getSlackId());
        deliveryPersonRepository.save(deliveryPerson);

        return DeliveryPersonInfoDto.of(deliveryPerson);
    }
}
