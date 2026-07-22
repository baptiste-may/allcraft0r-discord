package fr.may_baptiste.allcraft0r_discord.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "discord_user")
@Data
public class UserEntity {
  @Id private String id;

  private long money;

  private LocalDateTime lastDaily;
}
