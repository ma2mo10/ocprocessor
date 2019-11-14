// See README.md for license details.

package gcd

import chisel3._

/**
  * Compute GCD using subtraction method.
  * Subtracts the smaller from the larger until register y is zero.
  * value in register x is then the GCD
  */

class Fetch extends Module {
  val io = IO(new Bundle {
    val result = Input(UInt(16.W))
    val pc_w = Input(Bool())
    val inst = Output(UInt(16.W))
  })
  val inst_mem = Reg(Vec(8, UInt(16.W)))
  val pc = RegInit(0.U(16.W))
  val m = Module(new NextPC)
  m.io.pc := pc
  m.io.pc_w := false.B
  m.io.result := 0.U
  pc := m.io.next_pc

  io.inst := inst_mem(pc)
}

class NextPC extends Module {
  val io = IO(new Bundle {
    val pc = Input(UInt(16.W))
    val pc_w = Input(Bool())
    val result = Input(UInt(16.W))
    val next_pc = Output(UInt(16.W))
  })
  io.next_pc := Mux(io.pc_w, io.result, io.pc + 1.U)
}
