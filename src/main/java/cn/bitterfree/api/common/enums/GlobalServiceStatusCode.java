package cn.bitterfree.api.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalServiceStatusCode {
    /* 成功, 默认200 */
    SYSTEM_SUCCESS(200, "操作成功"),

    /* 需要重定向 */
    NEED_REDIRECT(302, "需要重定向"),

    /* 系统错误 500 - 1000 */
    SYSTEM_SERVICE_FAIL(-4396, "操作失败"),
    SYSTEM_SERVICE_ERROR(-500, "系统异常"),
    SYSTEM_TIME_OUT(-1, "请求超时"),

    REDIS_LOCK_FAIL(-900, "Redis 加锁失败"),

    SYSTEM_API_VISIT_FAIL(-1000, "路径访问失败"),


    /* 参数错误：1001～2000 */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    PARAM_FAILED_VALIDATE(1005, "参数未通过验证"),

    REQUEST_NOT_VALID(1101, "请求无效"),

    WX_CODE_NOT_VALID(1201, "微信 code 无效"),

    /* 用户错误 2001-3000 */
    USER_NO_AUTHENTICATED(2001, "用户未登录或 token 无效"),
    USER_NO_AUTHORIZED(2002, "用户无权访问"),
    USER_ACCOUNT_EXPIRED(2003, "账号已过期"),
    USER_CREDENTIALS_ERROR(2004, "密码错误"),
    USER_CREDENTIALS_EXPIRED(2005, "密码过期"),
    USER_ACCOUNT_DISABLE(2006, "账号不可用"),
    USER_ACCOUNT_LOCKED(2007, "账号被锁定"),
    USER_ACCOUNT_NOT_EXIST(2008, "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST(2009, "账号已存在"),
    USER_ACCOUNT_USE_BY_OTHERS(2010, "账号下线"),
    USER_ACCOUNT_MERGE_CONFLICT(2011, "账号合并冲突"),

    USER_TOKEN_NOT_VALID(2010, "用户登录凭据无效"),
    USER_LOGIN_CODE_VALID(2011, "用户登录码失效"),
    USER_LOGIN_NOT_CHECK(2012, "用户登录码未验证"),
    USER_BINDING_CODE_VALID(2012, "用户绑定码失效"),
    USER_BINDING_NOT_CHECK(2012, "用户绑定码未验证"),
    USER_BINDING_CHECKED(2013, "用户绑定码已验证"),

    USER_IDENTIFY_CODE_ERROR(2101, "验证码错误"),
    USER_USERNAME_PASSWORD_ERROR(2102, "用户名或密码错误"),

    USER_NO_PERMISSION(2403, "用户无权限"),

    USER_CANNOT_JOIN_TEAM(2600, "用户可能并非受邀，无法加入团队"),

    DATA_NOT_SECURITY(3000, "数据不安全"),

    // -------- 象限相关：
    FIRST_QUADRANT_UPDATE_ERROR(4001, "第一象限更新失败"),
    SECOND_QUADRANT_UPDATE_ERROR(4002, "第二象限更新失败"),
    THIRD_QUADRANT_UPDATE_ERROR(4003, "第三象限更新失败"),
    FOURTH_QUADRANT_UPDATE_ERROR(4004, "第四象限更新失败"),
    FIRST_QUADRANT_NOT_EXISTS(4002, "第一象限不存在"),
    SECOND_QUADRANT_NOT_EXISTS(4003, "第二象限不存在"),
    THIRD_QUADRANT_NOT_EXISTS(4004, "第三象限不存在"),
    FOURTH_QUADRANT_NOT_EXISTS(4005, "第四象限不存在"),

    SECOND_FIRST_QUADRANT_NOT_INIT(4100, "第二象限未初始化"),

    OKR_IS_OVER(4100, "OKR 已结束"),
    OKR_IS_NOT_OVER(4101, "OKR 未结束"),
    OKR_IS_SUMMARIZED(4102, "OKR 已总结"),

    INVALID_CELEBRATE_DAY(4200, "庆祝日非法更新"),
    CELEBRATE_DAY_CANNOT_CHANGE(4201, "庆祝日不能修改"),

    /* 团队相关 */
    TEAM_NOT_EXISTS(5000, "团队不存在"),
    TEAM_CREATE_TOO_FREQUENT(5001, "团队 OKR 创建太频繁了"),
    REPEATED_JOIN_TEAM(5002, "重复加入团队"),
    NON_TEAM_MEMBER(5003, "非团队人员"),
    NON_TEAM_MANAGER(5004, "非团队管理者"),
    REPEATED_GRANT(5005, "重复授权"),

    MEMBER_NOT_EXISTS(5100, "团队成员不存在"),
    MEMBER_CANNOT_REMOVE(5101, "此成员无法移除"),

    QR_CODE_GENERATE_FAIL(5200, "二维码生成失败"),

    /*内核相关*/
    CORE_NOT_EXISTS(6000, "OKR 内核不存在或者不存在将其当作内核的 p/t/tp OKR "),
    USER_NOT_CORE_MANAGER(6001, "用户并不是 OKR 的管理者"),

    KEY_RESULT_NOT_EXISTS(6100, "关键结果不存在"),
    STATUS_FLAG_NOT_EXISTS(6101, "状态指标不存在"),
    TASK_NOT_EXISTS(6102, "任务不存在"),

    SECOND_CYCLE_TOO_SHORT(6200, "第二象限周期太短了"),
    THIRD_CYCLE_TOO_SHORT(6201, "第三象限周期太短了"),

    /*邮箱与微信相关*/
    EMAIL_SEND_FAIL(7000, "邮箱发送异常"),
    EMAIL_SENDER_NOT_EXISTS(7001, "邮箱发送器不存在"),
    EMAIL_NOT_EXIST_RECORD(7002, "邮箱不存在记录"),
    EMAIL_LOGIN_IDENTIFY_CODE_ERROR(7003, "邮箱登录验证码错误"),
    EMAIL_BINDING_IDENTIFY_CODE_ERROR(7004, "邮箱绑定验证码错误"),
    EMAIL_IDENTIFY_CODE_COUNT_EXHAUST(7005, "申请次数达到上限"),
    EMAIL_USER_BE_BOUND(7006, "邮箱用户已被绑定"),
    USER_BOUND_EMAIL(7007, "用户已经绑定了微信"),

    WX_USER_BE_BOUND(7100, "微信用户已被绑定"),
    USER_BOUND_WX(7101, "用户已经绑定了微信"),
    WX_CODE_NOT_CONSISTENT(7102, "微信验证码不一致"),
    WX_NOT_EXIST_RECORD(7103, "微信不存在验证记录"),

    /*勋章相关*/
    MEDAL_NOT_EXISTS(8000, "勋章不存在"),

    /*内核记录相关*/
    CORE_RECORDER_NOT_EXISTS(9000, "内核记录器不存在"),
    DAY_RECORD_NOT_EXISTS(9001, "日记录不存在"),


    /*WebSocket想关*/
    USER_NOT_ONLINE(10000, "用户不在线"),

    /*SSE想关*/
    SSE_CONNECTION_NOT_EXIST(11000, "连接不存在或者超时"),
    SSE_CONNECTION_IS_EXIST(11001, "连接已被占用"),
    SSE_CONNECTION_CREATE_FAILED(11002, "连接创建失败"),

    /*文件资源想关*/
    FILE_RESOURCE_NOT_VALID(12000, "资源非法"),
    FILE_RESOURCE_TYPE_NOT_MATCH(12001, "资源类型不匹配"),
    FILE_RESOURCE_UPLOAD_FAILED(12002, "资源上传失败"),
    FILE_RESOURCE_LOAD_FAILED(12003, "资源加载失败"),
    FILE_RESOURCE_PREVIEW_FAILED(12004, "资源预览失败"),
    FILE_RESOURCE_DOWNLOAD_FAILED(12005, "资源下载失败"),
    FILE_RESOURCE_REMOVE_FAILED(12006, "资源删除失败"),
    RESOURCE_NOT_EXISTS(12007, "资源不存在"),

    /* -------------- */;

    private final Integer code;
    private final String message;

}
