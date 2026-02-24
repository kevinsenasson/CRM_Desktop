package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {
    private Integer id;
    private String societe;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;
    private LocalDate dateInscription;
    private StatutClient statut;
    private LocalDate dateDerniereModification;
    private SegmentClient segment;
    private SourceAcquisitionClient sourceAcquisition;
    List<Commentaire> commentaires = new ArrayList<>();
}
