//See LICENSE for license details
package firesim.bridges

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config.{Parameters, Field}
import freechips.rocketchip.util._

import firesim.lib._
import testchipip.serdes.StreamChannel
import icenet.{NICIOvonly}

class NICTargetIO extends Bundle {
  val clock = Input(Clock())
  val nic = Flipped(new NICIOvonly)
}

class NICBridge(implicit p: Parameters) extends BlackBox with Bridge {
  val moduleName = "SimpleNICBridgeModule"
  val io = IO(new NICTargetIO)
  val constructorArg = None
  generateAnnotations()
}


object NICBridge {
  def apply(clock: Clock, nicIO: NICIOvonly)(implicit p: Parameters): NICBridge = {
    val ep = Module(new NICBridge)
    ep.io.nic <> nicIO
    ep.io.clock := clock
    ep
  }
}
