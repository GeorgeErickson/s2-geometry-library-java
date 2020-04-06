/*
 * Copyright 2019 Google Inc.
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

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.geometry.S2Shape.MutableEdge;
import com.google.common.geometry.S2ShapeAspect.ChainAspect.Multi;
import com.google.common.geometry.S2ShapeAspect.ChainAspect.Simple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Exercises all mixes of the S2ShapeAspects components. */
@GwtIncompatible("Insufficient support for generics")
public class S2ShapeAspectTest extends GeometryTestCase {
  private static S2Point A = makePoint("0:0");
  private static S2Point B = makePoint("0:1");
  private static S2Point C = makePoint("1:1");
  private static S2Point D = makePoint("1:0");
  
  // Open, Simple, Array and Packed
  
  public void testOpenSimpleEmpty() {
    S2Point[][] chains = {{}};
    S2Point[][] edges = {};
    check(chains, edges, new OpenSimpleArray(chains[0]));
    check(chains, edges, new OpenSimplePacked(chains[0]));
  }
  
  public void testOpenSimpleSingleton() {
    S2Point[][] chains = {{A}};
    S2Point[][] edges = {};
    check(chains, edges, new OpenSimpleArray(chains[0]));
    check(chains, edges, new OpenSimplePacked(chains[0]));
  }
  
  public void testOpenSimpleTriangle() {
    S2Point[][] chains = {{A, B, C}};
    S2Point[][] edges = {{A, B}, {B, C}};
    check(chains, edges, new OpenSimpleArray(chains[0]));
    check(chains, edges, new OpenSimplePacked(chains[0]));
  }
  
  private static class OpenSimpleArray extends Simple.Array implements Open {
    OpenSimpleArray(S2Point ... vertices) {
      super(Arrays.asList(vertices));
    }
  }
  
  private static class OpenSimplePacked extends Simple.Packed implements Open {
    OpenSimplePacked(S2Point ... vertices) {
      super(Arrays.asList(vertices));
    }
  }
  
  // Closed, Simple, Array and Packed
  
  public void testClosedSimpleEmpty() {
    S2Point[][] chains = {{}};
    S2Point[][] edges = {};
    check(chains, edges, new ClosedSimpleArray(chains[0]));
    check(chains, edges, new ClosedSimplePacked(chains[0]));
  }
  
  public void testClosedSimpleSingleton() {
    S2Point[][] chains = {{A}};
    S2Point[][] edges = {{A, A}};
    check(chains, edges, new ClosedSimpleArray(chains[0]));
    check(chains, edges, new ClosedSimplePacked(chains[0]));
  }
  
  public void testClosedSimpleTriangle() {
    S2Point[][] chains = {{A, B, C}};
    S2Point[][] edges = {{A, B}, {B, C}, {C, A}};
    check(chains, edges, new ClosedSimpleArray(chains[0]));
    check(chains, edges, new ClosedSimplePacked(chains[0]));
  }
  
  private static class ClosedSimpleArray extends Simple.Array implements Closed {
    ClosedSimpleArray(S2Point ... vertices) {
      super(Arrays.asList(vertices));
    }
  }
  
  private static class ClosedSimplePacked extends Simple.Packed implements Closed {
    ClosedSimplePacked(S2Point ... vertices) {
      super(Arrays.asList(vertices));
    }
  }
  
  // Open, Multi, Array and Packed
  
  public void testOpenMultiEmpty() {
    S2Point[][] chains = {};
    S2Point[][] edges = {};
    check(chains, edges, new OpenMultiArray(chains));
    check(chains, edges, new OpenMultiPacked(chains));
  }
  
  public void testOpenMultiEmptyChain() {
    // JUnit's assertThrows would be nice, but it isn't supported on GWT.
    try {
      new OpenMultiArray(new S2Point[][]{{}});
    } catch (IllegalArgumentException e) {
      assertEquals("Must have at least 1 edge.", e.getMessage());
    }
    try {
      new OpenMultiPacked(new S2Point[][]{{A, B}, {}});
    } catch (IllegalArgumentException e) {
      assertEquals("Must have at least 1 edge.", e.getMessage());
    }
  }
  
