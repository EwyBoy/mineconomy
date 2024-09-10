import com.ewyboy.mineconomy.Constants;
import com.ewyboy.mineconomy.DatabaseManager;
import net.minecraft.core.registries.BuiltInRegistries;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.ewyboy.mineconomy.Mineconomy.getRandomNumber;

public class MineconomyForgeTest {

    @Test
    public void testRunQuery() {
        DatabaseManager.init();

        DatabaseManager.openConnection();

        DatabaseManager.runQuery("""
                CREATE TABLE IF NOT EXISTS items (
                 id integer PRIMARY KEY,
                 name text NOT NULL,
                 namespace text NOT NULL,
                 resource text NOT NULL,
                 price integer
                );"""
        );

        BuiltInRegistries.ITEM.forEach(
                item -> DatabaseManager.runQuery("INSERT INTO items (name, namespace, resource, price) VALUES ('"
                        + BuiltInRegistries.ITEM.getKey(item).getPath() + "', '"
                        + BuiltInRegistries.ITEM.getKey(item).getNamespace() + "', '"
                        + BuiltInRegistries.ITEM.getKey(item) + "', "
                        + getRandomNumber(1, 1000) + ");"
                ));

        DatabaseManager.runQuery("""
                CREATE TABLE IF NOT EXISTS players (
                 id integer PRIMARY KEY,
                 name text NOT NULL,
                 balance integer
                );"""
        );

        DatabaseManager.runQuery("INSERT INTO players (name, balance) VALUES ('Ewy', " + getRandomNumber(3500, 7500) + ");");
        DatabaseManager.runQuery("INSERT INTO players (name, balance) VALUES ('Bebo', " + getRandomNumber(3500, 7500) + ");");
        DatabaseManager.runQuery("INSERT INTO players (name, balance) VALUES ('Bysco', " + getRandomNumber(3500, 7500) + ");");
        DatabaseManager.runQuery("INSERT INTO players (name, balance) VALUES ('Tacooz', " + getRandomNumber(3500, 7500) + ");");

        DatabaseManager.runQuery("""
                CREATE TABLE IF NOT EXISTS transactions (
                 id integer PRIMARY KEY,
                 seller_id integer NOT NULL,
                 buyer_id integer NOT NULL,
                 item_id integer NOT NULL,
                 amount integer NOT NULL,
                 price integer NOT NULL
                );"""
        );

        for (int i = 0; i < 10; i++) {
            DatabaseManager.runQuery("INSERT INTO transactions (seller_id, buyer_id, item_id, amount, price) VALUES ("
                    + getRandomNumber(1, 4) + ", "
                    + getRandomNumber(1, 4) + ", "
                    + getRandomNumber(1, 10) + ", "
                    + getRandomNumber(1, 64) + ", "
                    + getRandomNumber(1, 1000) + ");"
            );
        }

        // Print out transactions with player names, item names, and block names
        var transactions = DatabaseManager.executeQuery("""
                SELECT players.name, items.name, transactions.amount, transactions.price, players.name
                FROM transactions
                JOIN players ON transactions.seller_id = players.id
                JOIN items ON transactions.item_id = items.id;
                """
        );

        if (transactions != null) {
            try {
                while (transactions.next()) {
                    // player name has bought item name for price from player name
                    Constants.LOG.info("{} has bought {} {} for {} from {}",
                            transactions.getString(1),
                            transactions.getString(2),
                            transactions.getInt(3),
                            transactions.getInt(4),
                            transactions.getString(5)
                    );
                }
            } catch (SQLException e) {
                Constants.LOG.error("Error while reading transactions: {}", e.getMessage(), e);
            }
        }

        assert transactions != null;

        DatabaseManager.closeConnection();
    }

}
