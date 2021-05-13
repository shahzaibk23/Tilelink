package tilelink

import chisel3._ 
import chisel3.util._ 

trait Config {
    val w = 4
    val a = 32
    val z = 8
    val o = 32
    val i = 32
}

val channelABundle extends Bundle with Config {
    val a_opcode = Input(UInt(3.W))
    val a_param = Input(UInt(3.W))
    val a_size = Input(UInt(z.W))
    val a_source = Input(UInt(o.W))
    val a_address = Input(UInt(a.W))
    val a_mask = Input(UInt(w.W))
    val a_corrupt = Input(UInt(1.W))
    val a_data = Input(UInt((8*w).W))
}

