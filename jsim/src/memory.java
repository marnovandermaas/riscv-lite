import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

class memory
{
    private int mem[];

    private boolean checkValidAddress(int byteaddress)
    {
	boolean valid;                            // check:
	valid = ((byteaddress & 0x3)==0)          //  alignment requirement
	     && ((byteaddress/4)<this.mem.length) //  upper bound
	     && (byteaddress>=0);                 //  lower bound
	if(!valid) {
	    if((byteaddress & 0x3)==0) {
		System.out.format("ERROR: out of range address sent to memory: 0x%08x = %d\n",
				  byteaddress, byteaddress);
	    } else {
		System.out.format("ERROR: incorrectly aligned address sent to memory: 0x%08x = 0b%s\n",
				  byteaddress,
				  String.format("%7s", Integer.toBinaryString(byteaddress)).replace(' ', '0')); // nasty hack to get exaclty 32 binary digits
	    }
	}
	return valid;
    }
    
    public int load(int byteaddress)
    { 
        if(this.checkValidAddress(byteaddress))
	    return this.mem[byteaddress/4];
	else
	    return 0xdead0000;
    }

    public void store(int byteaddress, int data)
    { 
        if(this.checkValidAddress(byteaddress))
	    this.mem[byteaddress/4] = data;
	// TODO raise exception on invalid address?
    }

    public void hexdump(int to, int from)
    {
	from = from/4;
	to = to/4;
	if(from<0)
	    from = 0;
	if(to>=mem.length)
	    to = mem.length-1;
	for(int a=to; a<=from; a++)
	    System.out.format("mem[0x%08x] = 0x%08x = %d\n",
			      a*4, this.mem[a], this.mem[a]);
    }
    
    public void memory(int memsizebytes, String filepath) throws IOException
    {
	// initialise the memory
	int memsizewords = memsizebytes/4;
	this.mem = new int [memsizewords];
	for(int a=0; a<memsizewords; a++) {
	    this.mem[a] = 0;
	}
	System.out.format("Memory size = %d words = %d bytes = %d KiB\n",
			  this.mem.length, this.mem.length*4, this.mem.length*4/1024);
	// load binary image into the memory
        byte [] b = Files.readAllBytes(Paths.get(filepath));
	// copy bytes from byte buffer into our word-sized mem[] array
	for(int a=0; (a<this.mem.length) && ((a*4+3)<b.length); a++) {
	    this.mem[a] = ((b[a*4+3] & 0xff)<<24)
		        | ((b[a*4+2] & 0xff)<<16)
    		        | ((b[a*4+1] & 0xff)<< 8)
		        | ((b[a*4+0] & 0xff));
	}
    } 
}
