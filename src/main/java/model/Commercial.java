package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Commercial {

    private Integer id;
    private Integer idEntreprise;
    private String nom;
    private String prenom;
    private String mail;
    private String telephone;
    private LocalDate dateEmbauche;
    private Integer ca;
    private Integer objectifCa;

}
