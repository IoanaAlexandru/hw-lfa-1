import java.util.Deque;
import java.util.LinkedList;

interface Node {
    String show();

    Node interpret();
}

class Symbol implements Node {
    private String symbol;

    public Symbol(String symbol) {
        this.symbol = symbol;
    }

    String get() {
        return symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol)
            return ((Symbol)obj).symbol.equals(symbol);
        return false;
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public String show() {
        return null;
    }

    @Override
    public Node interpret() {
        return null;
    }
}

class MainNode implements Node {
    private Deque<Node> prog = new LinkedList<>();

    void pushStmt(Node stmt) {
        prog.addFirst(stmt);
    }

    Node popStmt() {
        return prog.removeFirst();
    }

    private Node buildSequence() {
        Node node = prog.removeLast();
        if (prog.isEmpty())
            return node;
        return new SequenceNode(node, buildSequence());
    }

    @Override
    public String show() {
        return "<MainNode>\n" + Parser.addNewline(buildSequence().show());
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class IntNode implements Node {
    private int number;

    public IntNode(String number) {
        this.number = Integer.parseInt(number);
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }

    @Override
    public String show() {
        return "<IntNode> " + number + "\n";
    }

    @Override
    public Node interpret() {
        return this;
    }
}

class BoolNode implements Node {
    private boolean bool;

    public BoolNode(String bool) {
        this.bool = bool.equalsIgnoreCase("true");
    }

    @Override
    public String toString() {
        return (bool ? "true" : "false");
    }

    @Override
    public String show() {
        return "<BoolNode> " + (bool ? "true" : "false") + "\n";
    }

    @Override
    public Node interpret() {
        return this;
    }
}

class VarNode implements Node, Comparable {
    private String var;

    public VarNode(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public String show() {
        return "<VariableNode> " + var + "\n";
    }

    @Override
    public Node interpret() {
        return this;
    }

    @Override
    public int compareTo(Object o) {
        return ((VarNode) o).var.compareTo(this.var);
    }
}

class PlusNode implements Node {
    private Node aExpr1;
    private Node aExpr2;

    public PlusNode(Node aExpr1, Node aExpr2) {
        this.aExpr1 = aExpr1;
        this.aExpr2 = aExpr2;
    }

    @Override
    public String show() {
        String print = aExpr1.show() + aExpr2.show();

        return "<PlusNode> +\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class DivNode implements Node {
    private Node aExpr1;
    private Node aExpr2;

    public DivNode(Node aExpr1, Node aExpr2) {
        this.aExpr1 = aExpr1;
        this.aExpr2 = aExpr2;
    }

    @Override
    public String show() {
        String print = aExpr1.show() + aExpr2.show();

        return "<DivNode> /\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class BracketNode implements Node {
    private Node expr;

    public BracketNode(Node expr) {
        this.expr = expr;
    }

    @Override
    public String show() {
        return "<BracketNode> ()\n" + Parser.addNewline(expr.show());
    }

    @Override
    public Node interpret() {
        return expr.interpret();
    }
}

class AndNode implements Node {
    private Node bExpr1;
    private Node bExpr2;

    public AndNode(Node bExpr1, Node bExpr2) {
        this.bExpr1 = bExpr1;
        this.bExpr2 = bExpr2;
    }

    @Override
    public String show() {
        String print = bExpr1.show() + bExpr2.show();

        return "<AndNode> &&\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class GreaterNode implements Node {
    private Node aExpr1;
    private Node aExpr2;

    public GreaterNode(Node aExpr1, Node aExpr2) {
        this.aExpr1 = aExpr1;
        this.aExpr2 = aExpr2;
    }

    @Override
    public String show() {
        String print = aExpr1.show() + aExpr2.show();

        return "<GreaterNode> >\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class NotNode implements Node {
    private Node bExpr;

    public NotNode(Node bExpr) {
        this.bExpr = bExpr;
    }

    @Override
    public String show() {
        return "<NotNode> !\n" + Parser.addNewline(bExpr.show());
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class AssignmentNode implements Node {
    private Node var;
    private Node aExpr;

    public AssignmentNode(Node left, Node right) {
        this.var = left;
        this.aExpr = right;
    }

    @Override
    public String show() {
        String print = var.show() + aExpr.show();

        return "<AssignmentNode> =\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class BlockNode implements Node {
    private Node stmt;

    public BlockNode() {
        stmt = null;
    }

    public BlockNode(Node stmt) {
        this.stmt = stmt;
    }

    @Override
    public String show() {
        String blockNode = "<BlockNode> {}\n";
        return stmt == null ? blockNode : blockNode + Parser.addNewline(stmt.show());
    }

    @Override
    public Node interpret() {
        return stmt.interpret();
    }
}

class IfNode implements Node {
    private Node condition;
    private Node ifBlock;
    private Node elseBlock;

    @Override
    public String show() {
        String print = condition.show() + ifBlock.show() + elseBlock.show();

        return "<IfNode> if\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class WhileNode implements Node {
    private Node contition;
    private Node block;

    @Override
    public String show() {
        String print = contition.show() + block.show();

        return "<WhileNode> while\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}

class SequenceNode implements Node {
    private Node stmt1;
    private Node stmt2;

    SequenceNode(Node stmt1, Node stmt2) {
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
    }

    @Override
    public String show() {
        String print = stmt1.show() + stmt2.show();

        return "<SequenceNode>\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}
