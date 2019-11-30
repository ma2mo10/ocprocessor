
package gcd

import chisel3._
import chisel3.util._
import Properties._

class Decode extends Module {
  val io = IO(new Bundle() {
    val inst = Input(UInt(XLEN.W))
    val pc = Input(UInt(XLEN.W))
    val result = Input(UInt(XLEN.W))
    val result_w = Input(Bool())
    val result_addr = Input(UInt(REG.W))

    val mem_w = Output(Bool())
    val mem_r = Output(Bool())
    val pc_w = Output(Bool())
    val rf_w = Output(Bool())
    val alith = Output(UInt(2.W))
    val rd_addr = Output(UInt(REG.W))
    val rd = Output(UInt(XLEN.W))
    val rs = Output(UInt(XLEN.W))
    val src1 = Output(UInt(XLEN.W))
    val src2 = Output(UInt(XLEN.W))
  })

  val rf = Module(new RegFile)
  val inst = Wire(new Inst)
  inst := io.inst.asTypeOf(new Inst)

  val rd = Wire(UInt(16.W))
  val rs = Wire(UInt(16.W))

  val src1_sel = Wire(UInt(2.W))
  val src2_sel = Wire(UInt(3.W))

  rd := rf.io.rd_out
  rs := rf.io.rs_out
  rf.io.rd_addr := inst.rd
  rf.io.rs_addr := inst.rs
  rf.io.w_addr := io.result_addr
  rf.io.w_data := io.result
  rf.io.rf_w := io.result_w

  io.src1 := MuxLookup(src1_sel, rd, Array(
    Zero.id -> 0.U,
    RD.id -> rd,
    PC.id -> io.pc
  ))

  io.src2 := MuxLookup(src2_sel, rs, Array(
    Zero.id -> 0.U,
    One.id -> 1.U,
    Disp6.id -> inst.disp6,
    Imm9.id -> Cat(inst.rs, inst.disp6),
    RS.id -> rs
  ))


  def conOp(op: OP): Unit = {
    io.alith := op.alith.id
    io.pc_w := op.pc_w
    io.rf_w := op.rf_w
    io.mem_w := op.mem_w
    io.mem_r := op.mem_r
    src1_sel := op.rs1.id
    src2_sel := op.rs2.id
  }

  when(inst.op === ADD.op) {val op = ADD; conOp(op)}
    .elsewhen(inst.op === SUB.op) {val op = SUB; conOp(op)}
    .elsewhen(inst.op === AND.op) { val op =  AND; conOp(op) }
    .elsewhen(inst.op ===  OR.op) { val op =   OR; conOp(op) }
    .elsewhen(inst.op ===ADDI.op) { val op = ADDI; conOp(op) }
    .elsewhen(inst.op ===SUBI.op) { val op = SUBI; conOp(op) }
    .elsewhen(inst.op ===INCR.op) { val op = INCR; conOp(op) }
    .elsewhen(inst.op ===DECR.op) { val op = DECR; conOp(op) }
    .elsewhen(inst.op === LDI.op) { val op =  LDI; conOp(op) }
    .elsewhen(inst.op ===  LD.op) { val op =   LD; conOp(op) }
    .elsewhen(inst.op ===  ST.op) { val op =   ST; conOp(op) }
    .elsewhen(inst.op === BEQ.op) { val op =  BEQ; conOp(op) }
    .elsewhen(inst.op === BGT.op) { val op =  BGT; conOp(op) }
    .elsewhen(inst.op ===JUMP.op) { val op = JUMP; conOp(op) }
    .otherwise { val op = NOP; conOp(op) }
}

class Inst extends Bundle {
  val op = UInt(4.W)
  val rd = UInt(REG.W)
  val rs = UInt(REG.W)
  val imm9 = UInt(9.W)
  val disp6 = UInt(6.W)
}


class RegFile extends Module {
  val io = IO(new Bundle(){
    val rd_addr = Input(UInt(XLEN.W))
    val rs_addr = Input(UInt(XLEN.W))
    val w_data = Input(UInt(XLEN.W))
    val w_addr = Input(UInt(REG.W))
    val rf_w = Input(Bool())
    val rd_out = Output(UInt(XLEN.W))
    val rs_out = Output(UInt(XLEN.W))
  })

