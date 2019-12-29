package per.xck.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import per.xck.student.entity.Student;
import per.xck.student.reposiory.StudentRepository;
import per.xck.student.service.StudentService;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentService studentService;

    @GetMapping("/student/getPage/{page}")
    @ResponseBody
    public List<Student> getPage(@PathVariable("page") Integer page){
        return studentService.getPage(page);
    }

    @ResponseBody
    @GetMapping("/student/getPagination")
    public Integer getPagination(){                 // 返回页数
        return studentService.getPagination();
    }

    @PostMapping("/student/addOrUpdate")
    @ResponseBody
    public String addOrUpdateStudent(@RequestParam(name = "id",required = false) Integer id,
                             @RequestParam("userName") String userName,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("score") Double score){
        Student student = new Student();
        if (id != null){
            student.setId(id);
        }
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setUserName(userName);
        student.setScore(score);
        studentService.addOrUpdateStudent(student);
        return "success";
    }

    @RequestMapping("/student/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id){
        studentService.deleteById(id);
        return "success";
    }
}
