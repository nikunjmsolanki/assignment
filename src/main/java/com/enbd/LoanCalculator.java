package com.enbd;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;


public class LoanCalculator {

    public static Map<Integer, LoanAggSchema> ROLLOVER_MAP = new ConcurrentHashMap<>();
    private static SchemaRegistryClient schemaRegistryClient = new CachedSchemaRegistryClient("http://localhost:8081", 100);

    public static void main(String[] args) {

        PipelineOptions options = PipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);

        Map<String, Object> kafkaConfig = new ConcurrentHashMap<>();
        kafkaConfig.put(SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        PCollection<KV<Long, AccountSchema>> accRawDataAvro = p.apply(
                KafkaIO.<Long, AccountSchema>read()
                        .withBootstrapServers("localhost:9092")
                        .withTopic("Account")
                        .withKeyDeserializer(LongDeserializer.class)
                        .withValueDeserializerAndCoder(AccountDeserializer.class, AvroCoder.of(AccountSchema.class))
                        .updateConsumerProperties(kafkaConfig)
                        .withReadCommitted()
                        .withoutMetadata())
                .apply(ParDo.of(new DoFn<KV<Long, AccountSchema>, KV<Long, AccountSchema>>() {
                    @ProcessElement
                    public void processElement(@Element KV<Long, AccountSchema> element, OutputReceiver<KV<Long, AccountSchema>> out, @Timestamp Instant timestamp) {
                        out.outputWithTimestamp(KV.of(element.getValue().getAccountId(), element.getValue()), timestamp);
                    }
                }))
                .apply(
                        Window.<KV<Long, AccountSchema>>into(

                                FixedWindows.of(Duration.standardSeconds(30)))
//                                .withAllowedLateness(Duration.standardSeconds(5l))
//                                .discardingFiredPanes()
                );

        PCollection<KV<Long, LoanSchema>> loanRawDataAvro = p.apply(
                KafkaIO.<Long, LoanSchema>read()
                        .withBootstrapServers("localhost:9092")
                        .withTopic("Loan")
                        .withKeyDeserializer(LongDeserializer.class)
                        .withValueDeserializerAndCoder(LoanDeserializer.class, AvroCoder.of(LoanSchema.class))
                        .updateConsumerProperties(kafkaConfig)
                        .withReadCommitted()
                        .withoutMetadata())
                .apply(ParDo.of(new DoFn<KV<Long, LoanSchema>, KV<Long, LoanSchema>>() {
                    @ProcessElement
                    public void processElement(@Element KV<Long, LoanSchema> element, OutputReceiver<KV<Long, LoanSchema>> out, @Timestamp Instant timestamp) {
                        out.outputWithTimestamp(KV.of(element.getValue().getAccountId(), element.getValue()), timestamp);
                    }

                })).apply(
                        Window.<KV<Long, LoanSchema>>into(
                                FixedWindows.of(Duration.standardSeconds(30)))
//                                .withAllowedLateness(Duration.standardSeconds(5l))
//                                .accumulatingFiredPanes()
                );

        final TupleTag<AccountSchema> accTag = new TupleTag<>();
        final TupleTag<LoanSchema> loanTag = new TupleTag<>();

        PDone done =
                KeyedPCollectionTuple.of(accTag, accRawDataAvro)
                        .and(loanTag, loanRawDataAvro)
                        .apply(CoGroupByKey.create())
                        .apply(ParDo.of(new DoFn<KV<Long, CoGbkResult>, KV<Integer, LoanAggSchema>>() {
                            @ProcessElement
                            public void processElement(@Element KV<Long, CoGbkResult> element, OutputReceiver<KV<Integer, LoanAggSchema>> out) {

                                Iterator<AccountSchema> it = element.getValue().getAll(accTag).iterator();
                                Integer accType = null;
                                while (it.hasNext()) {
                                    accType = it.next().getAccountType();
                                }

                                LoanAggSchema loanAggSchema = new LoanAggSchema();
                                loanAggSchema.setAccountType(accType);
                                loanAggSchema.setLastMinuteCount(0);
                                loanAggSchema.setTotalAmount(0d);

                                for (LoanSchema currentLoan : element.getValue().getAll(loanTag)) {
                                    loanAggSchema.setTotalAmount(loanAggSchema.getTotalAmount() + currentLoan.getAmount());
                                    loanAggSchema.setLastMinuteCount(loanAggSchema.getLastMinuteCount() + 1);
                                }

                                LoanAggSchema valuesForAccType = ROLLOVER_MAP.get(accType);
                                if (valuesForAccType != null) {
                                    valuesForAccType.setTotalAmount(valuesForAccType.getTotalAmount() + loanAggSchema.getTotalAmount());
                                    valuesForAccType.setTotalCount(valuesForAccType.getTotalCount() + loanAggSchema.getLastMinuteCount());
                                    ROLLOVER_MAP.put(accType, valuesForAccType);
                                } else {
                                    loanAggSchema.setTotalCount(loanAggSchema.getLastMinuteCount());
                                    ROLLOVER_MAP.put(accType, loanAggSchema);
                                }
                                out.output(KV.of(accType, loanAggSchema));
                            }
                        }))
                        .apply(Combine.perKey(LoanCalculator::mergeMap))
                        .apply(ParDo.of(new DoFn<KV<Integer, LoanAggSchema>, KV<Integer,LoanAggSchema>>() {
                            @ProcessElement
                            public void processElement(@Element KV<Integer, LoanAggSchema> element, OutputReceiver<KV<Integer,LoanAggSchema>> out, @Timestamp Instant timestamp) {
                                Integer accType = element.getKey();
                                LoanAggSchema finalAgg = element.getValue();
                                LoanAggSchema toPublish = new LoanAggSchema();
                                LoanAggSchema rolledValues = ROLLOVER_MAP.get(accType);
                                toPublish.setLastMinuteCount(finalAgg.getLastMinuteCount());
                                toPublish.setTotalCount(rolledValues.getTotalCount());
                                toPublish.setTotalAmount(rolledValues.getTotalAmount());
                                toPublish.setAccountType(accType);
                                out.outputWithTimestamp(KV.of(accType,toPublish), timestamp);

                                for(Map.Entry<Integer, LoanAggSchema> entry : ROLLOVER_MAP.entrySet()){
                                    if(!entry.getKey().equals(accType)){
                                        LoanAggSchema publish = new LoanAggSchema();
                                        publish.setAccountType(entry.getValue().getAccountType());
                                        publish.setLastMinuteCount(0);
                                        publish.setTotalCount(entry.getValue().getTotalCount());
                                        publish.setTotalAmount(entry.getValue().getTotalAmount());
                                        out.outputWithTimestamp(KV.of(publish.getAccountType(),publish),timestamp);
                                    }

                                }

                            }
                        }))
                        .apply(GroupByKey.create())
                        .apply(ParDo.of(new DoFn<KV<Integer, Iterable<LoanAggSchema>>, LoanAggSchema>() {
                            @ProcessElement
                            public void processElement(@Element KV<Integer, Iterable<LoanAggSchema>> element, OutputReceiver<LoanAggSchema> out){

                                LoanAggSchema output=null;

                                for(LoanAggSchema loan : element.getValue() ){
                                    if(output==null){
                                        output=loan;
                                    }else{
                                        if(output.getLastMinuteCount()==0 && loan.getLastMinuteCount()!=0){
                                            output=loan;
                                        }
                                    }

                                }
                                out.output(output);
                            }
                        }))

                        .apply(KafkaIO.<Void, LoanAggSchema>write()
                                .withBootstrapServers("localhost:9092")
                                .withValueSerializer(AvroSerializer.class)
                                .withTopic("Output")
                                .updateProducerProperties(kafkaConfig).values());

        p.run().waitUntilFinish();
    }

