import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParseJson2 {
    public static void main(String[] args) throws Exception{
        // Objetivo: parsear o resultado do JSON, separando cada filme

        String apiKey = "9a7c1ca9-29b4-4eb3-8306-1adb9d159060";
        String url = "https://mocki.io/v1/" + apiKey;

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

        if(statusCode == 200){
            System.out.println("Requisição bem sucedida. Status code: $" );
        } else{
            System.out.println("Falha na requisição. Tente novamente");
        }

        // System.out.println("- O status code é:\n " + statusCode);
        // System.out.println("\n- Headers da api:\n " + headers);
        System.out.println("\n- Resultado api:\n" + body);

        // Filtrar os dados (filmes) com parsing do JSON,
        /* atributos a serem extraídos: título do filme (title)
        e URL da imagem (imagem) */

        //String moviesArray = body.substring(10,92345);
        // filtrando tudo o que está dentro dos "[ ]"
        // System.out.println("\n\n" + moviesArray);
        // String [] atributos = moviesArray.split("");
        // System.out.println("\n\n" + Arrays.toString(atributos));

        String[] moviesArray = parseJsonMovies(body);

        List<String> titles = parseTitles(moviesArray);
        System.out.println(titles);

        List<String> year = parseYear(moviesArray);
        year.forEach(System.out::println);

        List<String> urlImages = parseUrlImages(moviesArray);
        urlImages.forEach(System.out::println);

        List<String> rating = parseRating(moviesArray);
        rating.forEach(System.out::println);
    }

    private static String[] parseJsonMovies(String body) {
        Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(body);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("no match in " + body);
        }
        String[] moviesArray = matcher.group(1).split("\\},\\{");
        moviesArray[0] = moviesArray[0].substring(1);
        int last = moviesArray.length - 1;
        String lastString = moviesArray[last];
        moviesArray[last] = lastString.substring(0, lastString.length() - 1);
        return moviesArray;
    }
    private static List<String> parseAttribute(String[] moviesArray, int pos) {
        return Stream.of(moviesArray)
                .map(e -> e.split("\",\"")[pos])
                .map(e -> e.split(":\"")[1])
                .map(e -> e.replaceAll("\"", ""))
                .collect(Collectors.toList());
    }
    private static List<String> parseTitles(String[] moviesArray) {
        return parseAttribute(moviesArray, 2);
    }
    private static List<String> parseYear(String[] moviesArray){
        return parseAttribute(moviesArray, 4);
    }
    private static List<String> parseUrlImages(String[] moviesArray) {
        return parseAttribute(moviesArray, 5);
    }
    public static List<String> parseRating(String[] moviesArray){
        return parseAttribute(moviesArray, 7);
    }
}