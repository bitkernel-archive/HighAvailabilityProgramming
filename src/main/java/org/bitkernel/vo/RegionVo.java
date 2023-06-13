package org.bitkernel.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionVo {
    private String name;
    private long size;
    private long ttlLocal;
    private long ttlRedis;
}
