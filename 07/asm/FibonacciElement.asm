@256
D=A
@SP
M=D

@null.Sys.init$ret.1
D=A

@SP
A=M
M=D

@SP
M=M+1

@LCL
D=M

@SP
A=M
M=D

@SP
M=M+1

@ARG
D=M

@SP
A=M
M=D

@SP
M=M+1

@THIS
D=M

@SP
A=M
M=D

@SP
M=M+1

@THAT
D=M

@SP
A=M
M=D

@SP
M=M+1

@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D

@SP
D=M
@LCL
M=D

@Sys.init
0;JMP

(null.Sys.init$ret.1)
//function Main.fibonacci 0
(Main.fibonacci)

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

//push constant 2
@2
D=A
@SP
A=M
M=D
@SP
M=M+1

//lt
@SP
M=M-1
A=M

D=M

@SP
M=M-1
A=M

D=M-D

@TRUE0
D;JLT

D=0
@NEXT0
0;JMP

(TRUE0)
D=-1

(NEXT0)
@SP
A=M
M=D

@SP
M=M+1

//if-goto N_LT_2
@SP
M=M-1
A=M
D=M

@Main.Main.fibonacci$N_LT_2
D;JNE

//goto N_GE_2
@Main.Main.fibonacci$N_GE_2
0;JMP

//label N_LT_2
(Main.Main.fibonacci$N_LT_2)
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
D=M+1
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


//label N_GE_2
(Main.Main.fibonacci$N_GE_2)
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

//push constant 2
@2
D=A
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

//call Main.fibonacci 1
@Main.Main.fibonacci$ret.1
D=A

@SP
A=M
M=D

@SP
M=M+1

@LCL
D=M

@SP
A=M
M=D

@SP
M=M+1

@ARG
D=M

@SP
A=M
M=D

@SP
M=M+1

@THIS
D=M

@SP
A=M
M=D

@SP
M=M+1

@THAT
D=M

@SP
A=M
M=D

@SP
M=M+1

@SP
D=M
@5
D=D-A
@1
D=D-A
@ARG
M=D

@SP
D=M
@LCL
M=D

@Main.fibonacci
0;JMP

(Main.Main.fibonacci$ret.1)
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

//push constant 1
@1
D=A
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

//call Main.fibonacci 1
@Main.Main.fibonacci$ret.2
D=A

@SP
A=M
M=D

@SP
M=M+1

@LCL
D=M

@SP
A=M
M=D

@SP
M=M+1

@ARG
D=M

@SP
A=M
M=D

@SP
M=M+1

@THIS
D=M

@SP
A=M
M=D

@SP
M=M+1

@THAT
D=M

@SP
A=M
M=D

@SP
M=M+1

@SP
D=M
@5
D=D-A
@1
D=D-A
@ARG
M=D

@SP
D=M
@LCL
M=D

@Main.fibonacci
0;JMP

(Main.Main.fibonacci$ret.2)
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
D=M+1
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


//function Sys.init 0
(Sys.init)

//push constant 4
@4
D=A
@SP
A=M
M=D
@SP
M=M+1

//call Main.fibonacci 1
@Sys.Main.fibonacci$ret.1
D=A

@SP
A=M
M=D

@SP
M=M+1

@LCL
D=M

@SP
A=M
M=D

@SP
M=M+1

@ARG
D=M

@SP
A=M
M=D

@SP
M=M+1

@THIS
D=M

@SP
A=M
M=D

@SP
M=M+1

@THAT
D=M

@SP
A=M
M=D

@SP
M=M+1

@SP
D=M
@5
D=D-A
@1
D=D-A
@ARG
M=D

@SP
D=M
@LCL
M=D

@Main.fibonacci
0;JMP

(Sys.Main.fibonacci$ret.1)
//label END
(Sys.Sys.init$END)
//goto END
@Sys.Sys.init$END
0;JMP

