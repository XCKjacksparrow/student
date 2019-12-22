package per.xck.student.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean filterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 添加Shiro内置Filter
        /*
            Shiro内置Filter，可以实现权限相关的拦截器
            常用的Filter：
                anon:   无需认证（登录）可以访问的资源
                authc:  必须认证才可以访问
                user:   如果使用了remember me的功能可以直接访问
                perms:  该资源必须得到了资源权限才可以访问
                role:   该资源必须得到了角色权限才可以访问
         */
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/user/add","authc");
        filterMap.put("/update","authc");

        filterMap.put("/login","anon");
        filterMap.put("/","anon");
        filterMap.put("/*","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setLoginUrl("/");
        shiroFilterFactoryBean.setUnauthorizedUrl("/");
//
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
//        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
//        filterChainDefinitionMap.put("/webjars/**", "anon");
//
//        filterChainDefinitionMap.put("/", "anon");
//        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
//        filterChainDefinitionMap.put("/**", "authc");
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("customRealm") CustomRealm customRealm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(customRealm);
        return defaultWebSecurityManager;
    }

    @Bean(name = "customRealm")
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        return customRealm;
    }
}
