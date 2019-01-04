import java.util.*;

%%
 
%class ImpLexer
%line
%int
%{
    // variables in the order they were initialised in
    private LinkedList<VarNode> orderedVars = new LinkedList<>();
    // variable names associated with their respective values
    private TreeMap<VarNode, Integer> vars = new TreeMap<>();
    // true if parser is currently initialising the varList
    private boolean varList = false;
    // the whole program is essentially a block - the main() block
    private BlockNode mainBlock = new BlockNode();
    // stack of instructions that are currently "open" (still being parsed)
    private LinkedList<InstructionNode> openInstructions = new LinkedList<>();
    // list of Nodes that have to be parsed
    private LinkedList<Node> list = new LinkedList<>();
    // line count
    private int line = 1;
    // if an unassigned variable is found, this integer is set to its line and parsing continues
    private int unassignedVarLine = -1;

    // Get main statement
    public MainNode getMain() {
        return new MainNode(line, mainBlock.getStmt());
    }

    // Get list of vars as originally initialised
    public LinkedList<VarNode> getOrderedVars() {
        return orderedVars;
    }

    // Get first line where an unassigned variable was found; returns -1 if all variables were assigned
    public int getUnassignedVarLine() { return unassignedVarLine; }

    // Interpret main statement and assign values to variables
    public TreeMap<VarNode, Integer> interpret() throws ImpException {
        mainBlock.getStmt().interpret(vars);
        return vars;
    }

    // Build statement from list of Nodes, minding operator precedence
    // () -> ! -> / -> + -> > -> && -> =
    private Node buildStmt(List<Node> list) {
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


