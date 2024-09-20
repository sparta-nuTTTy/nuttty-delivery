package com.nuttty.eureka.hub.application.service;

import com.nuttty.eureka.hub.application.client.AuthClient;
import com.nuttty.eureka.hub.application.dto.HubDto;
import com.nuttty.eureka.hub.application.dto.UserDto;
import com.nuttty.eureka.hub.domain.model.Hub;
import com.nuttty.eureka.hub.domain.model.UserRoleEnum;
import com.nuttty.eureka.hub.exception.exceptionsdefined.DelegationException;
import com.nuttty.eureka.hub.infrastructure.repository.HubRepository;
import com.nuttty.eureka.hub.presestation.request.HubRequestDto;
import com.nuttty.eureka.hub.presestation.request.HubSearchRequestDto;
import com.nuttty.eureka.hub.presestation.response.HubResponseDto;
import com.nuttty.eureka.hub.presestation.response.HubSearchResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;
    private final AuthClient authClient;

    /**
     * 허브 생성 | 마스터 허용
     * @param request
     * @return
     */
    @Transactional
    @CachePut(cacheNames = "hubCache", key = "#result.hubDto.hub_id")
    @CacheEvict(cacheNames = "hubSearchCache", allEntries = true)
    public HubResponseDto createHub(HubRequestDto request) {

        // userId 존재 유무 확인, 허브 관리지인지 확인 v
        UserDto findAuth = authClient.findUserById(request.getUser_id(), "19092").getBody();
        if (findAuth == null) {
            log.error("not found user with id " + request.getUser_id());
            throw new EntityNotFoundException("not found user with id " + request.getUser_id());

        } else {
            if (!findAuth.getRole().equals(UserRoleEnum.HUB_MANAGER)) {
                log.error("You need to register a user with the HUB_MANAGER role.");
                throw new DelegationException("You need to register a user with the HUB_MANAGER role.");
            }
        }

        // 다른 허브에 등록된 허브 관리자인지 확인 v
        if (hubRepository.existsByUserId(request.getUser_id())) {
            log.error("User with id '" + request.getUser_id() + "' already exists");
            throw new DataIntegrityViolationException("User with id '" + request.getUser_id() + "' already exists");
        }

        // 허브 이름, 주소 중복 확인 v
        if (hubRepository.existsByAddressOrName(request.getAddress(), request.getName())) {
            log.error("Hub with name '" + request.getName() + "' or address '" + request.getAddress() + "' already exists");
            throw new DataIntegrityViolationException("Hub with name '" + request.getName() + "' or address '" + request.getAddress() + "' already exists");
        }

        Hub savedHub = hubRepository.save(request.toEntity());

        return new HubResponseDto(HttpStatus.CREATED.value(), "hub created", HubDto.toDto(savedHub));
    }

    /**
     * 허브 수정 | 마스터 허용
     * @param request
     * @param hubId
     * @param userId
     * @return
     */
    @Transactional
    @CachePut(cacheNames = "hubCache", key = "#hubId")
    @CacheEvict(cacheNames = "hubSearchCache", allEntries = true)
    public HubResponseDto updateHub(HubRequestDto request, UUID hubId, Long userId) {

        // hubId 조회 v
        Hub findHub = hubRepository.findById(hubId).orElseThrow(() ->
                new EntityNotFoundException("Hub with id " + hubId + " not found"));

        // userId 존재 유무 확인, 허브 관리지인지 확인 v
        UserDto findAuth = authClient.findUserById(request.getUser_id(), "19092").getBody();
        if (findAuth == null) {
            log.error("not found user with id " + request.getUser_id());
            throw new EntityNotFoundException("not found user with id " + request.getUser_id());

        } else {
            if (!findAuth.getRole().equals(UserRoleEnum.HUB_MANAGER)) {
                log.error("You need to register a user with the HUB_MANAGER role.");
                throw new DelegationException("You need to register a user with the HUB_MANAGER role.");
            }
        }

        // 다른 허브에 등록된 허브 관리자인지 확인 v
        if (hubRepository.existsByIdAndUserId(request.getUser_id(), hubId)) {
            log.error("User with id '" + request.getUser_id() + "' already exists for another hub.");
            throw new DataIntegrityViolationException("User with id '" + request.getUser_id() + "' already exists for another hub.");
        }

        // 수정 대상 제외 허브 이름, 주소 중복 확인 v
        if (hubRepository.existsByNameOrAddressExcludingId(request.getName(), request.getAddress(), hubId)) {
            log.error("Hub with name '" + request.getName() + "' or address '" + request.getAddress() + "' already exists for another hub.");
            throw new DataIntegrityViolationException("Hub with name '" + request.getName() + "' or address '" + request.getAddress() + "' already exists for another hub.");
        }

        // 허브 수정
        Hub updatedHub = findHub.update(request);

        return new HubResponseDto(HttpStatus.OK.value(), "hub updated", HubDto.toDto(updatedHub));
    }

    /**
     * 허브 삭제 | 마스터 허용
     * @param hubId
     * @param email
     */
    @Transactional
    public void deleteHub(UUID hubId, String email) {

        // hubId 조회 v
        Hub findHub = hubRepository.findById(hubId).orElseThrow(() ->
                new EntityNotFoundException("Hub with id " + hubId + " not found"));

        // 허브 삭제
        findHub.delete(email);
    }

    /**
     * 허브 단건 조회 | 모두 허용
     * @param hubId
     * @return
     */
    @Cacheable(cacheNames = "hubCache", key = "#hubId")
    public HubResponseDto findOneHub(UUID hubId) {

        Hub findHub = hubRepository.findById(hubId).orElseThrow(() ->
                new EntityNotFoundException("Hub with id " + hubId + " not found"));

        return new HubResponseDto(HttpStatus.OK.value(), "hub found", HubDto.toDto(findHub));
    }

    /**
     * 허브 페이징 조회 | 모두 허용
     * @param pageable
     * @param condition
     * @return
     */
    @Cacheable(cacheNames = "hubSearchCache", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort, #condition.hashCode()}")
    public Page<HubSearchResponseDto> findAllHub(Pageable pageable, HubSearchRequestDto condition) {

        return hubRepository.findAllHub(pageable, condition);
    }
}