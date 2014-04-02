/*
 * Copyright 2014 Kevin Herron
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
