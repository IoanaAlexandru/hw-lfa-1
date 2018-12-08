import java.util.Deque;
import java.util.LinkedList;

interface Node {
    String show();

    Node interpret();
}

interface InstructionNode extends Node {
    void openBlock();

    void closeBlock();

    void setCondition(Node condition);

    void addStmt(Node stmt);

    boolean done();
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
            return ((Symbol) obj).symbol.equals(symbol);
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
    private Node prog;

    public MainNode(Node prog) {
        this.prog = prog;
    }

    @Override
    public String show() {
        return "<MainNode>\n" + Parser.addNewline(prog.show());
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
    private Deque<Node> stmts = new LinkedList<>();

    void pushStmt(Node stmt) {
        stmts.addFirst(stmt);
    }

    Node popStmt() {
        return stmts.removeFirst();
    }

    void clear() {
        stmts.clear();
    }

    Node getStmt() {
        if (stmts.isEmpty())
            return null;
        if (stmts.size() == 1)
            return stmts.getFirst();
        return buildSequence();
    }

    private Node buildSequence() {
        Node node = stmts.removeLast();
        if (stmts.isEmpty())
            return node;
        return new SequenceNode(node, buildSequence());
    }

    @Override
    public String show() {
        Node stmt = getStmt();
        String blockNode = "<BlockNode> {}\n";
        return stmt == null ? blockNode : blockNode + Parser.addNewline(stmt.show());
    }

    @Override
    public Node interpret() {
        return getStmt().interpret();
    }
}

class IfNode implements InstructionNode {
    private Node condition;
    private BlockNode ifBlock;
    private BlockNode elseBlock;
    private boolean ifBlockOpen = false;
    private boolean elseBlockOpen = false;
    private boolean ifDone = false;
    private boolean elseDone = false;

    public IfNode() {
        condition = null;
        ifBlock = new BlockNode();
        elseBlock = new BlockNode();
    }

    public IfNode(Node condition) {
        this.condition = condition;
        ifBlock = new BlockNode();
        elseBlock = new BlockNode();
    }

    public IfNode(Node condition, BlockNode ifBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void openBlock() {
        if (done()) return;

        if (ifDone)
            elseBlockOpen = true;
        else
            ifBlockOpen = true;
    }

    @Override
    public void closeBlock() {
        if (done()) return;

        if (ifBlockOpen) {
            ifBlockOpen = false;
            ifDone = true;
        } else if (elseBlockOpen) {
            elseBlockOpen = false;
            elseDone = true;
        }
    }

    @Override
    public void setCondition(Node condition) {
        this.condition = condition;
    }

    @Override
    public void addStmt(Node stmt) {
        if (done()) return;

        if (ifBlockOpen)
            ifBlock.pushStmt(stmt);
        else if (elseBlockOpen)
            elseBlock.pushStmt(stmt);
    }

    @Override
    public boolean done() {
        return ifDone && elseDone;
    }

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

class WhileNode implements InstructionNode {
    private Node condition;
    private BlockNode block;
    private boolean blockOpen = false;
    private boolean done = false;

    public WhileNode() {
        condition = null;
        block = new BlockNode();
    }

    public WhileNode(Node condition) {
        this.condition = condition;
        block = new BlockNode();
    }

    public WhileNode(Node condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void openBlock() {
        if (done()) return;

        blockOpen = true;
    }

    @Override
    public void closeBlock() {
        if (done()) return;

        blockOpen = false;
        done = true;
    }

    @Override
    public void setCondition(Node condition) {
        this.condition = condition;
    }

    @Override
    public void addStmt(Node stmt) {
        if (done()) return;

        if (blockOpen)
            block.pushStmt(stmt);
    }

    @Override
    public boolean done() {
        return done;
    }

    @Override
    public String show() {
        String print = condition.show() + block.show();

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
        if (stmt1 == null || stmt2 == null)
            return "<SequenceNode>\n";

        String print = stmt1.show() + stmt2.show();
        return "<SequenceNode>\n" + Parser.addNewline(print);
    }

    @Override
    public Node interpret() {
        return null;  // TODO
    }
}
