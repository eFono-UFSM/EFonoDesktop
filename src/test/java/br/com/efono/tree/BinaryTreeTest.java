package br.com.efono.tree;

import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import br.com.efono.util.Defaults;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 */
public class BinaryTreeTest {

    @Test
    public void testRound() {
        int min = 0, max = 83;
        float m = (min + max) / 2f;
        System.out.println("m: " + m);
        int middleUp = (int) Math.ceil(m);
        int middleDown = (int) Math.floor(m);

        assertEquals(42, middleUp);
        assertEquals(41, middleDown);
    }

    /**
     * Tests {@link BinaryTree#init(E[])}.
     */
    @Test
    public void testInit() {
        System.out.println("testInit");
        final BinaryTree<String> tree = new BinaryTree<>(Defaults.DEFAULT_WORDS_COMPARATOR);
//        int[] arrayIndex = new int[]{4, 2, 1, 0, 3, 7, 6, 5, 8, 9};
        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        tree.init(words.toArray(new String[0]));
        
        System.out.println("print tree now");
        BinaryTreePrinter.print(tree, System.out);

        System.out.println("test");
        assertEquals(Defaults.SORTED_WORDS[2], tree.getRoot().getValue());
        assertEquals(Defaults.SORTED_WORDS[2], tree.getRoot().getLeft().getValue());
        assertEquals(Defaults.SORTED_WORDS[1], tree.getRoot().getLeft().getLeft().getValue());
        assertEquals(Defaults.SORTED_WORDS[7], tree.getRoot().getRight().getValue());
        assertEquals(Defaults.SORTED_WORDS[8], tree.getRoot().getRight().getRight().getValue());

        if (1 > 0) {
            fail("Just testing");
        }

        System.out.println("testInit - complete tree");
        tree.clear();
        tree.init(Defaults.SORTED_WORDS);

        BinaryTreePrinter.print(tree, System.out);

        assertEquals(Defaults.SORTED_WORDS[41], tree.getRoot().getValue());
        assertEquals(Defaults.SORTED_WORDS[20], tree.getRoot().getLeft().getValue());
        assertEquals(Defaults.SORTED_WORDS[10], tree.getRoot().getLeft().getLeft().getValue());
        assertEquals(Defaults.SORTED_WORDS[62], tree.getRoot().getRight().getValue());
        assertEquals(Defaults.SORTED_WORDS[72], tree.getRoot().getRight().getRight().getValue());
    }

    @Test
    @Ignore
    public void testa() {
        // TODO: rever a arvore. O certo seria fazer um método que adicione os valores de forma balanceada na árvore
        int[] treeArray = new int[]{41, 20, 10, 5, 2, 1, 0, 3, 4, 7, 6, 8, 9, 15, 12, 11, 13, 14, 17, 16, 18, 19, 30, 25, 22, 21, 23, 24, 27, 26, 28, 29, 35, 32, 31, 33, 34, 38, 36, 37, 39, 40,
            62, 51, 46, 43, 41, 44, 45, 48, 47, 49, 50, 56, 53, 52, 54, 55, 59, 57, 58, 60, 61, 72, 67, 64, 63, 65, 66, 69, 68, 70, 71, 77, 74, 73, 75, 76, 80, 78, 79, 82, 81, 83};

        BinaryTree<Integer> tree = new BinaryTree<>((final Integer o1, final Integer o2) -> {
            return o1 - o2;
        });

        for (int i : treeArray) {
            tree.add(i);
        }
        BinaryTreePrinter.print(tree, System.out);

        fail("Failed just to see printed tree");
    }

