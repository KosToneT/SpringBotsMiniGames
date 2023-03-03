package com.develop.SpringMiniGames.Bots;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<UserModel, Long> {
    
    List<UserModel> findByUserIdAndPlatfrom(Integer userId, Platform platfrom);
    UserModel findById(long id);
}
