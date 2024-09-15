package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.client.HubClient;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonInfoDto;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonSearchResponseDto;
import com.nuttty.eureka.auth.domain.model.DeliveryPerson;
import com.nuttty.eureka.auth.domain.model.DeliveryPersonTypeEnum;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.exception.custom.DeliveryPersonAlreadyExistsException;
import com.nuttty.eureka.auth.infrastructure.repository.DeliveryPersonRepository;
import com.nuttty.eureka.auth.infrastructure.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonCreateDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonSearchRequestDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonTypeUpdateRequestDto;
import com.nuttty.eureka.auth.presentation.request.HubRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) {
            // 허브 존재 여부 검사
            HubRequestDto hubInfo = hubClient.findOneHub(createDto.getHubId()).getBody();
            if (hubInfo == null) {
                throw new EntityNotFoundException("등록되지 않은 허브입니다.");
            }

            // 배송 담당자를 등록할 허브의 허브관리자와 로그인한 허브 매니저의 일치여부 검사
            if (!hubInfo.getHubDto().getUser_id().equals(userId)) {
                throw new AccessDeniedException("허브 관리자님의 허브가 아닙니다.");
            }
        }

        // 배송담당자 타입이 업체 배송 담당자인 경우, 소속 허브ID가 존재하는 허브인지 확인
        if (DeliveryPersonTypeEnum.valueOf(createDto.getDeliveryPersonType()).equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
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

        // 권한 == 배송 담당자
        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_DELIVERY_PERSON)) {
            if (!userId.equals(deliveryPersonId)) { // 본인의 정보만 조회 가능 - deliveryPersonId == userId
                throw new AccessDeniedException("본인의 정보가 아닙니다.");
            }
        }
        // 권한 == 허브 관리자
        else if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) {
            // 업체 배송 담당자 경우 허브 Id 존재 - 소속 허브Id 관리자와 로그인 유저 일치 여부 검사
            if (deliveryPerson.getDeliveryPersonType().equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
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
        }

        return DeliveryPersonInfoDto.of(deliveryPerson);
    }


    // 배송 담당자 정보 수정
    @Transactional
    @CachePut(cacheNames = "deliveryPersonInfoCache", key = "#role + ':' + #userId + ':' + #deliveryPersonId")
    @CacheEvict(cacheNames = "deliveryPersonSearchInfoCache", allEntries = true)
    public DeliveryPersonInfoDto updateDeliveryPersonType(String role, Long userId, Long deliveryPersonId, DeliveryPersonTypeUpdateRequestDto updateDto) {
        // 배송 담당자 등록 여부 검사
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 배송 담당자입니다."));

        // 권한 == 허브 관리자
        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) {
            // 업체 배송 담당자 경우 허브 Id 존재 - 소속 허브Id 관리자와 로그인 유저 일치 여부 검사
            if (deliveryPerson.getDeliveryPersonType().equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
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
            // 허브 이동 배송 담당자 경우 소속된 hubId 존재X - 검사 필요 X
        }
        // 권한 == 마스터 >> 검사 필요 X

        // 기존 배송 담당자 타입이 배송 담당자인 경우를 확인하는 것은 불필요하다 생각 됨 >> 생성할때 이미 허브의 존재를 확인했기 때문에

        DeliveryPersonTypeEnum updateType = DeliveryPersonTypeEnum.valueOf(updateDto.getType()); // 수정할 타입
        UUID updateHubId = null; // 수정할 허브 id
        // 수정할 배송 담당자 타입이 업체 배송 담당자인 경우, 소속 허브ID가 존재하는 허브인지 확인 ;
        if (updateType.equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
            HubRequestDto hubInfo = hubClient.findOneHub(updateDto.getHubId()).getBody();
            if (hubInfo == null) {
                throw new EntityNotFoundException("등록되지 않은 허브입니다.");
            }

            // 권한 == 허브 관리자
            if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) {
                // 로그인 유저와 업데이트할 허브 Id 일치 여부 검사
                if (!userId.equals(hubInfo.getHubDto().getUser_id())) {
                    throw new AccessDeniedException("해당 허브는 허브 관리자님의 허브가 아닙니다.");
                }
            }
            // 권한 == 마스터 >> 검사 필요 X

            // 허브가 존재하면 허브 아이디 지정
            updateHubId = hubInfo.getHubDto().getHub_id();
        }

        // 배송 담당자 타입 업데이트
        deliveryPerson.updateDeliveryPersonType(updateType, updateHubId);

        return DeliveryPersonInfoDto.of(deliveryPerson);
    }


    // 배송 담당자 정보 삭제
    @Transactional
    @CacheEvict(cacheNames = {"deliveryPersonInfoCache", "deliveryPersonSearchInfoCache"}, allEntries = true)
    public String deleteDeliveryPerson(String role, Long userId, String email, Long deliveryPersonId) {
        // 배송 담당자 등록 여부 검사
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 배송 담당자입니다."));

        // 권한 == 허브 관리자
        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) {
            // 업체 배송 담당자 경우 허브 Id 존재 - 소속 허브Id 관리자와 로그인 유저 일치 여부 검사
            if (deliveryPerson.getDeliveryPersonType().equals(DeliveryPersonTypeEnum.COMPANY_DELIVERY_PERSON)) {
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
        }

        deliveryPerson.delete(email);

        return deliveryPersonId + "님 배송 담당자 정보 삭제 완료";
    }


    // 배송 담당자 전체 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "deliveryPersonSearchInfoCache", key = "{#searchRequestDto.user_id, #searchRequestDto.delivery_person_id, #searchRequestDto.hub_id, #searchRequestDto.slack_id, #searchRequestDto.type}")
    public Page<DeliveryPersonSearchResponseDto> searchDeliveryPerson(Pageable pageable, DeliveryPersonSearchRequestDto searchRequestDto) {

        return deliveryPersonRepository.findAllDeliveryPerson(pageable, searchRequestDto);
    }
}
