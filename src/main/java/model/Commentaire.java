package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Commentaire {

    private Integer id;
    private Integer idClient;
    private String commentaire;
    private LocalDate date;
    private LocalDate dateDerniereModification;

}
