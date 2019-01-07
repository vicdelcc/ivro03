package komponenten.karten.repositories;

import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.karten.export.Spielkarte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KartenRepository extends JpaRepository<Spielkarte, Long> {

    @Query("SELECT s FROM Spielkarte s WHERE s.blatttyp = ?1 AND s.blattwert = ?2")
    Spielkarte findByWertUndTyp(Blatttyp blatttyp, Blattwert blattwert);

}
