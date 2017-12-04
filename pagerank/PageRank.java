// Sources:
// http://buttercola.blogspot.ch/2015/04/giraph-page-rank.html
// https://www.safaribooksonline.com/blog/2014/02/12/understanding-apache-giraph-application/

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;

 
import java.io.IOException;
 
public class PageRank extends BasicComputation<
    Text, DoubleWritable, DoubleWritable, DoubleWritable> {
 
  public static final int MAX_SUPERSTEPS = 30;
   
  @Override
  public void compute(Vertex<Text, DoubleWritable, DoubleWritable> vertex, 
      Iterable<DoubleWritable> messages) throws IOException {


   
    //System.out.println("STEP => " + getSuperstep());
    if (getSuperstep() == 0) {
      //if (vertex.getId().toString().indexOf("https://fr.wikipedia.org/wiki/Philippe_Couillard") > -1) {
      //  System.out.println("edges phil ==> " + vertex.getNumEdges()); 
      //}
      vertex.setValue(new DoubleWritable(1 / getTotalNumVertices()));
    } else {
      double sum = 0;
      for (DoubleWritable message : messages) {
        sum += message.get();
      }
      vertex.setValue(new DoubleWritable(0.01 / getTotalNumVertices() + 0.99 * sum));
    }

    if (getSuperstep() < MAX_SUPERSTEPS) {
       int numEdges = vertex.getNumEdges();
       DoubleWritable message = new DoubleWritable(vertex.getValue().get() / numEdges);
       for (Edge<Text, DoubleWritable> edge: vertex.getEdges()) {
         sendMessage(edge.getTargetVertexId(), message);
       }
    } else {

      Double toBeTruncated = new Double(vertex.getValue().get() * 100000000);
      Double truncatedDouble = BigDecimal.valueOf(toBeTruncated)
        .setScale(0, RoundingMode.HALF_UP)
        .doubleValue();

      vertex.setValue(new DoubleWritable(truncatedDouble));
      vertex.voteToHalt();
    }
  }  
}
