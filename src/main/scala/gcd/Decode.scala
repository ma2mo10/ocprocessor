
package gcd

import chisel3._

class Decode extends Module {
  val io = IO(new Bundle() {
    val inst = Input(UInt(16.W))
    val pc = Input(UInt(16.W))
    val result = Input(UInt(16.W))
    val result_w = Input(Bool())
    val mem_w = Output(Bool())
    val pc_w = Output(Bool())
    val is_eq = Output(Bool())
    val alith = Output(UInt(3.W))
    val rd_addr = Output(UInt(3.W))
    val rd = Output(UInt(16.W))
    val rs = Output(UInt(16.W))
    val rs1 = Output(UInt(16.W))
    val rs2 = Output(UInt(16.W))
  })
}
