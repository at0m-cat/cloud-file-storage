package matveyodintsov.cloudfilestorage.dto;

import jakarta.validation.Valid;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterDto {

    @Valid
    private String login;
    @Valid
    private String password;
    @Valid
    private String repeatPassword;

}
