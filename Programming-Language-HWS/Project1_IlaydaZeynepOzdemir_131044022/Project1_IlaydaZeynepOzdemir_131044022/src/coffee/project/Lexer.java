package coffee.project;

import coffee.IdentifierList;
import coffee.REPL;
import coffee.TokenList;
import coffee.datatypes.*;
import coffee.syntax.Keywords;
import coffee.syntax.Operators;

import java.util.StringTokenizer;

/**
 * ******************************************
 * *** Ilayda Zeynep Ozdemir 131044022   ****
 * ******************************************
 * DFA tasarimi icin yedi durum olusturdum
 * -- hepsi kendi icinde DFA olarak tasarlandi
 * Word olma durumu (harf gelince)
 * **final state'i harfteyken bosluk gelmesi durumunda ( ) operatorleri gelmesi ile olusur
 * **sayi gelirse basarisiz olur
 * Sayi olma durumu (sayi gelince ve -sayi gelince)
 * **final state'i ( ) gelmesiyle olusur harf gelirse basarisiz olur
 * bosluk olma durumu bosluk geldiginde
 * **final'i bosluk disinda bir sey gelmesiyle olur
 * aritmetik operator olma durumlari
 * ** aritmetik ifadeden sonra bosluk birakilmali
 * sol parantez olma durumu
 * **
 * sag parantez olma durumu
 * **
 * ' operatoru geldiginde list olma durumu
 * **
 *
 * bunlarin her birinden uygun olanlara yollar olusturdum
 * bu durumlari if statementlari ile bagladim
 * Created by ft on 10/14/15.
 */
public class Lexer implements REPL.LineInputCallback {
    private TokenList tokens;
    private IdentifierList ids;
    private Character next = null;
    // private Integer countParenthesis = null; // bu static c+'ta miydi bak?


    public Lexer() {
        tokens = TokenList.getInstance();
        ids = IdentifierList.getInstance();
    }


    private boolean isKeyword(String Word) {
        boolean rValue = true;
        if (Word.equals(Keywords.DEFFUN)) ;
        else if (Word.equals(Keywords.ELSE)) ;
        else if (Word.equals(Keywords.EQUAL)) ;
        else if (Word.equals(Keywords.FOR)) ;
        else if (Word.equals(Keywords.NOT)) ;
        else if (Word.equals(Keywords.OR)) ;
        else if (Word.equals(Keywords.SET)) ;
        else if (Word.equals(Keywords.THEN)) ;
        else if (Word.equals(Keywords.WHILE)) ;
        else if (Word.equals(Keywords.IF)) ;
        else if (Word.equals(Keywords.AND)) ;
        else if (Word.equals(Keywords.APPEND)) ;
        else if (Word.equals(Keywords.CONCAT)) ;
        else rValue = false;
        return rValue;
    }

    private boolean isBooleanValue(String Word) {
        if (Word.equals(Keywords.TRUE) || Word.equals(Keywords.FALSE))
            return true;
        else return false;
    }

    private boolean isArithmeticOpr(char expression) {
        return (((expression == Operators.PLUS.charAt(0)) || (expression == Operators.MINUS.charAt(0))) ||
                ((expression == Operators.SLASH.charAt(0)) || (expression == Operators.ASTERISK.charAt(0))));
    }

    /**
     * identifier'in unique olup olmadigini kontrol eder
     *
     * @param id id adi
     * @return unique ise true degilse false
     */
    private boolean isUnique(String id) { // aslinda bu kisim identifierlist icerisinde yapilsa daha iyi olur
        // ama orayi degistiremeyiz diye buraya yazdim
        for (int i = 0; i < ids.getIdentifiers().size(); ++i) {
            // System.out.println(ids.getIdentifiers().get(i));
            StringTokenizer tok = new StringTokenizer(ids.getIdentifiers().get(i), "_");
            String temp = tok.nextToken();
            temp = tok.nextToken();
            if (id.equals(temp)) return false;
        }
        return true;
    }

