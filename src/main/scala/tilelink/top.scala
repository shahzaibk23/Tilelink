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

class Top extends Module with OpCodes {

    val io = IO(new Bundle{
        // val opCode = Input(UInt(3.W))
        val channelA = Flipped(Decoupled(new channelABundle))
        val channelD = Decoupled(new channelDBundle)
    })

    val memory = SyncReadMem(1024,UInt(32.W))
    val r = Reg(UInt(32.W))

    memory.write(2.U, 4.U)

    io.channelA.ready := 1.B

    when(io.channelA.bits.a_opcode === Get.U){
        io.channelD.bits.d_opcode := io.channelA.bits.a_opcode
        io.channelD.bits.d_param := io.channelA.bits.a_param
        io.channelD.bits.d_size := io.channelA.bits.a_size
        io.channelD.bits.d_source := io.channelA.bits.a_source
        io.channelD.bits.d_sink := 0.U
        io.channelD.bits.d_denied := 0.U
        io.channelD.bits.d_corrupt := io.channelA.bits.a_corrupt
        io.channelD.bits.d_data := memory.read(io.channelA.bits.a_address)
        io.channelD.valid := io.channelA.valid
    }.otherwise{
        io.channelD.bits.d_opcode := io.channelA.bits.a_opcode
        io.channelD.bits.d_param := io.channelA.bits.a_param
        io.channelD.bits.d_size := io.channelA.bits.a_size
        io.channelD.bits.d_source := io.channelA.bits.a_source
        io.channelD.bits.d_sink := 0.U
        io.channelD.bits.d_denied := 0.U
        io.channelD.bits.d_corrupt := 1.B
        io.channelD.bits.d_data := io.channelA.bits.a_data
        io.channelD.valid := 1.B
    }
    

}

