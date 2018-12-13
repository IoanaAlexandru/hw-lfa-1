import java.util.*;

%%
 
%class ImpLexer
%line
%int
%{
    private LinkedList<VarNode> orderedVars = new LinkedList<>();
    private TreeMap<VarNode, Integer> vars = new TreeMap<>();
    private boolean varList = false;
    private BlockNode mainBlock = new BlockNode();
    private LinkedList<InstructionNode> openInstructions = new LinkedList<>();
    private LinkedList<Node> list = new LinkedList<>();
    private int line = 1;
    private int unassignedVarLine = -1;

    public MainNode getMain() {
        return new MainNode(line, mainBlock.getStmt());
    }

    // Get list of vars as originally initialised
    public LinkedList<VarNode> getOrderedVars() {
        return orderedVars;
    }

    public int getUnassignedVarLine() { return unassignedVarLine; }

    public TreeMap<VarNode, Integer> interpret() throws ImpException {
        mainBlock.getStmt().interpret(vars);
        return vars;
    }

    private Node buildStmt(List<Node> list) {
        // Mind operator precedence

        // Brackets
        Symbol openSym = new Symbol("(");
        Symbol closeSym = new Symbol(")");
        int close = list.indexOf(closeSym), open = -1;
        if (close != -1)
            open = list.subList(0, close).lastIndexOf(openSym);
        while (open != -1 && close != -1) {
            Node bracketNode = new BracketNode(line, buildStmt(list.subList(open + 1, close)));

            // Remove leftover brackets
            list.remove(open);
            list.remove(open);

            list.add(open, bracketNode);
            close = list.indexOf(closeSym);
            if (close != -1)
                open = list.subList(0, close).lastIndexOf(openSym);
        }

        // Not
        Symbol notSym = new Symbol("!");
        int not = list.indexOf(notSym);
        while (not != -1) {
            Node notNode = new NotNode(line, list.get(not + 1));
            list.subList(not, not + 2).clear();
            list.add(not, notNode);
            not = list.indexOf(notSym);
        }

        // Div
        Symbol divSym = new Symbol("/");
        int div = list.indexOf(divSym);
        while (div != -1) {
            Node divNode = new DivNode(line, list.get(div - 1), list.get(div + 1));
            list.subList(div - 1, div + 2).clear();
            list.add(div - 1, divNode);
            div = list.indexOf(divSym);
        }

        // Plus
        Symbol plusSym = new Symbol("+");
        int plus = list.indexOf(plusSym);
        while (plus != -1) {
            Node plusNode = new PlusNode(line, list.get(plus - 1), list.get(plus + 1));
            list.subList(plus - 1, plus + 2).clear();
            list.add(plus - 1, plusNode);
            plus = list.indexOf(plusSym);
        }


        // Greater
        Symbol greaterSym = new Symbol(">");
        int greater = list.indexOf(greaterSym);
        while (greater != -1) {
            Node greaterNode = new GreaterNode(line, list.get(greater - 1), list.get(greater + 1));
            list.subList(greater - 1, greater + 2).clear();
            list.add(greater - 1, greaterNode);
            greater = list.indexOf(greaterSym);
        }

        // And
        Symbol andSym = new Symbol("&&");
        int and = list.indexOf(andSym);
        while (and != -1) {
            Node andNode = new AndNode(line, list.get(and - 1), list.get(and + 1));
            list.subList(and - 1, and + 2).clear();
            list.add(and - 1, andNode);
            and = list.indexOf(andSym);
        }

        // Assignment
        Symbol assignSym = new Symbol("=");
        int assign = list.indexOf(assignSym);
        while (assign != -1) {
            Node assignNode = new AssignmentNode(line, list.get(assign - 1), list.get(assign + 1));
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

\n         { line++; }
{Int}      { varList = true; }
{AVal}     { list.addLast(new IntNode(line, yytext())); }
{BVal}     { list.addLast(new BoolNode(line, yytext())); }
{Plus}     { list.addLast(new Symbol("+")); }
{Div}      { list.addLast(new Symbol("/")); }
{Eq}       { list.addLast(new Symbol("=")); }
{If}       { openInstructions.addFirst(new IfNode(line)); }
{Else}     {}
{While}    { openInstructions.addFirst(new WhileNode(line)); }
{OpenPar}  { list.addLast(new Symbol("(")); }
{ClosePar} { list.addLast(new Symbol(")")); }
{And}      { list.addLast(new Symbol("&&")); }
{Greater}  { list.addLast(new Symbol(">")); }
{Not}      { list.addLast(new Symbol("!")); }
{OpenBr}   {
             InstructionNode instruction = openInstructions.peekFirst();
             if (!list.isEmpty())
                 instruction.setCondition(buildStmt(list));
             instruction.openBlock();
           }
{CloseBr}  {
             InstructionNode instruction = openInstructions.peekFirst();
             instruction.closeBlock();
             if (instruction.done()) {
                 openInstructions.removeFirst();
                 if (openInstructions.isEmpty())
                     mainBlock.pushStmt(instruction);
                 else
                     openInstructions.peekFirst().addStmt(instruction);
             }
           }
{Semi}     {
             if (varList) {
                 varList = false;
             } else {
                 if (openInstructions.isEmpty())
                     mainBlock.pushStmt(buildStmt(list));
                 else
                     openInstructions.peekFirst().addStmt(buildStmt(list));
             }
           }
{Var}      {
             VarNode var = new VarNode(line, yytext());
             if (varList) {
                 vars.put(var, null);
                 orderedVars.addLast(var);
             } else {
                 list.addLast(var);
             }
             if (!vars.containsKey(var))
                 unassignedVarLine = line;
           }
.          {}


