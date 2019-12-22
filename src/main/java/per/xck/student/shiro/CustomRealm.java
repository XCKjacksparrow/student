package per.xck.student.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import per.xck.student.entity.Administrator;
import per.xck.student.reposiory.AdministratorRepository;


public class CustomRealm extends AuthorizingRealm {

    @Autowired
    AdministratorRepository administratorRepository;

    /**
     * 执行授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        String username = (String) SecurityUtils.getSubject().getPrincipal();
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        Set<String> stringSet = new HashSet<>();
//        stringSet.add("user:show");
//        stringSet.add("user:admin");
//        info.setStringPermissions(stringSet);
        System.out.println("执行授权");
//        return info;
        return null;
    }

    /**
     * 执行认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证");


        // 编写判断逻辑，判断用户名和密码
        // 1.判断用户名
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)authenticationToken;

        Administrator administrator = administratorRepository.findByAccount(usernamePasswordToken.getUsername());
        if (administrator == null){
            // 用户名不存在
            return null;//shiro底层抛出UnknownAccountException
        }
        String password = administrator.getPassword();
        // 2.判断密码
        return new SimpleAuthenticationInfo(administrator,password,"");
    }
}
