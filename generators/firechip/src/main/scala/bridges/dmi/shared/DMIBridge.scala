// See LICENSE for license details
package firesim.bridges

import firesim.lib._

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config.Parameters
import freechips.rocketchip.devices.debug.{ClockedDMIIO, DMIReq, DMIResp}

/** Class which parameterizes the DMIBridge
  *
  * memoryRegionNameOpt, if unset, indicates that firesim-fesvr should not attempt to write a payload into DRAM through
  * the loadmem unit. This is suitable for target designs which do not use the FASED DRAM model. If a FASEDBridge for
  * the backing AXI4 memory is present, then memoryRegionNameOpt should be set to the same memory region name which is
  * passed to the FASEDBridge. This enables fast payload loading in firesim-fesvr through the loadmem unit.
  */
case class DMIBridgeParams(memoryRegionNameOpt: Option[String], addrBits: Int)

class DMIBridge(memoryRegionNameOpt: Option[String], addrBits: Int)
    extends BlackBox
    with Bridge[HostPortIO[DMIBridgeTargetIO]] {
  val moduleName = "DMIBridgeModule"
  val io             = IO(new DMIBridgeTargetIO(addrBits))
  val bridgeIO = HostPort(io)
  val constructorArg = Some(DMIBridgeParams(memoryRegionNameOpt, addrBits: Int))
  generateAnnotations()
}

object DMIBridge {
  def apply(
    clock:               Clock,
    port:                ClockedDMIIO,
    memoryRegionNameOpt: Option[String],
    reset:               Bool,
    addrBits:            Int,
  )(implicit p:          Parameters
  ): DMIBridge = {
    val ep = Module(new DMIBridge(memoryRegionNameOpt, addrBits))
    // req into target, resp out of target
    port.dmi.req     <> ep.io.debug.req
    ep.io.debug.resp <> port.dmi.resp
    port.dmiClock    := clock
    port.dmiReset    := reset
    ep.io.clock      := clock
    ep.io.reset      := reset
    ep
  }
}

// copy with no Parameters
class BasicDMIIO(addrBits: Int) extends Bundle {
  val req  = new DecoupledIO(new DMIReq(addrBits))
  val resp = Flipped(new DecoupledIO(new DMIResp))
}

class DMIBridgeTargetIO(addrBits: Int) extends Bundle {
  val debug = new BasicDMIIO(addrBits)
  val reset = Input(Bool())
  val clock = Input(Clock())
}
