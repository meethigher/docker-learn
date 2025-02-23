package top.meethigher;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static String project = null;

    private static Handler<RoutingContext> routingContextHandler() {
        return ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("Content-Type", "text/plain;charset=utf-8");
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement()) {

                log.info("成功连接到 PostgreSQL 数据库！");

                // 创建测试表
                stmt.execute("CREATE TABLE IF NOT EXISTS test (id SERIAL PRIMARY KEY, name VARCHAR(50))");
                stmt.execute("INSERT INTO test (name) VALUES ('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "')");

                // 查询数据
                ResultSet rs = stmt.executeQuery("SELECT * FROM test order by id desc");
                while (rs.next()) {
                    String x = "ID: " + rs.getInt("id") + ", Name: " + rs.getString("name");
                    response.end(x);
                    break;
                }

            } catch (Exception e) {
                log.error("error", e);
                response.end("error," + e.getMessage());
            }
        };
    }

    public static void main(String[] args) throws IOException {

        Properties properties = loadProperties("config.properties");
        project = properties.getProperty("project");

        Vertx vertx = Vertx.vertx();

        Router router = Router.router(vertx);
        router.route().handler(routingContextHandler());
        vertx.createHttpServer().requestHandler(router).listen(8080)
                .onSuccess(t -> log.info("start success on port {}", t.actualPort()))
                .onFailure(e -> log.error("start failed", e));
    }


    /**
     * 加载 Properties 配置文件，优先从应用目录加载，若不存在则从 classpath 加载
     * 确保使用 UTF-8 读取，避免中文乱码
     *
     * @param fileName 配置文件名（如 "config.properties"）
     * @return Properties 对象，包含加载的配置信息
     * @throws IOException 文件未找到或读取失败时抛出异常
     */
    public static Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();

        // 1. 优先加载应用目录下的配置文件
        Path appConfigPath = Paths.get(fileName); // 例如：config/config.properties
        if (Files.exists(appConfigPath)) {
            log.info("从应用目录加载：" + appConfigPath.toAbsolutePath());
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(appConfigPath), "UTF-8")) {
                properties.load(reader);
                return properties;
            }
        }

        // 2. 若不存在，则从 classpath 加载
        InputStream classpathConfig = App.class.getClassLoader().getResourceAsStream(fileName);
        if (classpathConfig != null) {
            log.info("从 classpath 加载：" + fileName);
            try (InputStreamReader reader = new InputStreamReader(classpathConfig, "UTF-8")) {
                properties.load(reader);
                return properties;
            }
        }

        // 3. 若仍不存在，则抛出异常
        throw new FileNotFoundException("未找到配置文件: " + fileName);
    }
}