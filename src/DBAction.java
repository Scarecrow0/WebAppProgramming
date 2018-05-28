

import java.sql.*;

public class DBAction {

    static private DBAction dbAction = null;
    private Connection connection;

    private DBAction() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/web_app",
                            "root", "654987321");

            PreparedStatement preparedStatement = connection.prepareStatement("show TABLES ;");

            ResultSet resultSet = preparedStatement.executeQuery();

            /*
                查询结果以行进行迭代每次调用next取一行 再用get方法get出来
                这行处理完成之后使用next取下一行 使用isLast判断是否到达最后一个
             */
//            while (!resultSet.isLast()) {
//                resultSet.next();
//                System.out.println(resultSet.getString(1));
//            }

        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }

    public static DBAction getInstance() {
        if (dbAction == null)
            dbAction = new DBAction();
        return dbAction;
    }

    public ResultSet doQuery(String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public boolean doInsert(String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.execute();
    }
}
