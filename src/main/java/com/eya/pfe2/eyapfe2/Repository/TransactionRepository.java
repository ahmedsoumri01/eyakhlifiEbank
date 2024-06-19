package com.eya.pfe2.eyapfe2.Repository;

import com.eya.pfe2.eyapfe2.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