  val reg_file = Vec(8, UInt(16.W))
  reg_file(0) := 0.U

  when(io.rf_w) {
    reg_file(io.w_addr) := io.w_data
  } .otherwise {}

  io.rd_out := reg_file(io.rd_addr)
  io.rs_out := reg_file(io.rs_out)
}

sealed trait Source { val id: UInt }
sealed trait Source1 extends Source
object Zero  extends Source2 with Source1 { val id = 0.U }
object RD    extends Source1 { val id = 1.U }
object PC    extends Source1 { val id = 2.U }
sealed trait Source2 extends Source
object RS    extends Source2 { val id = 1.U }
object Disp6 extends Source2 { val id = 2.U }
object Imm9  extends Source2 { val id = 3.U }
object One   extends Source2 { val id = 4.U }

sealed trait Alith { val id: UInt }
object AlithADD extends Alith { val id = 0.U }
object AlithSUB extends Alith { val id = 1.U }
object AlithAND extends Alith { val id = 2.U }
object AlithOR  extends Alith { val id = 3.U }


sealed trait OP {
  val op: UInt
  val alith: Alith
  val rs1: Source1
  val rs2: Source2
  val rf_w: Bool
  val mem_w: Bool
  val mem_r: Bool
  val pc_w: Bool
}

object  ADD extends OP { val op = "b0001".U; val alith = AlithADD; val rs1 = RD;   val rs2 = RS;    val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object  SUB extends OP { val op = "b0010".U; val alith = AlithSUB; val rs1 = RD;   val rs2 = RS;    val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object  AND extends OP { val op = "b0011".U; val alith = AlithAND; val rs1 = RD;   val rs2 = RS;    val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object   OR extends OP { val op = "b0100".U; val alith = AlithOR;  val rs1 = RD;   val rs2 = RS;    val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object ADDI extends OP { val op = "b0101".U; val alith = AlithADD; val rs1 = RD;   val rs2 = Disp6; val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object SUBI extends OP { val op = "b0110".U; val alith = AlithSUB; val rs1 = RD;   val rs2 = Disp6; val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object INCR extends OP { val op = "b0111".U; val alith = AlithADD; val rs1 = RD;   val rs2 = One;   val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object DECR extends OP { val op = "b1000".U; val alith = AlithSUB; val rs1 = RD;   val rs2 = One;   val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object  LDI extends OP { val op = "b1001".U; val alith = AlithADD; val rs1 = RD;   val rs2 = Imm9;  val rf_w = true.B;  val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }
object   LD extends OP { val op = "b1010".U; val alith = AlithADD; val rs1 = RD;   val rs2 = Disp6; val rf_w = true.B;  val mem_w = false.B; val mem_r = true.B; val pc_w = false.B }
object   ST extends OP { val op = "b1011".U; val alith = AlithADD; val rs1 = RD;   val rs2 = Disp6; val rf_w = false.B; val mem_w = true.B;  val mem_r = false.B; val pc_w = false.B }
object  BEQ extends OP { val op = "b1100".U; val alith = AlithADD; val rs1 = PC;   val rs2 = Disp6; val rf_w = false.B; val mem_w = false.B; val mem_r = false.B; val pc_w = true.B  }
object  BGT extends OP { val op = "b1101".U; val alith = AlithADD; val rs1 = PC;   val rs2 = Disp6; val rf_w = false.B; val mem_w = false.B; val mem_r = false.B; val pc_w = true.B  }
object JUMP extends OP { val op = "b1110".U; val alith = AlithADD; val rs1 = Zero; val rs2 = Imm9;  val rf_w = false.B; val mem_w = false.B; val mem_r = false.B; val pc_w = true.B  }
object  NOP extends OP { val op = "b0000".U; val alith = AlithADD; val rs1 = Zero; val rs2 = Zero;  val rf_w = false.B; val mem_w = false.B; val mem_r = false.B; val pc_w = false.B }


