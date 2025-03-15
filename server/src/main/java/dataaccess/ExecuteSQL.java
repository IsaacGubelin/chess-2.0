package dataaccess;

import chess.ChessGame;
import exception.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class ExecuteSQL {


    /**
     * This is a helper method for running a SQL statement and automatically inserting parameters.
     * @param statement SQL statement to execute
     * @param params parameters to fill statement with
     * @return success or fail
     * @throws SQLException if database error occurs
     */
    public static int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not execute update!");
        }
    }

    /**
     * This boolean function performs a query for a key to verify its existence in a specified table.
     */


}
