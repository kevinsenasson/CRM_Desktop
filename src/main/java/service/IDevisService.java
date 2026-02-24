package service;

import model.Devis;

import java.util.List;

public interface IDevisService {

    void saveDevis(Devis devis);
    List<Devis> getDevisByClientId(Integer id);
    void deleteDevisById(Integer id);
    void updateDevis(Devis devis);
}
