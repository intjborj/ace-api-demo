
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
 *         &lt;element name="qualitymgmttype" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="certifyingbody" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="philhealthaccreditation" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="validityfrom" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="validityto" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "qualitymgmttype",
    "description",
    "certifyingbody",
    "philhealthaccreditation",
    "validityfrom",
    "validityto",
    "reportingyear"
})
@XmlRootElement(name = "genInfoQualityManagement")
public class GenInfoQualityManagement {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String qualitymgmttype;
    @XmlElement(required = true, nillable = true)
    protected String description;
    @XmlElement(required = true, nillable = true)
    protected String certifyingbody;
    @XmlElement(required = true, nillable = true)
    protected String philhealthaccreditation;
    @XmlElement(required = true, nillable = true)
    protected String validityfrom;
    @XmlElement(required = true, nillable = true)
    protected String validityto;
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
     * Gets the value of the qualitymgmttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualitymgmttype() {
        return qualitymgmttype;
    }

    /**
     * Sets the value of the qualitymgmttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualitymgmttype(String value) {
        this.qualitymgmttype = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the certifyingbody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertifyingbody() {
        return certifyingbody;
    }

    /**
     * Sets the value of the certifyingbody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertifyingbody(String value) {
        this.certifyingbody = value;
    }

    /**
     * Gets the value of the philhealthaccreditation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhilhealthaccreditation() {
        return philhealthaccreditation;
    }

    /**
     * Sets the value of the philhealthaccreditation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhilhealthaccreditation(String value) {
        this.philhealthaccreditation = value;
    }

    /**
     * Gets the value of the validityfrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidityfrom() {
        return validityfrom;
    }

    /**
     * Sets the value of the validityfrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidityfrom(String value) {
        this.validityfrom = value;
    }

    /**
     * Gets the value of the validityto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidityto() {
        return validityto;
    }

    /**
     * Sets the value of the validityto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidityto(String value) {
        this.validityto = value;
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
