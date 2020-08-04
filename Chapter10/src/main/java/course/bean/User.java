package course.bean;

import lombok.Data;
import org.apache.http.entity.StringEntity;

@Data
public class User {
    private String name;
    private String password;
    private String age;
    private String sex;
}
