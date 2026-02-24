package dao;

import model.Enterprise;

public interface IEnterpriseDAO {

    void saveEnterprise(Enterprise enterprise);
    Enterprise getEnterprise();
    int updateEnterprise(Enterprise enterprise);
}