    public static class AvroSerializer implements org.apache.kafka.common.serialization.Serializer<LoanAggSchema> {

        private final KafkaAvroSerializer delegate = new KafkaAvroSerializer();

        @Override
        public void configure(Map<String, ?> map, boolean isKey) {
            delegate.configure(map, isKey);
        }

        @Override
        public byte[] serialize(String topic, LoanAggSchema data) {
            return delegate.serialize(topic, data);
        }

        @Override
        public void close() {
            delegate.close();
        }
    }

    private static LoanAggSchema mergeMap(Iterable<LoanAggSchema> iterable) {

        LoanAggSchema loanAggSchemaInitial = null;

        for (LoanAggSchema currentLoan : iterable) {
            if (loanAggSchemaInitial == null) {
                loanAggSchemaInitial = currentLoan;
            } else {
               if(loanAggSchemaInitial.getTotalCount().equals(0)){
                  loanAggSchemaInitial.setTotalCount(currentLoan.getTotalCount());
               }
               if(loanAggSchemaInitial.getTotalAmount().equals(0.0)){
                   loanAggSchemaInitial.setTotalAmount(currentLoan.getTotalAmount());
               }
                loanAggSchemaInitial.setLastMinuteCount(loanAggSchemaInitial.getLastMinuteCount() + currentLoan.getLastMinuteCount());
            }
        }
        return loanAggSchemaInitial;
    }

    private static Instant extractTimeStampFromData(String value) {
        DateTimeFormatter dtFormat = DateTimeFormat.forPattern("dd-MM-YY.hh:mm:ss");
        return new Instant(Instant.parse(value.split(" ")[0], dtFormat));

    }

    public static abstract class AvroDeserializer<T> extends AbstractKafkaAvroDeserializer implements org.apache.kafka.common.serialization.Deserializer<T> {
        private final KafkaAvroDeserializer delegate = new KafkaAvroDeserializer(schemaRegistryClient);

        @Override
        public void configure(Map<String, ?> map, boolean b) {
            delegate.configure(map, b);
        }

        @Override
        public T deserialize(String topic, byte[] data) {
            Object obj = delegate.deserialize(topic, data);
            return getValueFromGenericRecord(obj);
        }

        @Override
        public void close() {
            delegate.close();
        }

        protected abstract T getValueFromGenericRecord(Object obj);
    }

    public static class AccountDeserializer extends LoanCalculator.AvroDeserializer<AccountSchema> {
        @Override
        protected AccountSchema getValueFromGenericRecord(Object obj) {
            GenericRecord record = (GenericRecord) obj;
            return AccountSchema.newBuilder()
                    .setAccountId(getLongValue(record.get("AccountId")))
                    .setAccountType(getIntegerValue(record.get("AccountType")))
                    .build();
        }
    }

    public static class LoanDeserializer extends LoanCalculator.AvroDeserializer<LoanSchema> {
        @Override
        protected LoanSchema getValueFromGenericRecord(Object obj) {
            GenericRecord record = (GenericRecord) obj;
            return LoanSchema.newBuilder()
                    .setLoanId(getLongValue(record.get("LoanId")))
                    .setAccountId(getLongValue(record.get("AccountId")))
                    .setAmount(getDoubleValue(record.get("Amount")))
                    .build();
        }
    }

    public static Long getLongValue(Object value) {
        try {
            return value != null ? Long.valueOf(value.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getIntegerValue(Object value) {
        try {
            return value != null ? Integer.valueOf(value.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getDoubleValue(Object value) {
        try {
            return value != null ? Double.valueOf(value.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
