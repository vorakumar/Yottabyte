package com.thoughtworks.yottabyte.vehiclerepairdenormalization.secondarysortstrategy;

import org.apache.hadoop.io.Text;

public class Partitioner extends org.apache.hadoop.mapreduce.Partitioner<TaggedKey,Text> {
  @Override
  public int getPartition(TaggedKey key, Text taggedText, int numberOfReducers) {

    return Math.abs(key.vehicleType.hashCode()) % numberOfReducers;
  }
}
