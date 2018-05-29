import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;

public class BookDetail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = (String) req.getAttribute("action");
        Integer book_id = (Integer) req.getAttribute("book_id");

        switch (action) {
            case "show":
                try {
                    ResultSet resultSet = DBAction.getInstance()
                            .doQuery("select * from books where book_id = " + book_id);
                    resultSet.next();
                    Book book = new Book(resultSet);


                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                break;
            case "buy":
                break;
            default:
                break;

        }


    }
}
