import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainPage extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=uft-8");
        File file = new File("/home/scarecrow/IdeaProjects/WebAppProgramming/web/main.html");
        FileInputStream inputStream = new FileInputStream(file);
        OutputStream writer = resp.getOutputStream();
        writer.write(inputStream.readAllBytes());
        writer.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String curr_user = req.getParameter("curr_user");

            OutputStream outputStream = resp.getOutputStream();
            ResultSet resultSet = DBAction.getInstance()
                    .doQuery("select * from books ");
            File templete_file = new File("/home/scarecrow/IdeaProjects/WebAppProgramming/web/list_item_template.html");
            FileInputStream inputStream = new FileInputStream(templete_file);
            String list_item_template = new String(inputStream.readAllBytes());

            ArrayList<String> books = new ArrayList<>();

            while (resultSet.next()) {
                Book book = new Book(resultSet);
                String temp = new String(list_item_template.getBytes());
                String book_id = "book" + book.book_id;
                temp = String.format(temp, book_id, book.book_name, book.comment_info, book.date, book.price);
                books.add(temp);
            }
            Gson gson = new Gson();
            String json_res = gson.toJson(books.toArray());
            System.out.println(json_res);
            outputStream.write(json_res.getBytes());
            outputStream.close();

        } catch (Exception ee) {
            System.out.println("error in register");
            ee.printStackTrace();
        }

    }

}


class Book {
    String book_name, status, date, comment_info;
    int book_id;
    float price;

    public Book(ResultSet resultSet) throws SQLException {
        book_id = resultSet.getInt(1);
        book_name = resultSet.getString(2);
        status = resultSet.getString(3);
        price = resultSet.getFloat(4);
        date = resultSet.getString(5);
        comment_info = resultSet.getString(6);
    }

}