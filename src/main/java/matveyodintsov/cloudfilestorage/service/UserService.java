package matveyodintsov.cloudfilestorage.service;

public interface UserService<T> {

    void save(T user);

    boolean existsByLogin(String login);

}
