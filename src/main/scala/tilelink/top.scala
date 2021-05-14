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
    val AccessAckData = 1.U
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

    io.channelA.ready := 1.B

    val memory = SyncReadMem(1024,UInt(32.W))
    

    memory.write(2.U, 4.U)

    val reg_opcode = Reg(UInt(3.W))
    val reg_param = Reg(UInt(2.W))
    val reg_size = Reg(UInt(z.W))
    val reg_source = Reg(UInt(o.W))
    val reg_sink = Reg(UInt(i.W))
    val reg_denied = Reg(UInt(1.W))
    val reg_corrupt = Reg(UInt(1.W))
    val reg_data = Reg(UInt(8.W))
    val reg_valid = Reg(Bool())

    reg_opcode := 2.U
    reg_param := 3.U
    reg_size := io.channelA.bits.a_size
    reg_source := io.channelA.bits.a_source
    reg_sink := 6.U
    reg_denied := 1.U
    reg_corrupt := 1.U //io.channelA.bits.a_corrupt
    reg_data := memory.read(io.channelA.bits.a_address)
    reg_valid := io.channelA.valid

    // val r = RegInit({
    //     val bundle = Wire(new channelDBundle)
    //     bundle.d_opcode := AccessAckData.U
    //     bundle.d_param := 0.U
    //     bundle.d_size := io.channelA.bits.a_size
    //     bundle.d_source := io.channelA.bits.a_source
    //     bundle.d_sink := 0.U
    //     bundle.d_denied := 0.U 
    //     bundle.d_corrupt := io.channelA.bits.a_corrupt
    //     bundle.d_data := memory.read(io.channelA.bits.a_address)
    //     bundle
    // })

    // val reg = RegInit((new channelDBundle).Lit(
    //     _.d_opcode -> AccessAckData.U,
    //     _.d_param -> 0.U,
    //     _.d_size -> io.channelA.bits.a_size,
    //     _.d_source -> io.channelA.bits.a_source,
    //     _.d_sink -> 0.U,
    //     _.d_denied -> 0.U, 
    //     _.d_corrupt -> io.channelA.bits.a_corrupt,
    //     _.d_data -> memory.read(io.channelA.bits.a_address)
    // ))


    

    when(io.channelA.bits.a_opcode === Get.U ){
        // io.channelD.bits.d_opcode := AccessAckData.U
        // io.channelD.bits.d_param := 0.U
        // io.channelD.bits.d_size := io.channelA.bits.a_size
        // io.channelD.bits.d_source := io.channelA.bits.a_source
        // io.channelD.bits.d_sink := 0.U
        // io.channelD.bits.d_denied := 0.U
        // io.channelD.bits.d_corrupt := io.channelA.bits.a_corrupt
        // io.channelD.bits.d_data := memory.read(io.channelA.bits.a_address)
        // io.channelD.bits := r
        io.channelD.bits.d_opcode := reg_opcode
        io.channelD.bits.d_param := reg_param
        io.channelD.bits.d_size := reg_size
        io.channelD.bits.d_source := reg_source
        io.channelD.bits.d_sink := reg_sink
        io.channelD.bits.d_denied := reg_denied
        io.channelD.bits.d_corrupt := reg_corrupt
        io.channelD.bits.d_data := memory.read(io.channelA.bits.a_address)
        io.channelD.valid := reg_valid
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

