package service;

import model.Commentaire;

import java.util.List;

public interface ICommentaireService {

    void save(Commentaire commentaire);
    List<Commentaire> getCommentaireByClientId(Integer id);
    void deleteCommentaireById(Integer id);
    void updateCommentaire(Commentaire commentaire);

}
