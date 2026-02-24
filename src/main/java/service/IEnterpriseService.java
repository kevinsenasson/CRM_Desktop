package service;

import model.Enterprise;

public interface IEnterpriseService {

    void saveEnterprise(Enterprise enterprise);
    Enterprise getEnterprise();
    void updateEnterprise(Enterprise enterprise);
}
