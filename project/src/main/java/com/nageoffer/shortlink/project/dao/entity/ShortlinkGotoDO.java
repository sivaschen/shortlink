package com.nageoffer.shortlink.project.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_goto")
public class ShortlinkGotoDO {
    private Long id;

    private String gid;

    private String fullShortUrl;
}