    /**
     * kelimeye harf disinda bir sey gelmesini word state'inde sinirladigim icin
     * keyword ve binary value olmayanlar id olur
     * @param word eklenecek kelime
     */
    private void addWord(String word) {

        if (isKeyword(word)) {
            tokens.addToken(new Keyword(word));
        } else if (isBooleanValue(word)) {
            tokens.addToken(new ValueBinary(Boolean.valueOf(word))); //VALUE - keyword degiller
        } else { // keyword ve boolean degilse identifier olur. cunku araya baska karakter girmesini onluyorum
            if (isUnique(word))
                ids.addIdentifier(word);

            tokens.addToken(new Identifier(word));
        }
    }




    /**
     * harf gorunce bu state'e gelir
     * kelime bitisi harf disinda bir sey gelince olur
     * recursive
     * @param subLine
     * @param word
     * @throws WrongTokenException
     */
    private void stateWord(String subLine, String word) throws WrongTokenException {
        if (subLine.isEmpty()) {
            if (!word.isEmpty())
                addWord(word);
            return;
        }
        if (Character.isLetter(subLine.charAt(0))) {//sadece karakter olma durumlari alindi SOR? _ falan var mı
            stateWord(subLine.substring(1), word + subLine.charAt(0));
        } else {
            if (!Character.isDigit(subLine.charAt(0))) {
                if (subLine.charAt(0) == ' ') {
                    addWord(word); //add
                    stateWhiteSpace(subLine.substring(1));
                } else {
                    if (subLine.charAt(0) == Operators.LEFT_PARENTHESIS.charAt(0)) {
                        addWord(word); //add
                        stateLeftP(subLine);
                    } else if (subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0)) {
                        addWord(word); //add
                        stateRightP(subLine);
                    }else throw new WrongTokenException(String.format("Hatali input~ %s",word+subLine));
                }
            } else {
                throw new WrongTokenException(String.format("Hatali string~ %s ", word + subLine));
            }
        }
    }

    /**
     * bosluk gorunce bu state'e gelir
     * bu state'den butun her yere erisim vardir
     * recursive
     * @param subLine
     * @throws WrongTokenException
     */
    private void stateWhiteSpace(String subLine) throws WrongTokenException {

        if (subLine.isEmpty()) //endofline kontrol et
            return;
        if (subLine.charAt(0) == ' ') {
            stateWhiteSpace(subLine.substring(1));
        } else if (Character.isLetter(subLine.charAt(0))) {//sadece karakter olma durumlari alindi SOR? _ falan var mı
            stateWord(subLine.substring(0), new String());
        } else if (Character.isDigit(subLine.charAt(0))) {
            stateValue(subLine.substring(0), new String());
        } else if (subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0)) {
            stateRightP(subLine);
        } else if (subLine.charAt(0) == Operators.LEFT_PARENTHESIS.charAt(0)) {
            stateLeftP(subLine);
        } else if (isArithmeticOpr(subLine.charAt(0))) {
            StateArithmetic(subLine.substring(0));
        } else if (subLine.charAt(0) == Operators.APOSTROPHE.charAt(0)) { // ' isareti gelince liste olacak
            StateList(subLine.substring(1));
        } else throw new WrongTokenException(String.format("Yanlis giden bir şeyler var! %s",subLine));

    }


    /**
     * ilk karakterin - ya da sayi olmasi gerekir
     * -12 tanimlidir 12 tanimlidir ama +12 tanimli degildir(aritmetik state'inde hata verir)
     * recursive
     * @param subLine
     * @param Val
     * @throws WrongTokenException
     */
    private void stateValue(String subLine, String Val) throws WrongTokenException {
        if (subLine.isEmpty()) {
            if (!Val.isEmpty())
                tokens.addToken(new ValueInt(Integer.parseInt(Val)));
            return;
        }
        if (Character.isDigit(subLine.charAt(0)) || subLine.charAt(0) == Operators.MINUS.charAt(0)) {
            if (subLine.charAt(0) == '0' && Character.isDigit(subLine.charAt(1))) { //01 falan kabul etmez
                throw new WrongTokenException("0 ile baslayan birden fazla basamakli sayi yazdiniz");
            } else stateValue(subLine.substring(1), Val + subLine.charAt(0));
            //bir baska state'e gecene kadar olusturmuyorum cunku sayinin tamami icin olusturulmali
        } else { //bir baska state'e gececek
            if (subLine.charAt(0) == ' ') {
                tokens.addToken(new ValueInt(Integer.parseInt(Val)));
                stateWhiteSpace(subLine.substring(1));
            } else if (subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0)) {
                tokens.addToken(new ValueInt(Integer.parseInt(Val)));
                stateRightP(subLine);
            } else { //harf gibi
                throw new WrongTokenException(String.format("Sayidan sonra yanlis bir karakter geldi %s ", Val + subLine.charAt(0)));
            }
        }
    }


    /**
     * parantez acma gordugu anda buraya gelir
     * recursive
     * tekrar sol parantez gorurse kendisini cagirir
     * bosluk,(,),sayi,aritmetik ifade,harf
     * @param subLine
     * @throws WrongTokenException
     */
    private void stateLeftP(String subLine) throws WrongTokenException { // () kabul edilmez
        if (subLine.isEmpty()) return;
        if (Character.isLetter(subLine.charAt(0))) {//harfse q2
            stateWord(subLine, new String());
        } else if (subLine.charAt(0) == ' ') { //bosluk
            stateWhiteSpace(subLine.substring(1));
        } else if (subLine.charAt(0) == Operators.LEFT_PARENTHESIS.charAt(0)) { // (
            tokens.addToken(new Operator(Operators.LEFT_PARENTHESIS));
            stateLeftP(subLine.substring(1));//kendisine donerse bir sonrakinden yolla
        } else if (Character.isDigit(subLine.charAt(0))) {//sayi
            stateValue(subLine.substring(0), new String());
        } else if (isArithmeticOpr(subLine.charAt(0))) { //aritmetik ifade
            StateArithmetic(subLine.substring(0));
        } else if (subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0)) {
            stateRightP(subLine);
        } else if (subLine.charAt(0) == Operators.APOSTROPHE.charAt(0)) {
            StateList(subLine.substring(1));
        }else {
            throw new WrongTokenException(String.format("'(' operatorunden sonra yanlis bir karakter geldi~ %s", subLine));
        }
    }

    /**
     * sag parantezs gorunce buraya girer
     * sag parantezden sonra sadece sag parantez ve bosluk gelebilir
     * @param subLine
     * @throws WrongTokenException
     */
    private void stateRightP(String subLine) throws WrongTokenException { //finish
        if (subLine.isEmpty()) {
            return;
        } else {
            if (subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0)) { // if-else statementlari transation (edge) gibi dusunulebilir
                tokens.addToken(new Operator(Operators.RIGHT_PARENTHESIS));
                // if (countParenthesis != null) --countParenthesis;
                stateRightP(subLine.substring(1)); //kendisine doner
            } else if (subLine.charAt(0) == ' ') {
                stateWhiteSpace(subLine);
            } else throw new WrongTokenException(String.format("Hatali input ~ %s", subLine.charAt(0)));
            //
        }
    }
