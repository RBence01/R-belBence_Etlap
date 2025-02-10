package hu.petrik.etlap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtelService {
    private final Connection connection;

    public EtelService() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://root@localhost:3306/etlapdb");
    }

    public List<Etel> getAll() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM etlap");
        List<Etel> list = new ArrayList<>();
        while (result.next()) {
            int id = result.getInt("id");
            String nev = result.getString("nev");
            String leiras = result.getString("leiras");
            int ar = result.getInt("ar");
            String kategoria = result.getString("kategoria");
            list.add(new Etel(id, nev, leiras, ar, kategoria));
        }
        return list;
    }

    public boolean create(Etel etel) throws SQLException {
        PreparedStatement s = connection.prepareStatement("INSERT INTO etlap(nev, leiras, ar, kategoria) VALUES(?, ?, ?, ?)");
        s.setString(1, etel.getNev());
        s.setString(2, etel.getLeiras());
        s.setInt(3, etel.getAr());
        s.setString(4, etel.getKategoria());
        return s.executeUpdate() == 1;
    }

    public boolean change(Etel etel) throws SQLException {
        PreparedStatement s = connection.prepareStatement("UPDATE etlap SET nev = ?, leiras = ?, ar = ?, kategoria = ? WHERE id = ?");
        s.setString(1, etel.getNev());
        s.setString(2, etel.getLeiras());
        s.setInt(3, etel.getAr());
        s.setString(4, etel.getKategoria());
        s.setInt(5, etel.getId());
        return s.executeUpdate() == 1;
    }

    public boolean delete(int id) throws SQLException {
        PreparedStatement s = connection.prepareStatement("DELETE FROM etlap WHERE id = ?");
        s.setInt(1, id);
        return s.executeUpdate() == 1;
    }
}
