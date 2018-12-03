# Copyright 2018 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================
"""Tests for the ragged.segment_ids_to_row_splits() op."""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from tensorflow.python.framework import constant_op
from tensorflow.python.framework import test_util
from tensorflow.python.ops import ragged
from tensorflow.python.platform import googletest


class RaggedSplitsToSegmentIdsOpTest(test_util.TensorFlowTestCase):

  @test_util.run_deprecated_v1
  def testDocStringExample(self):
    segment_ids = [0, 0, 0, 2, 2, 3, 4, 4, 4]
    expected = [0, 3, 3, 5, 6, 9]
    splits = ragged.segment_ids_to_row_splits(segment_ids)
    with self.test_session():
      self.assertEqual(splits.eval().tolist(), expected)

  @test_util.run_deprecated_v1
  def testEmptySegmentIds(self):
    # Note: the splits for an empty ragged tensor contains a single zero.
    segment_ids = ragged.segment_ids_to_row_splits([])
    with self.test_session():
      self.assertEqual(segment_ids.eval().tolist(), [0])

  def testErrors(self):
    self.assertRaisesRegexp(TypeError,
                            r'segment_ids must be an integer tensor.*',
                            ragged.segment_ids_to_row_splits,
                            constant_op.constant([0.5]))
    self.assertRaisesRegexp(ValueError, r'Shape \(\) must have rank 1',
                            ragged.segment_ids_to_row_splits, 0)
    self.assertRaisesRegexp(ValueError, r'Shape \(1, 1\) must have rank 1',
                            ragged.segment_ids_to_row_splits, [[0]])

  @test_util.run_deprecated_v1
  def testNumSegments(self):
    segment_ids = [0, 0, 0, 2, 2, 3, 4, 4, 4]
    num_segments = 7
    expected = [0, 3, 3, 5, 6, 9, 9, 9]
    splits = ragged.segment_ids_to_row_splits(segment_ids, num_segments)
    with self.test_session():
      self.assertEqual(splits.eval().tolist(), expected)

  @test_util.run_deprecated_v1
  def testUnsortedSegmentIds(self):
    # Segment ids are not required to be sorted.
    segment_ids = [0, 4, 3, 2, 4, 4, 2, 0, 0]
    splits1 = ragged.segment_ids_to_row_splits(segment_ids)
    expected1 = [0, 3, 3, 5, 6, 9]

    splits2 = ragged.segment_ids_to_row_splits(segment_ids, 7)
    expected2 = [0, 3, 3, 5, 6, 9, 9, 9]
    with self.test_session():
      self.assertEqual(splits1.eval().tolist(), expected1)
      self.assertEqual(splits2.eval().tolist(), expected2)


if __name__ == '__main__':
  googletest.main()
