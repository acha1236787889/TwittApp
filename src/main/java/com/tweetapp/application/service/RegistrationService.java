package com.tweetapp.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tweetapp.application.model.ForgotPassword;
import com.tweetapp.application.model.Login;
import com.tweetapp.application.model.Registration;
import com.tweetapp.application.model.Reply;
import com.tweetapp.application.model.Tweet;
import com.tweetapp.application.model.UpdateTweet;
import com.tweetapp.application.repo.RegistrationRepo;
import com.tweetapp.application.repo.TweetRepo;

import ch.qos.logback.classic.Logger;

import com.tweetapp.application.exception.*;

@Service
public class RegistrationService {
    @Autowired
	RegistrationRepo repo;
    @Autowired
    TweetRepo tweetRepo;
    
    public boolean h1;
    public String u1;
    //private static final Logger l=Logger
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaTemplate<String, List<Tweet>> kafkaTemplate1;
    
    public void checkNull(Registration r) throws InvalidFieldException{
    	if(r.getPassword().isEmpty()) {
    		throw new InvalidFieldException("Password field is mandatory");
    	}
    	if(r.getLogin_id().isEmpty()) {
    		throw new InvalidFieldException("LoginId field is mandatory");
    	}
    	if(r.getLast_name().isEmpty()) {
    		throw new InvalidFieldException("LastName field is mandatory");
    	}
    	if(r.getFirst_name().isEmpty()) {
    		throw new InvalidFieldException("FirstName field is mandatory");
    	}
    	if(r.getEmail().isEmpty()) {
    		throw new InvalidFieldException("Email field is mandatory");
    	}
    	
    	if((Integer)r.getContact_number()==null) {
    		throw new InvalidFieldException("Contact number field is mandatory");
    	}
    	if(r.getCnf_password().isEmpty()) {
    		throw new InvalidFieldException("Confirm password field is mandatory");
    	}
    }
    
    public void doRegistration(Registration r) throws InvalidFieldException{
    	Optional<Registration> p=repo.findById(r.getLogin_id())   ; 
    	if(p.isPresent()) {
    		throw new InvalidFieldException("this login id already exist. try new one");
    	}
    	else if(!r.getPassword().equals(r.getCnf_password())) {
    		throw new InvalidFieldException("Password and Confirm password should match");
    	}
    	else if(r.getEmail().equals(r.getLogin_id())) {
    		throw new InvalidFieldException("email and login id must be different");
    	}
       checkNull(r);
       repo.save(r);
      // kafkaTemplate.send("user-topic", "Saved successfully", r);
    }
    
    public List<Registration> getAllUsers(){
    	return repo.findAll();
    	//kafkaTemplate1.send("user-topic2", "ccz",repo.findAll());
    }
    
    public boolean login(String h,String p) {
    	if(repo.findById(h).isPresent()) {
    		Optional<Registration> o=repo.findById(h);
    		if(o.get().getPassword().equals(p)) {
    			h1=true;
    			u1=o.get().getLogin_id();
    			return true;
    		}
    	}
    	h1=false;
    	return false;
    }
    public Registration searchByUsername(String j) throws NotAvaliableException {
    	if(!repo.findById(j).isPresent()) {
    		System.out.println((j));
    		throw new NotAvaliableException("Username does not exist");
    	}
    	System.out.println("inside");
    	Registration r=repo.findById(j).get();
    	return r;
    }
  
public void postmessage(Tweet t1) throws NotAvaliableException {
	   Tweet t=new Tweet();
	   System.out.println(h1);
	   if(h1==true ) {
		  // Tweet t=new Tweet();
		   Tweet y=new Tweet();
		   Optional<Tweet> j=tweetRepo.findById(t1.getUsername());
		   if(!(j==null)) {
			   System.out.println(tweetRepo.findById(t1.getUsername()));
			   if(tweetRepo.findById(t1.getUsername()).isPresent()) {
		    y=tweetRepo.findById(t1.getUsername()).get();
			   }
		   }
		  System.out.print(t1.getUsername());
		   t.setUsername(t1.getUsername());
		   List<String> l=new ArrayList();
		   String l1 = null;
		   if(y!=null && y.getTweetmessage()!=null) {
		   l1=y.getTweetmessage();
		   }
		   if(l1!=null) {
	       l1=l1+t1.getTweetmessage()+",";
		   }
		   else {
			   l1=t1.getTweetmessage()+",";
		   }
		  t.setTweetmessage(l1);
		   
	   }
	   else {
		   throw new NotAvaliableException("Please Login first");
	   }
	   tweetRepo.save(t);
	   String p=t1.getTweetmessage();
	   //kafkaTemplate.send("user-topic", t.getUsername(), p);
	   
   }

