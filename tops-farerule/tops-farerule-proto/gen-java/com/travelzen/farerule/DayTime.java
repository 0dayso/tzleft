/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.travelzen.farerule;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DayTime implements org.apache.thrift.TBase<DayTime, DayTime._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DayTime");

  private static final org.apache.thrift.protocol.TField DAY_TIME_ITEM_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("dayTimeItemList", org.apache.thrift.protocol.TType.LIST, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new DayTimeStandardSchemeFactory());
    schemes.put(TupleScheme.class, new DayTimeTupleSchemeFactory());
  }

  public List<com.travelzen.farerule.rule.DayTimeItem> dayTimeItemList; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    DAY_TIME_ITEM_LIST((short)1, "dayTimeItemList");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // DAY_TIME_ITEM_LIST
          return DAY_TIME_ITEM_LIST;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private _Fields optionals[] = {_Fields.DAY_TIME_ITEM_LIST};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DAY_TIME_ITEM_LIST, new org.apache.thrift.meta_data.FieldMetaData("dayTimeItemList", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.travelzen.farerule.rule.DayTimeItem.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DayTime.class, metaDataMap);
  }

  public DayTime() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DayTime(DayTime other) {
    if (other.isSetDayTimeItemList()) {
      List<com.travelzen.farerule.rule.DayTimeItem> __this__dayTimeItemList = new ArrayList<com.travelzen.farerule.rule.DayTimeItem>();
      for (com.travelzen.farerule.rule.DayTimeItem other_element : other.dayTimeItemList) {
        __this__dayTimeItemList.add(new com.travelzen.farerule.rule.DayTimeItem(other_element));
      }
      this.dayTimeItemList = __this__dayTimeItemList;
    }
  }

  public DayTime deepCopy() {
    return new DayTime(this);
  }

  @Override
  public void clear() {
    this.dayTimeItemList = null;
  }

  public int getDayTimeItemListSize() {
    return (this.dayTimeItemList == null) ? 0 : this.dayTimeItemList.size();
  }

  public java.util.Iterator<com.travelzen.farerule.rule.DayTimeItem> getDayTimeItemListIterator() {
    return (this.dayTimeItemList == null) ? null : this.dayTimeItemList.iterator();
  }

  public void addToDayTimeItemList(com.travelzen.farerule.rule.DayTimeItem elem) {
    if (this.dayTimeItemList == null) {
      this.dayTimeItemList = new ArrayList<com.travelzen.farerule.rule.DayTimeItem>();
    }
    this.dayTimeItemList.add(elem);
  }

  public List<com.travelzen.farerule.rule.DayTimeItem> getDayTimeItemList() {
    return this.dayTimeItemList;
  }

  public DayTime setDayTimeItemList(List<com.travelzen.farerule.rule.DayTimeItem> dayTimeItemList) {
    this.dayTimeItemList = dayTimeItemList;
    return this;
  }

  public void unsetDayTimeItemList() {
    this.dayTimeItemList = null;
  }

  /** Returns true if field dayTimeItemList is set (has been assigned a value) and false otherwise */
  public boolean isSetDayTimeItemList() {
    return this.dayTimeItemList != null;
  }

  public void setDayTimeItemListIsSet(boolean value) {
    if (!value) {
      this.dayTimeItemList = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case DAY_TIME_ITEM_LIST:
      if (value == null) {
        unsetDayTimeItemList();
      } else {
        setDayTimeItemList((List<com.travelzen.farerule.rule.DayTimeItem>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case DAY_TIME_ITEM_LIST:
      return getDayTimeItemList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case DAY_TIME_ITEM_LIST:
      return isSetDayTimeItemList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof DayTime)
      return this.equals((DayTime)that);
    return false;
  }

  public boolean equals(DayTime that) {
    if (that == null)
      return false;

    boolean this_present_dayTimeItemList = true && this.isSetDayTimeItemList();
    boolean that_present_dayTimeItemList = true && that.isSetDayTimeItemList();
    if (this_present_dayTimeItemList || that_present_dayTimeItemList) {
      if (!(this_present_dayTimeItemList && that_present_dayTimeItemList))
        return false;
      if (!this.dayTimeItemList.equals(that.dayTimeItemList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(DayTime other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    DayTime typedOther = (DayTime)other;

    lastComparison = Boolean.valueOf(isSetDayTimeItemList()).compareTo(typedOther.isSetDayTimeItemList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDayTimeItemList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dayTimeItemList, typedOther.dayTimeItemList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DayTime(");
    boolean first = true;

    if (isSetDayTimeItemList()) {
      sb.append("dayTimeItemList:");
      if (this.dayTimeItemList == null) {
        sb.append("null");
      } else {
        sb.append(this.dayTimeItemList);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class DayTimeStandardSchemeFactory implements SchemeFactory {
    public DayTimeStandardScheme getScheme() {
      return new DayTimeStandardScheme();
    }
  }

  private static class DayTimeStandardScheme extends StandardScheme<DayTime> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, DayTime struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // DAY_TIME_ITEM_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list16 = iprot.readListBegin();
                struct.dayTimeItemList = new ArrayList<com.travelzen.farerule.rule.DayTimeItem>(_list16.size);
                for (int _i17 = 0; _i17 < _list16.size; ++_i17)
                {
                  com.travelzen.farerule.rule.DayTimeItem _elem18; // required
                  _elem18 = new com.travelzen.farerule.rule.DayTimeItem();
                  _elem18.read(iprot);
                  struct.dayTimeItemList.add(_elem18);
                }
                iprot.readListEnd();
              }
              struct.setDayTimeItemListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, DayTime struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.dayTimeItemList != null) {
        if (struct.isSetDayTimeItemList()) {
          oprot.writeFieldBegin(DAY_TIME_ITEM_LIST_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.dayTimeItemList.size()));
            for (com.travelzen.farerule.rule.DayTimeItem _iter19 : struct.dayTimeItemList)
            {
              _iter19.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DayTimeTupleSchemeFactory implements SchemeFactory {
    public DayTimeTupleScheme getScheme() {
      return new DayTimeTupleScheme();
    }
  }

  private static class DayTimeTupleScheme extends TupleScheme<DayTime> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DayTime struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetDayTimeItemList()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetDayTimeItemList()) {
        {
          oprot.writeI32(struct.dayTimeItemList.size());
          for (com.travelzen.farerule.rule.DayTimeItem _iter20 : struct.dayTimeItemList)
          {
            _iter20.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DayTime struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list21 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.dayTimeItemList = new ArrayList<com.travelzen.farerule.rule.DayTimeItem>(_list21.size);
          for (int _i22 = 0; _i22 < _list21.size; ++_i22)
          {
            com.travelzen.farerule.rule.DayTimeItem _elem23; // required
            _elem23 = new com.travelzen.farerule.rule.DayTimeItem();
            _elem23.read(iprot);
            struct.dayTimeItemList.add(_elem23);
          }
        }
        struct.setDayTimeItemListIsSet(true);
      }
    }
  }

}
