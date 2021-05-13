package tilelink

import org.scalatest._ 
import chisel3._ 
import chisel3.util._ 
import chiseltest._
import chiseltest.experimental.TestOptionBuilder._ 
import chiseltest.internal.VerilatorBackendAnnotation

class topTest extends FreeSpec with ChiselScalatestTester {
    "TOP Test" in {
        test(new Top).withAnnotations(Seq(VerilatorBackendAnnotation)){ c =>
            c.io.channelA.valid.poke(1.B)
            c.io.channelA.bits.a_opcode.poke(4.U)
            c.io.channelA.bits.a_param.poke(0.U)
            c.io.channelA.bits.a_size.poke(2.U)
            c.io.channelA.bits.a_source.poke(2.U)
            c.io.channelA.bits.a_address.poke(2.U)
            c.io.channelA.bits.a_mask.poke(1.U)
            c.io.channelA.bits.a_corrupt.poke(0.U)
            c.io.channelA.bits.a_data.poke(0.U)
            c.clock.step(10)
        }
    }
}