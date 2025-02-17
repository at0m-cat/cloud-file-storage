package matveyodintsov.cloudfilestorage.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Breadcrumb {
    private String name;
    private String path;
}