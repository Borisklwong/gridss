package au.edu.wehi.idsv.util;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.util.Log;

import java.io.IOException;

import com.google.common.collect.ImmutableMap;

/**
 * Buffers entire reference to enable efficient random lookup of sequences
 * @author cameron.d
 *
 */
public class BufferedReferenceSequenceFile implements ReferenceSequenceFile {
	private static final Log log = Log.getInstance(BufferedReferenceSequenceFile.class);
	private final ReferenceSequenceFile underlying;
	/**
	 * Cached contigs
	 */
	private volatile ImmutableMap<String, ReferenceSequence> cache = ImmutableMap.of();
	public BufferedReferenceSequenceFile(ReferenceSequenceFile underlying) {
		this.underlying = underlying;
	}
	@Override
	public SAMSequenceDictionary getSequenceDictionary() {
		return underlying.getSequenceDictionary();
	}
	@Override
	public ReferenceSequence nextSequence() {
		return underlying.nextSequence();
	}
	@Override
	public void reset() {
		underlying.reset();
	}
	@Override
	public boolean isIndexed() {
		return underlying.isIndexed();
	}
	/**
	 * Updates the cache to include the new contig
	 * @param contig
	 */
	private synchronized ReferenceSequence addToCache(String contig) {
		ReferenceSequence seq = cache.get(contig);
		if (seq != null) {
			// already populated by another thread while we were waiting to enter
			// this synchronized block
			return seq;
		}
		log.debug("Caching reference genome contig ", contig);
		seq = underlying.getSequence(contig);
		cache = ImmutableMap.<String, ReferenceSequence>builder()
				.putAll(cache)
				.put(contig, seq)
				.build();
		return seq;
	}
	@Override
	public ReferenceSequence getSequence(String contig) {
		ReferenceSequence seq = cache.get(contig);
		if (seq == null) {
			seq = addToCache(contig);
		}
		return seq;
	}
	@Override
	public ReferenceSequence getSubsequenceAt(String contig, long start, long stop) {
        int length = (int)(stop - start + 1);
		ReferenceSequence fullContig = getSequence(contig);
		if (length > fullContig.length()) {
			throw new IllegalArgumentException("subsequence out of contig bounds");
		}
		if (start > stop + 1) {
			throw new IllegalArgumentException("start after stop");
		}
		byte[] target = new byte[length];
		System.arraycopy(fullContig.getBases(), (int) (start - 1), target, 0, target.length);
		return new ReferenceSequence(fullContig.getName(), fullContig.getContigIndex(), target);
	}
	@Override
	public void close() throws IOException {
		underlying.close();	
	}
}
