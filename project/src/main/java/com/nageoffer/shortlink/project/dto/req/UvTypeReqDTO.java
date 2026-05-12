package com.nageoffer.shortlink.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UvTypeReqDTO {
    private String gid;
    private String fullShortUrl;
    private Integer enableStatus;
    private String startDate;
    private String endDate;
    private List<String> userAccessLogsList;
}
