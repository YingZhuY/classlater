/*
 * CS:APP Data Lab
 *
 * <Zoey-Zhu>
 *
 * bits.c - Source file with your solutions to the Lab.
 *          This is the file you will hand in to your instructor.
 *
 * WARNING: Do not include the <stdio.h> header; it confuses the dlc
 * compiler. You can still use printf for debugging without including
 * <stdio.h>, although you might get a compiler warning. In general,
 * it's not good practice to ignore compiler warnings, but in this
 * case it's OK.
 */

#if 0
/*
 * Instructions to Students:
 *
 * STEP 1: Read the following instructions carefully.
 */

You will provide your solution to the Data Lab by
editing the collection of functions in this source file.

INTEGER CODING RULES:

  Replace the "return" statement in each function with one
  or more lines of C code that implements the function. Your code
  must conform to the following style:

  int Funct(arg1, arg2, ...) {
      /* brief description of how your implementation works */
      int var1 = Expr1;
      ...
      int varM = ExprM;

      varJ = ExprJ;
      ...
      varN = ExprN;
      return ExprR;
  }

  Each "Expr" is an expression using ONLY the following:
  1. Integer constants 0 through 255 (0xFF), inclusive. You are
      not allowed to use big constants such as 0xffffffff.
  2. Function arguments and local variables (no global variables).
  3. Unary integer operations ! ~
  4. Binary integer operations & ^ | + << >>

  Some of the problems restrict the set of allowed operators even further.
  Each "Expr" may consist of multiple operators. You are not restricted to
  one operator per line.

  You are expressly forbidden to:
  1. Use any control constructs such as if, do, while, for, switch, etc.
  2. Define or use any macros.
  3. Define any additional functions in this file.
  4. Call any functions.
  5. Use any other operations, such as &&, ||, -, or ?:
  6. Use any form of casting.
  7. Use any data type other than int.  This implies that you
     cannot use arrays, structs, or unions.


  You may assume that your machine:
  1. Uses 2s complement, 32-bit representations of integers.
  2. Performs right shifts arithmetically.
  3. Has unpredictable behavior when shifting if the shift amount
     is less than 0 or greater than 31.


EXAMPLES OF ACCEPTABLE CODING STYLE:
  /*
   * pow2plus1 - returns 2^x + 1, where 0 <= x <= 31
   */
  int pow2plus1(int x) {
     /* exploit ability of shifts to compute powers of 2 */
     return (1 << x) + 1;
  }

  /*
   * pow2plus4 - returns 2^x + 4, where 0 <= x <= 31
   */
  int pow2plus4(int x) {
     /* exploit ability of shifts to compute powers of 2 */
     int result = (1 << x);
     result += 4;
     return result;
  }

FLOATING POINT CODING RULES

For the problems that require you to implement floating-point operations,
the coding rules are less strict.  You are allowed to use looping and
conditional control.  You are allowed to use both ints and unsigneds.
You can use arbitrary integer and unsigned constants. You can use any arithmetic,
logical, or comparison operations on int or unsigned data.

You are expressly forbidden to:
  1. Define or use any macros.
  2. Define any additional functions in this file.
  3. Call any functions.
  4. Use any form of casting.
  5. Use any data type other than int or unsigned.  This means that you
     cannot use arrays, structs, or unions.
  6. Use any floating point data types, operations, or constants.


NOTES:
  1. Use the dlc (data lab checker) compiler (described in the handout) to
     check the legality of your solutions.
  2. Each function has a maximum number of operations (integer, logical,
     or comparison) that you are allowed to use for your implementation
     of the function.  The max operator count is checked by dlc.
     Note that assignment ('=') is not counted; you may use as many of
     these as you want without penalty.
  3. Use the btest test harness to check your functions for correctness.
  4. Use the BDD checker to formally verify your functions
  5. The maximum number of ops for each function is given in the
     header comment for each function. If there are any inconsistencies
     between the maximum ops in the writeup and in this file, consider
     this file the authoritative source.

/*
 * STEP 2: Modify the following functions according the coding rules.
 *
 *   IMPORTANT. TO AVOID GRADING SURPRISES:
 *   1. Use the dlc compiler to check that your solutions conform
 *      to the coding rules.
 *   2. Use the BDD checker to formally verify that your solutions produce
 *      the correct answers.
 */


