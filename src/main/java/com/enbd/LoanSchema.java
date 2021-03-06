/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.enbd;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class LoanSchema extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -3113655214256992390L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"LoanSchema\",\"namespace\":\"com.enbd\",\"fields\":[{\"name\":\"LoanId\",\"type\":\"long\"},{\"name\":\"AccountId\",\"type\":\"long\"},{\"name\":\"Amount\",\"type\":\"double\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<LoanSchema> ENCODER =
      new BinaryMessageEncoder<LoanSchema>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<LoanSchema> DECODER =
      new BinaryMessageDecoder<LoanSchema>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<LoanSchema> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<LoanSchema> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<LoanSchema>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this LoanSchema to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a LoanSchema from a ByteBuffer. */
  public static LoanSchema fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public long LoanId;
  @Deprecated public long AccountId;
  @Deprecated public double Amount;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public LoanSchema() {}

  /**
   * All-args constructor.
   * @param LoanId The new value for LoanId
   * @param AccountId The new value for AccountId
   * @param Amount The new value for Amount
   */
  public LoanSchema(java.lang.Long LoanId, java.lang.Long AccountId, java.lang.Double Amount) {
    this.LoanId = LoanId;
    this.AccountId = AccountId;
    this.Amount = Amount;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return LoanId;
    case 1: return AccountId;
    case 2: return Amount;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: LoanId = (java.lang.Long)value$; break;
    case 1: AccountId = (java.lang.Long)value$; break;
    case 2: Amount = (java.lang.Double)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'LoanId' field.
   * @return The value of the 'LoanId' field.
   */
  public java.lang.Long getLoanId() {
    return LoanId;
  }

  /**
   * Sets the value of the 'LoanId' field.
   * @param value the value to set.
   */
  public void setLoanId(java.lang.Long value) {
    this.LoanId = value;
  }

  /**
   * Gets the value of the 'AccountId' field.
   * @return The value of the 'AccountId' field.
   */
  public java.lang.Long getAccountId() {
    return AccountId;
  }

  /**
   * Sets the value of the 'AccountId' field.
   * @param value the value to set.
   */
  public void setAccountId(java.lang.Long value) {
    this.AccountId = value;
  }

  /**
   * Gets the value of the 'Amount' field.
   * @return The value of the 'Amount' field.
   */
  public java.lang.Double getAmount() {
    return Amount;
  }

  /**
   * Sets the value of the 'Amount' field.
   * @param value the value to set.
   */
  public void setAmount(java.lang.Double value) {
    this.Amount = value;
  }

  /**
   * Creates a new LoanSchema RecordBuilder.
   * @return A new LoanSchema RecordBuilder
   */
  public static com.enbd.LoanSchema.Builder newBuilder() {
    return new com.enbd.LoanSchema.Builder();
  }

  /**
   * Creates a new LoanSchema RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new LoanSchema RecordBuilder
   */
  public static com.enbd.LoanSchema.Builder newBuilder(com.enbd.LoanSchema.Builder other) {
    return new com.enbd.LoanSchema.Builder(other);
  }

  /**
   * Creates a new LoanSchema RecordBuilder by copying an existing LoanSchema instance.
   * @param other The existing instance to copy.
   * @return A new LoanSchema RecordBuilder
   */
  public static com.enbd.LoanSchema.Builder newBuilder(com.enbd.LoanSchema other) {
    return new com.enbd.LoanSchema.Builder(other);
  }

  /**
   * RecordBuilder for LoanSchema instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<LoanSchema>
    implements org.apache.avro.data.RecordBuilder<LoanSchema> {

    private long LoanId;
    private long AccountId;
    private double Amount;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.enbd.LoanSchema.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.LoanId)) {
        this.LoanId = data().deepCopy(fields()[0].schema(), other.LoanId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.AccountId)) {
        this.AccountId = data().deepCopy(fields()[1].schema(), other.AccountId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.Amount)) {
        this.Amount = data().deepCopy(fields()[2].schema(), other.Amount);
        fieldSetFlags()[2] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing LoanSchema instance
     * @param other The existing instance to copy.
     */
    private Builder(com.enbd.LoanSchema other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.LoanId)) {
        this.LoanId = data().deepCopy(fields()[0].schema(), other.LoanId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.AccountId)) {
        this.AccountId = data().deepCopy(fields()[1].schema(), other.AccountId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.Amount)) {
        this.Amount = data().deepCopy(fields()[2].schema(), other.Amount);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'LoanId' field.
      * @return The value.
      */
    public java.lang.Long getLoanId() {
      return LoanId;
    }

    /**
      * Sets the value of the 'LoanId' field.
      * @param value The value of 'LoanId'.
      * @return This builder.
      */
    public com.enbd.LoanSchema.Builder setLoanId(long value) {
      validate(fields()[0], value);
      this.LoanId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'LoanId' field has been set.
      * @return True if the 'LoanId' field has been set, false otherwise.
      */
    public boolean hasLoanId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'LoanId' field.
      * @return This builder.
      */
    public com.enbd.LoanSchema.Builder clearLoanId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'AccountId' field.
      * @return The value.
      */
    public java.lang.Long getAccountId() {
      return AccountId;
    }

    /**
      * Sets the value of the 'AccountId' field.
      * @param value The value of 'AccountId'.
      * @return This builder.
      */
    public com.enbd.LoanSchema.Builder setAccountId(long value) {
      validate(fields()[1], value);
      this.AccountId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'AccountId' field has been set.
      * @return True if the 'AccountId' field has been set, false otherwise.
      */
    public boolean hasAccountId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'AccountId' field.
      * @return This builder.
      */
    public com.enbd.LoanSchema.Builder clearAccountId() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'Amount' field.
      * @return The value.
      */
    public java.lang.Double getAmount() {
      return Amount;
    }

    /**
      * Sets the value of the 'Amount' field.
      * @param value The value of 'Amount'.
      * @return This builder.
      */
    public com.enbd.LoanSchema.Builder setAmount(double value) {
      validate(fields()[2], value);
      this.Amount = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'Amount' field has been set.
      * @return True if the 'Amount' field has been set, false otherwise.
      */
    public boolean hasAmount() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'Amount' field.
      * @return This builder.
      */
    public com.enbd.LoanSchema.Builder clearAmount() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LoanSchema build() {
      try {
        LoanSchema record = new LoanSchema();
        record.LoanId = fieldSetFlags()[0] ? this.LoanId : (java.lang.Long) defaultValue(fields()[0]);
        record.AccountId = fieldSetFlags()[1] ? this.AccountId : (java.lang.Long) defaultValue(fields()[1]);
        record.Amount = fieldSetFlags()[2] ? this.Amount : (java.lang.Double) defaultValue(fields()[2]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<LoanSchema>
    WRITER$ = (org.apache.avro.io.DatumWriter<LoanSchema>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<LoanSchema>
    READER$ = (org.apache.avro.io.DatumReader<LoanSchema>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
