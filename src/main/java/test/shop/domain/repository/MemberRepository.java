package test.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.member.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<List<Member>> findMembersByUsername(String username);
    Optional<Member> findMemberByUsername(String username);
    Optional<Member> findMemberById(Long memberId);
}
