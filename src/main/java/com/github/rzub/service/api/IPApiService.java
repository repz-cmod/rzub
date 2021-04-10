package com.github.rzub.service.api;

import lombok.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class IPApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(cacheNames = "ip-api", key = "#ip", unless="#result == null")
    public IpInfo getInfo(String ip){
        return restTemplate.getForEntity("http://ip-api.com/json/" + ip, IpInfo.class).getBody();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IpInfo {
        private String status;
        private String country;
        private String countryCode;
        private String region;
        private String regionName;
        private String city;
        private double lat;
        private double lon;
        private String isp;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IpInfo ipInfo = (IpInfo) o;
            return getCountryCode().equals(ipInfo.getCountryCode()) &&
                    getRegionName().equals(ipInfo.getRegionName()) &&
                    getCity().equals(ipInfo.getCity()) &&
                    getIsp().equals(ipInfo.getIsp());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getCountryCode(), getRegionName(), getCity(), getIsp());
        }
    }
}
