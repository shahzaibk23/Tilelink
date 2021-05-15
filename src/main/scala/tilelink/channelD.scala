package tilelink

import chisel3._ 
import chisel3.util._ 

class channelDBundle extends Bundle with Config {
    val d_opcode = UInt(3.W)
    val d_param = UInt(2.W)
    val d_size = UInt(z.W)
    val d_source = UInt(o.W)
    val d_sink = UInt(i.W)  
    val d_denied = UInt(1.W)
    val d_corrupt = UInt(1.W)
    val d_data = UInt((8*w).W)
}
