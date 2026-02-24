package dao;

import model.Commercial;

public interface ICommercialDAO {
    void saveCommercial(Commercial commercial);
    Commercial getCommercial();
    int updateCommercial(Commercial commercial);
}
