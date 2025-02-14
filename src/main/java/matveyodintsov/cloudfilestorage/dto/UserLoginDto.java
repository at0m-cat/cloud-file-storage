package matveyodintsov.cloudfilestorage.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDto {

    private String login;
    private String password;

}
