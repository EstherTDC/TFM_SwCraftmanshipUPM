package es.tfm.bingo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import es.tfm.bingo.model.*;

public interface BallRepository extends JpaRepository<Ball, Long> {
}
