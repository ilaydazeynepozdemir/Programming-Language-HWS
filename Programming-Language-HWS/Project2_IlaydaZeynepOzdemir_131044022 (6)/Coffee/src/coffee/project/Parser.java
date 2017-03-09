package coffee.project;

import coffee.IdentifierList;
import coffee.TokenList;
import coffee.datatypes.*;
import coffee.syntax.Keywords;
import coffee.syntax.Operators;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * **********************************************************************************
 * **** Ilayda Zeynep Ozdemir 131044022
 *
 * shift reduce parser -> COK YAVAS CALISIYOR CUNKU HER OLASILIGI TEK TEK DENIYOR
 * once stack bossa bir tane shift eder
 * sonra eger shift edilen reduce olmuyorsa
 * bir tane daha shift eder
 * ardindan once peek'tekine bakilir
 * eger reduce edilmiyorsa stack sonuca kadar
 * teker teker alinarak reduce edilip edilemedigine bakilir
 * <p>
 * <p>
 * Ama dil icindeki syntax tanimlamalarinda conflictler olustugu icin
 * yani ayni sey iki farkli sekilde tanimlanabilir oldugu icin hatalari engellemek adina
 * stack'e konulan yeni deger yani en son shift edilen token
 * integerValue ya da id ise  2'şerden dort farkli parse islemi yapiliyor(recursive)
 * Bu da kodu fazlasiyla yavaslatti. Zaten brute force sekilde tek tek bakan bir
 * algoritma kurmustum.
 * Bir de 2'ser recursive call isin icine girince sonlanmasi biraz zaman aliyor
 * <p>
 * NOT :
 * IntegerValue hem VALUES hem EXPI olabiliyor.
 * Burada Conflict var
 * Cozumu : shift edilen token integerValue olursa
 * parser hem EXPI(1) hem VALUES(0) icin cagirilir.
 * <p>
 * Ayni sekilde identifier da hem EXPI hem IDS olabilir
 * IDLIST -> ( IDS ) | ()
 * IDS -> IDS  IDENTIFIER |IDENTIFIER
 * Cozumu : shift edilen token identifier olursa
 * parser hem EXPI(3) hem IDS(4) icin cagirilir.
 * <p>
 * (deffun Id IDLIST EXPLISTI)
 * Buradaki IDLIST tanimlandi
 * Ayrica  Id olarak gecen yerler expi'dir ama EXPI icerisinde
 * tutulan exp arrayindeki deger id ise kabul edilir
 * (sum x) EXPI olarak alinir (CALL )
 *************************************************************/
public class Parser {
    private final String TOKEN_EXPI = "EXPI";
    private final String TOKEN_EXPB = "EXPB";
    private final String TOKEN_EXPLISTI = "EXPLISTI";
    private final String TOKEN_IDS = "IDS";
    private final String TOKEN_IDLIST = "IDLIST";
    private final String TOKEN_VALUES = "VALUES";
    private final String TOKEN_LISTVALUE = "LISTVALUE";
    private final String TOKEN_INPUT = "INPUT";

    private class INPUT implements Token {
        ArrayList<Token> exps = new ArrayList<Token>();
        ///Stack<Token> mstack = new Stack<Token>();

        INPUT(ArrayList<Token> given) {
            exps = given;
        }

        @Override
        public String toString() {
            return "INPUT";
        }

        @Override
        public String getTokenName() {
            return "INPUT";
        }

        @Override
        public Type getTokenType() {
            return null;
        }
    }

    private class EXPI extends INPUT {

        EXPI(ArrayList<Token> given) {
            super(given);
        }

        @Override
        public String toString() {
            return TOKEN_EXPI;
        }

        @Override
        public String getTokenName() {
            return TOKEN_EXPI;
        }
    }

    private class EXPB extends INPUT {

        EXPB(ArrayList<Token> given) {
            super(given);
        }

        @Override
        public String toString() {
            return TOKEN_EXPB;
        }

        @Override
        public String getTokenName() {
            return TOKEN_EXPB;
        }
    }

    private class IDS extends INPUT {

        IDS(ArrayList<Token> given) {
            super(given);
        }

        @Override
        public String toString() {
            return TOKEN_IDS;
        }

        @Override
        public String getTokenName() {
            return TOKEN_IDS;
        }
    }

    private class IDLIST extends IDS {
        IDLIST(ArrayList<Token> given) {
            super(given);
        }

