//See LICENSE for license details
package firesim.bridges

import firesim.lib._

import chisel3._
import org.chipsalliance.cde.config.Parameters

class GroundTestBridge extends BlackBox
    with Bridge[HostPortIO[GroundTestBridgeTargetIO]] {
  val moduleName = "GroundTestBridgeModule"
  val io = IO(new GroundTestBridgeTargetIO)
  val bridgeIO = HostPort(io)
  val constructorArg = None
  generateAnnotations()
}

object GroundTestBridge {
  def apply(clock: Clock, success: Bool)(implicit p: Parameters): GroundTestBridge = {
    val bridge = Module(new GroundTestBridge)
    bridge.io.success := success
    bridge.io.clock := clock
    bridge
  }
}

class GroundTestBridgeTargetIO extends Bundle {
  val success = Input(Bool())
  val clock = Input(Clock())
}
