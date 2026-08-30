package fr.may_baptiste.allcraft0r_discord.system.service;

import fr.may_baptiste.allcraft0r_discord.system.dto.UserMoneyDTO;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.exception.commands.CannotExecuteDailyException;
import fr.may_baptiste.allcraft0r_discord.system.mapper.UserMapper;
import fr.may_baptiste.allcraft0r_discord.system.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MoneyService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public static final ZoneId TIME_ZONE = ZoneId.of("Europe/Paris");
  public static final long DEFAULT_MONEY = 250;
  public static final long DAILY_MONEY = 100;

  @Transactional(readOnly = true)
  public long getMoney(String userId) {
    return getOrCreateUser(userId).getMoney();
  }

  public long addMoney(String userId, long amount) {
    final var user = getOrCreateUser(userId);
    user.setMoney(user.getMoney() + amount);
    userRepository.save(user);
    return user.getMoney();
  }

  public synchronized boolean tryDeductMoney(String userId, long amount) {
    final var user = getOrCreateUser(userId);
    if (user.getMoney() < amount) {
      return false;
    }
    user.setMoney(user.getMoney() - amount);
    userRepository.save(user);
    return true;
  }

  public synchronized long executeDaily(String userId) throws CannotExecuteDailyException {
    final var user = getOrCreateUser(userId);
    final var todayStart = LocalDate.now(TIME_ZONE).atStartOfDay();
    if (user.getLastDaily() == null || user.getLastDaily().isBefore(todayStart)) {
      user.setMoney(user.getMoney() + DAILY_MONEY);
      user.setLastDaily(LocalDateTime.now(TIME_ZONE));
      userRepository.save(user);
      return user.getMoney();
    }
    throw new CannotExecuteDailyException(todayStart.plusDays(1));
  }

  @Transactional(readOnly = true)
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
              final var newUser = new UserEntity();
              newUser.setId(userId);
              newUser.setMoney(DEFAULT_MONEY);
              return userRepository.save(newUser);
            });
  }
}
