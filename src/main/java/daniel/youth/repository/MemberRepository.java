package daniel.youth.repository;

import daniel.youth.domain.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByOrderByNameAsc();

    // 벌크 연산 주의 -> 바로 영속성 컨텍스트 비워야 함
    // 벌크 연산은 DB에 바로 적용하기 때문에 오래 된 영속성 컨텍스트 데이터는 지워야 한다. -> 1차캐시와 DB 데이터 정합성 깨짐
    @Modifying(flushAutomatically = true)
    @Query("delete from Member m")
    void clearAll();

    @Modifying(flushAutomatically = true)
    @Query("update Member m set m.team = null")
    void clearAllMemberTeams();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.name = :name")
    Optional<Member> findByNameWithLock(@Param("name") String name);

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m")
    List<Member> findAllWithLock();



}
