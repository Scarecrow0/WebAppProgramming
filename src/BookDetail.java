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

public class BookDetail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getRequestURL().toString());
        resp.setContentType("text/html;charset=uft-8");
        File file = new File("/home/scarecrow/IdeaProjects/WebAppProgramming/web/book_detail_page.html");
        FileInputStream inputStream = new FileInputStream(file);
        OutputStream writer = resp.getOutputStream();
        writer.write(inputStream.readAllBytes());
        writer.close();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Integer book_id = Integer.valueOf(req.getParameter("book_id"));
        ResultSet resultSet;
        try {
            switch (action) {
                case "show":
                    resultSet = DBAction.getInstance()
                            .doQuery("select * from books where book_id = " + book_id);
                    resultSet.next();
                    Book book = new Book(resultSet);

                    String book_detail_template = ResponseBody.fetchTemplate("book_detail");
                    book_detail_template =
                            String.format(book_detail_template,
                                    "book" + book.book_id, book.book_id + "", book.book_name, book.price, book.comment_info);

                    String post_user_detail = ResponseBody.fetchTemplate("post_user_detail");
                    resultSet = DBAction.getInstance()
                            .doQuery("select post_user from sell_relation where book_id = " + book_id);
                    resultSet.next();
                    String post_user = resultSet.getString(1);
                    post_user_detail =
                            String.format(post_user_detail, post_user);

                    BookDetailResponseBody responseBody =
                            new BookDetailResponseBody(book_detail_template, post_user_detail, req.getSession());
                    Gson gson = new Gson();
                    String response_json = gson.toJson(responseBody);

                    resp.getOutputStream().write(response_json.getBytes());
                    resp.getOutputStream().close();
                    System.out.println("show book detail completed");

                    break;
                case "buy":
                    String curr_user = (String) req.getSession().getAttribute("curr_user");
                    DBAction.getInstance()
                            .doInsert("update books set salestate='sold' where book_id = " + book_id);
                    DBAction.getInstance()
                            .doInsert(String.format("update sell_relation set buy_user='%s' where book_id=%d",
                                    curr_user, book_id));
                    resp.getOutputStream().write("ok".getBytes());
                    resp.getOutputStream().close();
                    System.out.println("buy book completed");


                    break;
                default:
                    break;

            }
        } catch (Exception ee) {
            ee.printStackTrace(System.out);
        }
    }
}

class BookDetailResponseBody extends ResponseBody {
    String book_detail, user_info;

    public BookDetailResponseBody(String book_detail, String post_user_info, HttpSession session) {
        super(session);
        this.user_info = post_user_info;
        this.book_detail = book_detail;
    }


}

