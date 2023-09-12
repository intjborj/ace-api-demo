
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
 *         &lt;element name="totalinpatients" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalnewborn" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldischarges" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalpad" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalibd" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalinpatienttransto" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalinpatienttransfrom" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalpatientsremaining" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "totalinpatients",
    "totalnewborn",
    "totaldischarges",
    "totalpad",
    "totalibd",
    "totalinpatienttransto",
    "totalinpatienttransfrom",
    "totalpatientsremaining",
    "reportingyear"
})
@XmlRootElement(name = "hospOptSummaryOfPatients")
public class HospOptSummaryOfPatients {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String totalinpatients;
    @XmlElement(required = true, nillable = true)
    protected String totalnewborn;
    @XmlElement(required = true, nillable = true)
    protected String totaldischarges;
    @XmlElement(required = true, nillable = true)
    protected String totalpad;
    @XmlElement(required = true, nillable = true)
    protected String totalibd;
    @XmlElement(required = true, nillable = true)
    protected String totalinpatienttransto;
    @XmlElement(required = true, nillable = true)
    protected String totalinpatienttransfrom;
    @XmlElement(required = true, nillable = true)
    protected String totalpatientsremaining;
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
     * Gets the value of the totalinpatients property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalinpatients() {
        return totalinpatients;
    }

    /**
     * Sets the value of the totalinpatients property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalinpatients(String value) {
        this.totalinpatients = value;
    }

    /**
     * Gets the value of the totalnewborn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalnewborn() {
        return totalnewborn;
    }

    /**
     * Sets the value of the totalnewborn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalnewborn(String value) {
        this.totalnewborn = value;
    }

    /**
     * Gets the value of the totaldischarges property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldischarges() {
        return totaldischarges;
    }

    /**
     * Sets the value of the totaldischarges property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldischarges(String value) {
        this.totaldischarges = value;
    }

    /**
     * Gets the value of the totalpad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalpad() {
        return totalpad;
    }

    /**
     * Sets the value of the totalpad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalpad(String value) {
        this.totalpad = value;
    }

    /**
     * Gets the value of the totalibd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalibd() {
        return totalibd;
    }

    /**
     * Sets the value of the totalibd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalibd(String value) {
        this.totalibd = value;
    }

    /**
     * Gets the value of the totalinpatienttransto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalinpatienttransto() {
        return totalinpatienttransto;
    }

    /**
     * Sets the value of the totalinpatienttransto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalinpatienttransto(String value) {
        this.totalinpatienttransto = value;
    }

    /**
     * Gets the value of the totalinpatienttransfrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalinpatienttransfrom() {
        return totalinpatienttransfrom;
    }

    /**
     * Sets the value of the totalinpatienttransfrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalinpatienttransfrom(String value) {
        this.totalinpatienttransfrom = value;
    }

    /**
     * Gets the value of the totalpatientsremaining property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalpatientsremaining() {
        return totalpatientsremaining;
    }

    /**
     * Sets the value of the totalpatientsremaining property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalpatientsremaining(String value) {
        this.totalpatientsremaining = value;
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
