JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        KnowledgeBase.java \
        ParameterChecker.java \
        Parser.java \
        Predicate.java \
		Prover.java \
		Sentence.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class