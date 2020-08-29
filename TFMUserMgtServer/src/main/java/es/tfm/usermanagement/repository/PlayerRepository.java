package es.tfm.usermanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfm.usermanagement.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
	List<Player> findByMyGroup_idOrderByPointsDesc(Long id);
	List<Player> findByPlayerName(String playerName);
	List<Player> findByPlayerNameAndIsGroupAdmin(String playerName,boolean isGroupAdmin);
	Player findByPlayerNameAndMyGroup_id(String playerName,Long id);
}
