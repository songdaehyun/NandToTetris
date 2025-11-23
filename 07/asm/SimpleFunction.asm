//function SimpleFunction.test 2
(SimpleFunction.test)
@SP
A=M
M=0

@SP
M=M+1

@SP
A=M
M=0

@SP
M=M+1


//push local 0
@0
D=A
@LCL
A=D+M

D=M
@SP
A=M
M=D

@SP
M=M+1

//push local 1
@1
D=A
@LCL
A=D+M

D=M
@SP
A=M
M=D

@SP
M=M+1

//add
@SP
M=M-1
A=M

D=M

@SP
M=M-1
A=M

D=D+M
@SP
A=M
M=D

@SP
M=M+1

//not
@SP
M=M-1
A=M

D=M
D=!D
@SP
A=M
M=D

@SP
M=M+1

//push argument 0
@0
D=A
@ARG
A=D+M

D=M
@SP
A=M
M=D

@SP
M=M+1

//add
@SP
M=M-1
A=M

D=M

@SP
M=M-1
A=M

D=D+M
@SP
A=M
M=D

@SP
M=M+1

//push argument 1
@1
D=A
@ARG
A=D+M

D=M
@SP
A=M
M=D

@SP
M=M+1

//sub
@SP
M=M-1
A=M

D=M

@SP
M=M-1
A=M

D=M-D
@SP
A=M
M=D

@SP
M=M+1

//return
@LCL
D=M
@R13
M=D

@R13
D=M
@5
D=D-A
A=D
D=M
@R14
M=D

@SP
M=M-1
A=M
D=M
@ARG
A=M
M=D

@ARG
M=M+1
D=M
@SP
M=D

@R13
D=M
@1
A=D-A
D=M
@THAT
M=D

@R13
D=M
@2
A=D-A
D=M
@THIS
M=D

@R13
D=M
@3
A=D-A
D=M
@ARG
M=D

@R13
D=M
@4
A=D-A
D=M
@LCL
M=D

@R14
A=M
0;JMP


