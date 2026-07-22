package fr.may_baptiste.allcraft0r_discord.system.repository;

import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
  List<UserEntity> findAllByOrderByMoneyDesc();
}
