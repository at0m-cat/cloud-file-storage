package matveyodintsov.cloudfilestorage.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Breadcrumb {
    private String name;
    private String path;
}