package com.nageoffer.shortlink.project.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShortlinkCreateRespDTO {


    private String gid;


    private String fullShortUrl;


    private String originalUrl;
}
