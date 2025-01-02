package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.application.feign.dto.HubDto;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheService {

    @CachePut(value = "hubCache", key = "'all'")
    public List<HubDto> saveHubListToCache(List<HubDto> hubList){
        return hubList;
    }

    @Cacheable(value = "hubCache", key = "'all'")
    public List<HubDto> getHubListFromCache(){
        // 캐시 데이터 있으면 반환, 없으면 빈 리스트 반환
        return List.of();
    }
}