        @Override
        public String toString() {
            return TOKEN_IDLIST;
        }

        @Override
        public String getTokenName() {
            return TOKEN_IDLIST;
        }
    }

    private class EXPLISTI extends EXPI {

        //ISTVALUE list;

        EXPLISTI(ArrayList<Token> given) {

            super(given);
            //list = new LISTVALUE(given);
        }

        @Override
        public String toString() {
            return TOKEN_EXPLISTI;
        }

        @Override
        public String getTokenName() {
            return TOKEN_EXPLISTI;
        }
    }


    private class VALUES extends INPUT {

        VALUES(ArrayList<Token> given) {
            super(given);
        }

        @Override
        public String toString() {
            return TOKEN_VALUES;
        }

        @Override
        public String getTokenName() {
            return TOKEN_VALUES;
        }

    }

    private class LISTVALUE implements Token { ////////?
        VALUES values;

        LISTVALUE(VALUES given) {
            values = given;
        }

        LISTVALUE(ArrayList<Token> given) {
            values = new VALUES(given);
        }


        @Override
        public String toString() {
            return TOKEN_LISTVALUE;
        }

        @Override
        public String getTokenName() {
            return TOKEN_LISTVALUE;
        }

        @Override
        public Type getTokenType() {
            return null;
        }

    }

    private Integer shift(Integer Icurrent, Stack<Token> stackTemp) {
        // System.out.println("shift");
        if (Icurrent < TokenList.getInstance().getAllTokens().size())
            stackTemp.push(TokenList.getInstance().getAllTokens().get(Icurrent));

        if (Icurrent < TokenList.getInstance().getAllTokens().size())
            ++Icurrent;

        return Icurrent;

    }

    private boolean isLeftParanthes(Token left) {
        return (left.toString().equals(left.getTokenName() + "_" + Operators.LEFT_PARENTHESIS));

    }

    private boolean isRightParanthes(Token right) {
        return (right.toString().equals(right.getTokenName() + "_" + Operators.RIGHT_PARENTHESIS));
    }


