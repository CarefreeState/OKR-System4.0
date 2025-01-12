/**
 * 参考 DDD 的设计理念(但该项目依然是 MVC 架构)，对每一个领域进行分包处理（DDD 与 MVC 融合的架构）
 * 一来避免业务职责脏污，二来方便分模块开发
 * 每一个领域模块就在 domain 包下新开一个包
 * 每个领域模块里面的内容应当包含四个（controller、model、repository、service）
 *
 */
package cn.lbcmmszdntnt.domain;