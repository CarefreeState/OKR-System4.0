package cn.lbcmmszdntnt.domain.okr.constants;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:28
 */
public interface OkrConstants {

    Long ALLOW_NOT_COMPLETED_PERSONAL_OKR_COUNT = 1L; // 允许同时存在多少个未完成的 OKR

    String USER_CORE_MAP = "userCoreMap:";
    String TEAM_ID_NAME_MAP = "teamIdNameMap:";
    String TEAM_ID_MANAGER_MAP = "teamIdManagerMap:";
    String TEAM_ROOT_MAP = "teamRootMap:";
    String TEAM_CHILD_LIST = "teamChildList:";
    String CREATE_CD_FLAG = "createCDFlag:";
    String USER_TEAM_MEMBER = "userTeamMember:";

    Long USER_CORE_MAP_TTL = 1L;
    Long TEAM_ID_NAME_TTL = 1L;
    Long TEAM_ID_MANAGER_TTL = 1L;
    Long TEAM_ROOT_TTL = 30L;
    Long TEAM_CHILD_TTL = 1L;
    Long CREATE_CD = 1L;
    Long USER_TEAM_MEMBER_TTL = 30L;

    TimeUnit USER_CORE_MAP_TTL_UNIT = TimeUnit.DAYS;
    TimeUnit TEAM_ID_NAME_UNIT = TimeUnit.DAYS;
    TimeUnit TEAM_ID_MANAGER_UNIT = TimeUnit.DAYS;
    TimeUnit TEAM_ROOT_TTL_UNIT = TimeUnit.DAYS;
    TimeUnit TEAM_CHILD_TTL_UNIT = TimeUnit.DAYS;
    TimeUnit CD_UNIT = TimeUnit.DAYS;
    TimeUnit USER_TEAM_MEMBER_TTL_UNIT = TimeUnit.DAYS;

}
