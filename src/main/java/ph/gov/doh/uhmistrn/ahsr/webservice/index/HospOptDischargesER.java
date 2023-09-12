
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
 *         &lt;element name="erconsultations" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="icd10code" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="icd10category" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "erconsultations",
    "number",
    "icd10Code",
    "icd10Category",
    "reportingyear"
})
@XmlRootElement(name = "hospOptDischargesER")
public class HospOptDischargesER {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String erconsultations;
    @XmlElement(required = true, nillable = true)
    protected String number;
    @XmlElement(name = "icd10code", required = true, nillable = true)
    protected String icd10Code;
    @XmlElement(name = "icd10category", required = true, nillable = true)
    protected String icd10Category;
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
     * Gets the value of the erconsultations property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErconsultations() {
        return erconsultations;
    }

    /**
     * Sets the value of the erconsultations property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErconsultations(String value) {
        this.erconsultations = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the icd10Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcd10Code() {
        return icd10Code;
    }

    /**
     * Sets the value of the icd10Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcd10Code(String value) {
        this.icd10Code = value;
    }

    /**
     * Gets the value of the icd10Category property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcd10Category() {
        return icd10Category;
    }

    /**
     * Sets the value of the icd10Category property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcd10Category(String value) {
        this.icd10Category = value;
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
