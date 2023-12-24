package legacystore.store.legacyloader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends JavaPlugin {

    private String mysqlUrl = "jdbc:mysql://104.troqueioip.47.aqtbm/legacyst_plugin?characterEncoding=UTF-8";
    private String mysqlUser = "legacyst_kadminadminadmin";
    private String mysqlPass = "minhasenhamuhaaeutroqueinocodigo";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§b _      _____ _____   ___  _______   _______ _____ ___________ _____ ");
        Bukkit.getConsoleSender().sendMessage("§b| |    |  ___|  __ \\ / _ \\/  __ \\ \\ / /  ___|_   _|  _  | ___ \\  ___|");
        Bukkit.getConsoleSender().sendMessage("§b| |    | |__ | |  \\/ /_\\ \\ /  \\/\\ V /\\ `--.  | | | | | | |_/ / |__  ");
        Bukkit.getConsoleSender().sendMessage("§b| |    |  __|| | __ |  _  | |     \\ /  `--. \\ | | | | | |    /|  __| ");
        Bukkit.getConsoleSender().sendMessage("§b| |____| |___| |_/ \\| | | | \\__/\\ | | /\\__/ / | | \\ \\/ / |\\ \\| |___ ");
        Bukkit.getConsoleSender().sendMessage("§b\\_____\\____/ \\____/\\_| |_|\\____/ \\_/ \\____/  \\_/  \\___/\\_| \\_\\____/ ");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        reloadConfig();
        if (testMySQLConnection()) {
            Bukkit.getConsoleSender().sendMessage("§b[LegacyStore] Conexão com o MySQL bem-sucedida!");
            FileConfiguration playerConfig = getConfig();
            String chavedousuario = playerConfig.getString("chavedousuario");
            if (chavedousuario != null && !chavedousuario.isEmpty()) {
                if (checkPlayerInDatabase(chavedousuario)) {
                    List<String> pluginsAdquiridos = getPluginsAdquiridosDoJogador(chavedousuario);
                    Bukkit.getConsoleSender().sendMessage("§b[LegacyStore] A chave '" + chavedousuario + "' foi encontrada.");
                    if (!pluginsAdquiridos.isEmpty()) {
                        Bukkit.getConsoleSender().sendMessage("§b[LegacyStore] Plugins adquiridos pelo jogador: §f" + String.join(", ", pluginsAdquiridos));
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] O jogador não adquiriu nenhum plugin.");
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] A chave '" + chavedousuario + "' não foi encontrada.");
                }
            } else {
                Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] Chave não identificada na config.yml");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] Erro na conexão com o MySQL. Verifique as configurações.");
        }

    }

    private boolean testMySQLConnection() {
        try {
            Connection connection = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPass);
            connection.close();
            return true;
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] Erro na conexão com o MySQL: " + e.getMessage());
            return false;
        }
    }

    private boolean checkPlayerInDatabase(String chavedousuario) {
        try {
            Connection connection = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPass);
            String query = "SELECT * FROM plugins WHERE chave = ? AND chave IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, chavedousuario);
            ResultSet resultSet = statement.executeQuery();

            boolean playerExists = resultSet.next();

            resultSet.close();
            statement.close();
            connection.close();

            return playerExists;
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] Erro na verificação do servidor no MySQL: " + e.getMessage());
            return false;
        }
    }

    private List<String> getPluginsAdquiridosDoJogador(String chavedousuario) {
        try {
            Connection connection = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPass);
            String query = "SELECT pluginsadquiridos FROM plugins WHERE chave = ? AND chave IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, chavedousuario);
            ResultSet resultSet = statement.executeQuery();

            List<String> pluginsAdquiridos = new ArrayList<>();
            if (resultSet.next()) {
                String plugins = resultSet.getString("pluginsadquiridos");
                if (plugins != null && !plugins.isEmpty()) {
                    // Separar os nomes dos plugins usando uma vírgula (ou outro separador)
                    pluginsAdquiridos.addAll(Arrays.asList(plugins.split(",")));
                }
            }

            resultSet.close();
            statement.close();
            connection.close();

            return pluginsAdquiridos;
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c[LegacyStore] Erro ao obter a lista de plugins adquiridos do jogador: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}

