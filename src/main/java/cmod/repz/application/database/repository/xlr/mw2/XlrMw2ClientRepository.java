package cmod.repz.application.database.repository.xlr.mw2;

import cmod.repz.application.database.entity.xlr.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XlrMw2ClientRepository extends JpaRepository<ClientEntity, Integer> {
    List<ClientEntity> findAllByNameLike(String name);
    ClientEntity findByGuid(String guid);
    ClientEntity findByGuidLike(String guid);
}
