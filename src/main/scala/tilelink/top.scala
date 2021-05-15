package tilelink

import chisel3._ 
import chisel3.util._ 
import chisel3.experimental.BundleLiterals._


class Top extends Module with OpCodes with Config {

    val io = IO(new Bundle{
        // val opCode = Input(UInt(3.W))
        val channelA = Flipped(Decoupled(new channelABundle))
        val channelD = Decoupled(new channelDBundle)
    })

    val stall = Module(new stallUnit)    

    io.channelA.ready := 1.B

    val mem = Mem(1024, UInt(32.W))
    // mem.write(2.U, 4.U)

    def checkOpcode(value:Int):Bool = if (io.channelA.bits.a_opcode == value.U) true.B else false.B


    when(io.channelA.bits.a_opcode === Get.U || io.channelA.bits.a_opcode === PutFullData.U || io.channelA.bits.a_opcode === PutPartialData.U){
        
        when(io.channelA.bits.a_opcode === Get.U){
            stall.io.bundle_in.d_opcode := AccessAckData.U
            stall.io.bundle_in.d_data := mem.read(io.channelA.bits.a_address)

        }.otherwise{
            stall.io.bundle_in.d_opcode := AccessAck.U
            stall.io.bundle_in.d_data := 0.U
            mem.write(io.channelA.bits.a_address, io.channelA.bits.a_data)
        }

        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := io.channelA.bits.a_size
        stall.io.bundle_in.d_source := io.channelA.bits.a_source
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := 0.U
        stall.io.bundle_in.d_corrupt := io.channelA.bits.a_corrupt
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


// TODO: EXCEPTIONS