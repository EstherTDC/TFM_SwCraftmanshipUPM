package es.tfm.bingo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.tfm.bingo.model.BingoCard;

public interface BingoCardRepository extends JpaRepository<BingoCard, String> {
	
}
