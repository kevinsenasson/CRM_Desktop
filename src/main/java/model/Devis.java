package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Devis {

    private Integer id;
    private Integer idClient;
    private String reference;
    private LocalDate dateCreation;
    private Double montant;
    private StatutDevis statut;
    private String description;
}