#endif
//1
/*
 * bitXor - x^y using only ~ and &
 *   Example: bitXor(4, 5) = 1
 *   Legal ops: ~ &
 *   Max ops: 14
 *   Rating: 1
 */
int bitXor(int x, int y) {  //x | y=~((~x) & (~y))
	int var1 = (~x) & y;
	int var2 = x & (~y);    //x^y=((~x) & y) | (x & (~y))=var1 | var2
	int var3 = ~((~var1) & (~var2));
	return var3;
}
/*
 * tmin - return minimum two's complement integer
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 4
 *   Rating: 1
 */
int tmin(void) {    //即0x80000000
	return 1<<31;
}
//2
/*
 * isTmax - returns 1 if x is the maximum, two's complement number,
 *     and 0 otherwise
 *   Legal ops: ! ~ & ^ | +
 *   Max ops: 10
 *   Rating: 1
 */
int isTmax(int x) {
	int var1=1<<31; //按位取反即Tmax=~var1
	return !(~(((~var1) & x) ^ (var1 & (~x)))); //判断两二进制数是否相等：将两个二进制数分别按位相与以及取反相与，再做异或结果全为1，两数即相等
}
/*
 * allOddBits - return 1 if all odd-numbered bits in word set to 1
 *   where bits are numbered from 0 (least significant) to 31 (most significant)
 *   Examples allOddBits(0xFFFFFFFD) = 0, allOddBits(0xAAAAAAAA) = 1
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 12
 *   Rating: 2
 */
int allOddBits(int x) {
	int var1=0xAA & x;
	int var2=0xAA & (x>>8);
	int var3=0xAA & (x>>16);
	int var4=0xAA & (x>>24);
	int var5=(var1 ^ 0xAA) + (var2 ^ 0xAA) + (var3 ^ 0xAA) + (var4 ^ 0xAA);
	return !var5;
}
/*
 * negate - return -x
 *   Example: negate(1) = -1.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 5
 *   Rating: 2
 */
int negate(int x) {
    return ~x+1;
}
//3
/*
 * isAsciiDigit - return 1 if 0x30 <= x <= 0x39 (ASCII codes for characters '0' to '9')
 *   Example: isAsciiDigit(0x35) = 1.
 *            isAsciiDigit(0x3a) = 0.
 *            isAsciiDigit(0x05) = 0.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 15
 *   Rating: 3
 */
int isAsciiDigit(int x) { //48~57
    return (0x1F<<24>>(x>>1)) &!(x>>6); //右移24-28 & 保证高位都是0
}
/*
 * conditional - same as x ? y : z
 *   Example: conditional(2,4,5) = 4
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 16
 *   Rating: 3
 */
int conditional(int x, int y, int z) {
    int var=((x | (~x+1))>>31); //当x全0时，var全0；当x不全零时，x与-x中总有一个为负数，完成移位后得到全1
    return (y & var) + (z & (~var));
}
/*
 * isLessOrEqual - if x <= y  then return 1, else return 0
 *   Example: isLessOrEqual(4,5) = 1.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 24
 *   Rating: 3
 */
int isLessOrEqual(int x, int y) {
    //ERROR: Test isLessOrEqual(-2147483648[0x80000000],2147483647[0x7fffffff]) failed...
    //要考虑最高位符号位，上边ERROR符号位参与计算做差结果为1，正数，产生错误
    int x_sign=(x>>31) & 1;
    int y_sign=(y>>31) & 1;
    int var1=x+(~y+1);
    int var1_sign=(var1>>31)&1;
    //return var1_sign | !((x & x)+(y & y));
    return (x_sign & !y_sign) | ((!(x_sign^y_sign)) & var1_sign) | (!var1); //异号 | 同号 | 相等
}
//4
/*
 * logicalNeg - implement the ! operator, using all of
 *              the legal operators except !
 *   Examples: logicalNeg(3) = 0, logicalNeg(0) = 1
 *   Legal ops: ~ & ^ | + << >>
 *   Max ops: 12
 *   Rating: 4
 */
