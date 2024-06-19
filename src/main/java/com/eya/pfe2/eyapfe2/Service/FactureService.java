package com.eya.pfe2.eyapfe2.Service;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.DTO.FactureDTO;
import com.eya.pfe2.eyapfe2.Models.Facture;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.FactureRepository;
import com.eya.pfe2.eyapfe2.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactureService {
    private final FactureRepository factureRepository;
    private final UserRepository userRepository;
    private final CompteRepository compteRepository;

    public List<FactureDTO> getClientFactures(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return factureRepository.findByUser(user).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public FactureDTO getFactureDetails(Long id) {
        return factureRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public FactureDTO payFacture(Long id, Long compteId) {
        Optional<Facture> factureOptional = factureRepository.findById(id);
        Optional<Compte> compteOptional = compteRepository.findById(compteId);

        if (factureOptional.isPresent() && compteOptional.isPresent()) {
            Facture facture = factureOptional.get();
            Compte compte = compteOptional.get();

            if (compte.getSolde() >= facture.getMontant() && !facture.isPaye()) {
                compte.setSolde(compte.getSolde() - facture.getMontant());
                facture.setPaye(true);
                compteRepository.save(compte);
                factureRepository.save(facture);
                return convertToDTO(facture);
            }
        }
        return null;
    }

    public FactureDTO createFakeFacture(FactureDTO factureDTO, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Facture facture = new Facture();
            facture.setMontant(factureDTO.getMontant());
            facture.setReference(factureDTO.getReference());
            facture.setPaye(false);
            facture.setLibelle(factureDTO.getLibelle());
            facture.setUser(user);
            Facture savedFacture = factureRepository.save(facture);
            return convertToDTO(savedFacture);
        }
        return null;
    }

    private FactureDTO convertToDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setMontant(facture.getMontant());
        dto.setReference(facture.getReference());
        dto.setPaye(facture.isPaye());
        dto.setLibelle(facture.getLibelle());
        return dto;
    }
}
