import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ThreadSafeWriter{
	private final RandomAccessFile sharedFile;
	private final Object obj = new Object();
	
	ThreadSafeWriter(String filepath) throws IOException 
	{
		sharedFile = new RandomAccessFile(filepath, "rw");
	}

	public void writeChunk(long offset, String str) throws IOException
	{
		synchronized(obj){
			sharedFile.seek(offset);
			sharedFile.writeChars(str);
		}
	}

	public void close() throws IOException
	{
		if(sharedFile!=null)
			sharedFile.close();
	}
}

class RandomFileWriter{
	public static void main(String[] args) throws IOException
	{
		ThreadSafeWriter writer = new ThreadSafeWriter("test_file.txt");

		ExecutorService exec = Executors.newFixedThreadPool(2);
		exec.submit(() -> testWrite(writer, 1000, "chunk 2\n"));
		exec.submit(() -> testWrite(writer, 0, "chunk 1\n"));
		exec.submit(() -> testWrite(writer, 2000, "chunk 3\n"));

		exec.shutdown();
		writer.close();
	}

	private static void testWrite(ThreadSafeWriter writer, long offset, String str){
		try{
			writer.writeChunk(offset, str);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}


