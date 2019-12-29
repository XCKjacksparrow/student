package per.xck.student.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import per.xck.student.reposiory.AdministratorRepository;

@Controller
public class LoginController {

    @Autowired
    AdministratorRepository administratorRepository;

    @PostMapping("/login")
    public String login(@RequestParam("account")String account,
                        @RequestParam("password")String password,
                        Model model){
        /*
            使用Shiro编写认证操作
         */
        // 1.获取Subject
        Subject subject = SecurityUtils.getSubject();
        // 2.封装用户数据
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(account,password);
        // 3.执行登录方法
        try{
            subject.login(usernamePasswordToken);
            //登录成功
            return "temp";
        }catch (UnknownAccountException e){
            // 登录失败：用户名不存在
            model.addAttribute("error","Account was not exists try to sign up");
            return "index";
        }catch (IncorrectCredentialsException e){
            // 登录失败：密码错误
            model.addAttribute("error","Password was wrong, Please try again");
            return "index";
        }
    }

    @RequestMapping("/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }

    @RequestMapping("/success")
    public String success(){
        return "success";
    }


}
