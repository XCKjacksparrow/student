package per.xck.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import per.xck.student.entity.Student;
import per.xck.student.reposiory.StudentRepository;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @GetMapping("/student/getPage/{page}")
    @ResponseBody
    public List<Student> getPage(@PathVariable("page") Integer page){
        page = (page-1) * 5;
        List<Student> students = studentRepository.getPage(page);
        return students;
    }

    @ResponseBody
    @GetMapping("/student/getPagination")
    public Integer getPagination(){                 // 返回页数
        Integer counts = Math.toIntExact(studentRepository.count());    // 总人数
//        System.out.println(counts);
        if (counts % 5 == 0){
            return counts / 5;
        }else {
            return counts / 5 + 1;
        }
    }

    @PostMapping("/student/add")
    @ResponseBody
    public String addStudent(@RequestParam(name = "id",required = false) Integer id,
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
        studentRepository.saveAndFlush(student);

        return "success";
    }

    @RequestMapping("/student/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id){
        studentRepository.deleteById(id);
        return "success";
    }
}
