package cn.bitterfree.api.template.engine;


import cn.bitterfree.api.template.model.dto.ReplaceResource;
import cn.bitterfree.api.template.model.dto.Resource;
import cn.bitterfree.api.template.util.MarkdownUtil;
import cn.bitterfree.api.template.util.TemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-08-29
 * Time: 11:07
 */
@Component
@RequiredArgsConstructor
public class HtmlEngine {

    /**
     * 注入的属性会被认为是纯字符串，防止 xss 与文本的部分字符串影响整体结构
     * 前提提供的模板里面，是通过标签注入的方式
     */
    private final TemplateEngine templateEngine;

    private final TextEngine textEngine;

    public HtmlBuilder builder() {
        return new HtmlBuilder();
    }

    public class HtmlBuilder {

        private final StringBuffer htmlBuffer = new StringBuffer();

        public String build() {
            return htmlBuffer.toString();
        }

        public HtmlBuilder clear() {
            htmlBuffer.setLength(0);
            return this;
        }

        // 追加一段 html
        public HtmlBuilder append(String html) {
            htmlBuffer.append(html);
            return this;
        }

        public HtmlBuilder reset(String html) {
            return clear().append(html);
        }

        // 合并 html 并追加
        public <T> HtmlBuilder append(String template, T data) {
            String html = templateEngine.process(template, TemplateUtil.getContext(data));
            return append(html);
        }

        // 合并 html 并追加
        public HtmlBuilder append(Resource resource) {
            return append(resource.getTemplate(), resource.getContext());
        }

        // 合并 html 并依次追加
        public HtmlBuilder append(List<Resource> resourceList) {
            resourceList.forEach(this::append);
            return this;
        }

        // md -> html 追加
        public HtmlBuilder appendMarkdown(String markdown) {
            String html = MarkdownUtil.markdownToHtml(markdown);
            return append(html);
        }

        // 合并 md 后转化为 html 追加
        public <T> HtmlBuilder appendMarkdown(String template, T data) {
            String markdown = textEngine.builder().append(template, data).build();
            return appendMarkdown(markdown);
        }

        // 合并 md 后转化为 html 追加
        public HtmlBuilder appendMarkdown(Resource resource) {
            String markdown = textEngine.builder().append(resource).build();
            return appendMarkdown(markdown);
        }

        // 合并 html 并依次追加
        public HtmlBuilder appendMarkdown(List<Resource> resourceList) {
            String markdown = textEngine.builder().append(resourceList).build();
            return appendMarkdown(markdown);
        }

        // html 替换 target
        public HtmlBuilder replace(String target, String html) {
            String finalHtml = build().replace(target, html);
            return reset(finalHtml);
        }

        // html 替换 target
        public HtmlBuilder replace(ReplaceResource resource) {
            return replace(resource.getTarget(), resource.getReplacement());
        }

        // html 集合替换元素对应的 target
        public HtmlBuilder replace(List<ReplaceResource> resourceList) {
            String finalHtml = TemplateUtil.replaceSafely(build(), resourceList, html -> html);
            return reset(finalHtml);
        }

        /**
         * 拓展功能：
         * 通过替换标识符，向原 html 插入一段 markdown 文本转换的 html
         * @param target 待替换的标识符
         * @param markdown 待插入的 markdown 文本
         * @return HtmlBuilder.this
         * 推荐用法：
         * 假设邮件模板为 template，邮件模板数据为 data，data 里有一个字段 content，而 content 为 markdown 文本
         * 1. 通过调用 htmlEngine.getUniqueSymbol()，获得一个唯一标识符 uniqueSymbol
         * 2. content 先设置为 uniqueSymbol，然后调用 append，传入 template 和 data 合成初步的 html
         * 3. 紧接着调用 replaceMarkdown，传入 uniqueSymbol 和原 markdown 文本，文本转换为 html 并替换 uniqueSymbol 的位置
         */
        public HtmlBuilder replaceMarkdown(String target, String markdown) {
            String html = MarkdownUtil.markdownToHtml(markdown);
            return replace(target, html);
        }

        // md -> html 替换 target
        public HtmlBuilder replaceMarkdown(ReplaceResource resource) {
            return replaceMarkdown(resource.getTarget(), resource.getReplacement());
        }

        // md 转化为 html 替换对应的 target
        public HtmlBuilder replaceMarkdown(List<ReplaceResource> resourceList) {
            String finalHtml = TemplateUtil.replaceSafely(build(), resourceList, MarkdownUtil::markdownToHtml);
            return reset(finalHtml);
        }

    }

}
