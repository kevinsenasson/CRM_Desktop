package service;

import model.Client;

import java.util.List;

public interface IClientService {

    void saveClient(Client client);
    List<Client> findAll();
    Client getClientById(Integer id);
    void deleteClientById(Integer id);
    void updateClient(Client client);
    Client getClientWithCommentsById(Integer id);
}
