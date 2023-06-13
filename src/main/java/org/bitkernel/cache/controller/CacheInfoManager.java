package org.bitkernel.cache.controller;

import net.oschina.j2cache.J2CacheRegionConfig;
import org.bitkernel.cache.service.AccountService;
import org.bitkernel.vo.RegionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("info")
public class CacheInfoManager {
    @Autowired
    private AccountService accountService;

    @GetMapping("ConfiguredRegions")
    public List<RegionVo> getConfiguredRegions() {
        List<RegionVo> regionVoList = new ArrayList<>();
        for (String region : J2CacheRegionConfig.getConfiguredRegions()) {
            J2CacheRegionConfig.RegionConfig regionConfig = J2CacheRegionConfig.findRegionConfig(region);
            regionVoList.add(
                    RegionVo.builder()
                            .name(region)
                            .size(regionConfig.getSize())
                            .ttlLocal(regionConfig.getLocalTtl())
                            .ttlRedis(regionConfig.getRedisTtl())
                            .build()
            );
        }
        return regionVoList;
    }
}
