package com.enbd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//import simple producer packages
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.Producer;

//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.record.Record;
import org.apache.kafka.common.serialization.StringSerializer;

public class DataGenerator {

    private static final int MIN_ACC_ID = 1;
    private static final int MAX_ACC_ID = 10;

    private static final int MIN_ACC_TYPE = 1;
    private static final int MAX_ACC_TYPE = 4;

    private static final int MIN_LOAN_AMT = 5;
    private static final int MAX_LOAN_AMT = 30;

    private static AtomicInteger LOAN_ID = new AtomicInteger(1);

    private static final Random random = new Random();

    private static Map<Integer, Integer> accIdVsAccType = new HashMap<Integer, Integer>();


    private static String getFormattedTime() {
        SimpleDateFormat hh_MM_ss = new SimpleDateFormat("dd-MM-yy.hh:mm:ss");
        return hh_MM_ss.format(new Date());
    }

    private static Integer getRandomAccNumber() {
        return random.nextInt(MAX_ACC_ID - MIN_ACC_ID + 1) + MIN_ACC_ID;
    }

    private static Integer getRandomAccType() {
        return random.nextInt(MAX_ACC_TYPE - MIN_ACC_TYPE + 1) + MIN_ACC_TYPE;
    }

    private static Double getRandomLoanAmount() {
        return (random.nextInt(MAX_LOAN_AMT - MIN_LOAN_AMT) + MIN_LOAN_AMT) * 1000d;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Properties props = getKafkaProducerPropertiesAvro();

        Producer<Long, GenericRecord> producer = new KafkaProducer
                <>(props);

        Schema accountSchema = Schema.parse(new File("src/main/resources/avro/account.avsc"));
        Schema loanSchema = Schema.parse(new File("src/main/resources/avro/loan.avsc"));

        int counter = 1;
        while (counter < 1000) {
            int accNo = getRandomAccNumber();
            Integer accType = null;
            if (accIdVsAccType.containsKey(accNo)) {
                accType = accIdVsAccType.get(accNo);
            } else {
                accType = getRandomAccType();
                accIdVsAccType.put(accNo, accType);
            }
            GenericData.Record avroAccount = new GenericRecordBuilder(accountSchema)
                    .set("AccountId", (long) accNo)
                    .set("AccountType", accType)
                    .build();
            Double loanAmt = getRandomLoanAmount();
            GenericData.Record avroLoan = new GenericRecordBuilder(loanSchema)
                    .set("LoanId", (long) counter)
                    .set("AccountId", (long) accNo)
                    .set("Amount", loanAmt)
                    .build();
            String accData = getFormattedTime() + " Account " + accNo + "," + accType;
            String loanData = getFormattedTime() + " Loan " +
                    LOAN_ID.getAndAdd(1) + ","
                    + accNo + "," + loanAmt;
            System.out.println("SENDING DATA");
            System.out.println(accData);
            System.out.println(loanData );

            producer.send(new ProducerRecord("Account", avroAccount));
            producer.send(new ProducerRecord("Loan", avroLoan));
            producer.flush();
            System.out.println("\n-----DATA SENT-------");

            Thread.sleep(2000l);

            counter++;
        }
    }

    private static Properties getKafkaProducerPropertiesAvro() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer",
                org.apache.kafka.common.serialization.StringSerializer.class);
        props.put("value.serializer",
                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put("schema.registry.url",
                "http://localhost:8081");
        return props;
    }
}