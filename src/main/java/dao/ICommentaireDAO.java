package dao;

import model.Commentaire;

import java.util.List;

public interface ICommentaireDAO {
    void saveCommentaire(Commentaire commentaire);
    List<Commentaire> getCommentairesByClientId(Integer id);
    int deleteCommentaireById(Integer id);
    int updateCommentaire(Commentaire commentaire);
}
