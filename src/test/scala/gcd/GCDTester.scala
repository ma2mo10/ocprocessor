package gcd.test

import chisel3.iotesters._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import gcd.{Fetch, NextPC}

/*
class GCDUnitTester(f: Fetch) extends PeekPokeTester(f) {
  for(i <- 0 to 10) {
    expect(f.pc, i)
    step(1)
  }
}
*/

class PCUnitTester(pc: NextPC) extends PeekPokeTester(pc) {
  poke(pc.io.pc, 0)
  poke(pc.io.pc_w, 0)
  poke(pc.io.result, 0)
  step(1)
  expect(pc.io.next_pc, 1)
  poke(pc.io.pc_w, value = 1)
  poke(pc.io.result, value = 5)
  step(1)
  expect(pc.io.next_pc, expected = 5)
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly example.test.GCDTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly example.test.GCDTester'
  * }}}
  */
class GCDTester extends ChiselFlatSpec {
  // Disable this until we fix isCommandAvailable to swallow stderr along with stdout
  // private val backendNames = Array("firrtl", "verilator")
  private val backendNames = Array("firrtl")
  for ( backendName <- backendNames ) {
    "GCD" should s"calculate proper greatest common denominator (with $backendName)" in {
      Driver(() => new NextPC, backendName) {
        c => new PCUnitTester(c)
      } should be (true)
    }
  }
}
