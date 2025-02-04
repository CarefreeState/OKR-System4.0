package cn.lbcmmszdntnt.xxljob.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.common.util.web.HttpRequestUtil;
import cn.lbcmmszdntnt.xxljob.config.Admin;
import cn.lbcmmszdntnt.xxljob.cookie.XxlJobCookie;
import cn.lbcmmszdntnt.xxljob.enums.XxlJobRequest;
import cn.lbcmmszdntnt.xxljob.model.dto.GroupPageListDTO;
import cn.lbcmmszdntnt.xxljob.model.dto.GroupSaveDTO;
import cn.lbcmmszdntnt.xxljob.model.dto.InfoPageListDTO;
import cn.lbcmmszdntnt.xxljob.model.dto.LoginDTO;
import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobGroup;
import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobInfo;
import cn.lbcmmszdntnt.xxljob.model.vo.GroupPageListVO;
import cn.lbcmmszdntnt.xxljob.model.vo.InfoPageListVO;
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

    public static void groupSave(GroupSaveDTO groupSaveDTO) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.GROUP_SAVE;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        try(HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), groupSaveDTO, null, cookie)) {
            log.info("保存 group {}", groupSaveDTO);
            return;
        }
    }

    public static void infoAdd(XxlJobInfo xxlJobInfo) {
        XxlJobRequest xxlJobRequest = XxlJobRequest.INFO_ADD;
        String url = HttpRequestUtil.getBaseUrl(ADMIN.getAddresses(), xxlJobRequest.getUri());
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        try(HttpResponse response = HttpRequestUtil.formRequest(url, xxlJobRequest.getMethod(), xxlJobInfo, null, cookie)) {
            log.info("新增 info {}", xxlJobInfo);
            return;
        }
    }


}
