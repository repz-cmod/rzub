package cmod.repz.application.database.repository.xlr.bf3.bo2;

import cmod.repz.application.database.entity.xlr.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface XlrBf3ClientRepository extends JpaRepository<ClientEntity, Integer> {
    List<ClientEntity> findAllByNameLike(String name);
    ClientEntity findByGuid(String guid);
    ClientEntity findByGuidLike(String guid);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update #{#entityName} c set c.greeting = :greeting where c.id = :id")
    void updateGreeting(int id, String greeting);
}
