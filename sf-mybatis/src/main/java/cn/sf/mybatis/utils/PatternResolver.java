package cn.sf.mybatis.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PatternResolver {

    private final static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public PatternResolver() {}

    private static List<Resource> resolve (ResourcePatternResolver resolver, String... patterns) {
        List<Resource> resources = Lists.newArrayList();
        for (int i = 0; i < patterns.length; ++ i) {
            try {
                resources.addAll(Arrays.asList(resolver.getResources(patterns[i])));
            } catch (IOException e) {
                log.warn("No such resources : {}", patterns[i]);
            }
        }
        return resources;
    }

    public static List<Resource> resolve (String... patterns) {
        return resolve(resolver, patterns);
    }

    public static PathMatchingResourcePatternResolver resolver (final URL... jars) {
        if (null == jars || jars.length <= 0) return resolver;
        ClassLoader cl = URLClassLoader.newInstance(jars, ClassLoader.getSystemClassLoader());
        return new PathMatchingResourcePatternResolver(cl);
    }
}