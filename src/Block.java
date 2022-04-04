import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Block {		 
	 
	private int blockSize;
	private BlockHeader header;
	private String[] transactions;
	private int noOfTransactions;
	public String hash;	
	
	
	public void setBlock(int size, BlockHeader bh, String[] transactions, int noOfTransactions) {
		this.blockSize = size;
		this.header = bh;
		this.transactions = transactions;
		this.noOfTransactions = this.transactions.length;
		this.hash = getHash();
	}
	
	public String getPreviousHash() {
		return this.header.previousBlockHash;
	}

 

	public String getHash() {
		//a string buffer to return the hash in creating the hash
		StringBuffer sb = new StringBuffer() ;
				
		try {			
//			getting the (SHA-256) hash function from MessageDigest class
			MessageDigest hash = MessageDigest.getInstance("SHA-256");
//			hashing the header using (digest) method that returns the hash as a byte array
			byte[] hashArray = hash.digest(header.getHeader().getBytes("UTF-8"));
			
//			converting the hashed byte-array to (hex) and appending it to the StringBuffer
			for (byte b : hashArray) {
		        sb.append(String.format("%02x", b));	
		    }			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return sb.toString();
	}
	
	
	
	
	 public String mine(int difficulty) {
		 this.header.nonce = 0;
		 
		 //regex expression to test hahs begining
		 while (!this.getHash().matches("0{"+this.header.difficulty+"}[a-z0-9]{62}")) {
			 this.header.nonce++;
			 hash = this.getHash();
		}
		 return hash;			
	 }
	
	
	
	public String getBlock() {			
		return 	"Size"  					+ this.blockSize +
				"Header" 	   	        	+ header.getHeader()+
				"Transactions" 				+ this.transactions +
				"Number of Transactions"  	+ this.noOfTransactions + 
				"Hash" 						+ getHash();
	}
	   
	
	static public void blocksExplorer(ArrayList<Block> blocks ) {
		
		for (Block b : blocks) {
/*			GsonBuilder library converts objects to (json) objects using (create().toJson()) methods
			and (setPrettyPrinting) method to print it in a readable format */
			System.out.println((new GsonBuilder().setPrettyPrinting().create().toJson(b)));
			System.out.println();
		}
	}
	
	//an inner class (BlockHeader) to save block's header details
	 static class BlockHeader {
		
		private int version;
		private String previousBlockHash;
		private String merkleRoot;
		private long timestamp;
		private int difficulty;
		private int nonce;		
		
		public BlockHeader(int version, String previousBlockHash, String merkleRoot, long timestamp, int difficulty) {
			this.version = version;
			this.previousBlockHash = previousBlockHash;
			this.merkleRoot = merkleRoot;
			this.timestamp = timestamp;
			this.difficulty = difficulty;			
		}
		
		public int getNonce() {
			return this.nonce;
		}
		
		public int getDifficulty() {
			return this.difficulty;
		}
		
		
		
//		(getHeader) method to return header details
		public String getHeader() {			
			return 	"Version"  					+ this.version +
					"Previous Block Root" 	   	+ this.previousBlockHash +
					"Merkle Root" 				+ this.merkleRoot +
					"Nonce"      				+ this.nonce +
					"Timestamp"  				+ this.timestamp + 
					"Difficulty"  				+ this.difficulty;	
		}
	 }
	 
	 
	 
	 
	 
	static class BlockChain {
		 ArrayList<Block> blockChain = new ArrayList<>();
		 ArrayList<String> unconfirmedTransactions = new ArrayList<>();

		 
		 BlockChain(){
			 ArrayList<Block> blockchain = new ArrayList<>();
			 blockchain.add(createGenesisBlock());
		 }
		 
		 
		 public Block createGenesisBlock() {
			Block genesisblock = new Block();
			String[] genesisBlockTransactions = {"Alex sent James 10 Solanas", "Bob sent David 20 Solanas"};
			Block.BlockHeader bHeader = new Block.BlockHeader(1, "0", "0", System.currentTimeMillis()*10000, 2);
			genesisblock.setBlock(1, bHeader, genesisBlockTransactions, 2);

			 return genesisblock;
		 }
		 
		 
		 public Block lastBlock() {
			 return this.blockChain.get(blockChain.size() - 1);
		 }
		 
		 
		 public Boolean addBlock(Block newBlock) {
			 Block.BlockHeader bHeader = new Block.BlockHeader(1, lastBlock().getHash(), "0", System.currentTimeMillis()*10000, 2);
			 newBlock.header = bHeader;
			 newBlock.mine(newBlock.header.getDifficulty());
			 this.blockChain.add(newBlock);
						
			 return true;
		 }
		 
		 
		 public Boolean validBlockChain() {			 
			 for (int i = 1; i < this.blockChain.size(); i++) {
			      Block currentBlock = this.blockChain.get(i);
			      Block previousBlock = this.blockChain.get(i - 1);

			      if (previousBlock.hash != currentBlock.getPreviousHash()) {
			        return false;
			      }

			      if (currentBlock.hash != currentBlock.getHash()) {
			        return false;
			      }
			    }

			    return true;		 
		 }
		 
		 
		 public String PoW(Block currentBlock) {
			 
			 currentBlock.header.nonce = 0;
			 String hash = currentBlock.getHash();
			 
			 while (hash.matches("0{"+currentBlock.header.difficulty+"}[a-z0-9]{62}")) {
				 currentBlock.header.nonce++;
				 hash = currentBlock.getHash();
			}
			 return hash;
		 }

		 
	 }
	
	
	public static void main(String[] args) {			
		
		Block thirdblock = new Block();
		String[] thirdBlockTransactions = {"Alex sent James 10 Solanas", "Bob sent David 20 Solanas"};
		Block.BlockHeader bHeader = new Block.BlockHeader(1, "0", "0", System.currentTimeMillis()*10000, 2);
		thirdblock.setBlock(1, bHeader, thirdBlockTransactions, 2);

		
		Block secondBlock = new Block();
		String[] block2Transactions = {"Ben sent Joe 10 Solanas", "Sarah sent David 20 Solanas"};
		Block.BlockHeader b2Header = new Block.BlockHeader(1, thirdblock.getHash(), "0", System.currentTimeMillis()*10000, 2);		
		secondBlock.setBlock(1, b2Header, block2Transactions, 0);
		
		Block.BlockChain bChain= new Block.BlockChain();
		bChain.addBlock(secondBlock);
		bChain.addBlock(thirdblock);

		
		// created a Json file to save the Blockchain in it
		FileWriter file;
		try {
			file = new FileWriter("output.json");
			for (Block getBlock : bChain.blockChain) {
				file.write(new GsonBuilder().setPrettyPrinting().create().toJson(getBlock));				
			}
			file.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}












