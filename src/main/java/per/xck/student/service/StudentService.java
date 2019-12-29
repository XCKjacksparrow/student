package per.xck.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import per.xck.student.entity.Student;
import per.xck.student.reposiory.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    StudentRepository studentRepository;

    public void deleteById(Integer id) {
        studentRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "total")
    public Integer getPagination(){
        Integer counts = Math.toIntExact(studentRepository.count());    // 总人数
        if (counts % 5 == 0){
            return counts / 5;
        }else {
            return counts / 5 + 1;
        }
    }

    @Cacheable(cacheNames = "students",key = "#page")
    public List<Student> getPage(Integer page){
        page = (page-1) * 5;
        List<Student> students = studentRepository.getPage(page);
        return students;
    }

    public void addOrUpdateStudent(Student student){
        studentRepository.saveAndFlush(student);
    }
}
