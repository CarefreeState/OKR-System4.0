package cn.bitterfree.api.xxljob.util;

import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.web.HttpRequestUtil;
import cn.bitterfree.api.xxljob.config.Admin;
import cn.bitterfree.api.xxljob.cookie.XxlJobCookie;
import cn.bitterfree.api.xxljob.enums.XxlJobRequest;
import cn.bitterfree.api.xxljob.model.dto.GroupPageListDTO;
import cn.bitterfree.api.xxljob.model.dto.InfoPageListDTO;
import cn.bitterfree.api.xxljob.model.dto.JobGroupDTO;
import cn.bitterfree.api.xxljob.model.dto.LoginDTO;
import cn.bitterfree.api.xxljob.model.entity.XxlJobGroup;
import cn.bitterfree.api.xxljob.model.entity.XxlJobInfo;
import cn.bitterfree.api.xxljob.model.vo.GroupPageListVO;
import cn.bitterfree.api.xxljob.model.vo.InfoPageListVO;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 18:58
 */
@Slf4j
public class XxlJobRequestUtil {

    private final static String XXL_JOB_LOGIN_IDENTITY = "XXL_JOB_LOGIN_IDENTITY";

    private final static Admin ADMIN = SpringUtil.getBean(Admin.class);

    public static String login() {
        XxlJobRequest xxlJobRequest = XxlJobRequest.LOGIN;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        LoginDTO loginDTO = LoginDTO.builder().userName(ADMIN.getUsername()).password(ADMIN.getPassword()).build();
        try (HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), loginDTO, null, "")) {
            return response.getCookies()
                    .stream()
                    .filter(cookie -> cookie.getName().equals(XXL_JOB_LOGIN_IDENTITY))
                    .findFirst()
                    .map(httpCookie ->{
                        String cookie = String.format("%s=%s", XXL_JOB_LOGIN_IDENTITY, httpCookie.getValue());
                        log.info("xxljob cookie {}", cookie);
                        return cookie;
                    })
                    .orElseThrow(() ->
                            new GlobalServiceException("get xxl-job cookie error!")
                    );
        }
    }

    public static List<XxlJobGroup> groupPageList(GroupPageListDTO groupPageListDTO) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.GROUP_PAGE_LIST;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        GroupPageListVO groupPageListVO = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), groupPageListDTO, GroupPageListVO.class, null, cookie);
        return groupPageListVO.getData();
    }

    public static List<XxlJobInfo> infoPageList(InfoPageListDTO infoPageListDTO) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.INFO_PAGE_LIST;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        InfoPageListVO infoPageListVO = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), infoPageListDTO, InfoPageListVO.class, null, cookie);
        return infoPageListVO.getData();
    }

    public static void groupSave(JobGroupDTO jobGroupDTO) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.GROUP_SAVE;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        try(HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), jobGroupDTO, null, cookie)) {
            log.info("保存成功 group {}", jobGroupDTO);
            return;
        }
    }

    public static void groupUpdate(JobGroupDTO jobGroupDTO) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.GROUP_UPDATE;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        try(HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), jobGroupDTO, null, cookie)) {
            log.info("更新成功 group {}", jobGroupDTO);
            return;
        }
    }

    public static void infoAdd(XxlJobInfo xxlJobInfo) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.INFO_ADD;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        try(HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), xxlJobInfo, null, cookie)) {
            log.info("新增成功 info {}", xxlJobInfo);
            return;
        }
    }

    public static void infoUpdate(XxlJobInfo xxlJobInfo) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.INFO_UPDATE;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        try(HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), xxlJobInfo, null, cookie)) {
            log.info("更新成功 info {}", xxlJobInfo);
            return;
        }
    }
}
