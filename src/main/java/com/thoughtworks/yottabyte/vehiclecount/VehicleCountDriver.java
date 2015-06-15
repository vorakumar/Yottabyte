package com.thoughtworks.yottabyte.vehiclecount;

import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import scala.Int;
import scala.sys.process.ProcessBuilderImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.thoughtworks.yottabyte.constants.FileNameConstants.VEHICLES;
import static com.thoughtworks.yottabyte.constants.FileNameConstants.VEHICLES_COUNT;
import static com.thoughtworks.yottabyte.vehiclecount.VehicleMapper.*;

public class VehicleCountDriver extends Configured implements Tool {

  private static ClassLoader loader;
  private Properties properties = new Properties();

  @Override
  public int run(String[] args) throws Exception {
    loadPropertiesFile(args[0]);

    Configuration configuration = getConf();
    configuration.set(COLUMN_SEPARATOR, get(VEHICLES.columnSeparator()));
    configuration.set(VEHICLE_DATE_FORMAT, get(VEHICLES.dateFormat()));


    Job job = Job.getInstance(configuration,this.getClass().getSimpleName());
    job.setJarByClass(getClass());
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);
    job.setMapperClass(VehicleMapper.class);

    job.setReducerClass(VehicleCountReducer.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, getPath(VEHICLES.path()));
    FileOutputFormat.setOutputPath(job, getPath(VEHICLES_COUNT.path()));

    return job.waitForCompletion(true) ? 0 : 1;
  }

  protected void loadPropertiesFile(String propertyFilePath) throws IOException {
    try(InputStream propertiesInputStream = new FileInputStream(propertyFilePath)){
      properties.load(propertiesInputStream);
    }catch (NullPointerException npe){
      System.out.println("No properties file found");
      System.exit(1);
    }
  }

  protected String get(String propertyName){
    return Preconditions.checkNotNull(properties.getProperty(propertyName),
      "Expected %s to be present, but was not", propertyName);
  }

  protected Path getPath(String propertyName){
    return new Path(get(propertyName));
  }

  public static void main(String[] args) throws Exception {
    loader = VehicleCountDriver.class.getClassLoader();
    if (args.length < 1) {
      args = new String[]{loader.getResource("config.properties").getPath()};
    }
    int exitCode = ToolRunner.run(new Configuration(), new VehicleCountDriver(), args);
    System.exit(exitCode);
  }

}
