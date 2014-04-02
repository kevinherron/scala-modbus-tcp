package com.digitalpetri.modbus


package object serialization {

  import scala.language.implicitConversions

  private implicit def bool2int(b: Boolean) = if (b) 1 else 0

  def bits2Int(bits: Seq[Boolean]) = {
    bits.reverse.foldLeft(0) {
      (i, bit) => (i << 1) | bit
    }
  }

}
