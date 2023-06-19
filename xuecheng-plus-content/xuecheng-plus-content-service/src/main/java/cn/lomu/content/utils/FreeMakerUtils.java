package cn.lomu.content.utils;

import cn.lomu.base.exception.XueChengPlusException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author LoMu
 * Date  2023-06-13 7:15
 */
public class FreeMakerUtils {
    private static final Configuration configuration = new Configuration(Configuration.getVersion());

    static {
        configuration.setClassForTemplateLoading(FreeMakerUtils.class, "/static/templates/");
        configuration.setEncoding(Locale.CHINESE, StandardCharsets.UTF_8.name());
    }

    public static String parseData(Object dataModel, String templateName) {
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter sw = new StringWriter();
            template.process(dataModel, sw);
            return sw.toString();
        } catch (IOException e) {
            throw new XueChengPlusException("获取freemarker模板失败");
        } catch (TemplateException e) {
            throw new XueChengPlusException("freemarker模板处理失败");
        }
    }
}
