package com.nageoffer.shortlink.admin.controller;


import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.GroupOrderReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkSaveGroupReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.GroupRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.ShortlinkRemoteService;
import com.nageoffer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortLinkSaveGroupReqDTO requestParam) {
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupRespDTO>> saveGroup() {
        return Results.success(groupService.listGroup());
    }
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkUpdateReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    @PostMapping("/api/short-link/admin/v1/group/order")
    public Result<Void> updateGroupOrder(@RequestBody List<GroupOrderReqDTO> requestParam){
        groupService.updateGroupOrder(requestParam);
        return Results.success();
    }


}
