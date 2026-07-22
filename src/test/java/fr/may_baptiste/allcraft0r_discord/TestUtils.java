package fr.may_baptiste.allcraft0r_discord;

import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;

public class TestUtils {

  public static UserEntity buildUserEntity(String userId, long money) {
    final UserEntity userEntity = new UserEntity();
    userEntity.setId(userId);
    userEntity.setMoney(money);
    return userEntity;
  }

  public static UserEntity buildUserEntity(String userId) {
    return buildUserEntity(userId, MoneyService.DEFAULT_MONEY);
  }
}
