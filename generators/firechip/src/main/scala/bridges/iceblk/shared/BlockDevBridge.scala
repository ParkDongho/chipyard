//See LICENSE for license details
package firesim.bridges

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config.Parameters
import freechips.rocketchip.util.DecoupledHelper

import firesim.lib._
import testchipip.iceblk.{BlockDeviceIO, BlockDeviceRequest, BlockDeviceData, BlockDeviceKey, BlockDeviceConfig}

class BlockDevBridgeTargetIO(bdParams: BlockDeviceConfig) extends Bundle {
  val bdev = Flipped(new BlockDeviceIO(bdParams))
  val reset = Input(Bool())
  val clock = Input(Clock())
}

class BlockDevBridge(bdParams: BlockDeviceConfig) extends BlackBox
    with Bridge[HostPortIO[BlockDevBridgeTargetIO]] {
  val moduleName = "BlockDevBridgeModule"
  val io = IO(new BlockDevBridgeTargetIO(bdParams))
  val bridgeIO = HostPort(io)
  val constructorArg = Some(bdParams)
  generateAnnotations()
}

object BlockDevBridge  {
  def apply(clock: Clock, blkdevIO: BlockDeviceIO, reset: Bool): BlockDevBridge = {
    val ep = Module(new BlockDevBridge(blkdevIO.bdParams))
    ep.io.bdev <> blkdevIO
    ep.io.clock := clock
    ep.io.reset := reset
    ep
  }
}
