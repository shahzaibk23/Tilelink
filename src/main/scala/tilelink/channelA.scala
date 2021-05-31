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
    val a_corrupt = Bool()
    val a_data = UInt((8*w).W)
}

// Channel A pin details

//          Signal Name  | No of Bits           | Description                                                                                         
//          a_opcode     | [2:0]                | Request opcode (read, write, or partial write)                                                      
//          a_param      | [2:0]                | Unused/Ignored                                                                                      
//          a_size       | config.z [2:0]       | Request size (requested size is 2^a_size, thus 0 = byte, 1 = 16b, 2 = 32b, 3 = 64b, etc)            
//          a_source     | config.o [8:0]       | Request identifier of configurable width                                                            
//          a_address    | config.a [32:0]      | Request address of configurable width                                                               
//          a_mask       | config.w [4:0]       | Write strobe, one bit per byte indicating which lanes of data are valid for this write request      
//          a_corrupt    | Bool                 |  Reserved; must be 0                                                                                
//          a_data       | config.w x 8 [32:0]  | Write request data of configurable width                                                            
