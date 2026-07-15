package com.honjaopseoyae.domain.term.repository;

import com.honjaopseoyae.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}
