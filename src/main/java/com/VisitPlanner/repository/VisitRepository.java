package com.VisitPlanner.repository;

import com.VisitPlanner.entity.User;
import com.VisitPlanner.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Integer> {
    Visit findByVisitNumberIgnoreCase(@NonNull String visitNumber);

    List<Visit> findAllByStatusOrderByReservedTimeAsc(Visit.Status status);

    List<Visit> findByUserOrderByStatusDescReservedTimeAsc(@NonNull Optional<User> user);

    List<Visit> findByUserOrderByReservedTimeAsc(@NonNull Optional<User> user);

}
