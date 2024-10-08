package com.nuttty.eureka.company.application.service;

import com.nuttty.eureka.company.application.client.AuthClient;
import com.nuttty.eureka.company.application.client.HubClient;
import com.nuttty.eureka.company.application.dto.CompanyDto;
import com.nuttty.eureka.company.application.dto.HubRequestDto;
import com.nuttty.eureka.company.application.dto.UserDto;
import com.nuttty.eureka.company.application.dto.UserRoleEnum;
import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.exception.exceptionsdefined.DelegationException;
import com.nuttty.eureka.company.exception.exceptionsdefined.MissmatchException;
import com.nuttty.eureka.company.infastructure.repository.CompanyRepository;
import com.nuttty.eureka.company.presentation.request.CompanyRequestDto;
import com.nuttty.eureka.company.presentation.request.CompanySearchRequestDto;
import com.nuttty.eureka.company.presentation.response.CompanyDelResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanyResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanySearchResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
     * 업체 생성 | 마스터, 허브 관리자(본인 허브 업체만), 허브 업체(본인만) 허용
     * @param request
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto request, Long userId, UserRoleEnum role) {

        if (role.equals(UserRoleEnum.MASTER)) {
            // 유저 ID 존재 유무 확인 v
            UserDto findAuth = null;
            try {
                findAuth = authClient.findUserById(request.getUser_id()).getBody();

            } catch (RuntimeException e) {
                log.error("User not found");
                throw new EntityNotFoundException("User not found");
            }

            // 허브 업체권한 유저인지 확인 v
            if (!findAuth.getRole().equals(UserRoleEnum.HUB_COMPANY)) {
                log.error("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
                throw new DelegationException("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
            }

            // 허브 ID 존재 유무 확인 v
            try {
                HubRequestDto findHub = hubClient.findByHubId(request.getHub_id(), "19092");
            } catch (RuntimeException e) {
                log.error("Hub not found");
                throw new EntityNotFoundException("Hub not found");
            }

            //  이미 등록된 유저인지 확인 v
            if (companyRepository.existsByUserId(request.getUser_id())) {
                log.error(request.getUser_id() + " 이미 등록된 User ID 입니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 이미 등록된 User ID 입니다.");
            }

            // 주소 중복 확인 v
            if (companyRepository.existsByAddress(request.getAddress())) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            Company savedCompany = companyRepository.save(request.toEntity());

            return new CompanyResponseDto(HttpStatus.CREATED.value(), "company created", CompanyDto.toDto(savedCompany));

        }  else if(role.equals(UserRoleEnum.HUB_COMPANY)){ // HUB_COMPANY 일 경우

            // 로그인 한 유저와 등록하려는 유저가 같은지 확인 v
            if (!request.getUser_id().equals(userId)) {
                log.error("본인의 User Id 로 등록이 가능합니다.");
                throw new MissmatchException("본인의 User Id 로 등록이 가능합니다.");
            }

            //  이미 등록된 유저인지 확인 v
            if (companyRepository.existsByUserId(request.getUser_id())) {
                log.error(request.getUser_id() + " 이미 등록된 User ID 입니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 이미 등록된 User ID 입니다.");
            }

            // 허브 ID 존재 유무 확인
            try {
                hubClient.findByHubId(request.getHub_id(), "19092");
                
            } catch (RuntimeException e) {
                log.error("Hub not found");
                throw new EntityNotFoundException("Hub not found");
            }

            // 주소 중복 확인
            if (companyRepository.existsByAddress(request.getAddress())) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            Company savedCompany = companyRepository.save(request.toEntity());

            return new CompanyResponseDto(HttpStatus.CREATED.value(), "company created", CompanyDto.toDto(savedCompany));

        } else { // HUB_MANAGER

            // 허브 ID 존재 유무 확인 v
            HubRequestDto findHub = null;
            try {
                findHub = hubClient.findByHubId(request.getHub_id(), "19092");
            } catch (RuntimeException e) {
                log.error("Hub not found");
                throw new EntityNotFoundException("Hub not found");
            }

            // 대리 등록하는 허브 관리자의 허브 ID 와 같은지 조회
            // 다른 허브의 업체는 등록 불가
            if (!findHub.getHubDto().getUser_id().equals(userId)) {
                log.error("본인 허브의 업체만 등록 가능합니다.");
                throw new DelegationException("본인 허브의 업체만 등록 가능합니다.");
            }

            // 유저 ID 존재 유무 확인 v
            UserDto findAuth = null;
            try {
                findAuth = authClient.findUserById(request.getUser_id()).getBody();

            } catch (RuntimeException e) {
                log.error("User not found");
                throw new EntityNotFoundException("User not found");
            }

            // 허브 업체권한 유저인지 확인 v
            if (!findAuth.getRole().equals(UserRoleEnum.HUB_COMPANY)) {
                log.error("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
                throw new DelegationException("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
            }

            //  이미 등록된 유저인지 확인 v
            if (companyRepository.existsByUserId(request.getUser_id())) {
                log.error(request.getUser_id() + " 이미 등록된 User ID 입니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 이미 등록된 User ID 입니다.");
            }

            // 주소 중복 확인 v
            if (companyRepository.existsByAddress(request.getAddress())) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            Company savedCompany = companyRepository.save(request.toEntity());

            return new CompanyResponseDto(HttpStatus.CREATED.value(), "company created", CompanyDto.toDto(savedCompany));
        }
    }

    /**
     * 업체 수정 | 마스터, 허브 관리자(본인 허브 업체만), 허브 업체(본인만) 허용
     * @param request
     * @param companyId
     * @param userId
     * @param role
     * @return
     */
    @Transactional
    public CompanyResponseDto updateCompany(CompanyRequestDto request, UUID companyId, Long userId, String role) {

        if (role.equals("MASTER")) {

            // companyId 존재 유무 확인 및 조회 v
            Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                    new EntityNotFoundException("not found company"));

            // 존재하는 유저 ID 인지 확인 v
            UserDto findAuth = null;
            try {
                findAuth = authClient.findUserById(request.getUser_id()).getBody();

            } catch (RuntimeException e) {
                log.error("User not found");
                throw new EntityNotFoundException("User not found");
            }

            // 허브 업체 유저인지 확인 v
            if (!findAuth.getRole().equals(UserRoleEnum.HUB_COMPANY)) {
                log.error("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
                throw new DelegationException("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
            }

            // 다른 업체에 유저 ID 로 등록된 업체가 존재 유무 확인 v
            if (companyRepository.existsByUserIdAndExcludeId(request.getUser_id(), companyId)) {
                log.error(request.getUser_id() + " 다른 업체에 이미 등록되어 있습니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 다른 업체에 이미 등록되어 있습니다.");
            }

            // 허브 ID 존재 유무 확인 v
            try {
                hubClient.findByHubId(request.getHub_id(), "19092");

            } catch (RuntimeException e) {
                log.error("Hub not found");
                throw new EntityNotFoundException("Hub not found");
            }

            // 다른 업체에 등록된 주소 인지 확인 v
            if (companyRepository.existsByAddressAndExcludeId(request.getAddress(), companyId)) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            Company updatedCompany = findCompany.update(request);
            return new CompanyResponseDto(HttpStatus.OK.value(), "company updated", CompanyDto.toDto(updatedCompany));

        } else if (role.equals("HUB_COMPANY")){ // HUB_COMPANY 일 경우

            // companyId 존재 유무 확인 및 조회 v
            Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                    new EntityNotFoundException("not found company"));

            // 로그인 한 유저와 등록하려는 유저가 같은지 확인 v
            if (!request.getUser_id().equals(userId) || !findCompany.getUserId().equals(request.getUser_id())) {
                log.error("본인의 업체만 수정이 가능합니다.");
                throw new MissmatchException("본인의 업체만 수정이 가능합니다.");
            }

            // 허브 ID 존재 유무 확인 v
            try {
                hubClient.findByHubId(request.getHub_id(), "19092");

            } catch (RuntimeException e) {
                log.error("Hub not found");
                throw new EntityNotFoundException("Hub not found");
            }

            // 다른 업체에 등록된 주소 인지 확인 v
            if (companyRepository.existsByAddressAndExcludeId(request.getAddress(), companyId)) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

            Company updatedCompany = findCompany.update(request);
            return new CompanyResponseDto(HttpStatus.OK.value(), "company updated", CompanyDto.toDto(updatedCompany));

        }else { // HUB_MANAGER

            // 허브 ID 존재 유무 확인 v
            HubRequestDto findHub = null;
            try {
                findHub = hubClient.findByHubId(request.getHub_id(), "19092");
            } catch (RuntimeException e) {
                log.error("Hub not found");
                throw new EntityNotFoundException("Hub not found");
            }

            // 대리 등록하는 허브 관리자의 허브 ID 와 같은지 조회
            // 다른 허브의 업체는 등록 불가
            if (!findHub.getHubDto().getUser_id().equals(userId)) {
                log.error("본인 허브의 업체만 수정 가능합니다.");
                throw new DelegationException("본인 허브의 업체만 수정 가능합니다.");
            }

            // companyId 존재 유무 확인 및 조회 v
            Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                    new EntityNotFoundException("not found company"));

            // 존재하는 유저 ID 인지 확인 v
            UserDto findAuth = null;
            try {
                findAuth = authClient.findUserById(request.getUser_id()).getBody();

            } catch (RuntimeException e) {
                log.error("User not found");
                throw new EntityNotFoundException("User not found");
            }

            // 허브 업체 유저인지 확인 v
            if (!findAuth.getRole().equals(UserRoleEnum.HUB_COMPANY)) {
                log.error("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
                throw new DelegationException("HUB_COMPANY 권한의 유저를 등록해야 합니다.");
            }

            // 다른 업체에 유저 ID 로 등록된 업체가 존재 유무 확인 v
            if (companyRepository.existsByUserIdAndExcludeId(request.getUser_id(), companyId)) {
                log.error(request.getUser_id() + " 다른 업체에 이미 등록되어 있습니다.");
                throw new DataIntegrityViolationException(request.getUser_id() + " 다른 업체에 이미 등록되어 있습니다.");
            }

            // 다른 업체에 등록된 주소 인지 확인 v
            if (companyRepository.existsByAddressAndExcludeId(request.getAddress(), companyId)) {
                log.error(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
                throw new DataIntegrityViolationException(request.getAddress() + " 이 주소는 이미 등록된 주소 입니다.");
            }

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
    @Transactional
    public CompanyDelResponseDto deleteCompany(UUID companyId, String email) {
        Company findCompany = companyRepository.findById(companyId).orElseThrow(() ->
                new EntityNotFoundException("not found company"));

        findCompany.softDelete(email);
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

    /**
     * 공급업체 ID = CompanyID, 공급 업체 ID 로 업체 리스트 조회
     * @param supplierId
     * @return
     */
    public List<CompanyDto> findAllSupplierIdOfCompany(List<UUID> supplierId) {

        List<Company> companies = companyRepository.findByIdIn(supplierId);

        return companies.stream()
                .map(CompanyDto::toDto)
                .toList();
    }
}
