package dao;

import model.Devis;

import java.util.List;

public interface IDevisDao {
    List<Devis> findAll();
    void saveDevis(Devis devis);
    List<Devis> getDevisByClientId(Integer id);
    int deleteDevisById(Integer id);
    int updateDevis(Devis devis);
}
