
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
 *         &lt;element name="servicecapability" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="general" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="specialty" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="specialtyspecify" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="traumacapability" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="natureofownership" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="government" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="national" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="local" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="private" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="reportingyear" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="ownershipothers" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "servicecapability",
    "general",
    "specialty",
    "specialtyspecify",
    "traumacapability",
    "natureofownership",
    "government",
    "national",
    "local",
    "_private",
    "reportingyear",
    "ownershipothers"
})
@XmlRootElement(name = "genInfoClassification")
public class GenInfoClassification {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String servicecapability;
    @XmlElement(required = true, nillable = true)
    protected String general;
    @XmlElement(required = true, nillable = true)
    protected String specialty;
    @XmlElement(required = true, nillable = true)
    protected String specialtyspecify;
    @XmlElement(required = true, nillable = true)
    protected String traumacapability;
    @XmlElement(required = true, nillable = true)
    protected String natureofownership;
    @XmlElement(required = true, nillable = true)
    protected String government;
    @XmlElement(required = true, nillable = true)
    protected String national;
    @XmlElement(required = true, nillable = true)
    protected String local;
    @XmlElement(name = "private", required = true, nillable = true)
    protected String _private;
    @XmlElement(required = true, nillable = true)
    protected String reportingyear;
    @XmlElement(required = true, nillable = true)
    protected String ownershipothers;

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
     * Gets the value of the servicecapability property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicecapability() {
        return servicecapability;
    }

    /**
     * Sets the value of the servicecapability property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicecapability(String value) {
        this.servicecapability = value;
    }

    /**
     * Gets the value of the general property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneral() {
        return general;
    }

    /**
     * Sets the value of the general property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneral(String value) {
        this.general = value;
    }

    /**
     * Gets the value of the specialty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the value of the specialty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialty(String value) {
        this.specialty = value;
    }

    /**
     * Gets the value of the specialtyspecify property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialtyspecify() {
        return specialtyspecify;
    }

    /**
     * Sets the value of the specialtyspecify property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialtyspecify(String value) {
        this.specialtyspecify = value;
    }

    /**
     * Gets the value of the traumacapability property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTraumacapability() {
        return traumacapability;
    }

    /**
     * Sets the value of the traumacapability property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTraumacapability(String value) {
        this.traumacapability = value;
    }

    /**
     * Gets the value of the natureofownership property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNatureofownership() {
        return natureofownership;
    }

    /**
     * Sets the value of the natureofownership property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNatureofownership(String value) {
        this.natureofownership = value;
    }

    /**
     * Gets the value of the government property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGovernment() {
        return government;
    }

    /**
     * Sets the value of the government property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGovernment(String value) {
        this.government = value;
    }

    /**
     * Gets the value of the national property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNational() {
        return national;
    }

    /**
     * Sets the value of the national property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNational(String value) {
        this.national = value;
    }

    /**
     * Gets the value of the local property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocal() {
        return local;
    }

    /**
     * Sets the value of the local property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocal(String value) {
        this.local = value;
    }

    /**
     * Gets the value of the private property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivate() {
        return _private;
    }

    /**
     * Sets the value of the private property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivate(String value) {
        this._private = value;
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

    /**
     * Gets the value of the ownershipothers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnershipothers() {
        return ownershipothers;
    }

    /**
     * Sets the value of the ownershipothers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnershipothers(String value) {
        this.ownershipothers = value;
    }

}
