import com.google.gson.Gson;

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
import java.sql.SQLException;
import java.util.ArrayList;

public class UserCenter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getRequestURL().toString());
        resp.setContentType("text/html;charset=uft-8");
        File file = new File("/home/scarecrow/IdeaProjects/WebAppProgramming/web/user_center.html");
        FileInputStream inputStream = new FileInputStream(file);
        OutputStream writer = resp.getOutputStream();
        writer.write(inputStream.readAllBytes());
        writer.close();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession();
        String curr_user;
        if (session.isNew())
            curr_user = "";
        else
            curr_user = (String) req.getSession().getAttribute("curr_user");

        try {
            switch (action) {
                case "show":
                    ResultSet resultSet = DBAction.getInstance()
                            .doQuery(String.format("select phone_number from users where username = '%s'", curr_user));
                    resultSet.next();
                    String phone_number = resultSet.getString(1);

                    String query_cmd =
                            "select books.bookname, books.price, books.book_id, sell_relation.post_user\n" +
                                    "from users, sell_relation, books\n" +
                                    "where users.username = '%s' and sell_relation.buy_user = users.username and sell_relation.book_id = books.book_id";
                    query_cmd = String.format(query_cmd, curr_user);
                    resultSet = DBAction.getInstance()
                            .doQuery(query_cmd);


                    String list_item_template = ResponseBody.fetchTemplate("bought_books");
                    ArrayList<String> infos = new ArrayList<>();
                    while (resultSet.next()) {
                        BoughtBookInfo info = new BoughtBookInfo(resultSet);
                        String list_item = String.format(list_item_template,
                                info.book_name, info.book_price, info.book_id, info.post_user);
                        infos.add(list_item);
                    }

                    UserCenterResponsBody responsBody
                            = new UserCenterResponsBody(req.getSession(), infos.toArray(), phone_number, curr_user);
                    Gson gson = new Gson();
                    String result = gson.toJson(responsBody);
                    System.out.println(result);
                    resp.getOutputStream().write(result.getBytes());
                    resp.getOutputStream().close();
                    break;

                default:
                    break;

            }
        } catch (Exception ee) {
            ee.printStackTrace(System.out);
        }


    }


}

class BoughtBookInfo {
    String book_name, post_user;
    float book_price;
    int book_id;

    public BoughtBookInfo(ResultSet resultSet) throws SQLException {
        this.book_name = resultSet.getString(1);
        this.book_price = resultSet.getFloat(2);
        this.book_id = resultSet.getInt(3);
        this.post_user = resultSet.getString(4);
    }

}

class UserCenterResponsBody extends ResponseBody {
    Object[] brought_infos;
    String phone_number;

    public UserCenterResponsBody(HttpSession session, Object[] infos, String phone_number, String curr_user) {
        super(session);
        this.phone_number = phone_number;
        this.curr_user = curr_user;
        brought_infos = infos;
    }
}
