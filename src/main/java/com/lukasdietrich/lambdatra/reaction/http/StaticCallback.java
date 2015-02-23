package com.lukasdietrich.lambdatra.reaction.http;

import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link HttpCallback} implementation for a simple fileserver.
 * 
 * @author Lukas Dietrich
 *
 */
public class StaticCallback implements HttpCallback {

	private final String[] INDICE = { "index.html", "index.htm" };
	
	private File source;
	private boolean dirListing;
	private long maxAge;
	
	/**
	 * Creates a simple fileserver as a {@link HttpCallback}
	 * 
	 * @param source the root directory
	 * @param dirListing whether or not directory contents should be listed
	 * @param maxAge 0 for no caching, or the number of seconds for a file to expire
	 */
	public StaticCallback(File source, boolean dirListing, long maxAge) {
		this.source = source;
		this.dirListing = dirListing;
		this.maxAge = maxAge;
	}
	
	/**
	 * Shorthand for {@link #StaticCallback(File, boolean, long)}
	 * <br>
	 * with <code>dirListing = false</code>, <code>maxAge = 0</code>
	 * @param source
	 */
	public StaticCallback(File source) {
		this(source, false, 0);
	}
	
	/**
	 * Shorthand for {@link #StaticCallback(File, boolean, long)}
	 * <br>
	 * with <code>maxAge = 0</code>
	 * @param source
	 * @param dirListing
	 */
	public StaticCallback(File source, boolean dirListing) {
		this(source, dirListing, 0);
	}
	
	/**
	 * Shorthand for {@link #StaticCallback(File, boolean, long)}
	 * <br>
	 * with <code>dirListing = false</code>
	 * @param source
	 * @param dirListing
	 */
	public StaticCallback(File source, long maxAge) {
		this(source, false, maxAge);
	}
	
	private void serveFile(File f, WrappedResponse res) throws IOException {
		if (maxAge > 0) {
			res.enableCache(maxAge);
		}
		
		String mime = Files.probeContentType(f.toPath());
		
		if (mime != null)
			res.setMime(mime);
		
		byte[] buf = new byte[4096];
		int len;
		
		InputStream  in  = new FileInputStream(f);
		
		while((len = in.read(buf)) > -1) {
			res.getBuffer().writeBytes(buf, 0, len);
		}
		
		in.close();
	}
	
	@Override
	public void call(WrappedRequest req, WrappedResponse res) throws IOException {
		File dest = new File(source, req.getPath());
		
		if (dest.exists()) {
			if (dest.isFile()) {
				serveFile(dest, res);
			} else {
				for (String i : INDICE) {
					File index = new File(dest, i);
					
					if (index.exists()) {
						serveFile(index, res);
						return;
					}
				}
				
				if (dirListing) {
					Map<String, Object> partials = new HashMap<>();
					File[] files = dest.listFiles();
					
					Arrays.sort(files, (a, b) ->  {
						if (a.isFile() && b.isDirectory()) {
							return 1;
						}
						
						if (a.isDirectory() && b.isFile()) {
							return -1;
						}
						
						return a.compareTo(b);
					});
					
					partials.put("path", req.getPath());
					partials.put("files", files);
					
					res.render(StaticCallback.class.getResource("/com/lukasdietrich/lambdatra/tmpl/dirlist.jade"), partials);
				} else {
					res.setStatus(HttpResponseStatus.FORBIDDEN);
					ByteBufUtil.writeUtf8(res.getBuffer(), "FORBIDDEN");
				}
			}
		} else {
			res.setStatus(HttpResponseStatus.NOT_FOUND);
			ByteBufUtil.writeUtf8(res.getBuffer(), "NOT FOUND");
		}
	}

}