/**
    private boolean isValidValue(String val) {
        if (val.charAt(0) == '0' && val.length() != 1)
            return false;
        else {
            if(val.charAt(0) == Operators.MINUS.charAt(0)){
                return isValidValue(val.substring(1));
            }
            for (int i = 0; i < val.length(); ++i) {
                if (!Character.isDigit(val.charAt(i)))
                    return false;
            }
            return true;
        }
    }

    private boolean isValidIDentifier(String id) {
        for (int i = 0; i < id.length(); ++i) {
            if (!Character.isLetter(id.charAt(i))) return false;
        }
        return true;
    }
*/
    /**
     * list icinde her sey gelebilir -> lexer bunu anlamaz (?)
     * ),(,bosluk,sayi,harf,aritmetik operator
     * @param subLine
     * @throws WrongTokenException
     */
    private void StateList(String subLine) throws WrongTokenException { // buraya ' gorunce gelsin

        tokens.addToken(new Operator(Operators.APOSTROPHE));
        if(subLine.isEmpty()) return;
        String commaFree = new String();
        for(int i=0;i<subLine.length();++i){
            if(subLine.charAt(i) == ','){
                commaFree += " ";
            }else commaFree += String.valueOf(subLine.charAt(i));
        }
        if(commaFree.charAt(0) == Operators.ASTERISK.charAt(0) ) StateList(subLine);
        if (commaFree.charAt(0) == Operators.LEFT_PARENTHESIS.charAt(0) ) {
           stateLeftP(commaFree);
        }else if(subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0) ) {
            stateRightP(commaFree);
        }else if(Character.isDigit(commaFree.charAt(0))){
            stateValue(commaFree,new String());
        }else if(Character.isLetter(commaFree.charAt(0))) {
            stateWord(commaFree,new String());
        }else if(isArithmeticOpr(commaFree.charAt(0))) {
            StateArithmetic(commaFree);
        }else if(commaFree.charAt(0) == ' '){
            stateWhiteSpace(commaFree.substring(1));
        }


    }


    /**
     * - disindaki ifadeler ardindan bosluk birakmadan bir ifade gelirse
     * hata verir
     * @param subLine
     * @throws WrongTokenException
     */
    private void StateArithmetic(String subLine) throws WrongTokenException {
        if (subLine.charAt(0) == Operators.MINUS.charAt(0) && Character.isDigit(subLine.charAt(1))) {
            //eger eksi ise ve bir sonraki sayi ise //yani bosluk yok aralarinda
            stateValue(subLine, new String()); // negatif
        } else {
            tokens.addToken(new Operator(Character.toString(subLine.charAt(0))));

           /* if (Character.isDigit(subLine.charAt(1))) { //sayi
                stateValue(subLine, new String());
            } else if (Character.isLetter(subLine.charAt(1))) { //harf
                stateWord(subLine, new String());
            } else*/
            if (subLine.charAt(1) == ' ') { //bosluk
                stateWhiteSpace(subLine.substring(1));
            } else if (subLine.charAt(1) == Operators.LEFT_PARENTHESIS.charAt(0)) { //(
                stateLeftP(subLine.substring(1));
            } else if (subLine.charAt(1) == Operators.RIGHT_PARENTHESIS.charAt(0)) { //)
                stateRightP(subLine.substring(1));
            } else throw new WrongTokenException("Aritmetik ifade sonrasinda hatali kullanim!");
        }

    }

    /**
     * baslangic inputu gelmesiyle baslar
     * @param subLine
     * @throws WrongTokenException
     */
    private void Start(String subLine) throws WrongTokenException {

        if (subLine.isEmpty()) return;
        StringTokenizer tok = new StringTokenizer(subLine, "\t");
        subLine = tok.nextToken();
        if (subLine.charAt(0) == Operators.LEFT_PARENTHESIS.charAt(0)) {
            stateLeftP(subLine);
        } else if (subLine.charAt(0) == Operators.RIGHT_PARENTHESIS.charAt(0)) {
            stateRightP(subLine);
        }else if (subLine.charAt(0) == ' ') {
            stateWhiteSpace(subLine.substring(1));
        } else if (Character.isLetter(subLine.charAt(0))) {
            stateWord(subLine, new String());
        } else if (Character.isDigit(subLine.charAt(0))) {
            stateValue(subLine, new String());
        } else if (isArithmeticOpr(subLine.charAt(0))) {
            StateArithmetic(subLine);
        } else if (subLine.charAt(0) == Operators.APOSTROPHE.charAt(0)) {
            StateList(subLine.substring(1));
        }
    }

    /**
     * hata class'i
     */
    public class WrongTokenException extends Exception {
        private String errorFormat;

        public WrongTokenException(String error) {
            super(error);
        }

    }

    /**
     * start ile baslar ve kendi icinde transitionlar(edge) uzerinden gerekli statelere gider
     * eger hatali gordugu durumlar varsa exception firlatarak o satirin geri kalanini almaz
     * @param line User input
     * @return
     */
    @Override
    public String lineInput(String line) { //exception firlatmasi gerekmez mi hatali satirin
        try {
            // length += line.length();
            Start(line);
            return line;
        } catch (WrongTokenException ex) {
            System.err.println(ex.getMessage());
            //System.err.println(tokens.getAllTokens()); //bu satir basarili sonlanamadi
            return null;
        }

    }
}