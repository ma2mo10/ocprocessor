
package gcd

import chisel3._
import chisel3.util._
import Properties._

class Execute extends Module {
  val io = IO(new Bundle() {
    val rd = Input(UInt(XLEN.W))
    val rs = Input(UInt(XLEN.W))
    val alu_op = Input(UInt(2.W))
    val src1 = Input(UInt(XLEN.W))
    val src2 = Input(UInt(XLEN.W))
    val cond_type = Input(UInt(2.W))

    val alu_out = Output(UInt(XLEN.W))
    val pc_w = Output(Bool())
  })

  val alu = Module(new ALU)
  alu.io.src1 := io.src1
  alu.io.src2 := io.src2
  alu.io.alu_op := io.alu_op

  io.alu_out := alu.io.out

  io.pc_w := MuxLookup(io.cond_type, false.B, Array(
    NB.id -> false.B,
    BQ.id -> io.src1.===(io.src2),
    GT.id -> io.src1.>(io.src2),
    JP.id -> true.B
  ))
}

class ALU extends Module {
  val io = IO(new Bundle() {
    val src1 = Input(UInt(XLEN.W))
    val src2 = Input(UInt(XLEN.W))
    val alu_op = Input(UInt(2.W))

    val out = Output(UInt(XLEN.W))
  })

  io.out := MuxLookup(io.alu_op, 0.U, Array(
    ALUADD.id -> io.src1.+(io.src2),
    ALUSUB.id -> io.src1.-(io.src2),
    ALUAND.id -> io.src1.&(io.src2),
    ALUOR.id -> io.src1.|(io.src2)
   ))

}

sealed trait ALUOP {val id: UInt}

object ALUADD extends ALUOP {val id = 0.U}
object ALUSUB extends ALUOP {val id = 1.U}
object ALUAND extends ALUOP {val id = 2.U}
object ALUOR extends ALUOP {val id = 3.U}

sealed trait PCcond {val id: UInt}

object NB extends PCcond {val id = 0.U}
object BQ extends PCcond {val id = 1.U}
object GT extends PCcond {val id = 2.U}
object JP extends PCcond {val id = 3.U}