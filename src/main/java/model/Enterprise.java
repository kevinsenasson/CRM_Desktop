package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Enterprise {

    private Integer id;
    private String nom;
    private String adresse;
    private String codePostal;
    private String ville;
    private String telephone;
    private String email;
    private String siret;
}
