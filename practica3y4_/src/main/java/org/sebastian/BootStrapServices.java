package org.sebastian;

import org.h2.tools.Server;
import java.sql.SQLException;


public class BootStrapServices {
    private static Server server;

    public static void startDB() {
        try {
            server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers", "-ifNotExists").start();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void stopDB() throws SQLException {
        if(server != null){
            server.stop();
        }
    }

}
