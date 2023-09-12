
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
 *         &lt;element name="totalifdelivery" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totallbvdelivery" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totallbcdelivery" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalotherdelivery" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "totalifdelivery",
    "totallbvdelivery",
    "totallbcdelivery",
    "totalotherdelivery",
    "reportingyear"
})
@XmlRootElement(name = "hospOptDischargesNumberDeliveries")
public class HospOptDischargesNumberDeliveries {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String totalifdelivery;
    @XmlElement(required = true, nillable = true)
    protected String totallbvdelivery;
    @XmlElement(required = true, nillable = true)
    protected String totallbcdelivery;
    @XmlElement(required = true, nillable = true)
    protected String totalotherdelivery;
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
     * Gets the value of the totalifdelivery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalifdelivery() {
        return totalifdelivery;
    }

    /**
     * Sets the value of the totalifdelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalifdelivery(String value) {
        this.totalifdelivery = value;
    }

    /**
     * Gets the value of the totallbvdelivery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotallbvdelivery() {
        return totallbvdelivery;
    }

    /**
     * Sets the value of the totallbvdelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotallbvdelivery(String value) {
        this.totallbvdelivery = value;
    }

    /**
     * Gets the value of the totallbcdelivery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotallbcdelivery() {
        return totallbcdelivery;
    }

    /**
     * Sets the value of the totallbcdelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotallbcdelivery(String value) {
        this.totallbcdelivery = value;
    }

    /**
     * Gets the value of the totalotherdelivery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalotherdelivery() {
        return totalotherdelivery;
    }

    /**
     * Sets the value of the totalotherdelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalotherdelivery(String value) {
        this.totalotherdelivery = value;
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
