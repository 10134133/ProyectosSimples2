package org.sebastian;

import org.sebastian.Entidades.*;
import org.sebastian.Servicios.*;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinVelocity;
import org.jasypt.util.text.AES256TextEncryptor;
import java.io.IOException;
import java.util.*;

/* Sebastián Sanchez 20180032 | 10134133
Para acceder a administrador y a la base de datos de h2 es user: admin   passw: ADMIN (asi en mayuscula)*/
public class Main {

    private static String modoConexion = "";

    public static void main(String[] args){


        BootStrapServices.startDB();
        Javalin app = Javalin.create().start(7000);
        JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm");
        crearUsuarios();
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra();
            }
            ctx.sessionAttribute("carrito",carrito);
        });


        app.post("/registrar", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = ctx.formParam("precio",Integer.class).get();
            String desc = ctx.formParam("desc");
            List<Foto> fotos = new ArrayList<Foto>();
            ctx.uploadedFiles("img").forEach(uploadedFile -> {
                try {
                    byte[] bytes = uploadedFile.getContent().readAllBytes();
                    String encodedString = Base64.getEncoder().encodeToString(bytes);
                    Foto foto = new Foto(uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                    FotoS.getInstancia().create(foto);
                    fotos.add(foto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Producto temp = new Producto(nombre,precio,desc);
            temp.setFotos(fotos);
            ProductoS.getInstance().create(temp);
            ctx.redirect("/productos");
        });

        app.get("/ver/:id",ctx -> {
            int id = ctx.pathParam("id", Integer.class).get();
            Producto temp = ProductoS.getInstance().find(id);
            List<Comentario> comments = ComentarioS.getInstancia().findComments(id);
            Map<String, Object> modelo = new HashMap<>();
            String user = ctx.cookie("usuario");
            modelo.put("temp",temp);
            modelo.put("comments",comments);
            modelo.put("user",user);
            ctx.render("/publico/verprod.vm",modelo);
        });

        app.get("/logout", ctx -> {
            if(ctx.cookie("usuario")!= null && ctx.cookie("mist")!= null){
                ctx.removeCookie("usuario");
                ctx.removeCookie("mist");
            }
            ctx.redirect("/");
        });

        app.post("/addComment/:id", ctx->{
           String comment = ctx.formParam("coment");
           int id = ctx.pathParam("id", Integer.class).get();
           Comentario temp = new Comentario(comment,id);
           ComentarioS.getInstancia().create(temp);
           ctx.redirect("/ver/"+id);
        });

        app.get("/delComent/:id/:coment", ctx ->{
            int id = ctx.pathParam("id", Integer.class).get();
            int comment = ctx.pathParam("coment",Integer.class).get();
            System.out.println("El id del comentario es: "+comment);
            ComentarioS.getInstancia().deleteComent(comment);
            ctx.redirect("/ver/"+id);
        });


        /* Agregar al carrito*/
        app.get("/", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ProductoS.getInstance().findProd(0, 10);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            List<String> paginas = getPaginas();
            modelo.put("paginas",paginas);
            ctx.render("/publico/catalogo.vm", modelo);
        });

        app.get("/comprar/:id", ctx -> {
            int pos = ctx.pathParam("id", Integer.class).get() * 10;
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ProductoS.getInstance().findProd(pos, pos+10);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            List<String> paginas = getPaginas();
            modelo.put("paginas",paginas);
            ctx.render("/publico/catalogo.vm", modelo);
        });


        app.post("/comprar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            Producto temp = carrito.getProductosPorID(ctx.formParam("id",Integer.class).get());
            if(temp == null){
                temp = ProductoS.getInstance().find(ctx.formParam("id", Integer.class).get());
                temp.setCantidad(ctx.formParam("cantidad",Integer.class).get() );
                carrito.addProducto(temp);
                ctx.sessionAttribute("carrito",carrito);
                ctx.redirect("/comprar");
            }else{
                int pos = carrito.getPos(ctx.formParam("id",Integer.class).get());
                temp.setCantidad(ctx.formParam("cantidad",Integer.class).get() + temp.getCantidad());
                carrito.cambiarProducto(temp,pos);
            }

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });

        /*Ventas realizadas*/
        app.get("/ventas", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/ventas");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!UsuarioS.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<VentasProductos> ventas = VentasS.getInstance().getVentas();
            for (VentasProductos venta: ventas) {
                System.out.println(venta.getId());
            }
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("ventas",ventas);
            modelo.put("cantidad",carrito.getProductos().size());

            ctx.render("/publico/ventasrealizadas.vm",modelo);
        });


        app.get("/productos", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/productos");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!UsuarioS.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            List<Producto> productos = ProductoS.getInstance().findProd(0,0);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/administrarproductos.vm",modelo);
        });


        app.get("/registrar", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("accion","/registrar");
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/productoCE.vm",modelo);
        });

        

        app.get("/remover/:id", ctx -> {
            ProductoS.getInstance().deleteProducto(ctx.pathParam("id",Integer.class).get());
            ctx.redirect("/productos");
        });

        app.get("/editar/:id", ctx -> {
            Producto temp = ProductoS.getInstance().find(ctx.pathParam("id", Integer.class).get());
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto",temp);
            modelo.put("accion","/editar/"+ctx.pathParam("id", Integer.class).get());

            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/productoCE.vm",modelo);
        });


        app.post("/editar/:id", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = ctx.formParam("precio",Integer.class).get();
            String desc = ctx.formParam("desc");
            Producto temp = new Producto(nombre,precio,desc);
            temp.setId(ctx.pathParam("id",Integer.class).get());
            ProductoS.getInstance().edit(temp);

            ctx.redirect("/productos");
        });

        app.get("/autenti/:direc", ctx -> {
            String direc = ctx.pathParam("direc");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("direc",direc);
            ctx.render("/publico/autentificador.vm",modelo);
        });


        app.post("/autenti/:direc",ctx -> {
            String usuario = ctx.formParam("usuario");
            String pass = ctx.formParam("password");
            String temp = ctx.pathParam("direc");
            String recordar = ctx.formParam("recordar");

            if(usuario == null || pass == null){
                ctx.redirect("/autenti/"+temp);
            }
            Usuario user = new Usuario(usuario,pass);
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword("myEncryptionPassword");
            pass = textEncryptor.encrypt(pass);
            if(recordar != null){
                ctx.cookie("usuario", usuario,(3600*24*7));//Una semana en segundos
                ctx.cookie("mist", pass,(3600*24*7));
            }
            //Encriptar cookie
            ctx.cookie("usuario", usuario);
            ctx.cookie("mist", pass);
            ctx.redirect("/"+temp);

        });

        app.get("/carrito", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra();
            }
            ctx.sessionAttribute("carrito",carrito);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",carrito.getProductos());
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/carrito.vm",modelo);
        });

        app.get("/eliminar/:id", ctx -> {
            int id = ctx.pathParam("id", Integer.class).get();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.eliminarProductoPorId(id);

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/carrito");
        });

        app.post("/procesar",ctx -> {
           CarroCompra carrito = ctx.sessionAttribute("carrito");
           if(carrito.getProductos().size() < 1){
               ctx.redirect("/carrito");
           }
           String nombre = ctx.formParam("nombre");
           VentasProductos venta = new VentasProductos(nombre);
           List<ProdComprado> list = ProdCompradoS.getInstance().convertProd(carrito.productos,venta.getId());
           venta.setListaProductos(list);
           VentasS.getInstance().create(venta);
           carrito.borrarProductos();
           ctx.sessionAttribute("carrito",carrito);
           ctx.redirect("/comprar");
        });

        /*Limpiar carrito*/
        app.get("/limpiar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.borrarProductos();

            ctx.redirect("/comprar");
        });
        
    }
    private static void crearUsuarios(){
        String nombre;
        int precio;
        String desc;
        List<Foto> fotos = new ArrayList<Foto>();
        for(int i = 0 ; i < 19; i++){
            nombre = "producto "+ i;
            precio = 10 * i;
            desc = "Este es el producto "+i;
            Producto temp = new Producto(nombre,precio,desc);
            temp.setFotos(fotos);
            ProductoS.getInstance().create(temp);
        }
            
    }
    
    private static List<String> getPaginas() {
        int pag = ProductoS.getInstance().pag();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i <= pag; i++){
            String aux = "<a class=\"page-link\" href=\"/comprar/"+i+"\">"+(i+1)+"</a>";
            list.add(aux);
        }
        return list;
    }

    public static String getConnection(){
        return modoConexion;
    }

}
