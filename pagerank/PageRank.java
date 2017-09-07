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
 
import java.io.IOException;
 
public class PageRank extends BasicComputation<
    Text, DoubleWritable, DoubleWritable, DoubleWritable> {
 
  public static final int MAX_SUPERSTEPS = 30;
   
  @Override
  public void compute(Vertex<Text, DoubleWritable, DoubleWritable> vertex, 
      Iterable<DoubleWritable> messages) throws IOException {
   
    System.out.println("STEP => " + getSuperstep());
    //System.out.print("total vertices ==> " + getTotalNumVertices());
    if (getSuperstep() >= 1) {
      double sum = 0;
      for (DoubleWritable message : messages) {
        sum += message.get();
      }
      vertex.setValue(new DoubleWritable(0.15 / getTotalNumVertices() + 0.85 * sum));

    }

    if (getSuperstep() < MAX_SUPERSTEPS) {
      int numEdges = vertex.getNumEdges();
      DoubleWritable message = new DoubleWritable(vertex.getValue().get() / numEdges);
      for (Edge<Text, DoubleWritable> edge: vertex.getEdges()) {
        //System.out.print(" " + edge.getTargetVertexId());
        //System.out.print("numEdges => " + numEdges);
        sendMessage(edge.getTargetVertexId(), message);
      }
    } else {
      vertex.voteToHalt();
    }
  }  
}
