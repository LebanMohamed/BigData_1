import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.net.URI;

public class SupportMapper extends Mapper<Object, Text, Text, IntWritable> {
    
    private IntWritable value = new IntWritable(1);
    private Text allSports = new Text();
    
    private final static int FILTER = 140;
    
    private ArrayList<String> athletesName;
    private Map<String, String> athletesSport;
    
    public void map(Object key, Text input, Context context) throws IOException, InterruptedException {
        
        String[] arrList = input.toString().split(";");
        
        if (arrList.length == 4 && arrList[2].length() <= FILTER) {
            for (String athlete : athletesName) {
                if (arrList[2].contains(athlete)) {
                    allSports.set(athletesSport.get(athlete));
                    context.write(allSports, value);
                }
            }
        }
    }
    
    protected void setup(Context context) throws IOException, InterruptedException {
        
        this.athletesName = new ArrayList<String>();
        this.athletesSport = new HashMap<String, String>();
        
        URI fileUri = context.getCacheFiles()[0];
        FileSystem fs = FileSystem.get(context.getConfiguration());
        FSDataInputStream in = fs.open(new Path(fileUri));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String line = null;
        try {
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 11) {
                    this.athletesName.add(fields[1]);
                    this.athletesSport.put(fields[1], fields[7]);
                }
            }
            br.close();
        } catch (IOException e1) {
        }
        
        super.setup(context);
    }
}
