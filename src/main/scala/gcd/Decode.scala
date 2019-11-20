
package gcd

import chisel3._
import chisel3.util.{is, switch}
import Properties._

class Decode extends Module {
  val io = IO(new Bundle() {
    val inst = Input(UInt(16.W))
    val pc = Input(UInt(16.W))
    val result = Input(UInt(16.W))
    val result_w = Input(Bool())
    val mem_w = Output(Bool())
    val pc_w = Output(Bool())
    val is_eq = Output(Bool())
    val alith = Output(UInt(2.W))
    val rd_addr = Output(UInt(3.W))
    val rd = Output(UInt(16.W))
    val rs = Output(UInt(16.W))
    val rs1 = Output(UInt(16.W))
    val rs2 = Output(UInt(16.W))
  })

}

class RtypeDecode extends Module {
  val io = IO(new Bundle(){
    val inst = Input(UInt(16.W))
    val op = Input(UInt(4.W))
    val reg_w = Output(Bool())
    val rd_addr = Output(UInt(3.W))
    val rs_addr = Output(UInt(3.W))
    val alith = Output(UInt(2.W))
  })



  io.reg_w := true.B
  io.rd_addr := io.inst(11, 9)
  io.rs_addr := io.inst(8, 6)

  switch(io.op) {
    is()
  }

}

class BranchDecode extends Module {
  val io = IO(new Bundle(){
    val inst = Input(UInt(16.W))
    val pc_w = Output(Bool())
    val alith = Output(UInt(2.W))
    val rs1_addr = Output(UInt(3.W))
    val rs2_addr = Output(UInt(3.W))
  })
}

class ImmDecode extends Module {
  val io = IO(new Bundle() {
    val inst = Input(UInt(16.W))
    val mem_w = Output(Bool())
    val pc_w = Output(Bool())
    val rd_addr = Output(UInt(3.W))
    val imm9 = Output(UInt(6.W))
    val alith = Output(UInt(2.W))
  })
}

class DispDecode extends Module {
  val io = IO(new Bundle() {
    val inst = Input(UInt(16.W))
    val mem_w = Output(Bool())
    val pc_w = Output(Bool())
    val disp6 = Output(UInt(6.W))
    val reg_addr = Output(UInt(3.W))
    val rb_addr = Output(UInt(3.W))
  })
}

class RegFile extends Module {
  val io = IO(new Bundle(){
    val rd_addr = Input(UInt(16.W))
    val rs_addr = Input(UInt(16.W))
    val w_data = Input(UInt(16.W))
    val w_addr = Input(UInt(16.W))
    val mem_w = Input(Bool())
    val rd_out = Output(UInt(16.W))
    val rs_out = Output(UInt(16.W))
  })

  val reg_file = Vec(8, UInt(16.W))
  reg_file(0) := 0.U

  when(io.mem_w) {
    reg_file(io.w_addr) := io.w_data
  } .otherwise {}

  io.rd_out := reg_file(io.rd_addr)
  io.rs_out := reg_file(io.rs_out)
}
