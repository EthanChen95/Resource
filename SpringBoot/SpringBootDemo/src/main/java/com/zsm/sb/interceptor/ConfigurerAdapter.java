package com.zsm.sb.interceptor;

import com.zsm.sb.util.StringToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;


/**
 * @Author: zengsm.
 * @Description:
 * @Date:Created in 2017/12/28 17:57.
 * @Modified By:
 */
@Configuration
public class ConfigurerAdapter extends WebMvcConfigurerAdapter
{
    @Autowired
    private RequestMappingHandlerAdapter adapter;

    /**
     * 配置静态访问资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        //addResourceLocations指的是文件放置的目录，addResoureHandler指的是对外暴露的访问路径,
        registry.addResourceHandler("/local/**").addResourceLocations("classpath:/user/local/my/");
        super.addResourceHandlers(registry);
    }

    /**
     * 以前要访问一个页面需要先创建个Controller控制类，再写方法跳转到页面
     * 在这里配置后就不需要那么麻烦了，直接访问http://localhost:8080/toLogin就跳转到login.jsp页面了
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/toLogin").setViewName("login");
        registry.addViewController("/index").setViewName("index");
        super.addViewControllers(registry);
    }

    /**
     * 拦截器，配置拦截请求和排除拦截请求
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截,注意排除资源文件请求
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**")
            .excludePathPatterns("/toLogin", "/login", "/test/toLogin", "/js/**", "/css/**");
        super.addInterceptors(registry);
    }

    /**
     * 手动配置静态资源路径
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer)
    {
        configurer.setUseSuffixPatternMatch(false).setUseTrailingSlashMatch(true);
    }

    /**
     * Cors的跨域
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "DELETE", "PUT")
            .allowCredentials(false)
            .maxAge(3600);
    }

    /**
     * 日期类型转换
     */
    @PostConstruct
    public void initEditableValidation()
    {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)adapter.getWebBindingInitializer();
        if (initializer.getConversionService() != null)
        {
            GenericConversionService service = (GenericConversionService)initializer.getConversionService();
            service.addConverter(new StringToDateConverter());
        }
    }
}
