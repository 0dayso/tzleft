/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.travelzen.farerule.rule;

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

public class SegmentNode implements org.apache.thrift.TBase<SegmentNode, SegmentNode._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SegmentNode");

  private static final org.apache.thrift.protocol.TField NODE_INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("nodeIndex", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField IS_ARRIVE_POINT_FIELD_DESC = new org.apache.thrift.protocol.TField("isArrivePoint", org.apache.thrift.protocol.TType.BOOL, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SegmentNodeStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SegmentNodeTupleSchemeFactory());
  }

  public int nodeIndex; // required
  public boolean isArrivePoint; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NODE_INDEX((short)1, "nodeIndex"),
    IS_ARRIVE_POINT((short)2, "isArrivePoint");

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
        case 1: // NODE_INDEX
          return NODE_INDEX;
        case 2: // IS_ARRIVE_POINT
          return IS_ARRIVE_POINT;
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
  private static final int __NODEINDEX_ISSET_ID = 0;
  private static final int __ISARRIVEPOINT_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NODE_INDEX, new org.apache.thrift.meta_data.FieldMetaData("nodeIndex", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.IS_ARRIVE_POINT, new org.apache.thrift.meta_data.FieldMetaData("isArrivePoint", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SegmentNode.class, metaDataMap);
  }

  public SegmentNode() {
  }

  public SegmentNode(
    int nodeIndex,
    boolean isArrivePoint)
  {
    this();
    this.nodeIndex = nodeIndex;
    setNodeIndexIsSet(true);
    this.isArrivePoint = isArrivePoint;
    setIsArrivePointIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SegmentNode(SegmentNode other) {
    __isset_bitfield = other.__isset_bitfield;
    this.nodeIndex = other.nodeIndex;
    this.isArrivePoint = other.isArrivePoint;
  }

  public SegmentNode deepCopy() {
    return new SegmentNode(this);
  }

  @Override
  public void clear() {
    setNodeIndexIsSet(false);
    this.nodeIndex = 0;
    setIsArrivePointIsSet(false);
    this.isArrivePoint = false;
  }

  public int getNodeIndex() {
    return this.nodeIndex;
  }

  public SegmentNode setNodeIndex(int nodeIndex) {
    this.nodeIndex = nodeIndex;
    setNodeIndexIsSet(true);
    return this;
  }

  public void unsetNodeIndex() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NODEINDEX_ISSET_ID);
  }

  /** Returns true if field nodeIndex is set (has been assigned a value) and false otherwise */
  public boolean isSetNodeIndex() {
    return EncodingUtils.testBit(__isset_bitfield, __NODEINDEX_ISSET_ID);
  }

  public void setNodeIndexIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NODEINDEX_ISSET_ID, value);
  }

  public boolean isIsArrivePoint() {
    return this.isArrivePoint;
  }

  public SegmentNode setIsArrivePoint(boolean isArrivePoint) {
    this.isArrivePoint = isArrivePoint;
    setIsArrivePointIsSet(true);
    return this;
  }

  public void unsetIsArrivePoint() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ISARRIVEPOINT_ISSET_ID);
  }

  /** Returns true if field isArrivePoint is set (has been assigned a value) and false otherwise */
  public boolean isSetIsArrivePoint() {
    return EncodingUtils.testBit(__isset_bitfield, __ISARRIVEPOINT_ISSET_ID);
  }

  public void setIsArrivePointIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ISARRIVEPOINT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NODE_INDEX:
      if (value == null) {
        unsetNodeIndex();
      } else {
        setNodeIndex((Integer)value);
      }
      break;

    case IS_ARRIVE_POINT:
      if (value == null) {
        unsetIsArrivePoint();
      } else {
        setIsArrivePoint((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NODE_INDEX:
      return Integer.valueOf(getNodeIndex());

    case IS_ARRIVE_POINT:
      return Boolean.valueOf(isIsArrivePoint());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NODE_INDEX:
      return isSetNodeIndex();
    case IS_ARRIVE_POINT:
      return isSetIsArrivePoint();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SegmentNode)
      return this.equals((SegmentNode)that);
    return false;
  }

  public boolean equals(SegmentNode that) {
    if (that == null)
      return false;

    boolean this_present_nodeIndex = true;
    boolean that_present_nodeIndex = true;
    if (this_present_nodeIndex || that_present_nodeIndex) {
      if (!(this_present_nodeIndex && that_present_nodeIndex))
        return false;
      if (this.nodeIndex != that.nodeIndex)
        return false;
    }

    boolean this_present_isArrivePoint = true;
    boolean that_present_isArrivePoint = true;
    if (this_present_isArrivePoint || that_present_isArrivePoint) {
      if (!(this_present_isArrivePoint && that_present_isArrivePoint))
        return false;
      if (this.isArrivePoint != that.isArrivePoint)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(SegmentNode other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    SegmentNode typedOther = (SegmentNode)other;

    lastComparison = Boolean.valueOf(isSetNodeIndex()).compareTo(typedOther.isSetNodeIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNodeIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.nodeIndex, typedOther.nodeIndex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIsArrivePoint()).compareTo(typedOther.isSetIsArrivePoint());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIsArrivePoint()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.isArrivePoint, typedOther.isArrivePoint);
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
    StringBuilder sb = new StringBuilder("SegmentNode(");
    boolean first = true;

    sb.append("nodeIndex:");
    sb.append(this.nodeIndex);
    first = false;
    if (!first) sb.append(", ");
    sb.append("isArrivePoint:");
    sb.append(this.isArrivePoint);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'nodeIndex' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'isArrivePoint' because it's a primitive and you chose the non-beans generator.
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SegmentNodeStandardSchemeFactory implements SchemeFactory {
    public SegmentNodeStandardScheme getScheme() {
      return new SegmentNodeStandardScheme();
    }
  }

  private static class SegmentNodeStandardScheme extends StandardScheme<SegmentNode> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SegmentNode struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NODE_INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.nodeIndex = iprot.readI32();
              struct.setNodeIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // IS_ARRIVE_POINT
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.isArrivePoint = iprot.readBool();
              struct.setIsArrivePointIsSet(true);
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
      if (!struct.isSetNodeIndex()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'nodeIndex' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetIsArrivePoint()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'isArrivePoint' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, SegmentNode struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(NODE_INDEX_FIELD_DESC);
      oprot.writeI32(struct.nodeIndex);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(IS_ARRIVE_POINT_FIELD_DESC);
      oprot.writeBool(struct.isArrivePoint);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SegmentNodeTupleSchemeFactory implements SchemeFactory {
    public SegmentNodeTupleScheme getScheme() {
      return new SegmentNodeTupleScheme();
    }
  }

  private static class SegmentNodeTupleScheme extends TupleScheme<SegmentNode> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SegmentNode struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.nodeIndex);
      oprot.writeBool(struct.isArrivePoint);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SegmentNode struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.nodeIndex = iprot.readI32();
      struct.setNodeIndexIsSet(true);
      struct.isArrivePoint = iprot.readBool();
      struct.setIsArrivePointIsSet(true);
    }
  }

}

