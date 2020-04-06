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

import static com.google.common.geometry.EncodedInts.decodeZigZag32;
import static com.google.common.geometry.EncodedInts.decodeZigZag64;
import static com.google.common.geometry.EncodedInts.encodeZigZag32;
import static com.google.common.geometry.EncodedInts.encodeZigZag64;

import com.google.common.annotations.GwtCompatible;
import junit.framework.TestCase;

@GwtCompatible
public class EncodedIntsTest extends TestCase {

  public void testEncodeZigZag32() {
    assertEquals(0, encodeZigZag32(0));
    assertEquals(1, encodeZigZag32(-1));
    assertEquals(2, encodeZigZag32(1));
    assertEquals(3, encodeZigZag32(-2));
    assertEquals(0x7FFFFFFE, encodeZigZag32(0x3FFFFFFF));
    assertEquals(0x7FFFFFFF, encodeZigZag32(0xC0000000));
    assertEquals(0xFFFFFFFE, encodeZigZag32(0x7FFFFFFF));
    assertEquals(0xFFFFFFFF, encodeZigZag32(0x80000000));

    // Some easier-to-verify round-trip tests.  The inputs (other than 0, 1, -1)
    // were chosen semi-randomly via keyboard bashing.
    assertEquals(0, encodeZigZag32(decodeZigZag32(0)));
    assertEquals(1, encodeZigZag32(decodeZigZag32(1)));
    assertEquals(-1, encodeZigZag32(decodeZigZag32(-1)));
    assertEquals(14927, encodeZigZag32(decodeZigZag32(14927)));
    assertEquals(-3612, encodeZigZag32(decodeZigZag32(-3612)));
  }

  public void testEncodeZigZag64() {
    assertEquals(0, encodeZigZag64(0));
    assertEquals(1, encodeZigZag64(-1));
    assertEquals(2, encodeZigZag64(1));
    assertEquals(3, encodeZigZag64(-2));
    assertEquals(0x000000007FFFFFFEL, encodeZigZag64(0x000000003FFFFFFFL));
    assertEquals(0x000000007FFFFFFFL, encodeZigZag64(0xFFFFFFFFC0000000L));
    assertEquals(0x00000000FFFFFFFEL, encodeZigZag64(0x000000007FFFFFFFL));
    assertEquals(0x00000000FFFFFFFFL, encodeZigZag64(0xFFFFFFFF80000000L));
    assertEquals(0xFFFFFFFFFFFFFFFEL, encodeZigZag64(0x7FFFFFFFFFFFFFFFL));
    assertEquals(0xFFFFFFFFFFFFFFFFL, encodeZigZag64(0x8000000000000000L));

    // Some easier-to-verify round-trip tests.  The inputs (other than 0, 1, -1)
    // were chosen semi-randomly via keyboard bashing.
    assertEquals(0, encodeZigZag64(decodeZigZag64(0)));
    assertEquals(1, encodeZigZag64(decodeZigZag64(1)));
    assertEquals(-1, encodeZigZag64(decodeZigZag64(-1)));
    assertEquals(14927, encodeZigZag64(decodeZigZag64(14927)));
    assertEquals(-3612, encodeZigZag64(decodeZigZag64(-3612)));

    assertEquals(856912304801416L, encodeZigZag64(decodeZigZag64(856912304801416L)));
    assertEquals(-75123905439571256L, encodeZigZag64(decodeZigZag64(-75123905439571256L)));
  }

  public void testDecodeZigZag32() {
    assertEquals(0, decodeZigZag32(0));
    assertEquals(-1, decodeZigZag32(1));
    assertEquals(1, decodeZigZag32(2));
    assertEquals(-2, decodeZigZag32(3));
    assertEquals(0x3FFFFFFF, decodeZigZag32(0x7FFFFFFE));
    assertEquals(0xC0000000, decodeZigZag32(0x7FFFFFFF));
    assertEquals(0x7FFFFFFF, decodeZigZag32(0xFFFFFFFE));
    assertEquals(0x80000000, decodeZigZag32(0xFFFFFFFF));
  }

  public void testDecodeZigZag64() {
    assertEquals(0, decodeZigZag64(0));
    assertEquals(-1, decodeZigZag64(1));
    assertEquals(1, decodeZigZag64(2));
    assertEquals(-2, decodeZigZag64(3));
    assertEquals(0x000000003FFFFFFFL, decodeZigZag64(0x000000007FFFFFFEL));
    assertEquals(0xFFFFFFFFC0000000L, decodeZigZag64(0x000000007FFFFFFFL));
    assertEquals(0x000000007FFFFFFFL, decodeZigZag64(0x00000000FFFFFFFEL));
    assertEquals(0xFFFFFFFF80000000L, decodeZigZag64(0x00000000FFFFFFFFL));
    assertEquals(0x7FFFFFFFFFFFFFFFL, decodeZigZag64(0xFFFFFFFFFFFFFFFEL));
    assertEquals(0x8000000000000000L, decodeZigZag64(0xFFFFFFFFFFFFFFFFL));
  }
}
