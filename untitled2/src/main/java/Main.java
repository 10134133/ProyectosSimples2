import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        System.out.println("APLICACION 2");
        Javalin app = Javalin.create().start(7001);
        app.get("/", ctx -> ctx.result("----------APLICATION 2-------------"));
    }
}