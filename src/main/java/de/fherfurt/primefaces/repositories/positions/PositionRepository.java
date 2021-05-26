package de.fherfurt.primefaces.repositories.positions;

import de.fherfurt.primefaces.domains.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

}
