package dao;

import model.Client;

import java.util.List;

public interface IClientDAO {
    void saveClient(Client client);
    List<Client> findAll();
    Client getClientById(Integer id);
    int updateClient(Client client);
    int deleteClientById(Integer id);
    Client getClientWithCommentsById(Integer id);
}
