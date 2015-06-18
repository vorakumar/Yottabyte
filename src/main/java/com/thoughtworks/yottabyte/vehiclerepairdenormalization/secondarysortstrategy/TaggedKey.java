package com.thoughtworks.yottabyte.vehiclerepairdenormalization.secondarysortstrategy;

import com.google.common.collect.ComparisonChain;
import com.thoughtworks.yottabyte.vehiclerepairdenormalization.domain.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NoArgsConstructor
public class TaggedKey implements WritableComparable<TaggedKey> {

  @Getter
  public String vehicleType;

  public Tag tag;

  public TaggedKey(String vehicleType, Tag tag) {
    this.vehicleType = vehicleType;
    this.tag = tag;
  }

  @Override
  public int compareTo(TaggedKey o) {
    return ComparisonChain.start()
            .compare(vehicleType, o.vehicleType)
            .compare(tag, o.tag)
            .result();
  }

  @Override
  public void write(DataOutput out) throws IOException {}

  @Override
  public void readFields(DataInput in) throws IOException {}
}
