package com.nuttty.eureka.hub.application.service;

import com.nuttty.eureka.hub.application.client.UserClient;
import com.nuttty.eureka.hub.application.dto.HubDto;
import com.nuttty.eureka.hub.domain.model.Hub;
import com.nuttty.eureka.hub.infrastructure.repository.HubRepository;
import com.nuttty.eureka.hub.presestation.request.HubRequest;
import com.nuttty.eureka.hub.presestation.response.HubResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;
    private final UserClient userClient;

    /**
     * 허브 생성 | 관리자 허용
     * @param request
     * @param userId
     * @return
     */
    // TODO findUserById 주석 해제, UserClinet 확인
    @Transactional
    public HubResponse createHub(HubRequest request, Long userId) {

        hubRepository.findByName(request.getName()).ifPresent(hub -> {
            throw new DataIntegrityViolationException("Hub with name " + request.getName() + " already exists");
        });

        hubRepository.findByAddress(request.getAddress()).ifPresent(hub -> {
            throw new DataIntegrityViolationException("Hub with address " + request.getAddress() + " already exists");
        });

//        UserDto userDto = userClient.findUserById(userId);
//        if (userDto == null) {
//            throw new EntityNotFoundException("not found user with id " + userId);
//        }

        Hub savedHub = hubRepository.save(request.toEntity());

        return new HubResponse(HttpStatus.CREATED.value(), "hub created", HubDto.toDto(savedHub));
    }


}
