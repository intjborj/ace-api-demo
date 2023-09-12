
package ph.gov.doh.uhmistrn.ahsr.webservice.index;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hfhudcode" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="professiondesignation" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="specialtyboardcertified" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fulltime40permanent" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fulltime40contractual" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="parttimepermanent" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="parttimecontractual" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="activerotatingaffiliate" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="outsourced" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="reportingyear" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "hfhudcode",
    "professiondesignation",
    "specialtyboardcertified",
    "fulltime40Permanent",
    "fulltime40Contractual",
    "parttimepermanent",
    "parttimecontractual",
    "activerotatingaffiliate",
    "outsourced",
    "reportingyear"
})
@XmlRootElement(name = "staffingPattern")
public class StaffingPattern {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String professiondesignation;
    @XmlElement(required = true, nillable = true)
    protected String specialtyboardcertified;
    @XmlElement(name = "fulltime40permanent", required = true, nillable = true)
    protected String fulltime40Permanent;
    @XmlElement(name = "fulltime40contractual", required = true, nillable = true)
    protected String fulltime40Contractual;
    @XmlElement(required = true, nillable = true)
    protected String parttimepermanent;
    @XmlElement(required = true, nillable = true)
    protected String parttimecontractual;
    @XmlElement(required = true, nillable = true)
    protected String activerotatingaffiliate;
    @XmlElement(required = true, nillable = true)
    protected String outsourced;
    @XmlElement(required = true, nillable = true)
    protected String reportingyear;

    /**
     * Gets the value of the hfhudcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHfhudcode() {
        return hfhudcode;
    }

    /**
     * Sets the value of the hfhudcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHfhudcode(String value) {
        this.hfhudcode = value;
    }

    /**
     * Gets the value of the professiondesignation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfessiondesignation() {
        return professiondesignation;
    }

    /**
     * Sets the value of the professiondesignation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfessiondesignation(String value) {
        this.professiondesignation = value;
    }

    /**
     * Gets the value of the specialtyboardcertified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialtyboardcertified() {
        return specialtyboardcertified;
    }

    /**
     * Sets the value of the specialtyboardcertified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialtyboardcertified(String value) {
        this.specialtyboardcertified = value;
    }

    /**
     * Gets the value of the fulltime40Permanent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFulltime40Permanent() {
        return fulltime40Permanent;
    }

    /**
     * Sets the value of the fulltime40Permanent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFulltime40Permanent(String value) {
        this.fulltime40Permanent = value;
    }

    /**
     * Gets the value of the fulltime40Contractual property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFulltime40Contractual() {
        return fulltime40Contractual;
    }

    /**
     * Sets the value of the fulltime40Contractual property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFulltime40Contractual(String value) {
        this.fulltime40Contractual = value;
    }

    /**
     * Gets the value of the parttimepermanent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParttimepermanent() {
        return parttimepermanent;
    }

    /**
     * Sets the value of the parttimepermanent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParttimepermanent(String value) {
        this.parttimepermanent = value;
    }

    /**
     * Gets the value of the parttimecontractual property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParttimecontractual() {
        return parttimecontractual;
    }

    /**
     * Sets the value of the parttimecontractual property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParttimecontractual(String value) {
        this.parttimecontractual = value;
    }

    /**
     * Gets the value of the activerotatingaffiliate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActiverotatingaffiliate() {
        return activerotatingaffiliate;
    }

    /**
     * Sets the value of the activerotatingaffiliate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActiverotatingaffiliate(String value) {
        this.activerotatingaffiliate = value;
    }

    /**
     * Gets the value of the outsourced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutsourced() {
        return outsourced;
    }

    /**
     * Sets the value of the outsourced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutsourced(String value) {
        this.outsourced = value;
    }

    /**
     * Gets the value of the reportingyear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportingyear() {
        return reportingyear;
    }

    /**
     * Sets the value of the reportingyear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportingyear(String value) {
        this.reportingyear = value;
    }

}
