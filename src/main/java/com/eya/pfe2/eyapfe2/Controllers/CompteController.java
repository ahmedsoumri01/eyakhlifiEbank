package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/compte")
@RequiredArgsConstructor
@Slf4j
public class CompteController {

    private final CompteRepository compteRepository;

    @GetMapping("/{numCompte}/solde")
    public ResponseEntity<Double> checkSolde(@PathVariable String numCompte) {
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(numCompte);
        return compteOptional.map(compte -> ResponseEntity.ok(compte.getSolde()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{numCompte}/desactivate")
    public ResponseEntity<String> desactivateAccount(@PathVariable String numCompte) {
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(numCompte);

        if (compteOptional.isPresent()) {
            Compte compte = compteOptional.get();
            compte.setEtat("INACTIVE");
            compteRepository.save(compte);
            return ResponseEntity.ok("Account deactivated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{numCompte}/activate")
    public ResponseEntity<String> activateAccount(@PathVariable String numCompte) {
        Optional<Compte> compteOptional = compteRepository.findByNumCompte(numCompte);

        if (compteOptional.isPresent()) {
            Compte compte = compteOptional.get();
            compte.setEtat("ACTIVE");
            compteRepository.save(compte);
            return ResponseEntity.ok("Account activated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}