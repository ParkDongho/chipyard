//See LICENSE for license details.
package firesim.bridges

import org.chipsalliance.cde.config.Config
import firesim.bridges.{LoopbackNIC}

// Enables NIC loopback the NIC widget
class WithNICWidgetLoopback  extends Config((site, here, up) => {
  case LoopbackNIC => true
})
