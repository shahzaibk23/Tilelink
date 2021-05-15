package tilelink

import chisel3._ 
import chisel3.util._ 
import chisel3.experimental.BundleLiterals._

trait Config {
    val w = 4
    val a = 32
    val z = 8
    val o = 32
    val i = 32
}

trait OpCodes {
    val Get = 4
    val AccessAckData = 1
    val PutFullData = 0
    val PutPartialData = 1
    val AccessAck = 0
}

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

class Top extends Module with OpCodes with Config {

    val io = IO(new Bundle{
        // val opCode = Input(UInt(3.W))
        val channelA = Flipped(Decoupled(new channelABundle))
        val channelD = Decoupled(new channelDBundle)
    })

    val stall = Module(new stallUnit)    

    io.channelA.ready := 1.B

    val mem = Mem(1024, UInt(32.W))
    mem.write(2.U, 4.U)

    

    when(io.channelA.bits.a_opcode === Get.U){
        stall.io.bundle_in.d_opcode := AccessAckData.U
        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := io.channelA.bits.a_size
        stall.io.bundle_in.d_source := io.channelA.bits.a_source
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := 0.U
        stall.io.bundle_in.d_corrupt := io.channelA.bits.a_corrupt
        stall.io.bundle_in.d_data := mem.read(io.channelA.bits.a_address)
        stall.io.valid_in := 1.U
    }.otherwise{
        stall.io.bundle_in.d_opcode := 0.U
        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := 0.U
        stall.io.bundle_in.d_source := 0.U
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := 0.U
        stall.io.bundle_in.d_corrupt := 1.U
        stall.io.bundle_in.d_data := 0.U
        stall.io.valid_in := 0.U
    }


    io.channelD.bits := stall.io.bundle_out
    io.channelD.valid := stall.io.valid_out
    

    

}