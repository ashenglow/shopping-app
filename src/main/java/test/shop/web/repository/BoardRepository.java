package test.shop.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.shop.domain.Board;


@Repository
public interface BoardRepository extends JpaRepository<Board, Long>{

Board findBoardDistinctById(Long id);

}
