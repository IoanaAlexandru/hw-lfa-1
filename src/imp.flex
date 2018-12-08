import java.util.*;

%%
 
%class ImpLexer
%line
%int
%{
    private TreeSet<VarNode> vars = new TreeSet<>();
    private Block mainBlock = new Block();

    Node cond;
    private boolean varList = false, ifCond = false, whileCond = false, elseCond = true, getBlock1 = false, getBlock2 = false;
    private Block block1 = new Block(), block2 = new Block();
    private LinkedList<Node> list = new LinkedList<>();

    public MainNode getMain() {
        return new MainNode(mainBlock);
    }

    private Node buildStmt(List<Node> list) {
        // Mind operator precedence

        // Brackets
        Symbol openSym = new Symbol("(");
        Symbol closeSym = new Symbol(")");
        int open = list.lastIndexOf(openSym);
        while (open != -1) {
            int close = list.indexOf(closeSym);
            Node bracketNode = new BracketNode(buildStmt(list.subList(open + 1, close)));

            // Remove leftover brackets
            list.remove(open);
            list.remove(open);

            list.add(open, bracketNode);
            open = list.indexOf(openSym);
        }

        // Not
        Symbol notSym = new Symbol("!");
        int not = list.indexOf(notSym);
        while (not != -1) {
            Node notNode = new NotNode(list.get(not + 1));
            list.subList(not, not + 2).clear();
            list.add(not, notNode);
            not = list.indexOf(notSym);
        }

        // Div
        Symbol divSym = new Symbol("/");
        int div = list.indexOf(divSym);
        while (div != -1) {
            Node divNode = new DivNode(list.get(div - 1), list.get(div + 1));
            list.subList(div - 1, div + 2).clear();
            list.add(div - 1, divNode);
            div = list.indexOf(divSym);
        }

        // Plus
        Symbol plusSym = new Symbol("+");
        int plus = list.indexOf(plusSym);
        while (plus != -1) {
            Node plusNode = new PlusNode(list.get(plus - 1), list.get(plus + 1));
            list.subList(plus - 1, plus + 2).clear();
            list.add(plus - 1, plusNode);
            plus = list.indexOf(plusSym);
        }


        // Greater
        Symbol greaterSym = new Symbol(">");
        int greater = list.indexOf(greaterSym);
        while (greater != -1) {
            Node greaterNode = new GreaterNode(list.get(greater - 1), list.get(greater + 1));
            list.subList(greater - 1, greater + 2).clear();
            list.add(greater - 1, greaterNode);
            greater = list.indexOf(greaterSym);
        }

        // And
        Symbol andSym = new Symbol("&&");
        int and = list.indexOf(andSym);
        while (and != -1) {
            Node andNode = new AndNode(list.get(and - 1), list.get(and + 1));
            list.subList(and - 1, and + 2).clear();
            list.add(and - 1, andNode);
            and = list.indexOf(andSym);
        }

        // Assignment
        Symbol assignSym = new Symbol("=");
        int assign = list.indexOf(assignSym);
        while (assign != -1) {
            Node assignNode = new AssignmentNode(list.get(assign - 1), list.get(assign + 1));
            list.subList(assign - 1, assign + 2).clear();
            list.add(assign - 1, assignNode);
            assign = list.indexOf(assignSym);
        }

        assert (list.size() == 1);
        Node stmt = list.get(0);
        list.clear();
        return stmt;
    }
%}

Var = [a-z]+
AVal = [1-9][0-9]* | 0
BVal = "true"|"false"
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
{AVal}     { list.addLast(new IntNode(yytext())); }
{BVal}     { list.addLast(new BoolNode(yytext())); }
{Plus}     { list.addLast(new Symbol("+")); }
{Div}      { list.addLast(new Symbol("/")); }
{Eq}       { list.addLast(new Symbol("=")); }
{If}       { ifCond = true; }
{Else}     {
             ifCond = false;
             elseCond = true;
           }
{While}    { whileCond = true; }
{OpenPar}  { list.addLast(new Symbol("(")); }
{ClosePar} { list.addLast(new Symbol(")")); }
{And}      { list.addLast(new Symbol("&&")); }
{Greater}  { list.addLast(new Symbol(">")); }
{Not}      { list.addLast(new Symbol("!")); }
{OpenBr}   {
             if (ifCond || whileCond) {
                 cond = buildStmt(list);
                 getBlock1 = true;
             }
             if (elseCond)
                 getBlock2 = true;
           }
{CloseBr}  {
             if (getBlock1 && whileCond) {
                mainBlock.pushStmt(new WhileNode(cond, block1.buildNode()));
                whileCond = false;
                block1.clear();
             }
             else if (getBlock2 && elseCond && !ifCond) {
                 mainBlock.pushStmt(new IfNode(cond, block1.buildNode(), block2.buildNode()));
                 ifCond = false;
                 elseCond = false;
                 block1.clear();
                 block2.clear();
             }

             if (getBlock1)
                 getBlock1 = false;
             if (getBlock2)
                 getBlock2 = false;
           }
{Semi}     {
             if (varList) {
                 varList = false;
             } else {
                 if (getBlock1)
                     block1.pushStmt(buildStmt(list));
                 else if (getBlock2)
                     block2.pushStmt(buildStmt(list));
                 else
                    mainBlock.pushStmt(buildStmt(list));
             }
           }
{Var}      {
             VarNode var = new VarNode(yytext());
            if (varList)
                vars.add(var);
            else
                list.addLast(var);
             if (!vars.contains(var))
                 System.out.println("UnassignedVar");
           }
.          {}


