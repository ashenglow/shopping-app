package test.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.member.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<List<Member>> findMembersByUserId(String userId);
    Optional<Member> findMemberByUserId(String userId);
    Optional<Member> findMemberById(Long memberId);
    Optional<Member> findMemberByProviderAndProviderId(String provider, String providerId);
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.orders WHERE m.id = :memberId")
    Optional<Member> findByIdWithOrders(@Param("memberId") Long memberId);
}
