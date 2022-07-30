import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DomainModeling3 {
    public static record Movie (String title, String url, String imDbRating, String year){}
    public static void main(String[] args) throws Exception {
        // Objetivo: encapsular os atributos de cada filme (usando a classe movie)

        String apiKey = "9a7c1ca9-29b4-4eb3-8306-1adb9d159060";
        String url = "https://mocki.io/v1/" + apiKey;

        HttpClient client = HttpClient.newHttpClient(); // instanciando o cliente http
        HttpRequest.Builder builder = HttpRequest.newBuilder() // criando requisição e definindo parâmetros
                .GET().timeout(Duration.ofSeconds(10)) // criando timeout de resposta de 10 seg
                .uri(URI.create(url)); // criando URI e atribuindo solicitação à url destino
        var request = builder.build();
        HttpResponse<String> response = client  // construindo a requisição com resposta no tipo string
                .send(request, HttpResponse.BodyHandlers.ofString());

        // var statusCode = response.statusCode();
        // var headers = response.headers();
        String body = response.body();
        List<Movie> movies = parse(body);
        
        System.out.println("Elementos no moviesArray: " + movies.size()); // imprime o número de elementos na lista de array
        for(int i = 0; i < movies.size(); i++){
            System.out.println(movies.get(i)); // imprime o/os índice/s com o/os filme/s e seus atributos
        }
        /*int i = 0;
        while(i < movies.size()){
            System.out.println(movies.get(i)); 
            i++;
        }*/
    }
    private static List<Movie> parse(String body) {
        String[] moviesArray = parseJsonMovies(body);

        List<String> titles = parseTitles(moviesArray);
        List<String> urlImages = parseUrlImages(moviesArray);
        List<String> ratings = parseRatings(moviesArray);
        List<String> years = parseYears(moviesArray);

        List<Movie> movies = new ArrayList<>(titles.size());

        for (int i =0; i < titles.size(); i++) {
            movies.add(new Movie(titles.get(i), urlImages.get(i) , ratings.get(i), years.get(i)));
        }
        return movies;
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
    private static List<String> parseAttribute(String[] jsonMovies, int pos) {
        return Stream.of(jsonMovies)
                .map(e -> e.split("\",\"")[pos])
                .map(e -> e.split(":\"")[1])
                .map(e -> e.replaceAll("\"", ""))
                .collect(Collectors.toList());
    }
    private static List<String> parseTitles(String[] moviesArray) {
        return parseAttribute(moviesArray, 2);
    }
    private static List<String> parseUrlImages(String[] moviesArray) {
        return parseAttribute(moviesArray, 5);
    }
    private static List<String> parseRatings(String[] moviesArray) {
        return parseAttribute(moviesArray, 7);
    }
    private static List<String> parseYears(String[] moviesArray) {
        return parseAttribute(moviesArray, 4);
    }
}
