
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
 *         &lt;element name="abc" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="implementingbeds" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="bor" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "abc",
    "implementingbeds",
    "bor",
    "reportingyear"
})
@XmlRootElement(name = "genInfoBedCapacity")
public class GenInfoBedCapacity {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String abc;
    @XmlElement(required = true, nillable = true)
    protected String implementingbeds;
    @XmlElement(required = true, nillable = true)
    protected String bor;
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
     * Gets the value of the abc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbc() {
        return abc;
    }

    /**
     * Sets the value of the abc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbc(String value) {
        this.abc = value;
    }

    /**
     * Gets the value of the implementingbeds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImplementingbeds() {
        return implementingbeds;
    }

    /**
     * Sets the value of the implementingbeds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImplementingbeds(String value) {
        this.implementingbeds = value;
    }

    /**
     * Gets the value of the bor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBor() {
        return bor;
    }

    /**
     * Sets the value of the bor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBor(String value) {
        this.bor = value;
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