 public boolean login2(Login l)
{
	 if(repo.findById(l.getUsername()).isPresent()) {
 		Optional<Registration> o=repo.findById(l.getUsername());
 		if(o.get().getPassword().equals(l.getPassword())) {
 			h1=true;
 			 u1=l.getUsername();
 			return true;
 		}
 	}
 	h1=false;
 	return false;
}
 public List<Tweet> getAllTweets(){
	 //kafkaTemplate.send("user-topic2", "ccz","Tweet List has been generated");
	 return tweetRepo.findAll();
	 
	 
	 
 }
 public void doForget(String username,ForgotPassword fg) throws NotAvaliableException {
	 Optional<Registration> o=repo.findById(u1);
	 if(!o.isPresent()) {
		 throw new NotAvaliableException("The username does not exist");
	 }
	 else {
		 o.get().setPassword(fg.getNewPassword());
		 o.get().setCnf_password(fg.getNewPassword());
	 }
	 repo.save(o.get());
 }
 
 public void updateTweet(String username,UpdateTweet u) throws NotAvaliableException {
	if(h1==true) {
		if(username.equals(u.getUsername()) && u1.equals(u.getUsername())) {
		Tweet t=tweetRepo.findById(username).get();
		
		String m=t.getTweetmessage();
		String p[]=m.split(",");
		System.out.println(u.getTweetMessagetoBeUpdated());
		for(int i=0;i<p.length;i++) {
			if(p[i].equalsIgnoreCase(u.getTweetMessagetoBeUpdated()) && p[i]!=null) {
				p[i]=u.getTweetmessage();
				System.out.println(p[i]);
			}
			if(p[i]!=null) {
			System.out.println(p[i]);
			}
		}
		System.out.print(p);
		String k="";
		for(int i=0;i<p.length;i++) {
			k=k+p[i]+","; 
		}
		
		System.out.println(k);
		t.setTweetmessage(k);
		tweetRepo.save(t);
		}
	}
	else if(h1==false) {
		throw new NotAvaliableException("Login Please");
	}
	else {
		throw new NotAvaliableException("Username does not exist");
	}
 }
 
 public void deleteTweet(String username) throws NotAvaliableException {
	 Optional<Tweet> t=tweetRepo.findById(username);
	 System.out.print(t);
	 if(h1==true && username.equals(u1)) {
		 
		 if(t.isPresent()) {
			 tweetRepo.deleteById(username);
		 }
			
		}
		else if(h1==false) {
			throw new NotAvaliableException("Login Please");
		}
		else if(!t.isPresent()){
			throw new NotAvaliableException("Username does not exist");
		}
 }
 
  public void likeTweet(String username) throws NotAvaliableException {
	  if(h1==true) {
	Tweet t=tweetRepo.findById(username).get();
	 int like=t.getLike();
	 like++;
	 t.setLike(like);
	 tweetRepo.save(t);
	  }
	  else {
		  throw new NotAvaliableException("Login First");
	  }
  }
  
  public void replyTweet(Reply r) throws NotAvaliableException {
	  if(h1==true) {
		  Optional<Tweet> t=tweetRepo.findById(r.getUsername());
		  if((t==null)) {
			  throw new NotAvaliableException("username does not exist");
		  }
		  else if(t.get().getTweetmessage().contains(r.getTweetmessage())){
			  List<String> l=new ArrayList<>();
			  l.add(r.getReply()+":"+u1+":"+r.getTweetmessage());
			  
			  t.get().setReply(r.getReply()+":"+u1+":"+r.getTweetmessage());
			  tweetRepo.save(t.get());
		  }
		  else if(!(t.get().getTweetmessage().contains(r.getTweetmessage()))) {
			  throw new NotAvaliableException("This tweet does not exist");
		  
	  }
	  }
		  
	  else  {
		  throw new NotAvaliableException("Login First");
	  }
	  String d=r.getReply()+":"+u1+":"+r.getTweetmessage();
	 // kafkaTemplate.send("user-topic1", r.getUsername(),d );
  }
  
  public void logout() {
	  h1=false;
	  System.out.println(h1);
  }
 
}


