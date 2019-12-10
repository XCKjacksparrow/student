package per.xck.student.reposiory;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import per.xck.student.entity.Administrator;

public interface AdministratorRepository extends JpaRepository<Administrator,Integer> {

    @Query(name = "select * from administrator where account = ?1",nativeQuery = true)
    Administrator findByAccount(@Param("account") String account);
}
