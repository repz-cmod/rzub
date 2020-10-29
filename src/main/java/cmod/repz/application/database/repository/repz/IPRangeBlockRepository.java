package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.IPRangeBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

public interface IPRangeBlockRepository extends JpaRepository<IPRangeBlockEntity, Integer> {
    @Query("select case when count(c)> 0 then true else false end from #{#entityName} c WHERE c.startLong <= :input AND c.endLong >= :input")
    boolean existsByRange(long input);
    @Transactional
    void deleteAllByExpirationBefore(Date date);
}
