import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Api1 {
    public static void main(String[] args) throws Exception{
        /* Objetivo: consumir a API do IMDB (api alternativa) e imprimir os
        resultados de uma busca dos filmes. */

        String url = "https://mocki.io/v1/9a7c1ca9-29b4-4eb3-8306-1adb9d159060";

        HttpClient client = HttpClient.newHttpClient(); // instanciando o cliente http
        HttpRequest.Builder builder = HttpRequest.newBuilder() // criando requisição e definindo parâmetros
                .GET().timeout(Duration.ofSeconds(10)) // criando timeout de resposta de 10 seg
                .uri(URI.create(url)); // criando URI e atribuindo solicitação à url destino
        var request = builder.build();
        HttpResponse<String> response = client  // construindo a requisição com resposta no tipo string
                .send(request,HttpResponse.BodyHandlers.ofString());

        var statusCode = response.statusCode();
        var headers = response.headers();
        var body = response.body();

        // System.out.println("- O status code é:\n " + statusCode);
        // System.out.println("- Headers da api:\n " + headers);
        System.out.println("- Resultado api:\n" + body);
    }
}