/*
 * This file is part of Apparat.
 * 
 * Apparat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Apparat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Apparat. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2009 Joa Ebert
 * http://www.joa-ebert.com/
 * 
 */
package apparat.abc

import java.io.OutputStream

class AbcOutputStream(val output: OutputStream) extends OutputStream {
	private def encodeInt(value: Long) = {//todo can do this with match case...
		if (value < 128 && value > -1) {
			write(value.asInstanceOf[Byte])
		} else if (value < 16384 && value > -1) {
			write(((value & 0x7f) | 0x80).asInstanceOf[Byte])
			write(((value >> 7) & 0x7f).asInstanceOf[Byte])
		} else if (value < 2097152 && value > -1) {
			write(((value & 0x7f) | 0x80).asInstanceOf[Byte])
			write((value >> 7 | 0x80).asInstanceOf[Byte])
			write((((value >> 14)) & 0x7f).asInstanceOf[Byte])
		} else if (value < 268435456 && value > -1) {
			write(((value & 0x7f) | 0x80).asInstanceOf[Byte])
			write((value >> 7 | 0x80).asInstanceOf[Byte])
			write((value >> 14 | 0x80).asInstanceOf[Byte])
			write(((value >> 21) & 0x7f).asInstanceOf[Byte])
		} else {
			write(((value & 0x7f) | 0x80).asInstanceOf[Byte])
			write((value >> 7 | 0x80).asInstanceOf[Byte])
			write((value >> 14 | 0x80).asInstanceOf[Byte])
			write((value >> 21 | 0x80).asInstanceOf[Byte])
			write(((value >> 28) & 0x0f).asInstanceOf[Byte])
		}
	}

	def writeD64(value: Double) = {
		val bits = java.lang.Double.doubleToRawLongBits(value);
		write(bits.asInstanceOf[Byte])
		write((bits >> 8).asInstanceOf[Byte])
		write((bits >> 16).asInstanceOf[Byte])
		write((bits >> 24).asInstanceOf[Byte])
		write((bits >> 32).asInstanceOf[Byte])
		write((bits >> 40).asInstanceOf[Byte])
		write((bits >> 48).asInstanceOf[Byte])
		write((bits >> 56).asInstanceOf[Byte])
	}

	def writeS24(value: Int) = {
		write(value & 0xff)
		write((value & 0xff00) >> 0x08)
		write((value & 0xff0000) >> 0x10)
	}

	def writeS32(value: Int) = encodeInt(value)

	def writeString(value: String) = {
		val bytes = value getBytes "UTF8"
		writeU30(bytes.length)
		write(bytes)
	}

	def writeU08(value: Int) = write(value & 0xff)

	def writeU16(value: Int) = {
		write(value & 0xff)
		write((value & 0xff00) >> 0x08)
	}

	def writeU30(value: Int) = encodeInt(value & 0x3fffffff)

	def writeU32(value: Long) = encodeInt(value & 0xffffffffL)

	override def close() = output.close()

	override def flush() = output.flush()

	override def write(value: Array[Byte]) = output write value

	override def write(value: Array[Byte], offset: Int, length: Int) = output write (value, offset, length)

	override def write(value: Int) = output write value
}