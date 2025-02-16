package matveyodintsov.cloudfilestorage.service;

public interface UserService<T, K> {

    void save(T user);

    boolean existsByLogin(String login);

    K findByLogin(String login);

}
