package com.eya.pfe2.eyapfe2.Controllers;

import com.eya.pfe2.eyapfe2.Models.Compte;
import com.eya.pfe2.eyapfe2.Models.DTO.CompteWithOwnerDTO;
import com.eya.pfe2.eyapfe2.Models.DTO.UserDTO;
import com.eya.pfe2.eyapfe2.Models.User;
import com.eya.pfe2.eyapfe2.Repository.CompteRepository;
import com.eya.pfe2.eyapfe2.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final CompteRepository compteRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getAdminProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = convertToUserDTO(user);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/comptes")
    public ResponseEntity<List<CompteWithOwnerDTO>> getAllComptes() {
        List<Compte> comptes = compteRepository.findAll();
        List<CompteWithOwnerDTO> compteWithOwnerDTOs = comptes.stream()
                .map(this::convertToCompteWithOwnerDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(compteWithOwnerDTOs);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNom(user.getNom());
        userDTO.setPrenom(user.getPrenom());
        userDTO.setDateNaissance(user.getDateNaissance());
        userDTO.setTelephone(user.getTelephone());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private CompteWithOwnerDTO convertToCompteWithOwnerDTO(Compte compte) {
        CompteWithOwnerDTO dto = new CompteWithOwnerDTO();
        dto.setId(compte.getId());
        dto.setNumCompte(compte.getNumCompte());
        dto.setSolde(compte.getSolde());
        dto.setEtat(compte.getEtat());
        dto.setDateOuverture(compte.getDateOuverture());
        dto.setCompteType(compte.getCompteType());
        dto.setOwnerName(compte.getUser().getNom() + " " + compte.getUser().getPrenom());
        dto.setOwnerEmail(compte.getUser().getEmail());
        return dto;
    }
}
