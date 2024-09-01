Just playing and trying to create my own simple declarative language KroLang.

To build the project

```shell
 mvn package appassembler:assemble
```

Run your program

```shell
./target/build/bin/kro hello.kro
```

<h2>
Grammer
</h2>

Expression      -->  Literals | Unary | Binary | Grouping
<br/>
Literals        -->  NUM_LIT | STR_LIT | "True" | "False" | "Nil"
<br/>
Unary           -->  ("!" | "-") Expression;
<br/>
Binary          -->  Expression Operator Expression
<br/>
Grouping        -->  