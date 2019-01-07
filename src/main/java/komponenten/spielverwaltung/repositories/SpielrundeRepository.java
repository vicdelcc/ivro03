package komponenten.spielverwaltung.repositories;

import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.karten.export.Spielkarte;
import komponenten.spielverwaltung.export.Spielrunde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpielrundeRepository extends JpaRepository<Spielrunde, Long> {


}
