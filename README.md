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

\begin{align*}
    \text{expression} &\rightarrow \text{equality} \\
    \text{equality} &\rightarrow \text{comparison} \ \left(\ \text{"!="} \mid \text{"=="}\ \right) \ \text{comparison} \ \ast \\
    \text{comparison} &\rightarrow \text{term} \ \left(\ \text{"<"} \mid \text{">"} \mid \text{"<="} \mid \text{">="}\ \right) \ \text{term} \ \ast \\
    \text{term} &\rightarrow \text{factor} \ \left(\ \text{"+"} \mid \text{"-"}\ \right) \ \text{factor} \ \ast \\
    \text{factor} &\rightarrow \text{unary} \ \left(\ \text{"/"} \mid \text{"*"}\ \right) \ \text{unary} \ \ast \\
    \text{unary} &\rightarrow \left(\ \text{"!"} \mid \text{"-"}\ \right) \ \text{unary} \mid \text{primary} \\
    \text{primary} &\rightarrow \left(\ \text{"("} \ \text{expression} \ \text{")"} \right) \mid \text{NUM\_LIT} \mid \text{STR\_LIT} \mid \text{"True"} \mid \text{"False"} \mid \text{"Nil"}
\end{align*}
