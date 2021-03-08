import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(
        name = "prekesRedagavimoServlet",
        urlPatterns = "/PrekesRedagavimoServlet"
)
public class PrekesRedagavimoServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        PrintWriter out = resp.getWriter();

        // Nusiskautau iš html formos reikšmes
        String ivestasPav = req.getParameter("pav");
        String ivestaKainaStr = req.getParameter("kaina");
        String ivestasKiekisStr = req.getParameter("kiekis");
        String ivestasAprasas = req.getParameter("aprasas");
        String ivestasIdStr = req.getParameter("id");

        // Skaitiniu reiksmiu kintamieji
        double ivestaKaina;
        int ivestasKiekis;

        int ivestasId;

        // Jeigu ID nera tuscias - tada konvertuoti ID i skaiciu ir pradeti prisijungima prie duomenu bazes
        // Kiti skaitiniai kintamieju bus konvertuojame is tekstiniu tolimesniame kode
        if (!ivestasIdStr.isEmpty())
        {
            ivestasId = Integer.parseInt(ivestasIdStr);

            try
            {
                // Sukuriame prisijungima/jungti i duombaze
                Connection jungtis = DuombazesSukurimas.sukurtiDuombaze();

                // Redaguojamos prekės gavimas iš duomenų bazės
                String sqlGautiPreke = "SELECT * FROM prekes WHERE id=?";

                // Sakinio paruosimas
                PreparedStatement prepSt = jungtis.prepareStatement(sqlGautiPreke);
                prepSt.setInt(1, ivestasId);

                // Sakinio ivykdymas ir grazinimas i ResultSet
                ResultSet rezultatai = prepSt.executeQuery();

                // Kol yra eiluciu gautu
                while (rezultatai.next())
                {
                    String orgPav = rezultatai.getString("pav");
                    double orgKaina = rezultatai.getDouble("kaina");
                    int orgKiekis = rezultatai.getInt("kiekis");
                    String orgAprasas = rezultatai.getString("aprasas");

                    // Tikriname, jeigu kazkuris laukelis būtų tuščias, tada priskiriame originalią reikšmę (iš duombazės)
                    // Taip pat kai kuriems laukeliams, jeigu jie ne tušti - juos konvertuoti į skaitines reikšmes
                    if (ivestasPav.isEmpty())
                    {
                        ivestasPav = orgPav;
                    }

                    if (ivestaKainaStr.isEmpty())
                    {
                        ivestaKaina = orgKaina;
                    }
                    else
                    {
                        ivestaKaina = Double.parseDouble(ivestaKainaStr);
                    }

                    if (ivestasKiekisStr.isEmpty())
                    {
                        ivestasKiekis = orgKiekis;
                    }
                    else
                    {
                        ivestasKiekis = Integer.parseInt(ivestasKiekisStr);
                    }

                    if (ivestasAprasas.isEmpty())
                    {
                        ivestasAprasas = orgAprasas;
                    }

                    // Testinis išvedimas patikrinti ar priskiriamos tinkamai reikšmės prie tų laukelių, kurie buvo palikti tušti
                    out.println(ivestasPav);
                    out.println(ivestaKaina);
                    out.println(ivestasKiekis);
                    out.println(ivestasAprasas);


                    //-------------------- PREKĖS DUOMENŲ ATNAUJINIMAS---------------------
                    String sqlAtnaujintiPreke = "UPDATE prekes SET pav=?, kaina=?, kiekis=?, aprasas=? WHERE id=?";
                    prepSt = jungtis.prepareStatement(sqlAtnaujintiPreke);
                    prepSt.setString(1, ivestasPav);
                    prepSt.setDouble(2, ivestaKaina);
                    prepSt.setInt(3, ivestasKiekis);
                    prepSt.setString(4, ivestasAprasas);
                    prepSt.setInt(5, ivestasId);

                    prepSt.executeUpdate();

                    out.println("Idejome i duombaze");
                }

            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            out.println("BUTINA IVESTI PREKES ID... <a href='redaguoti_preke.html'>Grįžti atgal</a>");
        }


        // Testiniai išvedimai pažiūrėti ar gauname iš HTML formos
      /*  out.println(ivestasPav);
        out.println(ivestaKaina);
        out.println(ivestasKiekis);
        out.println(ivestasAprasas);
        out.println(ivestasId);*/
    }
}
