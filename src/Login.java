import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;

public class Login extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=uft-8");
        File file = new File("/home/scarecrow/IdeaProjects/WebAppProgramming/web/login1.html");
        FileInputStream inputStream = new FileInputStream(file);
        OutputStream writer = resp.getOutputStream();
        writer.write(inputStream.readAllBytes());
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username"),
                password = req.getParameter("password");
        System.out.println(username);
        System.out.println(password);
        try {
            OutputStream outputStream = resp.getOutputStream();
            ResultSet resultSet = DBAction.getInstance()
                    .doQuery(String.format("select * from users where username=\'%s\'and password=\'%s\';",
                            username, password));
            resultSet.next();
            if (resultSet.isLast()) {
                HttpSession session = req.getSession();
                session.setAttribute("curr_user", username);
                outputStream.write("ok".getBytes());
            } else {
                outputStream.write("failed".getBytes());
            }
            outputStream.close();

        } catch (Exception ee) {
            System.out.println("error in register");
            ee.printStackTrace();
        }
    }
}
