package tilelink

import chisel3._ 
import chisel3.util._ 

class channelDBundle extends Bundle with Config {
    val d_opcode = UInt(3.W)
    val d_param = UInt(2.W)
    val d_size = UInt(z.W)
    val d_source = UInt(o.W)
    val d_sink = UInt(i.W)  
    val d_denied = Bool()
    val d_corrupt = Bool()
    val d_data = UInt((8*w).W)
}

// Channel D pin details

//          Signal Name  | No of Bits           | Description                                                                                         
//          d_opcode     | [2:0]                | Response opcode (Ack or Data)                                                                       
//          d_param      | [2:0]                | Response parameter (unused)                                                                         
//          d_size       | config.z [2:0]       | Response data size                                                                                  
//          d_source     | config.o [8:0]       | Bouncing of request ID of configurable width                                                        
//          d_sink       | config.i [0]         | Response ID of configurable width (possibly unused)                                                 
//          d_denied     | Bool                 | The slave was unable to service the request.                                                        
//          d_corrupt    | Bool                 | Reserved; must be 0                                                                                 
//          d_data       | config.w x 8 [32:0]  | Response data of configurable width                                                                 
