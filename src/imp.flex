import java.util.*;
import java.io.IOException;

%%
 
%class ImpLexer
%line
%int
%{
  	private TreeSet<VarNode> vars = new TreeSet<>();
    private boolean varList = false;
    MainNode main = new MainNode();
    private Deque<Node> deque = new LinkedList<>();

    /*
    Get data from inside the brackets.
    */
    private Deque<Node> getBracketContent(Deque<Node> deque) {
        Deque<Node> content = new LinkedList<>();

        Node curr = deque.pollFirst();
        assert (curr instanceof Symbol);
        String open = ((Symbol)curr).get(), close;
        if (open.equals("(")) {
            close = ")";
        } else {
            assert (open.equals("{"));
            close = "}";
        }

        curr = deque.pollFirst();
        while (!(curr instanceof Symbol) || !((Symbol)curr).get().equals(close)) {
            content.add(curr);
            curr = deque.pollFirst();
        }
        return content;
    }

    private Node buildStmt(Deque<Node> deque) throws IOException{
        Node curr = deque.peek();

        if (curr instanceof Symbol) {
            String symbol = ((Symbol)curr).get();
            switch(symbol) {
                case "!":
                    deque.pollFirst();
                    Node bExpr;
                    Node peek = deque.peek();
                    if (peek instanceof Symbol) {
                      String bracket = ((Symbol)peek).get();
                      if (!bracket.equals("("))
                          throw new IOException("'!' can only be followed by a BVal or '('.");
                      else
                          bExpr = buildStmt(getBracketContent(deque));

                    } else if (peek instanceof BoolNode) {
                        bExpr = deque.pollFirst();
                    } else {
                        throw new IOException("'!' can only be followed by a BVal or '('.");
                    }
                    return new NotNode(bExpr);

                case "(":
                    Node expr = buildStmt(getBracketContent(deque));
                    return new BracketNode(expr);
                case "+":
                case "/":
                case "&&":
                case ">":
                case "=":
                    deque.pollFirst();
                    Node e1 = main.popStmt();
                    Node e2 = buildStmt(deque);
                    switch(symbol){
                        case "+": return new PlusNode(e1, e2);
                        case "/": return new DivNode(e1, e2);
                        case "&&": return new AndNode(e1, e2);
                        case ">": return new GreaterNode(e1, e2);
                        case "=": return new AssignmentNode(e1, e2);
                    }
                default:
                    throw new IOException("Invalid symbol " + symbol + "\n");
            }

        } else {
            deque.pollFirst();
            return curr;
        }
    }
%}

Var = [a-z]+
AVal = [1-9][0-9]* | 0
BVal = "True"|"False"
Plus = "+"
Div = "/"
OpenPar = "("
ClosePar = ")"
And = "&&"
Greater = ">"
Not = "!"
OpenBr = "{"
CloseBr = "}"
Eq = "="
Semi = ";"
If = "if"
Else = "else"
While = "while"
Int = "int"

%%   

\n         {}
{Int}      { varList = true; }
{AVal}     { deque.addLast(new IntNode(yytext())); }
{BVal}     { deque.addLast(new BoolNode(yytext())); }
{Plus}     { deque.addLast(new Symbol("+")); }
{Div}      { deque.addLast(new Symbol("/")); }
{OpenPar}  { deque.addLast(new Symbol("(")); }
{ClosePar} { deque.addLast(new Symbol(")")); }
{And}      { deque.addLast(new Symbol("&&")); }
{Greater}  { deque.addLast(new Symbol(">")); }
{Not}      { deque.addLast(new Symbol("!")); }
{OpenBr}   { deque.addLast(new Symbol("{")); }
{CloseBr}  { deque.addLast(new Symbol("}")); }
{Eq}       { deque.addLast(new Symbol("=")); }
{If}       { deque.addLast(new Symbol("if")); }
{Else}     { deque.addLast(new Symbol("else")); }
{While}    { deque.addLast(new Symbol("while")); }
{Semi}     {
             if (varList) {
                 varList = false;
             } else {
                 while (!deque.isEmpty()) {
                     Node stmt = buildStmt(deque); // removing deque elements creating the statement
                     main.pushStmt(stmt);
                 }
             }
           }
{Var}      {
             VarNode var = new VarNode(yytext());
            if (varList)
                vars.add(var);
            else
                deque.addLast(var);
             if (!vars.contains(var))
                 System.out.println("UnassignedVar");
           }
.          {}


