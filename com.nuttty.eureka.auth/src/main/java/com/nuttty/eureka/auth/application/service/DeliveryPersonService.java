package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.client.HubClient;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonInfoDto;
import com.nuttty.eureka.auth.domain.model.DeliveryPerson;
import com.nuttty.eureka.auth.domain.model.DeliveryPersonTypeEnum;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.exception.custom.DeliveryPersonAlreadyExistsException;
import com.nuttty.eureka.auth.infrastructure.repository.DeliveryPersonRepository;
import com.nuttty.eureka.auth.infrastructure.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonCreateDto;
import com.nuttty.eureka.auth.presentation.request.HubRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final UserRepository userRepository;
    private final HubClient hubClient;


    @Transactional
    @CachePut(cacheNames = "deliveryPersonInfoCache", key = "#role + ':' + #userId + ':' + #result.user_id")
    public DeliveryPersonInfoDto createDeliveryPerson(String role, Long userId, DeliveryPersonCreateDto createDto) {
        // 회원 가입 유무 검사
        User user = userRepository.findById(createDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 배송 담당자 권한 유저만 등록 가능
        if (!user.getRole().equals(UserRoleEnum.HUB_DELIVERY_PERSON)) {
            throw new AccessDeniedException("배송 담당자 권한인 유저가 아닙니다.");
        }

        // 허브 관리자 권한 경우
        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)){
            // 허브 존재 여부 검사
            HubRequestDto hubInfo = hubClient.findOneHub(createDto.getHubId()).getBody();
            if (hubInfo == null) {
                throw new EntityNotFoundException("등록되지 않은 허브입니다.");
            }

            // 배송 담당자를 등록할 허브의 허브관리자와 로그인한 허브 매니저의 일치여부 검사
            if(!hubInfo.getHubDto().getUser_id().equals(userId)){
                throw new AccessDeniedException("허브 관리자님의 허브가 아닙니다.");
            }
        }

        // 배송담당자 타입이 업체 배송 담당자인 경우, 소속 허브ID가 존재하는 허브인지 확인
        if (DeliveryPersonTypeEnum.valueOf(createDto.getDeliveryPersonType())
                .equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
            HubRequestDto hubInfo = hubClient.findOneHub(createDto.getHubId()).getBody();
            if (hubInfo == null) {
                throw new EntityNotFoundException("등록되지 않은 허브입니다.");
            }
        } else { // 허브 이동 배송 담당자 경우 == 소속 허브 필요 x (등록 x)
            createDto.setHubId(null);
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


    // 배송 담당자 개별 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "deliveryPersonInfoCache", key = "#role + ':' + #userId + ':' + #deliveryPersonId")
    public DeliveryPersonInfoDto getDeliveryPersonInfo(String role, Long userId, Long deliveryPersonId) {
        // 배송 담당자 등록 여부 검사
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 배송 담당자입니다."));

        // 권한에 따른 구분
        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_DELIVERY_PERSON)) { // 권한 == 배송 담당자
            if (!userId.equals(deliveryPersonId)) { // 본인의 정보만 조회 가능 - deliveryPersonId == userId
                throw new AccessDeniedException("본인의 정보가 아닙니다.");
            }

        } else if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) { // 권한 == 허브 관리자
            // 배송 담당자의 허브의 허브 관리자 Id 가져오기
            HubRequestDto hubInfo = hubClient.findOneHub(deliveryPerson.getHubId()).getBody();
            if (hubInfo == null) {
                throw new EntityNotFoundException("등록되지 않은 허브입니다.");
            }

            // 로그인한 유저와 허브 관리자 Id 비교
            if (!userId.equals(hubInfo.getHubDto().getUser_id())) {
                throw new AccessDeniedException("허브 관리자님 허브 소속의 배송담당자가 아닙니다.");
            }
        }

        return DeliveryPersonInfoDto.of(deliveryPerson);
    }
}
