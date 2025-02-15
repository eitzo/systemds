/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.runtime.compress.colgroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sysds.runtime.compress.CompressionSettings;
import org.apache.sysds.runtime.compress.colgroup.mapping.MapToFactory;
import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.utils.MemoryEstimates;

public class ColGroupSizes {
	protected static final Log LOG = LogFactory.getLog(ColGroupSizes.class.getName());

	public static long estimateInMemorySizeGroup(int nrColumns) {
		long size = 0;
		size += 16; // Object header
		size += 4; // int numRows,
		size += 1; // _zeros boolean reference
		size += 1; // _lossy boolean reference
		size += 2; // padding
		size += MemoryEstimates.intArrayCost(nrColumns);
		return size;
	}

	public static long estimateInMemorySizeGroupValue(int nrColumns, int nrValues, boolean lossy) {
		long size = estimateInMemorySizeGroup(nrColumns);
		size += 8; // Dictionary Reference.
		if(lossy)
			size += QDictionary.getInMemorySize(nrValues);
		else
			size += Dictionary.getInMemorySize(nrValues);

		return size;
	}

	public static long estimateInMemorySizeDDC(int nrCols, int numTuples, int dataLength, boolean lossy) {
		// LOG.error("Arguments for DDC memory Estimate " + nrCols + " " + numTuples + " " + dataLength + " " + lossy);
		long size = estimateInMemorySizeGroupValue(nrCols, numTuples * nrCols, lossy);
		size += 8; // Map toFactory reference;
		size += MapToFactory.estimateInMemorySize(dataLength, numTuples);
		return size;
	}

	public static long estimateInMemorySizeOffset(int nrColumns, int nrValues, int pointers, int offsetLength,
		boolean lossy) {
		long size = estimateInMemorySizeGroupValue(nrColumns, nrValues, lossy);
		size += MemoryEstimates.intArrayCost(pointers);
		size += MemoryEstimates.charArrayCost(offsetLength);
		return size;
	}

	public static long estimateInMemorySizeOLE(int nrColumns, int nrValues, int offsetLength, int nrRows,
		boolean lossy) {
		// LOG.error(nrColumns + " " + nrValues + " " + offsetLength + " " + nrRows + " " + lossy);
		nrColumns = nrColumns > 0 ? nrColumns : 1;
		offsetLength += (nrRows / CompressionSettings.BITMAP_BLOCK_SZ) * 2;
		long size = 0;
		size = estimateInMemorySizeOffset(nrColumns, nrValues, (nrValues / nrColumns) + 1, offsetLength, lossy);
		if(nrRows > CompressionSettings.BITMAP_BLOCK_SZ * 2) {
			size += MemoryEstimates.intArrayCost((int) nrValues / nrColumns);
		}
		return size;
	}

	public static long estimateInMemorySizeRLE(int nrColumns, int nrValues, int nrRuns, int nrRows, boolean lossy) {
		nrColumns = nrColumns > 0 ? nrColumns : 1;
		int offsetLength = (nrRuns) * 2;
		long size = estimateInMemorySizeOffset(nrColumns, nrValues, (nrValues / nrColumns) + 1, offsetLength, lossy);

		return size;
	}

	public static long estimateInMemorySizeSDC(int nrColumns, int nrValues, int nrRows, int largestOff, boolean lossy) {
		long size = estimateInMemorySizeGroupValue(nrColumns, nrValues, lossy);
		size += MemoryEstimates.intArrayCost(nrRows - largestOff);
		// size += MemoryEstimates.byteArrayCost(nrRows- largestOff);
		size += MapToFactory.estimateInMemorySize(nrRows - largestOff, nrValues);
		return size;
	}

	public static long estimateInMemorySizeSDCSingle(int nrColumns, int nrValues, int nrRows, int largestOff,
		boolean lossy) {
		long size = estimateInMemorySizeGroupValue(nrColumns, nrValues, lossy);
		// size += MemoryEstimates.intArrayCost(nrRows - largestOff);
		size += MemoryEstimates.byteArrayCost(nrRows - largestOff);
		return size;
	}

	public static long estimateInMemorySizeCONST(int nrColumns, int nrValues, boolean lossy) {
		long size = estimateInMemorySizeGroupValue(nrColumns, nrValues, lossy);
		return size;
	}

	public static long estimateInMemorySizeEMPTY(int nrColumns) {
		long size = estimateInMemorySizeGroup(nrColumns);
		size += 8; // null pointer to _dict
		return size;
	}

	public static long estimateInMemorySizeUncompressed(int nrRows, int nrColumns, double sparsity) {
		long size = 0;
		// Since the Object is a col group the overhead from the Memory Size group is added
		size += estimateInMemorySizeGroup(nrColumns);
		size += 8; // reference to MatrixBlock.
		size += MatrixBlock.estimateSizeInMemory(nrRows, nrColumns, sparsity);
		return size;
	}
}
