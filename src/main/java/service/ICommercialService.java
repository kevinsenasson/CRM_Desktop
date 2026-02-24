package service;

import model.Commercial;

public interface ICommercialService {

    void saveCommercial(model.Commercial commercial);
    Commercial getCommercial();
    void updateCommercial(Commercial commercial);
}