int logicalNeg(int x) { //x不为0时，x和-x符号总有一个为1
    int val1=(~((~x+1)>>31))&1;
    int val2=(~(x>>31))&1;
    return val1 & val2;
}
/* howManyBits - return the minimum number of bits required to represent x in
 *             two's complement
 *  Examples: howManyBits(12) = 5
 *            howManyBits(298) = 10
 *            howManyBits(-5) = 4
 *            howManyBits(0)  = 1
 *            howManyBits(-1) = 1
 *            howManyBits(0x80000000) = 32
 *  Legal ops: ! ~ & ^ | + << >>
 *  Max ops: 90
 *  Rating: 4
 */
int howManyBits(int x) {
    int var1;
    x=x^(x<<1);
    var1=(!(x>>16))<<4; //16
    var1^=24;
    var1^=(!(x>>var1))<<3;  //8
    var1^=4;
    var1^=(!(x>>var1))<<2;  //4
    var1+=( (~0x5B)>>((x>>var1) & 30) ) &3;
    return var1+1;
}
//float
/*
 * floatScale2 - Return bit-level equivalent of expression 2*f for
 *   floating point argument f.
 *   Both the argument and result are passed as unsigned int's, but
 *   they are to be interpreted as the bit-level representation of
 *   single-precision floating point values.
 *   When argument is NaN, return argument
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. also if, while
 *   Max ops: 30
 *   Rating: 4
 */
unsigned floatScale2(unsigned uf) {
    //Test floatScale2(8388608[0x800000]) failed...
    //...Gives 4194304[0x400000]. Should be 16777216[0x1000000]
    //Floating point value -0
    //Bit Representation 0x80000000, sign = 1, exponent = 0x00, fraction = 0x000000
    //Denormalized.  -0.0000000000 X 2^(-126)
    if(uf==0)   return uf;  //2*0=0
    if((uf==(1<<31)) || (((uf>>23) & 0xFF)==0xFF)) return uf;  //-0或正负无穷时
    if(((uf>>23) &0xFF)==0x00)  //处理正负非规格化数，非规格化数对应的阶码全0
        return ((uf & 0x007FFFFF)<<1) | (uf & 0xFF800000);
    return uf+0x00800000;  //规格化数，阶码加1
}
/*
 * floatFloat2Int - Return bit-level equivalent of expression (int) f
 *   for floating point argument f.
 *   Argument is passed as unsigned int, but
 *   it is to be interpreted as the bit-level representation of a
 *   single-precision floating point value.
 *   Anything out of range (including NaN and infinity) should return
 *   0x80000000u.
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. also if, while
 *   Max ops: 30
 *   Rating: 4
 */
int floatFloat2Int(unsigned uf) {
    //ERROR: Test floatScale2(8388608[0x800000]) failed...
    //...Gives 4194304[0x400000]. Should be 16777216[0x1000000]
    //ERROR: Test floatFloat2Int(1065353216[0x3f800000]) failed...
    //...Gives 0[0x0]. Should be 1[0x1]
    int S=(uf & 0x80000000)>>31;    //符号部分
    int E=((uf & 0x7F800000)>>23)-127;  //指数部分
    int M=(uf & 0x007FFFFF)+0x00800000;  //尾数部分，算上隐藏的一位，尾数部分一共24 位
    //M 记录的是小数部分(1.***)<<23
    int var1=0;
    if(E<0 || (E+127)==0) return 0;   //对应的整数绝对值小于1
    else if(E>=31)  return 0x80000000;  //溢出
        else if(E>23)  var1=M<<(E-23);
            else if(E<=23)   var1=M>>(23-E);
    if(S)   var1=~var1+1;
    return var1;
}
/*
 * floatPower2 - Return bit-level equivalent of the expression 2.0^x
 *   (2.0 raised to the power x) for any 32-bit integer x.
 *
 *   The unsigned value that is returned should have the identical bit
 *   representation as the single-precision floating-point number 2.0^x.
 *   If the result is too small to be represented as a denorm, return
 *   0. If too large, return +INF.
 *
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. Also if, while
 *   Max ops: 30
 *   Rating: 4
 */
unsigned floatPower2(int x) {
    if(x<-150)  return 0;
    if(x>=-150 && x<=-127)  //非规格化数
        return 1<<(-x-127);   //由于2的幂都为正
    if(x>=-126 && x<=127)   //规格化数
        return (x+127)<<23;
    if(x>=128)  //结果太大返回+INF
        return 0xFF<<23;
    //Floating point value inf
    //Bit Representation 0x7f800000, sign = 0, exponent = 0xff, fraction = 0x000000
    //+Infinity
    return 0;
}
