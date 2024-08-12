//See LICENSE for license details.
package firesim.bridges

import org.chipsalliance.cde.config.{Config, Parameters}
import freechips.rocketchip.subsystem.{PeripheryBusKey}
import testchipip.iceblk.{BlockDeviceConfig, BlockDeviceKey}

class BlockDevConfig extends Config((site, here, up) => {
  case PeripheryBusKey =>
  case BlockDeviceKey  => Some(BlockDeviceConfig())
})

class NoConfig extends Config(Parameters.empty)
