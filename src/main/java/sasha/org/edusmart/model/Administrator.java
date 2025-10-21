package sasha.org.edusmart.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import sasha.org.edusmart.dto.AdministratorDTO;

@Entity
@Data
@NoArgsConstructor
@RedisHash
public class Administrator extends Person{

    public static Administrator of(AdministratorDTO administratorDTO) {
        Administrator administrator = new Administrator();
        if (administratorDTO.getId() != null) {
            administrator.setId(administratorDTO.getId());
        }
        administrator.setEmail(administratorDTO.getEmail());
        administrator.setVerified(administratorDTO.isVerified());
        administrator.setPassword(administratorDTO.getPassword());
        administrator.setRole(administratorDTO.getRole());
        administrator.setFirstName(administratorDTO.getFirstName());
        administrator.setLastName(administratorDTO.getLastName());
        return administrator;
    }

    public AdministratorDTO administratorDTO() {
        AdministratorDTO administratorDTO = new AdministratorDTO();
        administratorDTO.setId(this.getId());
        administratorDTO.setEmail(this.getEmail());
        administratorDTO.setVerified(this.isVerified());
        administratorDTO.setPassword(this.getPassword());
        administratorDTO.setRole(this.getRole());
        administratorDTO.setFirstName(this.getFirstName());
        administratorDTO.setLastName(this.getLastName());
        return administratorDTO;
    }

}
