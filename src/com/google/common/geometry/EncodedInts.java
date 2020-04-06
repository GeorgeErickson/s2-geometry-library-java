/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.geometry;

import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Utilities for encoding and decoding integers. */
@GwtCompatible
public class EncodedInts {

  /**
   * Reads a variable-encoded signed long.
   *
   * <p>Note that if you frequently read/write negative numbers, you should consider zigzag-encoding
   * your values before storing them as varints. See {@link EncodedInts#encodeZigZag32} and {@link
   * #decodeZigZag32(int)}.
   *
   * @throws IOException if {@code input.read()} throws an {@code IOException} or returns -1 (EOF),
   *     or if the variable-encoded signed long is malformed.
   */
  static long readVarint64(InputStream input) throws IOException {
    long result = 0;
    for (int shift = 0; shift < 64; shift += 7) {
      final byte b = InputStreams.readByte(input);
      result |= (long) (b & 0x7F) << shift;
      if ((b & 0x80) == 0) {
        return result;
      }
    }
    throw new IOException("Malformed varint.");
  }

  /**
   * Writes a signed long using variable encoding.
   *
   * <p>Note that if you frequently read/write negative numbers, you should consider zigzag-encoding
   * your values before storing them as varints. See {@link EncodedInts#encodeZigZag32} and {@link
   * #decodeZigZag32(int)}.
   *
   * @throws IOException if {@code output.write(int)} throws an {@link IOException}.
   */
  static void writeVarint64(OutputStream output, long value) throws IOException {
    while (true) {
      if ((value & ~0x7FL) == 0) {
        output.write((byte) value);
        return;
      } else {
        output.write((byte) (((int) value & 0x7F) | 0x80));
        value >>>= 7;
      }
    }
  }

  /**
   * Decodes a unsigned integer consisting of {@code bytesPerWord} bytes from {@code supplier} in
   * little-endian format as an unsigned 64-bit integer.
   *
   * <p>This method is not compatible with {@link #readVarint64(InputStream)} or {@link
   * #writeVarint64(OutputStream, long)}.
   *
   * @throws IOException if {@code input.read()} throws an {@code IOException} or returns -1 (EOF).
   */
  static long decodeUintWithLength(InputStream input, int bytesPerWord) throws IOException {
    long x = 0;
    for (int i = 0; i < bytesPerWord; i++) {
      x += (InputStreams.readByte(input) & 0xffL) << (8 * i);
    }
    return x;
  }

  /**
   * Encodes an unsigned integer to {@code consumer} in little-endian format using {@code
   * bytesPerWord} bytes. (The client must ensure that the encoder's buffer is large enough).
   *
   * <p>This method is not compatible with {@link #readVarint64(InputStream)} or {@link
   * #writeVarint64(OutputStream, long)}.
   *
   * @throws IOException if {@code output.write(int)} throws an {@link IOException}.
   */
  static void encodeUintWithLength(OutputStream output, long value, int bytesPerWord)
      throws IOException {
    while (--bytesPerWord >= 0) {
      output.write((byte) value);
      value >>>= 8;
    }
    assert value == 0;
  }

  /**
   * Encode a ZigZag-encoded 32-bit value. ZigZag encodes signed integers into values that can be
   * efficiently encoded with varint. (Otherwise, negative values must be sign-extended to 64 bits
   * to be varint encoded, thus always taking 10 bytes on the wire.)
   *
   * @param n A signed 32-bit integer.
   * @return An unsigned 32-bit integer, stored in a signed int because Java has no explicit
   *     unsigned support.
   */
  static int encodeZigZag32(final int n) {
    // Note:  the right-shift must be arithmetic
    return (n << 1) ^ (n >> 31);
  }

  /**
   * Encode a ZigZag-encoded 64-bit value. ZigZag encodes signed integers into values that can be
   * efficiently encoded with varint. (Otherwise, negative values must be sign-extended to 64 bits
   * to be varint encoded, thus always taking 10 bytes on the wire.)
   *
   * @param n A signed 64-bit integer.
   * @return An unsigned 64-bit integer, stored in a signed int because Java has no explicit
   *     unsigned support.
   */
  static long encodeZigZag64(final long n) {
    // Note:  the right-shift must be arithmetic
    return (n << 1) ^ (n >> 63);
  }

  /**
   * Decode a ZigZag-encoded 32-bit signed value. ZigZag encodes signed integers into values that
   * can be efficiently encoded with varint. (Otherwise, negative values must be sign-extended to 64
   * bits to be varint encoded, thus always taking 10 bytes on the wire.)
   *
   * @param n A 32-bit integer, stored in a signed int because Java has no explicit unsigned
   *     support.
   * @return A signed 32-bit integer.
   */
  static int decodeZigZag32(final int n) {
    return (n >>> 1) ^ -(n & 1);
  }

  /**
   * Decode a ZigZag-encoded 64-bit signed value. ZigZag encodes signed integers into values that
   * can be efficiently encoded with varint. (Otherwise, negative values must be sign-extended to 64
   * bits to be varint encoded, thus always taking 10 bytes on the wire.)
   *
   * @param n A 64-bit integer, stored in a signed long because Java has no explicit unsigned
   *     support.
   * @return A signed 64-bit integer.
   */
  static long decodeZigZag64(final long n) {
    return (n >>> 1) ^ -(n & 1);
  }
}
