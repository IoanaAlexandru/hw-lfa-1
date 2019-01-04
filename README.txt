      _____                                  _ _____ _     _______  __
     |_   _|__ _ __ ___   __ _              | |  ___| |   | ____\ \/ /
       | |/ _ \ '_ ` _ \ / _` |  _____   _  | | |_  | |   |  _|  \  / 
       | |  __/ | | | | | (_| | |_____| | |_| |  _| | |___| |___ /  \ 
       |_|\___|_| |_| |_|\__,_|          \___/|_|   |_____|_____/_/\_\
                                                                  

    Am plecat de la exemplul de pe forum și am creat arborele de clase ce
simulează gramatica, necesar pentru parsarea limbajului IMP:

                                      +----------+
                                 +----> PlusNode |
                                 |    +----------+
                                 |    +---------+
                                 +----> DivNode |
                 +-------+       |    +---------+
            +----> ANode +-------+    +---------+
            |    +---+---+       +----> IntNode |
            |        |           |    +---------+
            |        |           |    +---------+
            |        |           +----> VarNode |
            |        |                +---------+
            |        |                +-------------+
            |        +----------------> BracketNode |
            |        |                +-------------+
            |        |                +---------+
            |        |           +----> AndNode |
            |        |           |    +---------+
            |        |           |    +-------------+
            |    +---+---+       +----> GreaterNode |
            +----> BNode +-------+    +-------------+
            |    +-------+       |    +---------+
+------+    |                    +----> NotNode |
| Node +----+                    |    +---------+
+------+    |                    |    +----------+
            |                    +----> BoolNode |
            |                         +----------+
            |                         +----------------+
            |                    +----> AssignmentNode |
            |                    |    +----------------+
            |                    |    +--------------+
            |                    +----> SequenceNode |           +--------+
            |    +----------+    |    +--------------+      +----> IfNode |
            +----> StmtNode +----+    +-----------------+   |    +--------+
            |    +----------+    +----> InstructionNode +---+    +-----------+
            |                    |    +-----------------+   +----> WhileNode |
            |                    |    +----------+               +-----------+
            |                    +----> MainNode |
            |                    |    +----------+
            |                    |    +-----------+
            |                    +----> BlockNode |
            |                         +-----------+
            |    +--------+
            +----> Symbol |
                 +--------+

    Fiecare nod terminal corespunde unei sintaxe specifice din limbaj și va fi
printat - show() - in fișierul de ieșire, cu excepția Symbol, care este un nod
primitiv auxiliar folosit pentru parsare. Printarea se face recursiv, începând
de la blocul main către cea mai mică instrucțiune singulară. Similar se reali-
zează și interpretarea.
    Lexerul generat din fișierul jflex returnează arborele sintactic generat
prin apel către funcția getMain(). Mai multe detalii de implementare se găsesc
în comentarii.

============================ ALEXANDRU Ioana | 334CB ===========================