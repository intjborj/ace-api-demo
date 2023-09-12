
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
 *         &lt;element name="amountfromdoh" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfromlgu" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfromdonor" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfromprivateorg" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfromphilhealth" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfrompatient" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfromreimbursement" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountfromothersources" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="grandtotal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "amountfromdoh",
    "amountfromlgu",
    "amountfromdonor",
    "amountfromprivateorg",
    "amountfromphilhealth",
    "amountfrompatient",
    "amountfromreimbursement",
    "amountfromothersources",
    "grandtotal",
    "reportingyear"
})
@XmlRootElement(name = "revenues")
public class Revenues {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String amountfromdoh;
    @XmlElement(required = true, nillable = true)
    protected String amountfromlgu;
    @XmlElement(required = true, nillable = true)
    protected String amountfromdonor;
    @XmlElement(required = true, nillable = true)
    protected String amountfromprivateorg;
    @XmlElement(required = true, nillable = true)
    protected String amountfromphilhealth;
    @XmlElement(required = true, nillable = true)
    protected String amountfrompatient;
    @XmlElement(required = true, nillable = true)
    protected String amountfromreimbursement;
    @XmlElement(required = true, nillable = true)
    protected String amountfromothersources;
    @XmlElement(required = true, nillable = true)
    protected String grandtotal;
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
     * Gets the value of the amountfromdoh property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromdoh() {
        return amountfromdoh;
    }

    /**
     * Sets the value of the amountfromdoh property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromdoh(String value) {
        this.amountfromdoh = value;
    }

    /**
     * Gets the value of the amountfromlgu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromlgu() {
        return amountfromlgu;
    }

    /**
     * Sets the value of the amountfromlgu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromlgu(String value) {
        this.amountfromlgu = value;
    }

    /**
     * Gets the value of the amountfromdonor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromdonor() {
        return amountfromdonor;
    }

    /**
     * Sets the value of the amountfromdonor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromdonor(String value) {
        this.amountfromdonor = value;
    }

    /**
     * Gets the value of the amountfromprivateorg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromprivateorg() {
        return amountfromprivateorg;
    }

    /**
     * Sets the value of the amountfromprivateorg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromprivateorg(String value) {
        this.amountfromprivateorg = value;
    }

    /**
     * Gets the value of the amountfromphilhealth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromphilhealth() {
        return amountfromphilhealth;
    }

    /**
     * Sets the value of the amountfromphilhealth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromphilhealth(String value) {
        this.amountfromphilhealth = value;
    }

    /**
     * Gets the value of the amountfrompatient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfrompatient() {
        return amountfrompatient;
    }

    /**
     * Sets the value of the amountfrompatient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfrompatient(String value) {
        this.amountfrompatient = value;
    }

    /**
     * Gets the value of the amountfromreimbursement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromreimbursement() {
        return amountfromreimbursement;
    }

    /**
     * Sets the value of the amountfromreimbursement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromreimbursement(String value) {
        this.amountfromreimbursement = value;
    }

    /**
     * Gets the value of the amountfromothersources property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountfromothersources() {
        return amountfromothersources;
    }

    /**
     * Sets the value of the amountfromothersources property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountfromothersources(String value) {
        this.amountfromothersources = value;
    }

    /**
     * Gets the value of the grandtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrandtotal() {
        return grandtotal;
    }

    /**
     * Sets the value of the grandtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrandtotal(String value) {
        this.grandtotal = value;
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
