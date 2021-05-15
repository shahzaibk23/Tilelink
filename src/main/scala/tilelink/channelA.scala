package tilelink

import chisel3._ 
import chisel3.util._ 

class channelABundle extends Bundle with Config {
    val a_opcode = UInt(3.W)
    val a_param = UInt(3.W)
    val a_size = UInt(z.W)
    val a_source = UInt(o.W)
    val a_address = UInt(a.W)
    val a_mask = UInt(w.W)
    val a_corrupt = UInt(1.W)
    val a_data = UInt((8*w).W)
}