  public void testOpenMultiOneChain() {
    S2Point[][] chains = {{A, B, C}};
    S2Point[][] edges = {{A, B}, {B, C}};
    check(chains, edges, new OpenMultiArray(chains));
    check(chains, edges, new OpenMultiPacked(chains));
  }
  
  public void testOpenMultiTwoChains() {
    S2Point[][] chains = {{A, B, C}, {D, C}};
    S2Point[][] edges = {{A, B}, {B, C}, {D, C}};
    check(chains, edges, new OpenMultiArray(chains));
    check(chains, edges, new OpenMultiPacked(chains));
  }
  
  public void testOpenMultiPartialChain() {
    // Note that while we don't allow empty chains, degenerate chains have no edges.
    S2Point[][] chains = {{D}, {A, B, C}, {D}, {D, C}, {D}};
    S2Point[][] edges = {{A, B}, {B, C}, {D, C}};
    check(chains, edges, new OpenMultiArray(chains));
    check(chains, edges, new OpenMultiPacked(chains));
  }
  
  private static class OpenMultiArray extends Multi.Array implements Open {
    OpenMultiArray(S2Point[][] chains) {
      super(toLists(chains));
    }
  }
  
  private static class OpenMultiPacked extends Multi.Packed implements Open {
    OpenMultiPacked(S2Point[][] chains) {
      super(toLists(chains));
    }
  }
  
  // Closed, Multi, Array and Packed
  
  public void testClosedMultiEmpty() {
    S2Point[][] chains = {};
    S2Point[][] edges = {};
    check(chains, edges, new ClosedMultiArray(chains));
    check(chains, edges, new ClosedMultiPacked(chains));
  }
  
  public void testClosedMultiEmptyChain() {
    S2Point[][] chains = {{}};
    S2Point[][] edges = {};
    check(chains, edges, new ClosedMultiArray(chains));
    check(chains, edges, new ClosedMultiPacked(chains));
  }
  
  public void testClosedMultiOneChain() {
    S2Point[][] chains = {{A, B, C}};
    S2Point[][] edges = {{A, B}, {B, C}, {C, A}};
    check(chains, edges, new ClosedMultiArray(chains));
    check(chains, edges, new ClosedMultiPacked(chains));
  }
  
  public void testClosedMultiTwoChainsWithEmpties() {
    S2Point[][] chains = {{}, {A, B, C}, {}, {D, C}, {}};
    S2Point[][] edges = {{A, B}, {B, C}, {C, A}, {D, C}, {C, D}};
    check(chains, edges, new ClosedMultiArray(chains));
    check(chains, edges, new ClosedMultiPacked(chains));
  }
  
  public void testClosedMultiPartialChain() {
    S2Point[][] chains = {{D}, {A, B, C}, {D}, {D, C}, {D}};
    S2Point[][] edges = {{D, D}, {A, B}, {B, C}, {C, A}, {D, D}, {D, C}, {C, D}, {D, D}};
    check(chains, edges, new ClosedMultiArray(chains));
    check(chains, edges, new ClosedMultiPacked(chains));
  }
  
  private static class ClosedMultiArray extends Multi.Array implements Closed {
    ClosedMultiArray(S2Point[][] chains) {
      super(toLists(chains));
    }
  }
  
  public void testClosedMultiPacked() {
    
  }
  
  private static class ClosedMultiPacked extends Multi.Packed implements Closed {
    ClosedMultiPacked(S2Point[][] chains) {
      super(toLists(chains));
    }
  }
  
  // Snapped
  