    private boolean isExpressionB(ArrayList<Token> control) {
        if (control.size() == 1) {
            try {
                if (control.get(0).getTokenType().equals(Token.Type.BINARY_VALUE)) {
                    return true;
                }
            } catch (NullPointerException e) {
                return false;
            }
        } else if (control.size() == 5) {
            if (isRightParanthes(control.get(0))) {
                if (control.get(1).getTokenName().equals(TOKEN_EXPB) &&
                        control.get(2).getTokenName().equals(TOKEN_EXPB)) {
                    if (control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.AND) ||
                            control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.OR) ||
                            control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.EQUAL)) {
                        if (isLeftParanthes(control.get(4))) return true;
                    }

                } else if (control.get(1).getTokenName().equals(TOKEN_EXPI) &&
                        control.get(2).getTokenName().equals(TOKEN_EXPI)) {
                    if (control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.EQUAL)) {
                        if (isLeftParanthes(control.get(4))) return true;
                    }
                }
            }

        } else if (control.size() == 4) {
            if (isRightParanthes(control.get(0))) {
                if (control.get(1).getTokenName().equals(TOKEN_EXPB)) {
                    if (control.get(2).toString().equals(control.get(2).getTokenName() + "_" + Keywords.NOT)) {
                        if (isLeftParanthes(control.get(3))) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isExpression(ArrayList<Token> control, int sign) {
        try {
            if (control.size() == 1 && (sign == 1 || sign == 3)) {
                //if (control.get(0).getTokenType() == null) return false;
                if (control.get(0).getTokenName().equals("VALUE_INT") && sign == 1) {
                    return true;
                } else if (control.get(0).getTokenName().equals("IDENTIFIER") && sign == 3) {
                    return true;
                } else return false;
            } else if (control.size() == 4) {
                if (isRightParanthes(control.get(0))) {
                    if (control.get(1).getTokenName().equals(TOKEN_EXPLISTI)) {
                        if (control.get(2).getTokenType().equals(Token.Type.IDENTIFIER)) {
                            if (isLeftParanthes(control.get(3))) return true;
                        }
                    }
                }

            } else if (control.size() == 5) {

                if (isRightParanthes(control.get(0))) {
                    if ((control.get(1).getTokenName().equals(TOKEN_EXPI)) &&
                            (control.get(2).getTokenName().equals(TOKEN_EXPI))) {
                        Token temp = control.get(3);
                        if (temp.toString().equals(temp.getTokenName() + "_" + Operators.PLUS) ||
                                temp.toString().equals(temp.getTokenName() + "_" + Operators.MINUS) ||
                                temp.toString().equals(temp.getTokenName() + "_" + Operators.ASTERISK) ||
                                temp.toString().equals(temp.getTokenName() + "_" + Operators.SLASH)) {
                            if (isLeftParanthes(control.get(4))) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean isAssignment(ArrayList<Token> control) {
        if (control.size() == 5) {
            if (isRightParanthes(control.get(0))) {
                if (control.get(1).getTokenName().equals(TOKEN_EXPI)) {
                    try {
                        if (control.get(2).getTokenType().equals(Token.Type.IDENTIFIER)) {
                            if (control.get(3).getTokenType().equals(Token.Type.KEYWORD)) {
                                Keyword tempKey = (Keyword) control.get(3);
                                if (tempKey.getKeyword().equals(Keywords.SET)) {
                                    return true;
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        return false;
                    }
                }
            }
        }
        return false;

    }

    private boolean isCall(ArrayList<Token> control) { // (sumup (- x 1))
        try {
            if (control.size() == 4) {
                if (isRightParanthes(control.get(0))) {
                    if (control.get(1) instanceof EXPI) { //.getTokenName().equals("EXPLISTI")
                        if (control.get(2).getTokenName().equals(TOKEN_EXPI)) {
                            EXPI temp = (EXPI) control.get(2);
                            if (temp.exps.get(0).getTokenType().equals(Token.Type.IDENTIFIER)) {//sumup kismi
                                if (isLeftParanthes(control.get(3))) return true;
                            }
                        }
                    }
                }

            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean isControlStatement(ArrayList<Token> control) {
        try {
            if (control.size() >= 5 && isRightParanthes(control.get(0)) && isLeftParanthes(control.get(control.size() - 1))) {
                if (control.size() == 9) {
                    if (control.get(1).getTokenName().equals(TOKEN_EXPLISTI)) {
                        if (isRightParanthes(control.get(2))) {
                            if (control.get(3).getTokenName().equals(TOKEN_EXPI)) {
                                if (control.get(4).getTokenName().equals(TOKEN_EXPI)) {
                                    if (control.get(5).getTokenName().equals(TOKEN_EXPI)) {
                                        EXPI temp = (EXPI) control.get(5);
                                        if (temp.exps.size() == 1 && temp.exps.get(0).getTokenType().equals(Token.Type.IDENTIFIER)) {
                                            if (isLeftParanthes(control.get(6))) {
                                                if (control.get(7).toString().equals(control.get(7).getTokenName() + "_" + Keywords.FOR)) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else if (control.size() == 8) {
                    if (control.get(1) instanceof EXPI) {//.getTokenName().equals(TOKEN_EXPLISTI")
                        if (control.get(2).toString().equals(control.get(2).getTokenName() + "_" + Keywords.ELSE)) {
                            if (control.get(3) instanceof EXPI) { //.getTokenName().equals("TOKEN_EXPLISTI")
                                if (control.get(4).toString().equals(control.get(4).getTokenName() + "_" + Keywords.THEN)) {
                                    if (control.get(5).getTokenName().equals(TOKEN_EXPB)) {
                                        if (control.get(6).toString().equals(control.get(6).getTokenName() + "_" + Keywords.IF)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (control.size() == 5) {
                    if (control.get(1) instanceof EXPI) {
                        if (control.get(2).getTokenName().equals(TOKEN_EXPB)) {
                            if (control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.IF)) {
                                return true;
                            }
                        }

                    }

                } else if (control.size() == 6) {
                    if (control.get(1) instanceof EXPI) {//.getTokenName().equals("EXPLISTI")
                        if (control.get(2) instanceof EXPI) {
                            if (control.get(3).getTokenName().equals(TOKEN_EXPB)) {
                                if (control.get(4).toString().equals(control.get(4).getTokenName() + "_" + Keywords.IF)) {
                                    return true;
                                }
                            }
                        }

                    }


                } else if (control.size() == 7) { //(while (and true false) '(1)) olunca (while ((and true false)) '(1)) olmali
                    //(while (true) '(1))
                    if (control.get(1) instanceof EXPI) {
                        if (isRightParanthes(control.get(2))) {
                            if (control.get(3).getTokenName().equals(TOKEN_EXPB)) {
                                if (isLeftParanthes(control.get(4))) {
                                    if (control.get(5).toString().equals(control.get(5).getTokenName() + "_" + Keywords.WHILE)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean isDefinition(ArrayList<Token> control) {
        try {
            if (control.size() == 6) {
                //   System.out.println( "\n\n"+control+"\n");
                if (isRightParanthes(control.get(0))) {
                    if (control.get(1) instanceof EXPI) {//.getTokenName().equals("EXPLISTI")
                        if (control.get(2).getTokenName().equals(TOKEN_IDLIST)) {
                            if (control.get(3).getTokenName().equals(TOKEN_EXPI)) {//(deffun func (a) '(1 2))
                                EXPI temp = (EXPI) control.get(3);
                                if (temp.exps.get(0).getTokenType().equals(Token.Type.IDENTIFIER)) {
                                    if (control.get(4).toString().equals(control.get(4).getTokenName() + "_" + Keywords.DEFFUN)) {
                                        if (isLeftParanthes(control.get(5))) return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean isExpressionL(ArrayList<Token> control) {
        //    System.out.println(control);
        if (control.size() == 1) {
            return false;
        } else if (control.size() == 5) {
            if (isRightParanthes(control.get(0))) {
                if (control.get(1).getTokenName().equals(TOKEN_EXPLISTI)) {
                    if (control.get(2).getTokenName().equals(TOKEN_EXPLISTI)) {
                        if (control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.CONCAT)) {
                            if (isLeftParanthes(control.get(4))) return true;
                        }
                    } else if (control.get(2).getTokenName().equals(TOKEN_EXPI)) {
                        if (control.get(3).toString().equals(control.get(3).getTokenName() + "_" + Keywords.APPEND)) {
                            if (isLeftParanthes(control.get(4))) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void transfer(Stack<Token> stackTemp, Stack<String> parseTree) {

        for (int i = stackTemp.size() - 1; i >= 0; --i) {
            Token Temp = stackTemp.get(i);
            if (Temp.getTokenName().equals("Operator")) {
                Operator t = (Operator) Temp;
                parseTree.push(t.getOperator());
            } else if (Temp.getTokenName().equals("Keyword")) {
                Keyword t = (Keyword) Temp;
                parseTree.push(t.getKeyword());
            } else
                parseTree.push(Temp.toString());
        }
        parseTree.push("\n");

        // System.out.println(parseTree);
    }

    private boolean isSTART(Stack<Token> stackTemp, Stack<String> parseTree) {
        if (!stackTemp.isEmpty()) {
            if (stackTemp.peek().getTokenName().equals(TOKEN_INPUT)) {
                parseTree.pop();
                parseTree.push("START");
                return true;
            }

        }
        return false;
    }

    private void printParseTree(Stack<String> parseTree) {

        int i = 0;
        while (!parseTree.isEmpty()) {
            if (i == 0) {
                System.out.print(parseTree.pop() + " -> ");
            } else if (parseTree.peek().equals("\n")) {
                System.out.print(parseTree.pop() + "      ->");
            } else System.out.print(parseTree.pop() + " ");
            ++i;
        }
    }

    /***************************************************************************************/
    private boolean isINPUT(Stack<Token> stackTemp, Stack<String> parseTree) {
        //    System.out.println(stackTemp);
        ArrayList<Token> list = new ArrayList<Token>();
        while (!stackTemp.isEmpty()) {
            list.add(stackTemp.pop());
        }
        for (int i = list.size() - 1; i >= 0; --i) {
            stackTemp.push(list.get(i));
        }
        int count = 0;
        for (int i = list.size() - 1; i >= 0; --i) {
            if (list.get(i) instanceof EXPI) {
                stackTemp.pop();
                ++count;
            }
        }
        if (count == list.size()) {
            stackTemp.push(new INPUT(list));
            transfer(stackTemp, parseTree);
            return true;
        }

        return false;
    }

    /***************************************************************************************/
    private boolean isEXPI(ArrayList<Token> control, int sign) {
        return isExpression(control, sign) || isControlStatement(control) || isAssignment(control) ||
                isCall(control) || isDefinition(control);
    }


    private boolean isEXPB(ArrayList<Token> control) {
        return isExpressionB(control);
    }

    //////////////////////////////////////////////////DON
    private boolean isEXPLISTI(ArrayList<Token> control) {
        return isLISTVALUE(control) || isExpressionL(control);
    }


    private boolean isVALUES(ArrayList<Token> control, int sign) {
        // System.out.println(control);
        if (sign == 0) {
            if (control.size() == 1 && (control.get(0).getTokenName().equals("VALUE_INT"))) {
                return true;
            } else if (control.size() == 2) {
                if (control.get(0).getTokenName().equals(TOKEN_VALUES)) {
                    if (control.get(1).getTokenName().equals(TOKEN_VALUES) ) {
                        return true; //
                    }
                }

            }
        }
        return false;
    }


    private boolean isLISTVALUE(ArrayList<Token> control) {
        try {
            //   System.out.println(control);
            if (control.size() == 3 || control.size() == 2) {
                if (isRightParanthes(control.get(0))) {
                    if (control.get(1).getTokenName().equals(TOKEN_VALUES)) {
                        if (control.size() == 3 && control.get(2).toString().equals(control.get(2).getTokenName() + "_" + Operators.CONS))
                            return true;
                    } else if (control.get(1).toString().equals(control.get(1).getTokenName() + "_" + Operators.CONS))
                        return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean isIDS(ArrayList<Token> control, int sign) {
        if (sign == 4) {
            if (control.size() == 1 && (control.get(0).getTokenName().equals("IDENTIFIER"))) {
                return true;
            } else if (control.size() == 2) {
                if (control.get(0).getTokenName().equals(TOKEN_IDS)) {
                    if (control.get(1).getTokenName().equals(TOKEN_IDS)) return true; //
                }
            }
        }
        return false;
    }

    private boolean isIDLIST(ArrayList<Token> control) {
        try {
            if (control.size() == 3 || control.size() == 2) {
                if (isRightParanthes(control.get(0))) {
                    if (control.get(1).getTokenName().equals(TOKEN_IDS)) {
                        if (control.size() == 3 && isLeftParanthes(control.get(2)))
                            return true;
                    } else if (isLeftParanthes(control.get(1)))
                        return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    /***************************************************************************************/


    //1 -> int - expi   0 -> int - values   3-> id - expi   4-> id - ids
    private boolean isReduce(ArrayList<Token> control, int sign) {
        return (isEXPLISTI(control) || isEXPB(control) || isEXPI(control, sign)
                || isVALUES(control, sign) || isLISTVALUE(control) || isIDS(control, sign) || isIDLIST(control));
    }


    private boolean reduce(ArrayList<Token> control, Stack<Token> stackTemp, int sign, Stack<String> parseTree) {
        //   System.out.println("Reduce gelen " + control + "sign " + sign);
        boolean returnV = false;
        if (isEXPI(control, sign)) {
            transfer(stackTemp, parseTree); // pop yapmadan transfer ediyoruz zaten sonrasında reduce if'i icinde transfer ediyor
            stackTemp.pop();
            EXPI exp = new EXPI(control);
            stackTemp.push(exp);
            returnV = true;
        } else if (isEXPB(control)) {
            transfer(stackTemp, parseTree);
            stackTemp.pop();
            stackTemp.push(new EXPB(control));
            returnV = true;
        } else if (isIDS(control, sign)) {
            stackTemp.pop();
            transfer(stackTemp, parseTree);
            stackTemp.push(new IDS(control));
            returnV = true;
        } else if (isIDLIST(control)) {
            stackTemp.pop();
            transfer(stackTemp, parseTree);
            stackTemp.push(new IDLIST(control));
            returnV = true;
        } else if (isVALUES(control, sign)) {
            transfer(stackTemp, parseTree);
            stackTemp.pop();
            stackTemp.push(new VALUES(control));
            returnV = true;
        } else if (isLISTVALUE(control)) {
            transfer(stackTemp, parseTree);
            stackTemp.pop();
            stackTemp.push(new LISTVALUE(control));
            returnV = true;
        }if (isEXPLISTI(control)) {
            transfer(stackTemp, parseTree);
            stackTemp.pop();
            stackTemp.push(new EXPLISTI(control));
            returnV = true;
        }
        return returnV;


    }

    private boolean checkControl(Stack<Token> stackTemp, int sign, Stack<String> parseTree) {
        // System.out.println(stack);
        int count = 0;
        while (count <= stackTemp.size()) {
            ArrayList<Token> control = new ArrayList<Token>();
            int i = 0;
            do {
                control.add(stackTemp.peek());
                //System.out.println("////////////////////////////"+control);
                if (isReduce(control, sign)) {
                    reduce(control, stackTemp, sign, parseTree);
                    transfer(stackTemp, parseTree);
                    //System.out.println(stack);
                    return true;
                } else stackTemp.pop();
                ++i;
            } while (i < count && !stackTemp.isEmpty());

            for (int j = control.size() - 1; j >= 0; --j)
                stackTemp.push(control.get(j));

            ++count;
        }

        return false;
    }


    public boolean parseRec(Stack<Token> stackTemp, int sign, Integer Icurrent, Stack<String> parseTree)
            throws ParserExceptions {

        IdentifierList identifierList = IdentifierList.getInstance();
        TokenList tokenList = TokenList.getInstance();
        if (Icurrent == TokenList.getInstance().getAllTokens().size()) return false;
        while (Icurrent < TokenList.getInstance().getAllTokens().size()) {

            if (stackTemp.isEmpty()) {
                Icurrent = shift(Icurrent, stackTemp);
                //  System.out.println("--*current " + Icurrent);
            } else {
                boolean shiftSign = true;
                do {
                    boolean isParse = false;
                    if (checkControl(stackTemp, sign, parseTree) || Icurrent >= tokenList.getAllTokens().size()) {
                        shiftSign = false;
                    } else {
                        Icurrent = shift(Icurrent, stackTemp);
                        ///hizlandirmaya bak!!!!
                        if (!stackTemp.isEmpty() &&
                                (stackTemp.peek().getTokenName().equals("VALUE_INT")
                                        || stackTemp.peek().getTokenName().equals("IDENTIFIER"))) {
                            //System.out.println("STACK PEEK "+stackTemp.peek());
                            Stack<Token> stack1 = new Stack<Token>();
                            stack1.addAll(stackTemp);
                            Stack<Token> stack2 = new Stack<Token>();
                            stack2.addAll(stackTemp);

                            Stack<String> parseTree1 = new Stack<String>();
                            parseTree1.addAll(parseTree);
                            Stack<String> parseTree2 = new Stack<String>();
                            parseTree2.addAll(parseTree);

                            Integer current1 = Icurrent;
                            Integer current2 = Icurrent;
                            if (!stackTemp.isEmpty() && stackTemp.peek().getTokenName().equals("VALUE_INT")) {
                                isParse = parseRec(stack1, 1, current1, parseTree1); // expi int 1
                                if (isParse) {
                                    stackTemp.removeAll(stackTemp);
                                    parseTree.removeAll(parseTree);
                                    stackTemp.addAll(stack1);
                                    parseTree.addAll(parseTree1);
                                    Icurrent = current1;
                                    return true;
                                } else {
                                    isParse = parseRec(stack2, 0, current2, parseTree2); // values sign 0
                                    if (isParse) {
                                        stackTemp.removeAll(stackTemp);
                                        parseTree.removeAll(parseTree);
                                        stackTemp.addAll(stack2);
                                        parseTree.addAll(parseTree2);
                                        Icurrent = current2;
                                        return true;

                                    }

                                }

                            } else if (!stackTemp.isEmpty() && stackTemp.peek().getTokenName().equals("IDENTIFIER")) {


                                isParse = parseRec(stack1, 3, current1, parseTree1); // expi id
                                if (isParse) {
                                    stackTemp.removeAll(stackTemp);
                                    parseTree.removeAll(parseTree);
                                    stackTemp.addAll(stack1);
                                    parseTree.addAll(parseTree1);
                                    Icurrent = current1;
                                    return true;

                                } else {
                                    isParse = parseRec(stack2, 4, current2, parseTree2); // ids
                                    if (isParse) {
                                        stackTemp.removeAll(stackTemp);
                                        parseTree.removeAll(parseTree);
                                        stackTemp.addAll(stack2);
                                        parseTree.addAll(parseTree2);
                                        Icurrent = current2;
                                        return true;

                                    }


                                }

                            }
                        }
                    }
                } while (shiftSign);
            }

        }

        if (isINPUT(stackTemp, parseTree)) {
            if (isSTART(stackTemp, parseTree)) {
                printParseTree(parseTree);
                return true;
            } else return false;
        } else
            return false;


    }


    // Parses the lexer result and prints *ONLY* the parsing result.

    public void parse() {
        try {
            parseRec(new Stack<Token>(), 2, new Integer(0), new Stack<String>()); // 2 bos int veya id ise iceride ayarlanir
        } catch (Exception a) {
            return;
        }
    }

    public class ParserExceptions extends Exception {
        public ParserExceptions(String error) {
            super(error);
        }
    }

}
