package komponenten.spielverwaltung.repositories;

import komponenten.spielverwaltung.entities.Protokoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtokollRepository extends JpaRepository<Protokoll, Long> {

}
