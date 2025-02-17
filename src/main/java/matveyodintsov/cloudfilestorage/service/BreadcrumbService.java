package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.models.Breadcrumb;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadcrumbService {

    public List<Breadcrumb> generateBreadcrumbs(String path) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        String[] parts = path.split("/");
        StringBuilder accumulatedPath = new StringBuilder();

        breadcrumbs.add(new Breadcrumb("main", "/storage"));

        for (String part : parts) {
            if (!part.isEmpty()) {
                accumulatedPath.append("/").append(part);
                breadcrumbs.add(new Breadcrumb(part, "/storage/my" + accumulatedPath.toString()));
            }
        }
        return breadcrumbs;
    }
}
