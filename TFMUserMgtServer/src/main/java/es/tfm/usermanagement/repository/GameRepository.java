package es.tfm.usermanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.tfm.usermanagement.model.Game;
import es.tfm.usermanagement.utils.GameStates;

public interface GameRepository extends JpaRepository<Game, Long> {

	Game findByGameNameAndUserGroup_GroupName(String gameName, String groupName);
	List<Game> findByGameAdmin_PlayerName(String playerName);
	List<Game> findByStatusAndUserGroup_Id(GameStates status,Long id);
}
