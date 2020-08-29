package es.tfm.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.tfm.usermanagement.model.GameUser;

public interface UserRepository extends JpaRepository<GameUser, Long> {
	GameUser findByUserName(String userName);
}
