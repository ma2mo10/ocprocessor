
package gcd

import chisel3._
import Properties._
import gcd.{Fetch, Decode, MemoryAccess, Execute}

class cpu extends Module {
  val io = IO(new Bundle() {
    val out = Output(UInt(XLEN.W))
  })


}