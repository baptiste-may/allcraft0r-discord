package fr.may_baptiste.allcraft0r_discord.system.mapper;

import fr.may_baptiste.allcraft0r_discord.system.dto.UserMoneyDTO;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserMoneyDTO toUserMoneyDTO(UserEntity userEntity);
}
