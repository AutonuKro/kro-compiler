Just playing and trying to create my own simple declarative language KroLang.

To build the project

```shell
 mvn package appassembler:assemble
```

Run your program

```shell
./target/build/bin/kro hello.kro
```
### Context free grammar for the language, with precedence lowest to highest
```markdown
program                     ->      declaration* EOF
declaration                 ->      variableDeclaration | statement
variableDeclaration         ->      "Let" IDENTIFIER ":" type ( "=" expression ) ? ";"
type                        ->      baseType listSpecifier?
baseType                    ->      "Num" | "Str" | "Bool"
listSpecifier               ->      "[" NUM_LIT? "]"
statement                   ->      exprStmt | printStmt | codeBlock | ifBlock
ifBlock                     ->      "If" expression "->" statement elifBlock* elseBlock?
elifBlock                   ->      "Elif" expression "->" statement
elseBlock                   ->      "Else" -> statement
codeBlock                   ->      "{" declaration* "}"
exprStmt                    ->      expression ";"
printStmt                   ->      "Print" "->" expression ";"
expression                  ->      assignment | equality
assignment                  ->      IDENTIFIER "=" assignment
equality                    ->      comparison ( ( "!=" | "==" ) comparison )*
comparison                  ->      term ( ( "<" | ">" | "<=" | ">=" ) term )*
term                        ->      factor ( ("+" | "-" ) factor )*
factor                      ->      unary ( ("/" | "*") unary )*
unary                       ->      ( "!" | "-" ) unary | primary
primary                     ->      "(" expression ")" | NUM_LIT | STR_LIT | "True" | "False" | "Nil" | IDENTIFIER
```
