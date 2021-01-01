package axon.jeju.repository;

import axon.jeju.aggregate.JejuAccountAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JejuAccountRepository extends JpaRepository<JejuAccountAggregate, String> {
}
