// See LICENSE for license details
package firesim.bridges

import firesim.lib._

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config.Parameters

import testchipip.tsi.{TSIIO, TSI}

/**
  * Class which parameterizes the TSIBridge
  *
  * memoryRegionNameOpt, if unset, indicates that firesim-fesvr should not attempt to write a payload into DRAM through the loadmem unit.
  * This is suitable for target designs which do not use the FASED DRAM model.
  * If a FASEDBridge for the backing AXI4 memory is present, then memoryRegionNameOpt should be set to the same memory region name which is passed
  * to the FASEDBridge. This enables fast payload loading in firesim-fesvr through the loadmem unit.
  */
case class TSIBridgeParams(memoryRegionNameOpt: Option[String])

class TSIBridge(memoryRegionNameOpt: Option[String]) extends BlackBox with Bridge[HostPortIO[TSIBridgeTargetIO]] {
  val moduleName = "TSIBridgeModule"
  val io = IO(new TSIBridgeTargetIO)
  val bridgeIO = HostPort(io)
  val constructorArg = Some(TSIBridgeParams(memoryRegionNameOpt))
  generateAnnotations()
}

object TSIBridge {
  def apply(clock: Clock, port: TSIIO, memoryRegionNameOpt: Option[String], reset: Bool)(implicit p: Parameters): TSIBridge = {
    val ep = Module(new TSIBridge(memoryRegionNameOpt))
    ep.io.tsi <> port
    ep.io.clock := clock
    ep.io.reset := reset
    ep
  }
}

class TSIBridgeTargetIO extends Bundle {
  val tsi = Flipped(new TSIIO)
  val reset = Input(Bool())
  val clock = Input(Clock())
}
