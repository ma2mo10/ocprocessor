package gcd.test

import chisel3.iotesters._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import gcd.{Decode, RegFile}

class DecodeTester(de: Decode) extends PeekPokeTester(de) {

}