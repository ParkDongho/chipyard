//See LICENSE for license details
package firesim.bridges

import chisel3._
import chisel3.util._

import testchipip.cosim.{SerializableTileTraceIO, SpikeCosimConfig, TileTraceIO, TraceBundleWidths}

import firesim.lib._

case class CospikeBridgeParams(
  widths: TraceBundleWidths,
  hartid: Int,
  cfg:    SpikeCosimConfig,
)

class CospikeTargetIO(widths: TraceBundleWidths) extends Bundle {
  val trace = Input(new SerializableTileTraceIO(widths))
}

/** Blackbox that is instantiated in the target
  */
class CospikeBridge(params: CospikeBridgeParams)
    extends BlackBox
    with Bridge[HostPortIO[CospikeTargetIO]] {
  val moduleName = "CospikeBridgeModule"
  val io       = IO(new CospikeTargetIO(params.widths))
  val bridgeIO = HostPort(io)

  // give the Cospike params to the GG module
  val constructorArg = Some(params)

  // generate annotations to pass to GG
  generateAnnotations()
}

/** Helper function to connect blackbox
  */
object CospikeBridge {
  def apply(trace: TileTraceIO, hartid: Int, cfg: SpikeCosimConfig) = {
    val params = new CospikeBridgeParams(trace.traceBundleWidths, hartid, cfg)
    val cosim  = withClockAndReset(trace.clock, trace.reset) {
      Module(new CospikeBridge(params))
    }
    cosim.io.trace.trace.insns.map(t => {
      t       := DontCare
      t.valid := false.B
    })
    cosim.io.trace := trace.asSerializableTileTrace
    cosim
  }
}
