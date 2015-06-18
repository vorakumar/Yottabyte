package com.thoughtworks.yottabyte.olympicsAnalysis;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Comparator;

public class SparkDriver {
    public static void main(String[] args) {
        String master = args[0];
        String appName = args[1];
        String path = args[2];
        String destination = args[3];

        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> lines = sc.textFile(path);

        JavaPairRDD<String, Integer> medalsByYearAndCountry = lines.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                String[] data = s.split(",");
                return new Tuple2<>(data[2]+","+data[1], new Integer(data[7]));
            }
        });

        JavaPairRDD<Integer, String> inverseMedalsByYearAndCountry = medalsByYearAndCountry.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
            @Override
            public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                return new Tuple2<>(stringIntegerTuple2._2(), stringIntegerTuple2._1());
            }
        });

        JavaPairRDD<String, Integer> medalsByCountry = inverseMedalsByYearAndCountry.mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {

            @Override
            public Tuple2<String, Integer> call(Tuple2<Integer, String> integerStringTuple2) throws Exception {
                return new Tuple2<>(integerStringTuple2._2().split(",")[0], integerStringTuple2._1());
            }
        });

        JavaPairRDD<String, Integer> totalMedalsByCountry = medalsByCountry.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer value1, Integer value2) throws Exception {
                return value1 + value2;
            }
        });

        JavaPairRDD<Integer, String> inverseTotalMedalsByCountry = totalMedalsByCountry.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
            @Override
            public Tuple2<Integer, String> call(Tuple2<String, Integer> t2) throws Exception {
                return new Tuple2<>(t2._2(), t2._1());
            }
        });

        Tuple2<Integer, String> countryWithMaxMedals = inverseTotalMedalsByCountry.sortByKey(false).first();

        System.out.println("Medals by Year and Country" + inverseMedalsByYearAndCountry.sortByKey(false).first());

        System.out.println("Country with most total medals" + countryWithMaxMedals);

    }
}
