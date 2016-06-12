package com.travelzen.rosetta.eterm.common.pojo.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerIdType;
import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;

/**
 * 乘客信息
 *
 * @author yiming.yan
 */
public class PassengerInfo {
    /**
     * 是否团队（暂时无用）
     */
    private boolean isGroup = false;
    /**
     * 是否有儿童
     */
    private boolean hasChild = false;
    /**
     * 是否有婴儿
     */
    private boolean hasInfant = false;
    /**
     * 乘客列表
     */
    private List<Passenger> passengers;
    /**
     * 全部票号集合
     */
    private Set<TicketInfo> ticketInfos;

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public boolean hasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public boolean hasInfant() {
        return hasInfant;
    }

    public void setHasInfant(boolean hasInfant) {
        this.hasInfant = hasInfant;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Set<TicketInfo> getTicketInfos() {
        return ticketInfos;
    }

    public void setTicketInfos(Set<TicketInfo> ticketInfos) {
        this.ticketInfos = ticketInfos;
    }

    @Override
    public String toString() {
        return "PassengerInfo [isGroup=" + isGroup + ", hasChild=" + hasChild
                + ", hasInfant=" + hasInfant + ", passengers=" + passengers
                + "]";
    }

    /**
     * 单个乘客的信息
     *
     * @author yiming.yan
     */
    public static class Passenger {

    	/**
         * 乘客类型
         */
        private PassengerType psgType = PassengerType.ADT;
        /**
         * 乘客序号
         */
        private int psgNo;
        /**
         * 乘客姓名
         */
        private String name;
        /**
         * 乘客称谓
         */
        private String title;
        /**
         * 乘客证件类型
         */
        private PassengerIdType idType;
        /**
         * 乘客证件号
         */
        private String id;
        /**
         * 乘客证件号有效期
         */
        private String validity;
        /**
         * 乘客性别
         */
        private String sex;
        /**
         * 出生日期
         */
        private String birthday;
        /**
         * 证件签发地
         */
        private String issuedAt;
        /**
         * 乘客国籍
         */
        private String nationality;
        /**
         * 票号
         */
        private List<TicketInfo> tktNos = new ArrayList<TicketInfo>();
        // ADT only
        /**
         * 携带婴儿的票号（仅成人有此项）
         */
        private List<TicketInfo> infTktNos = new ArrayList<TicketInfo>();
        // INF only
        /**
         * 跟随的乘客序号（仅婴儿有此项）
         */
        private int foPsgNo;

        public Passenger() {
        }

        public Passenger(PassengerType psgType) {
            this.psgType = psgType;
        }

		public PassengerType getPsgType() {
			return psgType;
		}

		public void setPsgType(PassengerType psgType) {
			this.psgType = psgType;
		}

		public int getPsgNo() {
			return psgNo;
		}

		public void setPsgNo(int psgNo) {
			this.psgNo = psgNo;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public PassengerIdType getIdType() {
			return idType;
		}

		public void setIdType(PassengerIdType idType) {
			this.idType = idType;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getValidity() {
			return validity;
		}

		public void setValidity(String validity) {
			this.validity = validity;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getBirthday() {
			return birthday;
		}

		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}

		public String getIssuedAt() {
			return issuedAt;
		}

		public void setIssuedAt(String issuedAt) {
			this.issuedAt = issuedAt;
		}

		public String getNationality() {
			return nationality;
		}

		public void setNationality(String nationality) {
			this.nationality = nationality;
		}

		public List<TicketInfo> getTktNos() {
			return tktNos;
		}

		public void setTktNos(List<TicketInfo> tktNos) {
			this.tktNos = tktNos;
		}

		public List<TicketInfo> getInfTktNos() {
			return infTktNos;
		}

		public void setInfTktNos(List<TicketInfo> infTktNos) {
			this.infTktNos = infTktNos;
		}

		public int getFoPsgNo() {
			return foPsgNo;
		}

		public void setFoPsgNo(int foPsgNo) {
			this.foPsgNo = foPsgNo;
		}

		@Override
		public String toString() {
			return "Passenger [psgType=" + psgType + ", psgNo=" + psgNo
					+ ", name=" + name + ", title=" + title + ", idType="
					+ idType + ", id=" + id + ", validity=" + validity
					+ ", sex=" + sex + ", birthday=" + birthday + ", issuedAt="
					+ issuedAt + ", nationality=" + nationality + ", tktNos="
					+ tktNos + ", infTktNos=" + infTktNos + ", foPsgNo="
					+ foPsgNo + "]";
		}

    }

    /**
     * 票号信息
     *
     * @author yiming.yan
     */
    public static class TicketInfo {
        /**
         * 乘客序号
         */
        private int psgNo;
        /**
         * 航段号
         */
        private int segmentNo;
        /**
         * 票号
         */
        private String tktNo;

        public int getPsgNo() {
            return psgNo;
        }

        public void setPsgNo(int psgNo) {
            this.psgNo = psgNo;
        }

        public int getSegmentNo() {
            return segmentNo;
        }

        public void setSegmentNo(int segmentNo) {
            this.segmentNo = segmentNo;
        }

        public String getTktNo() {
            return tktNo;
        }

        public void setTktNo(String tktNo) {
            this.tktNo = tktNo;
        }

        @Override
        public String toString() {
            return "TicketInfo [psgNo=" + psgNo + ", segmentNo=" + segmentNo
                    + ", tktNo=" + tktNo + "]";
        }

    }

    /**
     * 乘客证件号信息（仅在解析时临时使用）
     *
     * @author yiming.yan
     */
    public static class PassengerId {
    	
        private int psgNo;
        private String idType;
        private String issuedAt;
        private String id;
        private String nationality;
        private String birthday;
        private String sex;
        private String validity;
        private String name;
        
        public PassengerId() {
        }
        
        public PassengerId(int psgNo, String id) {
        	this.psgNo = psgNo;
        	this.id = id;
        }

		public String getIdType() {
			return idType;
		}

		public void setIdType(String idType) {
			this.idType = idType;
		}

		public int getPsgNo() {
			return psgNo;
		}

		public void setPsgNo(int psgNo) {
			this.psgNo = psgNo;
		}

		public String getIssuedAt() {
			return issuedAt;
		}

		public void setIssuedAt(String issuedAt) {
			this.issuedAt = issuedAt;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getNationality() {
			return nationality;
		}

		public void setNationality(String nationality) {
			this.nationality = nationality;
		}

		public String getBirthday() {
			return birthday;
		}

		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getValidity() {
			return validity;
		}

		public void setValidity(String validity) {
			this.validity = validity;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "PassengerId [psgNo=" + psgNo + ", idType=" + idType
					+ ", issuedAt=" + issuedAt + ", id=" + id
					+ ", nationality=" + nationality + ", birthday=" + birthday
					+ ", sex=" + sex + ", validity=" + validity + ", name="
					+ name + "]";
		}

    }
}
