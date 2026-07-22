package fr.may_baptiste.allcraft0r_discord.system.service;

import fr.may_baptiste.allcraft0r_discord.system.dto.UserMoneyDTO;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.exception.commands.CannotExecuteDailyException;
import fr.may_baptiste.allcraft0r_discord.system.mapper.UserMapper;
import fr.may_baptiste.allcraft0r_discord.system.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public static final long DEFAULT_MONEY = 250;
  public static final long DAILY_MONEY = 100;

  public long getMoney(String userId) {
    return getOrCreateUser(userId).getMoney();
  }

  public long addMoney(String userId, long amount) {
    final var user = getOrCreateUser(userId);
    user.setMoney(user.getMoney() + amount);
    userRepository.save(user);
    return user.getMoney();
  }

  public long executeDaily(String userId) throws CannotExecuteDailyException {
    final var user = getOrCreateUser(userId);
    if (user.getLastDaily() == null
        || user.getLastDaily().isBefore(LocalDateTime.now().minusDays(1))) {
      user.setMoney(user.getMoney() + DAILY_MONEY);
      user.setLastDaily(LocalDateTime.now());
      userRepository.save(user);
      return user.getMoney();
    }
    throw new CannotExecuteDailyException(user.getLastDaily().plusDays(1));
  }

  public List<UserMoneyDTO> getLeaderboard(long maxSize) {
    return userRepository.findAllByOrderByMoneyDesc().stream()
        .limit(maxSize)
        .map(userMapper::toUserMoneyDTO)
        .toList();
  }

  private UserEntity getOrCreateUser(String userId) {
    return userRepository
        .findById(userId)
        .orElseGet(
            () -> {
              UserEntity newUser = new UserEntity();
              newUser.setId(userId);
              newUser.setMoney(DEFAULT_MONEY);
              return userRepository.save(newUser);
            });
  }
}
