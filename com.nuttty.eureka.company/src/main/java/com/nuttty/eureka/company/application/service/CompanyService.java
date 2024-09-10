package com.nuttty.eureka.company.application.service;

import com.nuttty.eureka.company.application.client.AuthClient;
import com.nuttty.eureka.company.application.client.HubClient;
import com.nuttty.eureka.company.application.dto.AuthRequestDto;
import com.nuttty.eureka.company.application.dto.CompanyDto;
import com.nuttty.eureka.company.application.dto.HubRequestDto;
import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.exception.exceptionsdefined.DelegationException;
import com.nuttty.eureka.company.infastructure.repository.CompanyRepository;
import com.nuttty.eureka.company.presentation.request.CompanyRequestDto;
import com.nuttty.eureka.company.presentation.request.CompanySearchRequestDto;
import com.nuttty.eureka.company.presentation.response.CompanyDelResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanyResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanySearchResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final HubClient hubClient;
    private final AuthClient authClient;

    /**
     * 업체 생성 | 마스터, 허브 관리자, 허브 업체 허용
     * @param request
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto request, Long userId, String role) {

        if (role.equals("MASTER") || role.equals("HUB_MANAGER")) {
            // 허브 ID 존재 유무 확인
            HubRequestDto findHub = hubClient.findByHubId(request.getHub_id());
            if (findHub.getHubDto() == null) {
                log.error("허브 ID: " + request.getHub_id() +" 가 존재하지 않습니다.");
                throw new EntityNotFoundException("허브 ID: " + request.getHub_id() + " 가 존재하지 않습니다.");
            }

            // 유저 ID 존재 유무 확인
            AuthRequestDto findAuth = authClient.findByUserId(request.getUser_id());
            if (findAuth.getAuthDto() == null) {
                log.error("User ID: " + request.getUser_id() + " 가 존재하지 않습니다.");
                throw new EntityNotFoundException("User ID: " + request.getUser_id() + " 가 존재하지 않습니다.");
            }

            // 허브 업체 유저인지 확인
            if (!findAuth.getAuthDto().getRole().equals("HUB_COMPANY")) {
                log.error("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
                throw new DelegationException("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
            }

            // 주소 중복 확인
            if (companyRepository.existsByAddress(request.getAddress())) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            //  이미 등록된 유저인지 확인
            if (companyRepository.existsByUserId(request.getUser_id())) {
                log.error(request.getUser_id() + " 이미 등록된 User ID 입니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 이미 등록된 User ID 입니다.");
            }

            Company savedCompany = companyRepository.save(request.toEntity());

            return new CompanyResponseDto(HttpStatus.CREATED.value(), "company created", CompanyDto.toDto(savedCompany));

        }  else { // HUB_COMPANY 일 경우
            // 허브 ID 존재 유무 확인
            HubRequestDto findHub = hubClient.findByHubId(request.getHub_id());
            if (findHub.getHubDto() == null) {
                log.error("허브 ID: " + request.getHub_id() +" 가 존재하지 않습니다.");
                throw new EntityNotFoundException("허브 ID: " + request.getHub_id() + " 가 존재하지 않습니다.");
            }

            // 로그인 한 유저와 등록하려는 유저가 같은지 확인
            if (!request.getUser_id().equals(userId)) {
                log.error("본인의 User Id 로 등록이 가능합니다.");
                throw new NotAuthorizedException("본인의 User Id 로 등록이 가능합니다.");
            }

            // 주소 중복 확인
            if (companyRepository.existsByAddress(request.getAddress())) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            //  이미 등록된 유저인지 확인
            if (companyRepository.existsByUserId(request.getUser_id())) {
                log.error(request.getUser_id() + " 이미 등록된 User ID 입니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 이미 등록된 User ID 입니다.");
            }

            Company savedCompany = companyRepository.save(request.toEntity());

            return new CompanyResponseDto(HttpStatus.CREATED.value(), "company created", CompanyDto.toDto(savedCompany));
        }
    }

    /**
     * 업체 수정 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param request
     * @param companyId
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    public CompanyResponseDto updateCompany(CompanyRequestDto request, UUID companyId, Long userId, String role) {

        if (role.equals("MASTER") || role.equals("HUB_MANAGER")) {
            // 존재하는 유저 ID 인지 확인
            AuthRequestDto findAuth = authClient.findByUserId(request.getUser_id());
            if (findAuth.getAuthDto() == null) {
                log.error("User ID: " + request.getUser_id() + " 가 존재하지 않습니다.");
                throw new EntityNotFoundException("User ID: " + request.getUser_id() + " 가 존재하지 않습니다.");
            }

            // 허브 업체 유저인지 확인
            if (!findAuth.getAuthDto().getRole().equals("HUB_COMPANY")) {
                log.error("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
                throw new DelegationException("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
            }

            // 다른 업체에 유저 ID 로 등록된 업체가 존재 유무 확인
            if (companyRepository.existsByUserIdAndExcludeId(request.getUser_id(), companyId)) {
                log.error(request.getUser_id() + " 다른 업체에 이미 등록되어 있습니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 다른 업체에 이미 등록되어 있습니다.");
            }
            // 다른 업체에 등록된 주소 인지 확인
            if (companyRepository.existsByAddressAndExcludeId(request.getAddress(), companyId)) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            // hubId 존재 유무 확인
            HubRequestDto findHub = hubClient.findByHubId(request.getHub_id());
            if (findHub.getHubDto() == null) {
                log.error("허브 ID: " + request.getHub_id() +" 가 존재하지 않습니다.");
                throw new EntityNotFoundException("허브 ID: " + request.getHub_id() + " 가 존재하지 않습니다.");
            }

            // companyId 존재 유무 확인 및 조회
            Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                    new EntityNotFoundException("not found company"));

            Company updatedCompany = findCompany.update(request);
            return new CompanyResponseDto(HttpStatus.OK.value(), "company updated", CompanyDto.toDto(updatedCompany));

        } else { // HUB_COMPANY 일 경우

            // 로그인 한 유저와 등록하려는 유저가 같은지 확인
            if (!request.getUser_id().equals(userId)) {
                log.error("본인의 User Id 로 수정이 가능합니다.");
                throw new NotAuthorizedException("본인의 User Id 로 수정이 가능합니다.");
            }

            // 허브 ID 존재 유무 확인
            HubRequestDto findHub = hubClient.findByHubId(request.getHub_id());
            if (findHub.getHubDto() == null) {
                log.error("허브 ID: " + request.getHub_id() +" 가 존재하지 않습니다.");
                throw new EntityNotFoundException("허브 ID: " + request.getHub_id() + " 가 존재하지 않습니다.");
            }

            // 다른 업체에 등록된 주소 인지 확인
            if (companyRepository.existsByAddressAndExcludeId(request.getAddress(), companyId)) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            // companyId 존재 유무 확인 및 조회
            Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                    new EntityNotFoundException("not found company"));

            Company updatedCompany = findCompany.update(request);
            return new CompanyResponseDto(HttpStatus.OK.value(), "company updated", CompanyDto.toDto(updatedCompany));
        }
    }

    /**
     * 업체 삭제 | 마스터 허용
     * @param companyId
     * @param email
     * @return
     */
    public CompanyDelResponseDto deleteCompany(UUID companyId, String email) {
        Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                new EntityNotFoundException("not found company"));

        findCompany.delete(email);
        return new CompanyDelResponseDto(HttpStatus.OK.value(), "company deleted", companyId + " deleted");
    }

    /**
     * 업체 개별 조회 | 모두 허용
     * @param companyId
     * @return
     */
    public CompanyResponseDto findOneCompany(UUID companyId) {

        Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                new EntityNotFoundException("not found company"));

        return new CompanyResponseDto(HttpStatus.OK.value(), "company found", CompanyDto.toDto(findCompany));
    }

    /**
     * 업체 페이지 조회 | 모두 허용
     * @param pageable
     * @param condition
     * @return
     */
    public Page<CompanySearchResponseDto> findAllCompany(Pageable pageable, CompanySearchRequestDto condition) {

        return companyRepository.findAllCompany(pageable, condition);
    }
}
