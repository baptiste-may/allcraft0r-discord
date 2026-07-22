package fr.may_baptiste.allcraft0r_discord.service;

import static fr.may_baptiste.allcraft0r_discord.TestUtils.buildUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.system.dto.UserMoneyDTO;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.exception.commands.CannotExecuteDailyException;
import fr.may_baptiste.allcraft0r_discord.system.mapper.UserMapper;
import fr.may_baptiste.allcraft0r_discord.system.repository.UserRepository;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MoneyServiceTest {
  MoneyService moneyService;

  UserRepository userRepository;
  UserMapper userMapper;

  @BeforeEach
  void setup() {
    userRepository = mock(UserRepository.class);
    userMapper = mock(UserMapper.class);
    moneyService = new MoneyService(userRepository, userMapper);
  }

  @Nested
  class GetMoney {

    @Test
    @DisplayName("should return default money when user entity do not exists yet")
    void shouldReturnDefaultMoneyWhenUserEntityDoNotExistsYet() {
      when(userRepository.findById("userId")).thenReturn(Optional.empty());
      final var exceptedUserEntity = buildUserEntity("userId");
      when(userRepository.save(exceptedUserEntity)).thenReturn(exceptedUserEntity);

      assertThat(moneyService.getMoney("userId")).isEqualTo(MoneyService.DEFAULT_MONEY);
      verify(userRepository).save(exceptedUserEntity);
    }

    @Test
    @DisplayName("should return money when user entity exists")
    void shouldReturnMoneyWhenUserEntityExists() {
      when(userRepository.findById("userId"))
          .thenReturn(Optional.of(buildUserEntity("userId", 12345)));

      assertThat(moneyService.getMoney("userId")).isEqualTo(12345);
    }
  }

  @Nested
  class AddMoney {

    private final long ADDED_MONEY = 123;

    @Test
    @DisplayName("should return correct additionned money when user entity do not exists yet")
    void shouldReturnCorrectAdditionedMoneyWhenUserEntityDoNotExistsYet() {
      when(userRepository.findById("userId")).thenReturn(Optional.empty());
      final var defaultUserEntity = buildUserEntity("userId");
      when(userRepository.save(defaultUserEntity)).thenReturn(defaultUserEntity);
      final var exceptedUserEntity = buildUserEntity("userId");
      exceptedUserEntity.setMoney(defaultUserEntity.getMoney() + ADDED_MONEY);
      when(userRepository.save(exceptedUserEntity)).thenReturn(exceptedUserEntity);

      assertThat(moneyService.addMoney("userId", ADDED_MONEY))
          .isEqualTo(MoneyService.DEFAULT_MONEY + ADDED_MONEY);
      verify(userRepository).save(defaultUserEntity);
      verify(userRepository).save(exceptedUserEntity);
    }

    @Test
    @DisplayName("should return correct additionned money when user entity exists")
    void shouldReturnCorrectAdditionedMoneyWhenUserEntityExists() {
      final var userEntity = buildUserEntity("userId", 12345);
      when(userRepository.findById("userId")).thenReturn(Optional.of(userEntity));
      final var expectedUserEntity = buildUserEntity("userId", userEntity.getMoney() + ADDED_MONEY);
      when(userRepository.save(expectedUserEntity)).thenReturn(expectedUserEntity);

      assertThat(moneyService.addMoney("userId", ADDED_MONEY))
          .isEqualTo(expectedUserEntity.getMoney());
      verify(userRepository).save(expectedUserEntity);
    }
  }

  @Nested
  class ExecuteDaily {

    @Test
    @DisplayName("should execute daily and create user entity when it do not exists yet")
    void shouldExecuteDailyAndCreateUserEntityWhenItDoNotExistsYet()
        throws CannotExecuteDailyException {
      final List<UserEntity> savedUsers = new ArrayList<>();
      when(userRepository.findById("userId"))
          .thenAnswer(
              invocation -> {
                if (!savedUsers.isEmpty()) {
                  UserEntity last = savedUsers.getLast();
                  UserEntity copy = new UserEntity();
                  copy.setId(last.getId());
                  copy.setMoney(last.getMoney());
                  copy.setLastDaily(last.getLastDaily());
                  return Optional.of(copy);
                }
                return Optional.empty();
              });
      doAnswer(
              invocation -> {
                UserEntity user = invocation.getArgument(0);
                UserEntity copy = new UserEntity();
                copy.setId(user.getId());
                copy.setMoney(user.getMoney());
                copy.setLastDaily(user.getLastDaily());
                savedUsers.add(copy);
                return user;
              })
          .when(userRepository)
          .save(any(UserEntity.class));

      assertThat(moneyService.executeDaily("userId"))
          .isEqualTo(MoneyService.DEFAULT_MONEY + MoneyService.DAILY_MONEY);

      assertThat(savedUsers).hasSize(2);
      // First save: creating new user with default money
      assertThat(savedUsers.getFirst().getId()).isEqualTo("userId");
      assertThat(savedUsers.getFirst().getMoney()).isEqualTo(MoneyService.DEFAULT_MONEY);
      assertThat(savedUsers.getFirst().getLastDaily()).isNull();

      // Second save: after daily bonus and setting lastDaily
      assertThat(savedUsers.get(1).getId()).isEqualTo("userId");
      assertThat(savedUsers.get(1).getMoney())
          .isEqualTo(MoneyService.DEFAULT_MONEY + MoneyService.DAILY_MONEY);
      assertThat(savedUsers.get(1).getLastDaily()).isNotNull();
    }

    @Test
    @DisplayName("should execute daily and update user when user entity exists and can daily")
    void shouldExecuteDailyAndUpdateUserWhenUserEntityExistsAndCanDaily()
        throws CannotExecuteDailyException {
      final var userEntity = buildUserEntity("userId");
      LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
      userEntity.setLastDaily(yesterday);

      final List<UserEntity> savedUsers = new ArrayList<>();
      when(userRepository.findById("userId")).thenReturn(Optional.of(userEntity));

      doAnswer(
              invocation -> {
                UserEntity user = invocation.getArgument(0);
                UserEntity copy = new UserEntity();
                copy.setId(user.getId());
                copy.setMoney(user.getMoney());
                copy.setLastDaily(user.getLastDaily());
                savedUsers.add(copy);
                return user;
              })
          .when(userRepository)
          .save(any(UserEntity.class));

      assertThat(moneyService.executeDaily("userId"))
          .isEqualTo(MoneyService.DEFAULT_MONEY + MoneyService.DAILY_MONEY);

      assertThat(savedUsers).hasSize(1);
      // Single save: money increased with daily bonus and lastDaily set
      assertThat(savedUsers.getFirst().getId()).isEqualTo("userId");
      assertThat(savedUsers.getFirst().getMoney())
          .isEqualTo(MoneyService.DEFAULT_MONEY + MoneyService.DAILY_MONEY);
      assertThat(savedUsers.getFirst().getLastDaily()).isNotNull();
      assertThat(savedUsers.getFirst().getLastDaily()).isAfter(yesterday);
    }

    @Test
    @DisplayName("should throw CannotExecuteDailyException when user cannot daily yet")
    void shouldThrowCannotExecuteDailyExceptionWhenUserCannotDailyYet() {
      final var userEntity = buildUserEntity("userId");
      LocalDateTime now = LocalDateTime.now();
      userEntity.setLastDaily(now);
      when(userRepository.findById("userId")).thenReturn(Optional.of(userEntity));

      assertThatThrownBy(() -> moneyService.executeDaily("userId"))
          .isInstanceOf(CannotExecuteDailyException.class)
          .extracting(e -> ((CannotExecuteDailyException) e).getNextAvailableDaily())
          .isEqualTo(now.plusDays(1));
    }
  }

  @Nested
  class GetLeaderboard {

    @Test
    @DisplayName("should return empty leaderboard when no users exist")
    void shouldReturnEmptyLeaderboardWhenNoUsersExist() {
      when(userRepository.findAllByOrderByMoneyDesc()).thenReturn(List.of());

      final var result = moneyService.getLeaderboard(10);

      assertThat(result).isEmpty();
      verify(userRepository).findAllByOrderByMoneyDesc();
    }

    @Test
    @DisplayName("should return all users when count is less than maxSize")
    void shouldReturnAllUsersWhenCountIsLessThanMaxSize() {
      final var user1 = buildUserEntity("user1", 500);
      final var user2 = buildUserEntity("user2", 300);
      final var user3 = buildUserEntity("user3", 100);

      when(userRepository.findAllByOrderByMoneyDesc()).thenReturn(List.of(user1, user2, user3));
      when(userMapper.toUserMoneyDTO(user1)).thenReturn(new UserMoneyDTO("user1", 500));
      when(userMapper.toUserMoneyDTO(user2)).thenReturn(new UserMoneyDTO("user2", 300));
      when(userMapper.toUserMoneyDTO(user3)).thenReturn(new UserMoneyDTO("user3", 100));

      final var result = moneyService.getLeaderboard(10);

      assertThat(result)
          .hasSize(3)
          .containsExactly(
              new UserMoneyDTO("user1", 500),
              new UserMoneyDTO("user2", 300),
              new UserMoneyDTO("user3", 100));
    }

    @Test
    @DisplayName("should return limited number of users when count exceeds maxSize")
    void shouldReturnLimitedUsersWhenCountExceedsMaxSize() {
      final var user1 = buildUserEntity("user1", 1000);
      final var user2 = buildUserEntity("user2", 800);
      final var user3 = buildUserEntity("user3", 600);
      final var user4 = buildUserEntity("user4", 400);
      final var user5 = buildUserEntity("user5", 200);

      when(userRepository.findAllByOrderByMoneyDesc())
          .thenReturn(List.of(user1, user2, user3, user4, user5));
      when(userMapper.toUserMoneyDTO(user1)).thenReturn(new UserMoneyDTO("user1", 1000));
      when(userMapper.toUserMoneyDTO(user2)).thenReturn(new UserMoneyDTO("user2", 800));
      when(userMapper.toUserMoneyDTO(user3)).thenReturn(new UserMoneyDTO("user3", 600));

      final var result = moneyService.getLeaderboard(3);

      assertThat(result)
          .hasSize(3)
          .containsExactly(
              new UserMoneyDTO("user1", 1000),
              new UserMoneyDTO("user2", 800),
              new UserMoneyDTO("user3", 600));
    }

    @Test
    @DisplayName("should return users in descending order by money")
    void shouldReturnUsersInDescendingOrderByMoney() {
      final var user1 = buildUserEntity("alice", 999);
      final var user2 = buildUserEntity("bob", 555);
      final var user3 = buildUserEntity("charlie", 123);

      when(userRepository.findAllByOrderByMoneyDesc()).thenReturn(List.of(user1, user2, user3));
      when(userMapper.toUserMoneyDTO(user1)).thenReturn(new UserMoneyDTO("alice", 999));
      when(userMapper.toUserMoneyDTO(user2)).thenReturn(new UserMoneyDTO("bob", 555));
      when(userMapper.toUserMoneyDTO(user3)).thenReturn(new UserMoneyDTO("charlie", 123));

      final var result = moneyService.getLeaderboard(10);

      assertThat(result)
          .hasSize(3)
          .extracting(UserMoneyDTO::money)
          .containsExactly(999L, 555L, 123L);
    }
  }
}
