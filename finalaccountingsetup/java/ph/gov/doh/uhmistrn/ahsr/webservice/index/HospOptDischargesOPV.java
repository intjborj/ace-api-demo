
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
 *         &lt;element name="newpatient" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="revisit" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="adult" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="pediatric" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="adultgeneralmedicine" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="specialtynonsurgical" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="surgical" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="antenatal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="postnatal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "newpatient",
    "revisit",
    "adult",
    "pediatric",
    "adultgeneralmedicine",
    "specialtynonsurgical",
    "surgical",
    "antenatal",
    "postnatal",
    "reportingyear"
})
@XmlRootElement(name = "hospOptDischargesOPV")
public class HospOptDischargesOPV {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String newpatient;
    @XmlElement(required = true, nillable = true)
    protected String revisit;
    @XmlElement(required = true, nillable = true)
    protected String adult;
    @XmlElement(required = true, nillable = true)
    protected String pediatric;
    @XmlElement(required = true, nillable = true)
    protected String adultgeneralmedicine;
    @XmlElement(required = true, nillable = true)
    protected String specialtynonsurgical;
    @XmlElement(required = true, nillable = true)
    protected String surgical;
    @XmlElement(required = true, nillable = true)
    protected String antenatal;
    @XmlElement(required = true, nillable = true)
    protected String postnatal;
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
     * Gets the value of the newpatient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewpatient() {
        return newpatient;
    }

    /**
     * Sets the value of the newpatient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewpatient(String value) {
        this.newpatient = value;
    }

    /**
     * Gets the value of the revisit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevisit() {
        return revisit;
    }

    /**
     * Sets the value of the revisit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevisit(String value) {
        this.revisit = value;
    }

    /**
     * Gets the value of the adult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdult() {
        return adult;
    }

    /**
     * Sets the value of the adult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdult(String value) {
        this.adult = value;
    }

    /**
     * Gets the value of the pediatric property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPediatric() {
        return pediatric;
    }

    /**
     * Sets the value of the pediatric property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPediatric(String value) {
        this.pediatric = value;
    }

    /**
     * Gets the value of the adultgeneralmedicine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdultgeneralmedicine() {
        return adultgeneralmedicine;
    }

    /**
     * Sets the value of the adultgeneralmedicine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdultgeneralmedicine(String value) {
        this.adultgeneralmedicine = value;
    }

    /**
     * Gets the value of the specialtynonsurgical property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialtynonsurgical() {
        return specialtynonsurgical;
    }

    /**
     * Sets the value of the specialtynonsurgical property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialtynonsurgical(String value) {
        this.specialtynonsurgical = value;
    }

    /**
     * Gets the value of the surgical property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurgical() {
        return surgical;
    }

    /**
     * Sets the value of the surgical property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurgical(String value) {
        this.surgical = value;
    }

    /**
     * Gets the value of the antenatal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAntenatal() {
        return antenatal;
    }

    /**
     * Sets the value of the antenatal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAntenatal(String value) {
        this.antenatal = value;
    }

    /**
     * Gets the value of the postnatal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostnatal() {
        return postnatal;
    }

    /**
     * Sets the value of the postnatal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostnatal(String value) {
        this.postnatal = value;
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
