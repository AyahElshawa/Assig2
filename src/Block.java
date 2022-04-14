import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class Block {		 
	 
	private int height;
	private BlockHeader header;
	private String[] transactions;
	private int noOfTransactions;
	public String hash;	
	
	
	public void setBlock(int height, BlockHeader bh, String[] transactions, int noOfTransactions) {
		this.height = height;
		this.header = bh;
		this.transactions = transactions;
		this.noOfTransactions = this.transactions.length;
		this.hash = getHash();
	}
	
	public String getPreviousHash() {
		return this.header.previousBlockHash;
	}
	
	public int getHeight() {
		return this.height;
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
	
	
	public String getBlock() {			
		return 	"Height"  					+ this.height +
				"Header" 	   	        	+ header.getHeader()+
				"Transactions" 				+ this.transactions +
				"Number of Transactions"  	+ this.noOfTransactions + 
				"Hash" 						+ hash;
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
		private int difficulty = 2;
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
		 ArrayList<String> transactions = new ArrayList<>();

		 
		 BlockChain(){
			 createGenesisBlock();
			 mineBlock();
		 }
		 
		 
		 public Block createGenesisBlock() {
			 addTransaction("Sarah sent 10 BTC to James");
			
			 return mineBlock();
		 }
		 
		 
		 public Block lastBlock() {
			 if (blockChain.isEmpty()) {
				return null;
			}
			 return this.blockChain.get(blockChain.size() - 1);
		 }
		 
		 
		 public boolean addBlock(Block newBlock) {
			 
			 String previousHash;
			 //add the latest block as the previous block for the current block
			 if (blockChain.size() == 0) {
				System.out.println("empty chain");
				newBlock.header.previousBlockHash = "0";
			 }
			 else {
				previousHash = lastBlock().hash;
				System.out.println(previousHash);
				newBlock.header.previousBlockHash = lastBlock().hash;
			 }
			 newBlock.hash = PoW(newBlock);			 			
			 blockChain.add(newBlock);
			 return true;
		 }
		 
		 
		 public Boolean validBlockChain() {	
			 //don't start with genesis block, just validate the second block 
			 for (int i = 1; i < this.blockChain.size(); i++) {
			      Block currentBlock = this.blockChain.get(i);
			      Block previousBlock = this.blockChain.get(i - 1);

			      //recalculate the current block's hash and see 
			      //if it's equal to the already set hash(to ensure no changes have been made to the block) 
			      if (currentBlock.hash != currentBlock.getHash()) {
			    	  return false;
			      }
			      //do the same thing with the previous block of the current hash
			      if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
			    	  return false;
			      }
			    }
			 	//if none of the above is invalid, then the block is valid
			    return true;		 
		 }
		 
		 
		 public String PoW(Block currentBlock) {			 
			 currentBlock.header.nonce = 0;
			 String hash = currentBlock.getHash();
			 
			 //using a regex to make sure the block fits in the required difficulty pattern
			 while (!hash.matches("^0{"+currentBlock.header.difficulty+"}[a-z1-9]{62}$")) {
				 currentBlock.header.nonce++;
				 hash = currentBlock.getHash();				 
			}
			 return hash;
		 }
		 
		 
		 public void addTransaction(String transaction) {
			 transactions.add(transaction);			 
		 }
		 
		 
		 public Block mineBlock() {
			 Block block;
			 String[] data = new String[transactions.size()];
		     data = transactions.toArray(data);
			 
			 //if it's the genesis block, everything will be added manually
			 if (blockChain.size() == 0) {
				 block = new Block();
				 Block.BlockHeader bHeader = new Block.BlockHeader(1, "0", "0", System.currentTimeMillis()*10000, 2);
				 block.setBlock(0,  bHeader, data, transactions.size());
				 block.hash = PoW(block);

			 }else {
				block = new Block();
				Block.BlockHeader bHeader = new Block.BlockHeader(1, PoW(lastBlock()), "0", System.currentTimeMillis()*10000, 2);
				block.setBlock(lastBlock().getHeight()+1,  bHeader, data, transactions.size());
				block.hash = PoW(block);
			}
			 
			 blockChain.add(block);	
			 transactions.clear(); 
			 
			 return block;
		 }

		 
	 }
	
	
	public static void main(String[] args) {			
	
		Block.BlockChain bChain= new Block.BlockChain();
		
		bChain.addTransaction("sam to Cat 1 btc");
		bChain.addTransaction("sam to Cat 20 btc");
		bChain.addTransaction("sam to Cat 10 btc");
		bChain.mineBlock();
		
		bChain.addTransaction("cat to sam 10 btc");
		bChain.addTransaction("cat to sam 1 btc");
		bChain.addTransaction("cat to sam 20 btc");
		bChain.mineBlock();
		
		bChain.addTransaction("cat to sam 10 btc");
		bChain.addTransaction("cat to sam 1 btc");
		bChain.addTransaction("cat to sam 20 btc");
		bChain.mineBlock();
		
		for (Block b : bChain.blockChain) {
			System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(b));
		}
		
		
//		 created a Json file to save the Blockchain in it
		FileWriter file;
		try {
			file = new FileWriter("output.json");
			for (Block block : bChain.blockChain) {
				file.write(new GsonBuilder().setPrettyPrinting().create().toJson(block));				
			}
			file.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
