package per.xck.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import per.xck.student.reposiory.StudentRepository;

@SpringBootTest
class StudentApplicationTests {

    @Autowired
    StudentRepository studentRepository;

    @Test
    void contextLoads() {
//        Student student = new Student();
//        student.setId(13);
//        student.setUserName("qlwz");
//        student.setFirstName("zhichao");
//        student.setLastName("kong");
//        student.setScore(90.0);
//        studentRepository.saveAndFlush(student);
    }

}
