
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

public class TeePrintStream extends PrintStream {
        private final PrintStream second;

        public TeePrintStream(OutputStream main, PrintStream second) {
            super(main);
            
          //Date object
       	// Date date= new Date();
                //getTime() returns current time in milliseconds
       //	 long time = date.getTime();
                //Passed the milliseconds to constructor of Timestamp class 
       //	 Timestamp ts = new Timestamp(time);            
            this.second = second;            
        }
        
        
        public static String getCurrentTimeStamp() {
		    //SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
		  //  Date now = new Date();
		    //String strDate = sdfDate.format(now);
		    
		    //Date object
	       	 Date date= new Date();
	                //getTime() returns current time in milliseconds
	       	 long time = date.getTime();
	                //Passed the milliseconds to constructor of Timestamp class 
	       	 Timestamp ts = new Timestamp(time);		    
		    return ts.toString()+"=>   ";
		}
        

        /**
         * Closes the main stream. 
         * The second stream is just flushed but <b>not</b> closed.
         * @see java.io.PrintStream#close()
         */
        @Override
        public void close() {
            // just for documentation
            super.close();
        }

        @Override
        public void flush() {
            super.flush();
            second.flush();
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            super.write(buf, off, len);
            second.write(buf, off, len);
        }

        @Override
        public void write(int b) {
            super.write(b);
            second.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
            second.write(b);
        }
    }