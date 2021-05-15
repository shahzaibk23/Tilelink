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