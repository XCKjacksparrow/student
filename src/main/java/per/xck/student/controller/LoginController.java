package per.xck.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import per.xck.student.entity.Administrator;
import per.xck.student.reposiory.AdministratorRepository;

@Controller
public class LoginController {

    @Autowired
    AdministratorRepository administratorRepository;

    @PostMapping("/login")
    public String login(@RequestParam("account")String account,
                        @RequestParam("password")String password,
                        Model model){
        Administrator administrator = administratorRepository.findByAccount(account);
        if (administrator == null){
            model.addAttribute("error","Account was not exists try to sign up");
            return "index";
        }
        if (!password.equals(administrator.getPassword())){
            model.addAttribute("error","Password was wrong, Please try again");
            return "index";
        }
        return "temp";
    }

    @RequestMapping("/success")
    public String success(){
        return "success";
    }
}
