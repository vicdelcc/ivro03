package komponenten.spielverwaltung.repositories;

import komponenten.spielverwaltung.export.Ergebnis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErgebnisRepository extends JpaRepository<Ergebnis, Long> {

}