    /**
     * @return A list with real cases and all correct.
     */
    private List<KnownCase> getCases() {
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase barriga = new KnownCase("Barriga", "[ba’χigə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM), new Phoneme("g", Phoneme.POSITION.OM)));
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase bebê = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));
        KnownCase beijo = new KnownCase("Beijo", "[’beʒo]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("ʒ", Phoneme.POSITION.OM)));
        KnownCase biblioteca = new KnownCase("Biblioteca", "[biblio’tɛkə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("bl", Phoneme.POSITION.OCME), new Phoneme("t", Phoneme.POSITION.OM), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase bicicleta = new KnownCase("Bicicleta", "[bisi’klɛtə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM), new Phoneme("kl", Phoneme.POSITION.OCME), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase bolsa = new KnownCase("Bolsa", "[’bowsə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM)));
        KnownCase brinco = new KnownCase("Brinco", "[’bɾĩnko]", true, Arrays.asList(new Phoneme("bɾ", Phoneme.POSITION.OCI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase bruxa = new KnownCase("Bruxa", "[’bɾuʃə]", true, Arrays.asList(new Phoneme("bɾ", Phoneme.POSITION.OCI), new Phoneme("ʃ", Phoneme.POSITION.OM)));
        KnownCase cabelo = new KnownCase("Cabelo", "[ka’belu]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase cachorro = new KnownCase("Cachorro", "[ka’ʃoχo]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʃ", Phoneme.POSITION.OM), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase caixa = new KnownCase("Caixa", "[’kaʃə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʃ", Phoneme.POSITION.OM)));
        KnownCase calça = new KnownCase("Calça", "[’kawsə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase caminhão = new KnownCase("Caminhão", "[kami’ɲəw]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM), new Phoneme("ɲ", Phoneme.POSITION.OM)));
        KnownCase casa = new KnownCase("Casa", "[‘kazə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("z", Phoneme.POSITION.OM)));
        KnownCase cavalo = new KnownCase("Cavalo", "[ka’valu]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase chapéu = new KnownCase("Chapéu", "[ʃa’pɛw]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM)));
        KnownCase chiclete = new KnownCase("Chiclete", "[ʃi’klƐte]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("kl", Phoneme.POSITION.OCME), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase chifre = new KnownCase("Chifre", "[’ʃifɾi]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("fɾ", Phoneme.POSITION.OCME)));
        KnownCase chinelo = new KnownCase("Chinelo", "[ʃi’nɛlu]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase cobra = new KnownCase("Cobra", "[’kɔbɾə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("bɾ", Phoneme.POSITION.OCME)));
        KnownCase coelho = new KnownCase("Coelho", "[ko’eʎo]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʎ", Phoneme.POSITION.OM)));
        KnownCase colher = new KnownCase("Colher", "[ko’ʎɛɾ]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʎ", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase cruz = new KnownCase("Cruz", "[’kɾus]", true, Arrays.asList(new Phoneme("kɾ", Phoneme.POSITION.OCI), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase dragão = new KnownCase("Dragão", "[dɾa’gəw]", true, Arrays.asList(new Phoneme("dɾ", Phoneme.POSITION.OCI), new Phoneme("g", Phoneme.POSITION.OM)));
        KnownCase escrever = new KnownCase("Escrever", "[eskɾe’ve]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("kɾ", Phoneme.POSITION.OCME), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase espelho = new KnownCase("Espelho", "[is’peʎo]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("p", Phoneme.POSITION.OM), new Phoneme("ʎ", Phoneme.POSITION.OM)));
        KnownCase estrela = new KnownCase("Estrela", "[is’tɾelə]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("tɾ", Phoneme.POSITION.OCME), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase faca = new KnownCase("Faca", "[’fakə]", true, Arrays.asList(new Phoneme("f", Phoneme.POSITION.OI), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase flor = new KnownCase("Flor", "['floɾ]", true, Arrays.asList(new Phoneme("fl", Phoneme.POSITION.OCI), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase floresta = new KnownCase("Floresta", "[flo’ɾɛstə]", true, Arrays.asList(new Phoneme("fl", Phoneme.POSITION.OCI), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase fogo = new KnownCase("Fogo", "[’fogo]", true, Arrays.asList(new Phoneme("f", Phoneme.POSITION.OI), new Phoneme("g", Phoneme.POSITION.OM)));
        KnownCase folha = new KnownCase("Folha", "[‘foʎə]", true, Arrays.asList(new Phoneme("f", Phoneme.POSITION.OI), new Phoneme("ʎ", Phoneme.POSITION.OM)));
        KnownCase fralda = new KnownCase("Fralda", "[’fɾawdə]", true, Arrays.asList(new Phoneme("fɾ", Phoneme.POSITION.OCI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase fruta = new KnownCase("Fruta", "[’fɾutəs]", true, Arrays.asList(new Phoneme("fɾ", Phoneme.POSITION.OCI), new Phoneme("t", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase galinha = new KnownCase("Galinha", "[ga’liɲə]", true, Arrays.asList(new Phoneme("g", Phoneme.POSITION.OI), new Phoneme("l", Phoneme.POSITION.OM), new Phoneme("ɲ", Phoneme.POSITION.OM)));
        KnownCase garfo = new KnownCase("Garfo", "[’gaɾfu]", true, Arrays.asList(new Phoneme("g", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.CM), new Phoneme("f", Phoneme.POSITION.OM)));
        KnownCase gato = new KnownCase("Gato", "[’gatu]", true, Arrays.asList(new Phoneme("g", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase girafa = new KnownCase("Girafa", "[ʒi’ɾafə]", true, Arrays.asList(new Phoneme("ʒ", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("f", Phoneme.POSITION.OM)));
        KnownCase grama = new KnownCase("Grama", "[’gɾəmə]", true, Arrays.asList(new Phoneme("gɾ", Phoneme.POSITION.OCI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase gritar = new KnownCase("Gritar", "[gɾi’ta]", true, Arrays.asList(new Phoneme("gɾ", Phoneme.POSITION.OCI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase igreja = new KnownCase("Igreja", "[i’gɾeʒə]", true, Arrays.asList(new Phoneme("gɾ", Phoneme.POSITION.OCME), new Phoneme("ʒ", Phoneme.POSITION.OM)));
        KnownCase jacaré = new KnownCase("Jacaré", "[ʒaka’ɾɛ]", true, Arrays.asList(new Phoneme("ʒ", Phoneme.POSITION.OI), new Phoneme("k", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        KnownCase jornal = new KnownCase("Jornal", "[ʒoɾ’naw]", true, Arrays.asList(new Phoneme("ʒ", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.CM), new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase letra = new KnownCase("Letra", "[’letɾəs]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("tɾ", Phoneme.POSITION.OCME), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase livro = new KnownCase("Livro", "[’livɾo]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("vɾ", Phoneme.POSITION.OCME)));
        KnownCase lápis = new KnownCase("Lápis", "[’lapis]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase língua = new KnownCase("Língua", "[’lĩngʷa]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("gʷ", Phoneme.POSITION.OM)));
        KnownCase magro = new KnownCase("Magro", "[’magɾu]", true, Arrays.asList(new Phoneme("m", Phoneme.POSITION.OI), new Phoneme("gɾ", Phoneme.POSITION.OCME)));
        KnownCase mesa = new KnownCase("Mesa", "[’mezə]", true, Arrays.asList(new Phoneme("m", Phoneme.POSITION.OI), new Phoneme("z", Phoneme.POSITION.OM)));
        KnownCase microfone = new KnownCase("Microfone", "[mikɾo’foni]", true, Arrays.asList(new Phoneme("m", Phoneme.POSITION.OI), new Phoneme("kɾ", Phoneme.POSITION.OCME), new Phoneme("f", Phoneme.POSITION.OM), new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase nariz = new KnownCase("Nariz", "[na’ɾis]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase nuvem = new KnownCase("Nuvem", "[’nuvẽj̃s]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase passarinho = new KnownCase("Passarinho", "[pasa’ɾiɲo]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("ɲ", Phoneme.POSITION.OM)));
        KnownCase pastel = new KnownCase("Pastel", "[pas’tɛw]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase pedra = new KnownCase("Pedra", "[’pɛdɾə]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("dɾ", Phoneme.POSITION.OCME)));
        KnownCase placa = new KnownCase("Placa", "[’plakə]", true, Arrays.asList(new Phoneme("pl", Phoneme.POSITION.OCI), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase plástico = new KnownCase("Plástico", "[’plasʧiko]", true, Arrays.asList(new Phoneme("pl", Phoneme.POSITION.OCI), new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase porta = new KnownCase("Porta", "[’pɔɾtə]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.CM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase prato = new KnownCase("Prato", "[’pɾato]", true, Arrays.asList(new Phoneme("pɾ", Phoneme.POSITION.OCI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase presente = new KnownCase("Presente", "[pɾe’zẽnʧi]", true, Arrays.asList(new Phoneme("pɾ", Phoneme.POSITION.OCI), new Phoneme("z", Phoneme.POSITION.OM), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase rabo = new KnownCase("Rabo", "[’χabu]", true, Arrays.asList(new Phoneme("χ", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));
        KnownCase refri = new KnownCase("Refri", "[χe’fɾi]", true, Arrays.asList(new Phoneme("χ", Phoneme.POSITION.OI), new Phoneme("fɾ", Phoneme.POSITION.OCME)));
        KnownCase relógio = new KnownCase("Relógio", "[χe’lɔʒu]", true, Arrays.asList(new Phoneme("χ", Phoneme.POSITION.OI), new Phoneme("l", Phoneme.POSITION.OM), new Phoneme("ʒ", Phoneme.POSITION.OM)));
        KnownCase sapato = new KnownCase("Sapato", "[sa’pato]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase sapo = new KnownCase("Sapo", "[’sapu]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM)));
        KnownCase sofá = new KnownCase("Sofá", "[so’fa]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("f", Phoneme.POSITION.OM)));
        KnownCase soprar = new KnownCase("Soprar", "[so’pɾaɾ]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("pɾ", Phoneme.POSITION.OCME), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tesoura = new KnownCase("Tesoura", "[ʧi’zoɾə]", true, Arrays.asList(new Phoneme("ʧ", Phoneme.POSITION.OI), new Phoneme("z", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        KnownCase travesseiro = new KnownCase("Travesseiro", "[tɾave’seɾo]", true, Arrays.asList(new Phoneme("tɾ", Phoneme.POSITION.OCI), new Phoneme("v", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        KnownCase trem = new KnownCase("Trem", "[’tɾẽj̃]", true, Arrays.asList(new Phoneme("tɾ", Phoneme.POSITION.OCI)));
        KnownCase tênis = new KnownCase("Tênis", "[’tenis]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase vaca = new KnownCase("Vaca", "[’vakə]", true, Arrays.asList(new Phoneme("v", Phoneme.POSITION.OI), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase ventilador = new KnownCase("Ventilador", "[vẽnʧila’doɾ]", true, Arrays.asList(new Phoneme("v", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM), new Phoneme("d", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase vidro = new KnownCase("Vidro", "[vi’dɾu]", true, Arrays.asList(new Phoneme("v", Phoneme.POSITION.OI), new Phoneme("dɾ", Phoneme.POSITION.OCME)));
        KnownCase zebra = new KnownCase("Zebra", "[’zebɾə]", true, Arrays.asList(new Phoneme("z", Phoneme.POSITION.OI), new Phoneme("bɾ", Phoneme.POSITION.OCME)));
        KnownCase zero = new KnownCase("Zero", "[’zɛɾu]", true, Arrays.asList(new Phoneme("z", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        return Arrays.asList(anel, barriga, batom, bebê, beijo, biblioteca, bicicleta, bolsa, brinco, bruxa, cabelo, cachorro, caixa, calça, cama, caminhão, casa, cavalo, chapéu, chiclete, chifre, chinelo, cobra, coelho, colher, cruz, dado, dedo, dente, dragão, escrever, espelho, estrela, faca, flor, floresta, fogo, folha, fralda, fruta, galinha, garfo, gato, girafa, grama, gritar, igreja, jacaré, jornal, letra, livro, lápis, língua, magro, mesa, microfone, nariz, navio, nuvem, passarinho, pastel, pedra, placa, plástico, porta, prato, presente, rabo, refri, relógio, sapato, sapo, sofá, soprar, terra, tesoura, travesseiro, trem, tênis, vaca, ventilador, vidro, zebra, zero);

    }

}
