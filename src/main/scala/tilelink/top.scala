package tilelink

import chisel3._ 
import chisel3.util.{MuxLookup,Cat,Decoupled}
import chisel3.experimental.BundleLiterals._


class Top extends Module with OpCodes with Config {

    val io = IO(new Bundle{
        // val opCode = Input(UInt(3.W))
        val channelA = Flipped(Decoupled(new channelABundle))
        val channelD = Decoupled(new channelDBundle)
    })

    val stall = Module(new stallUnit)    

    io.channelA.ready := 1.B

    val mem = SyncReadMem(1024, UInt(32.W))
    // mem.write(2.U, 4.U)


    when(io.channelA.bits.a_opcode === Get.U || io.channelA.bits.a_opcode === PutFullData.U || io.channelA.bits.a_opcode === PutPartialData.U){
        
        when(io.channelA.bits.a_opcode === Get.U){
            stall.io.bundle_in.d_opcode := AccessAckData.U
            stall.io.bundle_in.d_data := MuxLookup(io.channelA.bits.a_mask, 
                                                    mem.read(io.channelA.bits.a_address),
                                                    Array(
                                                        ("b0001".U) -> Cat(0.U(24.W), mem.read(io.channelA.bits.a_address)(7,0)),
                                                        ("b0011".U) -> Cat(0.U(16.W), mem.read(io.channelA.bits.a_address)(15,0)),
                                                        ("b0111".U) -> Cat(0.U(8.W), mem.read(io.channelA.bits.a_address)(23,0)),
                                                        ("b1111".U) -> mem.read(io.channelA.bits.a_address),
                                                    ))

        }.otherwise{
            stall.io.bundle_in.d_opcode := AccessAck.U
            stall.io.bundle_in.d_data := 0.U
            val s = Mux(io.channelA.bits.a_opcode === PutFullData.U, mem.write(io.channelA.bits.a_address, io.channelA.bits.a_data), 
                MuxLookup(io.channelA.bits.a_mask, 
                            mem.write(io.channelA.bits.a_address, io.channelA.bits.a_data),
                            Array(
                                ("b0001".U) -> mem.write(io.channelA.bits.a_address, Cat(0.U(24.W), io.channelA.bits.a_data(7,0))),
                                ("b0011".U) -> mem.write(io.channelA.bits.a_address, Cat(0.U(16.W), io.channelA.bits.a_data(15,0))),
                                ("b0111".U) -> mem.write(io.channelA.bits.a_address, Cat(0.U(8.W), io.channelA.bits.a_data(23,0))),
                                ("b1111".U) -> mem.write(io.channelA.bits.a_address, io.channelA.bits.a_data),
                            )
                )

            )
            // mem.write(io.channelA.bits.a_address, io.channelA.bits.a_data)
        }

        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := io.channelA.bits.a_size
        stall.io.bundle_in.d_source := io.channelA.bits.a_source
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := false.B
        stall.io.bundle_in.d_corrupt := io.channelA.bits.a_corrupt
        stall.io.valid_in := true.B

    }.otherwise{
        stall.io.bundle_in.d_opcode := 0.U
        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := 0.U
        stall.io.bundle_in.d_source := 0.U
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := 0.U
        stall.io.bundle_in.d_corrupt := 1.U
        stall.io.bundle_in.d_data := 0.U
        stall.io.valid_in := false.B
    }


    io.channelD.bits := stall.io.bundle_out
    io.channelD.valid := stall.io.valid_out
    

    

}


// TODO: EXCEPTIONS