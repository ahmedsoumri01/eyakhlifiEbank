package com.eya.pfe2.eyapfe2.Service;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.DTO.RetraitDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.VersementDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.VirementDTO;
import com.eya.pfe2.eyapfe2.Models.Transaction;
import com.eya.pfe2.eyapfe2.Models.TransactionType;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public String virement(VirementDTO virementDTO) {
        Optional<Compte> fromCompteOpt = compteRepository.findByNumCompte(virementDTO.getFromCompte());
        Optional<Compte> toCompteOpt = compteRepository.findByNumCompte(virementDTO.getToCompte());

        if (fromCompteOpt.isPresent() && toCompteOpt.isPresent()) {
            Compte fromCompte = fromCompteOpt.get();
            Compte toCompte = toCompteOpt.get();

            if (fromCompte.getSolde() >= virementDTO.getMontant()) {
                fromCompte.setSolde(fromCompte.getSolde() - virementDTO.getMontant());
                toCompte.setSolde(toCompte.getSolde() + virementDTO.getMontant());

                Transaction fromTransaction = new Transaction();
                fromTransaction.setCompte(fromCompte);
                fromTransaction.setDate(new Date());
                fromTransaction.setMontant(virementDTO.getMontant());
                fromTransaction.setType(TransactionType.VIREMENT);
                transactionRepository.save(fromTransaction);

                Transaction toTransaction = new Transaction();
                toTransaction.setCompte(toCompte);
                toTransaction.setDate(new Date());
                toTransaction.setMontant(virementDTO.getMontant());
                toTransaction.setType(TransactionType.VIREMENT);
                transactionRepository.save(toTransaction);

                compteRepository.save(fromCompte);
                compteRepository.save(toCompte);

                return "Virement effectué avec succès.";
            } else {
                return "Solde insuffisant.";
            }
        } else {
            return "Compte non trouvé.";
        }
    }

    @Transactional
    public String versement(VersementDTO versementDTO) {
        Optional<Compte> compteOpt = compteRepository.findByNumCompte(versementDTO.getNumCompte());

        if (compteOpt.isPresent()) {
            Compte compte = compteOpt.get();
            compte.setSolde(compte.getSolde() + versementDTO.getMontant());

            Transaction transaction = new Transaction();
            transaction.setCompte(compte);
            transaction.setDate(new Date());
            transaction.setMontant(versementDTO.getMontant());
            transaction.setType(TransactionType.VERSEMENT);
            transactionRepository.save(transaction);

            compteRepository.save(compte);

            return "Versement effectué avec succès.";
        } else {
            return "Compte non trouvé.";
        }
    }

    @Transactional
    public String retrait(RetraitDTO retraitDTO) {
        Optional<Compte> compteOpt = compteRepository.findByNumCompte(retraitDTO.getNumCompte());

        if (compteOpt.isPresent()) {
            Compte compte = compteOpt.get();

            if (compte.getSolde() >= retraitDTO.getMontant()) {
                compte.setSolde(compte.getSolde() - retraitDTO.getMontant());

                Transaction transaction = new Transaction();
                transaction.setCompte(compte);
                transaction.setDate(new Date());
                transaction.setMontant(retraitDTO.getMontant());
                transaction.setType(TransactionType.RETRAIT);
                transactionRepository.save(transaction);

                compteRepository.save(compte);

                return "Retrait effectué avec succès.";
            } else {
                return "Solde insuffisant.";
            }
        } else {
            return "Compte non trouvé.";
        }
    }

    public List<Transaction> getTransactionsByCompte(String numCompte) {
        Optional<Compte> compteOpt = compteRepository.findByNumCompte(numCompte);

        return compteOpt.map(Compte::getTransactions).orElse(List.of());
    }
}
