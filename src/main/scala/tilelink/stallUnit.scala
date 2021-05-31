package tilelink

import chisel3._ 

class stallUnit extends Module {
    val io = IO(new Bundle{
        val bundle_in = Input(new channelDBundle)
        val valid_in = Input(Bool())
        val bundle_out = Output(new channelDBundle)
        val valid_out = Output(Bool())
    })

    

    val bundle_reg = RegInit(0.U.asTypeOf(new channelDBundle))
    val valid_reg = RegInit(0.U(1.W))
    
    bundle_reg := io.bundle_in
    valid_reg := io.valid_in

    io.bundle_out := bundle_reg
    io.valid_out := valid_reg
}