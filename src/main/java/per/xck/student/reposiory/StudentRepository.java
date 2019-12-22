package per.xck.student.reposiory;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import per.xck.student.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Integer> {
    @Query(value = "select * from student limit ?1, 5",nativeQuery = true)
    List<Student> getPage(@Param("page") Integer page);

//    @Query(value = "select id from grades where phone = ?1",nativeQuery = true)
//    Integer queryPhoneNum(@Param("phone") String phone);



}
