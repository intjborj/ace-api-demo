
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
 *         &lt;element name="emergencyvisits" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="emergencyvisitsadult" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="emergencyvisitspediatric" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="evfromfacilitytoanother" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "emergencyvisits",
    "emergencyvisitsadult",
    "emergencyvisitspediatric",
    "evfromfacilitytoanother",
    "reportingyear"
})
@XmlRootElement(name = "hospOptDischargesEV")
public class HospOptDischargesEV {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String emergencyvisits;
    @XmlElement(required = true, nillable = true)
    protected String emergencyvisitsadult;
    @XmlElement(required = true, nillable = true)
    protected String emergencyvisitspediatric;
    @XmlElement(required = true, nillable = true)
    protected String evfromfacilitytoanother;
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
     * Gets the value of the emergencyvisits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmergencyvisits() {
        return emergencyvisits;
    }

    /**
     * Sets the value of the emergencyvisits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmergencyvisits(String value) {
        this.emergencyvisits = value;
    }

    /**
     * Gets the value of the emergencyvisitsadult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmergencyvisitsadult() {
        return emergencyvisitsadult;
    }

    /**
     * Sets the value of the emergencyvisitsadult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmergencyvisitsadult(String value) {
        this.emergencyvisitsadult = value;
    }

    /**
     * Gets the value of the emergencyvisitspediatric property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmergencyvisitspediatric() {
        return emergencyvisitspediatric;
    }

    /**
     * Sets the value of the emergencyvisitspediatric property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmergencyvisitspediatric(String value) {
        this.emergencyvisitspediatric = value;
    }

    /**
     * Gets the value of the evfromfacilitytoanother property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvfromfacilitytoanother() {
        return evfromfacilitytoanother;
    }

    /**
     * Sets the value of the evfromfacilitytoanother property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvfromfacilitytoanother(String value) {
        this.evfromfacilitytoanother = value;
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
