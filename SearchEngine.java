import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class SearchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings) 
    public DirectedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    SearchEngine() {
    // Below is the directory that contains all the internet files
        HtmlParsing.internetFilesLocation = "internetFiles";
        wordIndex = new HashMap<String, LinkedList<String> > ();  
        internet = new DirectedGraph();    
    } // end of constructor//2017
    
    
    // Returns a String description of a searchEngine
    public String toString () {
        return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
       /* Hints
          0) This should take about 50-70 lines of code (or less)
          1) To parse the content of the url, call
          htmlParsing.getContent(url), which returns a LinkedList of Strings 
          containing all the words at the given url. Also call htmlParsing.getLinks(url).
          and assign their results to a LinkedList of Strings.
          2) To iterate over all elements of a LinkedList, use an Iterator,
          as described in the text of the assignment
          3) Refer to the description of the LinkedList methods at
          http://docs.oracle.com/javase/6/docs/api/ .
          You will most likely need to use the methods contains(String s), 
          addLast(String s), iterator()
          4) Refer to the description of the HashMap methods at
          http://docs.oracle.com/javase/6/docs/api/ .
          You will most likely need to use the methods containsKey(String s), 
          get(String s), put(String s, LinkedList l).  
       */
        //add the url vertex to the map
        internet.addVertex(url);
        //set the url as visited
        internet.setVisited(url, true);
        Stack<String> q = new Stack<String>();
        //put the url into the stack
        q.push(url);
        //if the stack is not empty then pop it and implement the wordIndex
        while(!q.empty()){
      	  //pop the link
      	  String link = q.pop();
      	  LinkedList<String> content = HtmlParsing.getContent(link);
            Iterator<String> i = content.iterator();  
            while(i.hasNext()){
                String word = i.next();
                //if the word is already included in the keyword of wordIndex
                if((wordIndex.containsKey(word))){
              	  //if the wordList does not contain the url then add it into the wordIndex
                    if(!(wordIndex.get(word).contains(url))){
                        wordIndex.get(word).addLast(url);
                      }
                }
                //if the word is not included in the keyword of wordIndex then add the keyword and link to the wordIndex
                else{
                    LinkedList<String> newWordlinks = new LinkedList<String>();
                    newWordlinks.addLast(url);
                    wordIndex.put(word, newWordlinks);
                  }
                }
        LinkedList<String> links = HtmlParsing.getLinks(link);
        
        Iterator<String> j = links.iterator();
          while(j.hasNext()){
            String next = j.next();
            //and edge between the two vertices
            internet.addEdge(link, next);
            //if the internet has not been visited then use recursive algorithm 
            if(!(internet.getVisited(next))){
              traverseInternet(next);
            }
          }
        }
    }
    
    // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       This method will probably fit in about 30 lines.
    */
    void computePageRanks() {
        //first put the vertices into the linkedlist
        LinkedList<String> vertices = internet.getVertices();
        Iterator<String> i = vertices.iterator();
        while(i.hasNext()){
          //and set every page default as 1
          internet.setPageRank(i.next(), 1);
        }
        //replicate 100 times
        for(int k = 0; k<100; k++){
          i = vertices.iterator();
          while(i.hasNext()){
            String a = i.next();
            Iterator<String> j = internet.getEdgesInto(a).iterator();
            double rank = 0.5;
            while(j.hasNext()){
              String next = j.next();
              //use the calculation to assign the different ranks into pages
              rank += 0.5*(internet.getPageRank(next)/internet.getOutDegree(next));
            }
            internet.setPageRank(a, rank);
            }
          }
     }
// end of computePageRanks
    
 
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
        double pageRank = 0.0;
        LinkedList<String> links = new LinkedList<String>();
        //lower case the letter of the query
        query = query.toLowerCase();
        //put the links together that contains the query
        if(wordIndex.containsKey(query)){
          links = wordIndex.get(query);
        }
        Iterator<String> i = links.iterator();
        String best = "";
        while(i.hasNext()){
        	String a = i.next();
          double pageRanks = internet.getPageRank(a);
          //if the pageRanks is larger than pageRank then replace the number
          if(pageRanks > pageRank){
        	 pageRank = pageRanks;
             best = a;
          }
        }
        System.out.println("p.r." + "=" + pageRank);
        return best;
    }      
     // end of getBestURL
    

    public static void main(String args[]) throws Exception{  
        SearchEngine mySearchEngine = new SearchEngine();
        // to debug your program, start with.
        // mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
     
        // When your program is working on the small example, move on to
        mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
     
     
        mySearchEngine.computePageRanks();
     
        BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
        String query;
        do {
            System.out.print("Enter query: ");
            query = stndin.readLine();
            if ( query != null && query.length() > 0 ) {
            System.out.println("Best site = " + mySearchEngine.getBestURL(query));
            }
        } while (query!=null && query.length()>0);    
    }
}// end of main