  public void testSnapped() {
    // Do a sanity test of the [open,closed]x[simple,multi] mixes, having snapped ABC to abc.
    S2CellId ia = S2CellId.fromPoint(A);
    S2CellId ib = S2CellId.fromPoint(B);
    S2CellId ic = S2CellId.fromPoint(C);
    S2Point a = ia.toPoint();
    S2Point b = ib.toPoint();
    S2Point c = ic.toPoint();
    S2CellId[] simpleCells = {ia, ib, ic};
    S2Point[][] simpleChains = {{a, b, c}};
    S2Point[][] simpleOpenEdges = {{a, b}, {b, c}};
    S2Point[][] simpleClosedEdges = {{a, b}, {b, c}, {c, a}};
    check(simpleChains, simpleOpenEdges, new OpenSimpleSnapped(simpleCells));
    check(simpleChains, simpleClosedEdges, new ClosedSimpleSnapped(simpleCells));
    S2CellId[][] multiCells = {{ia, ib, ic}, {ic, ib, ia}};
    S2Point[][] multiChains = {{a, b, c}, {c, b, a}};
    S2Point[][] multiOpenEdges = {{a, b}, {b, c}, {c, b}, {b, a}};
    S2Point[][] multiClosedEdges = {{a, b}, {b, c}, {c, a}, {c, b}, {b, a}, {a, c}};
    check(multiChains, multiOpenEdges, new OpenMultiSnapped(multiCells));
    check(multiChains, multiClosedEdges, new ClosedMultiSnapped(multiCells));
  }
  
  private static class OpenSimpleSnapped extends Simple.Snapped implements Open {
    OpenSimpleSnapped(S2CellId ... vertices) {
      super(Arrays.asList(vertices));
    }
  }
  
  private static class ClosedSimpleSnapped extends Simple.Snapped implements Closed {
    ClosedSimpleSnapped(S2CellId ... vertices) {
      super(Arrays.asList(vertices));
    }
  }
  
  private static class OpenMultiSnapped extends Multi.Snapped implements Open {
    OpenMultiSnapped(S2CellId[][] chains) {
      super(toLists(chains));
    }
  }

  private static class ClosedMultiSnapped extends Multi.Snapped implements Closed {
    ClosedMultiSnapped(S2CellId[][] chains) {
      super(toLists(chains));
    }
  }
  
  private interface Open extends Undef, S2ShapeAspect.EdgeAspect.Open {}
  
  private interface Closed extends Undef, S2ShapeAspect.EdgeAspect.Closed {}
  
  /** Leaves dimension methods effectively unimplemented. */
  private interface Undef extends S2Shape, S2ShapeAspect.TopoAspect {
    @Override default boolean hasInterior() {
      throw new UnsupportedOperationException();
    }
    @Override default boolean containsOrigin() {
      throw new UnsupportedOperationException();
    }
    @Override default int dimension() {
      throw new UnsupportedOperationException();
    }
  }
  
  /** Verifies the expected vertices, chains, and edges. */
  private static void check(
      S2Point[][] chains,
      S2Point[][] edges,
      S2ShapeAspect.Mixed shape) {
    List<S2Point> vertices = ImmutableList.copyOf(Iterables.concat(toLists(chains)));
    assertEquals("Unexpected vertices", vertices, shape.vertices());
    assertEquals("Unexpected chains", toLists(chains), shape.chains());
    assertEquals("Unexpected edges", toLists(edges), edges(shape));
  }
  
  private static <T> List<List<T>> toLists(T[][] edges) {
    return Lists.transform(Arrays.asList(edges), Arrays::asList);
  }
  
  private static List<List<S2Point>> edges(S2ShapeAspect.Mixed shape) {
    List<List<S2Point>> edges = new ArrayList<>();
    MutableEdge edge = new MutableEdge();
    for (int i = 0; i < shape.numEdges(); i++) {
      shape.getEdge(i, edge);
      S2Point a = edge.getStart();
      S2Point b = edge.getEnd();
      int chainId = shape.chainId(i);
      int offset = i - shape.getChainStart(chainId);
      shape.getChainEdge(chainId, offset, edge);
      assertEquals(a, edge.a);
      assertEquals(b, edge.b);
      edges.add(Arrays.asList(a, b));
    }
    return edges;
  }
}
