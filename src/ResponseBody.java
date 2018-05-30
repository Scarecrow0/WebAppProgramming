import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class ResponseBody {

    String curr_user;

    public ResponseBody(HttpSession session) {
        curr_user = (String) session.getAttribute("curr_user");

    }

    static public String fetchTemplate(String template_name) throws IOException {

        File template_file = new File(String.format("/home/scarecrow/IdeaProjects/WebAppProgramming/web/%s_template.html", template_name));
        FileInputStream inputStream = new FileInputStream(template_file);
        return new String(inputStream.readAllBytes());

    }
}
