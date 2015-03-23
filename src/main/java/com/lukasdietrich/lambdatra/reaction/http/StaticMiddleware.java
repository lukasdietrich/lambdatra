package com.lukasdietrich.lambdatra.reaction.http;

import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * {@link MiddlewareCallback} implementation for a simple fileserver.
 * 
 * @author Lukas Dietrich
 *
 */
public class StaticMiddleware<S> implements MiddlewareCallback<S> {

	private final String[] INDICE = { "index.html", "index.htm" };
	
	private File source;
	private long maxAge;
	
	/**
	 * Creates a simple fileserver as a {@link HttpCallback}
	 * 
	 * @param source the root directory
	 * @param maxAge 0 for no caching, or the number of seconds for a file to expire
	 */
	public StaticMiddleware(File source, long maxAge) {
		this.source = source;
		this.maxAge = maxAge;
	}
	
	/**
	 * Shorthand for {@link #StaticMiddleware(File, long)}
	 * <br>
	 * with <code>maxAge = 0</code>
	 * @param source path of static folder
	 */
	public StaticMiddleware(File source) {
		this(source, 0);
	}
	
	private void serveFile(File f, WrappedResponse<S> res) throws IOException {
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
	public boolean call(WrappedRequest<S> req, WrappedResponse<S> res) throws IOException {
		File dest = new File(source, req.getPath());
		
		if (dest.exists()) {
			if (dest.isFile()) {
				serveFile(dest, res);
			} else {
				for (String i : INDICE) {
					File index = new File(dest, i);
					
					if (index.exists()) {
						serveFile(index, res);
						return true;
					}
				}

				res.setStatus(HttpResponseStatus.FORBIDDEN);
				ByteBufUtil.writeUtf8(res.getBuffer(), "FORBIDDEN");
			}
			
			return true;
		}
		
		return false;
	}

}
