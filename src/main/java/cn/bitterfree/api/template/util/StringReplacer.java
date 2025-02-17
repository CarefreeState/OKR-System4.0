package cn.bitterfree.api.template.util;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-09
 * Time: 13:55
 */
public final class StringReplacer {

    private String text;

    public synchronized StringReplacer replace(String target, String replacement) {
        text = text.replace(target, replacement);
        return this;
    }

    public synchronized StringReplacer replaceAll(String regex, String replacement) {
        text = text.replaceAll(regex, replacement);
        return this;
    }

    public StringReplacer() {
        this.text = "";
    }

    public StringReplacer(String text) {
        this.text = text;
    }

    @Override
    public synchronized String toString() {
        return text;
    }
}
