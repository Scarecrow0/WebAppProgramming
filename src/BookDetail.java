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

        String curr_user;
        if (!req.getSession().isNew())
            curr_user = (String) req.getSession().getAttribute("curr_user");

        switch (action) {
            case "show":
                try {
                    ResultSet resultSet = DBAction.getInstance()
                            .doQuery("select * from books where book_id = " + book_id);
                    resultSet.next();
                    Book book = new Book(resultSet);

                    String book_detail_template = ResponseBody.fetchTemplate("book_detail");
                    book_detail_template =
                            String.format(book_detail_template,
                                    "book" + book.book_id, book.book_name, book.price, book.comment_info);

                    String user_detail_temlate = ResponseBody.fetchTemplate("post_user_detail");
                    resultSet = DBAction.getInstance()
                            .doQuery("select post_user from sell_relation where book_id = " + book_id);
                    resultSet.next();
                    String post_user = resultSet.getString(1);
                    user_detail_temlate =
                            String.format(user_detail_temlate, post_user);

                    BookDetailResponseBody responseBody =
                            new BookDetailResponseBody(book_detail_template, user_detail_temlate, req.getSession());
                    Gson gson = new Gson();
                    String response_json = gson.toJson(responseBody);

                    resp.getOutputStream().write(response_json.getBytes());
                    resp.getOutputStream().close();
                    System.out.println("show book detail completed");


                } catch (Exception ee) {
                    ee.printStackTrace(System.out);
                }
                break;
            case "buy":
                break;
            default:
                break;

        }
    }
}

class BookDetailResponseBody extends ResponseBody {
    String book_detail, user_info;

    public BookDetailResponseBody(String book_detail, String user_info, HttpSession session) {
        super(session);
        this.book_detail = book_detail;
        this.user_info = user_info;
        curr_user = (String) session.getAttribute("curr_user");
    }


}

