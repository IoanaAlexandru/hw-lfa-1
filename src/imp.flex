import java.util.*;
import java.util.Stack;

%%
 
%class ImpLexer
%line
%int
%{
  	private TreeSet<VarNode> vars = new TreeSet<>();
    private boolean varList = false;
    MainNode main = new MainNode();
    private Stack<Node> stack = new Stack<>();

    private Node getNthElementFromStack(int element_number) {
        Stack<Node> temp_stack = new Stack<>();

        if (element_number > stack.size()) {
            return null;
        }

        for (int j = 0; j < element_number; ++j) {
            temp_stack.push(stack.pop());
        }

        Node res = temp_stack.peek();

        for (int j = 0; j < element_number; ++j) {
            stack.push(temp_stack.pop());
        }
        return res;
    }

    private Node buildStmt() {
        Node e1 = getNthElementFromStack(3);
        Symbol op = (Symbol) getNthElementFromStack(2);
        Node e2 = stack.peek();

        if (op.get().equals("+"))
            return new PlusNode(e1, e2);
        if (op.get().equals("/"))
            return new DivNode(e1, e2);
        if (op.get().equals("&&"))
            return new AndNode(e1, e2);
        if (op.get().equals(">"))
            return new GreaterNode(e1, e2);
        if (op.get().equals("="))
            return new AssignmentNode(e1, e2);
        return null;
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
{AVal}     { stack.push(new IntNode(yytext())); }
{BVal}     { stack.push(new BoolNode(yytext())); }
{Plus}     { stack.push(new Symbol("+")); }
{Div}      { stack.push(new Symbol("/")); }
{OpenPar}  { stack.push(new Symbol("(")); }
{ClosePar} { stack.push(new Symbol(")")); }
{And}      { stack.push(new Symbol("&&")); }
{Greater}  { stack.push(new Symbol(">")); }
{Not}      { stack.push(new Symbol("!")); }
{OpenBr}   { stack.push(new Symbol("{")); }
{CloseBr}  { stack.push(new Symbol("}")); }
{Eq}       { stack.push(new Symbol("=")); }
{If}       { stack.push(new Symbol("if")); }
{Else}     { stack.push(new Symbol("else")); }
{While}    { stack.push(new Symbol("while")); }
{Semi}     {
             if (varList) {
                 varList = false;
             } else {
                 Node stmt = buildStmt(); // removing stack elements creating the statament
                 main.addStmt(stmt);
             }
           }
{Var}      {
             VarNode var = new VarNode(yytext());
            if (varList)
                vars.add(var);
            else
                stack.push(var);
             if (!vars.contains(var))
                 System.out.println("UnassignedVar");
           }
.          {}


