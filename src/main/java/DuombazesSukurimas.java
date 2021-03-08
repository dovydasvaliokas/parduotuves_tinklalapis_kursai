import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DuombazesSukurimas
{
    public static Connection sukurtiDuombaze() throws SQLException
    {
        String dbNuoroda = "jdbc:mysql:// localhost:3306/";
        String dbVardas = "parduotuves_tinklalapis_db";
        String dbUsername = "root";
        String dbPassword = "";

        return DriverManager.getConnection(dbNuoroda + dbVardas, dbUsername, dbPassword);
    }
}
