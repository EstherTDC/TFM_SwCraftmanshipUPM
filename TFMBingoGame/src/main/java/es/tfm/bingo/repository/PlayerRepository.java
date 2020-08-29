package es.tfm.bingo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.tfm.bingo.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
	
	Player findByForeignIdAndGame_Id(Long foreignId,Long id);
}
