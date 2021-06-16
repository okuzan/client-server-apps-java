package PW5;

import PW4.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyServer {
    private static final byte[] apiKeySecretBytes = "dklcxvkc4irjficjsriu3wiodjklcldkjfih3jlKX;.vc,mjk".getBytes(StandardCharsets.UTF_8);
    private final static SignatureAlgorithm algo = SignatureAlgorithm.HS256;
    private static final SecretKeySpec key = new SecretKeySpec(apiKeySecretBytes, algo.getJcaName());
    private final static int PORT = 3000;
    private static SQLMain sql;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        sql = new SQLMain();
        sql.initialization("Warehouse");
        server.start();
        run(server);
    }

    private static void run(HttpServer server) {
        ObjectMapper objectMapper = new ObjectMapper();
        server.createContext("/", exchange -> {
            if (exchange.getRequestMethod().equals("GET")) {
                byte[] response = "{\"status\": \"ok\"}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });

        server.createContext("/login", exchange -> {
            if (exchange.getRequestMethod().equals("POST")) {
                User user = objectMapper.readValue(exchange.getRequestBody(), User.class);
                User userFromObj = sql.getUserByLogin(user.getLogin());

                if (userFromObj != null) {
                    if (userFromObj.getPassword().equals(user.getPassword())) {
                        String jwt = createJWT(userFromObj.getLogin());
                        getUserLoginFromJwt(jwt);
                        exchange.getResponseHeaders().set("Authorization", jwt);
                        exchange.sendResponseHeaders(200, 0);

                    } else {
                        exchange.sendResponseHeaders(401, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(401, 0);
                }
                System.out.println(user);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });


        HttpContext context = server.createContext("/api/good", exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    System.out.println(exchange.getPrincipal());
                    String path = exchange.getRequestURI().getRawPath();
                    String idStr = path.split("/")[3];
                    System.out.println("ID " + idStr);
                    try {
                        int id = Integer.parseInt(idStr);
                        Product product = sql.readProduct(id);
                        if (product != null) {
                            byte[] response = objectMapper.writeValueAsBytes(product);
                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, response.length);
                            exchange.getResponseBody().write(response);
                        } else {
                            System.out.println("ID DOESN'T EXIST");
                            exchange.sendResponseHeaders(404, 0);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                }
                case "DELETE": {
                    System.out.println(exchange.getPrincipal());
                    String path = exchange.getRequestURI().getRawPath();
                    String idStr = path.split("/")[3];
                    System.out.println("ID " + idStr);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (sql.deleteProduct(id)) {
                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(204, 0);
                        } else {
                            System.out.println("ID DOESN'T EXIST");
                            exchange.sendResponseHeaders(404, 0);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(404, 0);
                    }

                    break;
                }
                case "PUT": {
                    System.out.println(exchange.getPrincipal());
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    try {
                        Product product = objectMapper.readValue(body, Product.class);
                        if (sql.checkData(product)) {
                            int id = sql.insertProductData(product).getId();
                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(201, 0);
                            byte[] response = ("{\"id\": \" " + id + "\"}").getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseBody().write(response);

                        } else {
                            System.out.println("INVALID INFO");
                            exchange.sendResponseHeaders(409, 0);
                        }
                    } catch (Exception ignored) {
                        System.out.println("INVALID INFO");
                        exchange.sendResponseHeaders(409, 0);
                    }

                    break;
                }
                case "POST":
                    System.out.println(exchange.getPrincipal());
                    String path = exchange.getRequestURI().getRawPath();
                    String idStr = path.split("/")[3];

                    System.out.println("ID " + idStr);
                    int id = Integer.parseInt(idStr);
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    try {
                        Product product = objectMapper.readValue(body, Product.class);
                        if (sql.checkData(product)) {
                            if (sql.updateProduct(id, product.getName(), product.getAmount(), product.getPrice())) {
                                exchange.getResponseHeaders().set("Content-Type", "application/json");
                                exchange.sendResponseHeaders(204, 0);
                            } else {
                                System.out.println("ID DOESN'T EXIST");
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            System.out.println("INVALID INFO");
                            exchange.sendResponseHeaders(409, 0);
                        }
                    } catch (Exception e) {
                        System.out.println("INVALID INFO");
                        e.printStackTrace();
                        exchange.sendResponseHeaders(409, 0);
                    }
                    break;
            }

            exchange.close();
        });
        context.setAuthenticator(new MyAuthenticator());

    }

    static class MyAuthenticator extends Authenticator {

        @Override
        public Result authenticate(HttpExchange exch) {
            String jwt = exch.getRequestHeaders().getFirst("Authorization");
            if (jwt != null) {
                try {
                    String login = getUserLoginFromJwt(jwt);
                    if (sql.getUserByLogin(login) != null)
                        return new Authenticator.Success(new HttpPrincipal(login, "admin"));
                } catch (Exception e) {
                    System.out.println("INVALID JWT - 403");
                }
            }
            return new Authenticator.Failure(403);
        }
    }

    private static String getUserLoginFromJwt(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Expiration: " + claims.getExpiration());
        System.out.println("UserName: " + claims.get("Username", String.class));
        return claims.getSubject();
    }

    private static String createJWT(String login) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TimeUnit.HOURS.toMillis(10)))
                .setSubject(login)
                .signWith(key, algo)
                .claim("Username", "John")
                .compact();
    }
}