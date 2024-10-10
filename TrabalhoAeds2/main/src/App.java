import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

enum TipoDeMedalha {
    OURO,PRATA,BRONZE
}

class Medalhista {
    private static final int MAX_MEDALHAS = 8;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String country;
    private Medalha[] medals;
    private int medalCount;

public Medalhista(String nome, String genero, LocalDate nascimento, String pais) {
        name = nome;
        gender = genero;
        birthDate = nascimento;
        country = pais;
        medalCount = 0;
        medals = new Medalha[MAX_MEDALHAS];
    }

public int incluirMedalha(Medalha medalha) {
    if (medalCount >= MAX_MEDALHAS) {
            return medalCount;
        }

        Medalha[] novMedalhas = new Medalha[MAX_MEDALHAS];
        int lastMedalCount = medalCount;
        for (int i = 0; i < lastMedalCount; i++) {
            novMedalhas[i] = medals[i];
        }
        medalCount++;
        novMedalhas[lastMedalCount] = medalha;
        medals = novMedalhas;
        return medalCount;
    }

    public int totalMedalhas() {
        return medalCount;
    }

    public String relatorioDeMedalhas(TipoDeMedalha tipo) {
        StringBuilder strBuilder = new StringBuilder("");
        for (Medalha medalha : medals) {
            if (medalha == null)
                continue;
            if (medalha.getTipo().equals(tipo)) {
                strBuilder.append(medalha.toString());
                strBuilder.append("\n");
            }
        }
        if (strBuilder.isEmpty()) {
            return "Nao possui medalha de " + tipo.toString() + "\n";
        }
        return strBuilder.toString();
    }

    public String getPais() {
        return country;
    }

    public LocalDate getNascimento() {
        return LocalDate.from(birthDate);
    }

    public String toString() {
        String dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(birthDate); // DD/MM/AAAA
        return name + ", " + gender + ". Nascimento: " + dataFormatada + ". Pais: " + country;
    }
}

class Medalha {
    private TipoDeMedalha metalType;
    private LocalDate medalDate;
    private String discipline;
    private String event;

    public Medalha(TipoDeMedalha tipo, LocalDate data, String disciplina, String evento) {
        metalType = tipo;
        medalDate = data;
        discipline = disciplina;
        event = evento;
    }

    public TipoDeMedalha getTipo() {
        return metalType;
    }

    public String toString() {
        String dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(medalDate); // DD/MM/AAAA
        return metalType.toString() + " - " + discipline + " - " + event + " - " + dataFormatada;
    }
}

public class App {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> content = FileReader.read("./tmp/medallists.csv");
        content.remove(0);
        Map<String, Medalhista> dataMap = getData(content);
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada.equals("FIM"))
                break;
            String[] entradaFormatada = entrada.split(",");
            String nomeAtleta = entradaFormatada[0];
            TipoDeMedalha medalha = TipoDeMedalha.valueOf(entradaFormatada[1]);

            Medalhista medalhista = dataMap.get(nomeAtleta);
            System.out.println(medalhista.toString());
            System.out.println(medalhista.relatorioDeMedalhas(medalha));
        }
        scanner.close();
    }

    private static Map<String, Medalhista> getData(List<String> rawContent) {
        Map<String, Medalhista> map = new HashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String line : rawContent) {
            // 0 name
            // 1 medal_type
            // 2 medal_date
            // 3 gender
            // 4 birth_date
            // 5 country
            // 6 discipline
            // 7 event
            String[] lineData = line.split(",");
            String name = lineData[0];
            TipoDeMedalha medalType = TipoDeMedalha.valueOf(lineData[1]);
            LocalDate medalDate = LocalDate.parse(lineData[2], dateFormatter);
            String gender = lineData[3];
            LocalDate birthDate = LocalDate.parse(lineData[4], dateFormatter);
            String country = lineData[5];
            String discipline = lineData[6];
            String event = lineData[7];
            Medalhista medalhista;
            if (map.containsKey(name)) {
                medalhista = map.get(name);
            } else {
                medalhista = new Medalhista(name, gender, birthDate, country);
                map.put(name, medalhista);
            }
            Medalha medalha = new Medalha(medalType, medalDate, discipline, event);
            medalhista.incluirMedalha(medalha);
        }
        return map;
    }
}

class FileReader {
    public static List<String> read(String pathname) {
        List<String> content = new ArrayList<>();
        File file = new File(pathname);
        try {
            Scanner teclado = new Scanner(file);
            while (teclado.hasNextLine()) {
                content.add(teclado.nextLine());
            }
            teclado.close();
        } catch (FileNotFoundException e) {
            System.out.println("Não foi possível ler o arquivo");
            e.printStackTrace();
        }

        return content;
    }
}