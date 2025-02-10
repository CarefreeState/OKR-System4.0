package cn.bitterfree.domain.core.controller.quadrant;

import cn.bitterfree.interceptor.annotation.Intercept;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 13:22
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/fourthquadrant")
@Tag(name = "OKR 内核/象限/第四象限")
@Intercept
@Validated
public class FourthQuadrantController {

}
