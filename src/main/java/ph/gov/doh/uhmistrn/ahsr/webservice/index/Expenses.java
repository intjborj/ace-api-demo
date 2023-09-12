
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
 *         &lt;element name="salarieswages" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="employeebenefits" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="allowances" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalps" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalamountmedicine" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalamountmedicalsupplies" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalamountutilities" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalamountnonmedicalservice" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalmooe" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountinfrastructure" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="amountequipment" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalco" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "salarieswages",
    "employeebenefits",
    "allowances",
    "totalps",
    "totalamountmedicine",
    "totalamountmedicalsupplies",
    "totalamountutilities",
    "totalamountnonmedicalservice",
    "totalmooe",
    "amountinfrastructure",
    "amountequipment",
    "totalco",
    "grandtotal",
    "reportingyear"
})
@XmlRootElement(name = "expenses")
public class Expenses {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String salarieswages;
    @XmlElement(required = true, nillable = true)
    protected String employeebenefits;
    @XmlElement(required = true, nillable = true)
    protected String allowances;
    @XmlElement(required = true, nillable = true)
    protected String totalps;
    @XmlElement(required = true, nillable = true)
    protected String totalamountmedicine;
    @XmlElement(required = true, nillable = true)
    protected String totalamountmedicalsupplies;
    @XmlElement(required = true, nillable = true)
    protected String totalamountutilities;
    @XmlElement(required = true, nillable = true)
    protected String totalamountnonmedicalservice;
    @XmlElement(required = true, nillable = true)
    protected String totalmooe;
    @XmlElement(required = true, nillable = true)
    protected String amountinfrastructure;
    @XmlElement(required = true, nillable = true)
    protected String amountequipment;
    @XmlElement(required = true, nillable = true)
    protected String totalco;
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
     * Gets the value of the salarieswages property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalarieswages() {
        return salarieswages;
    }

    /**
     * Sets the value of the salarieswages property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalarieswages(String value) {
        this.salarieswages = value;
    }

    /**
     * Gets the value of the employeebenefits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeebenefits() {
        return employeebenefits;
    }

    /**
     * Sets the value of the employeebenefits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeebenefits(String value) {
        this.employeebenefits = value;
    }

    /**
     * Gets the value of the allowances property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllowances() {
        return allowances;
    }

    /**
     * Sets the value of the allowances property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowances(String value) {
        this.allowances = value;
    }

    /**
     * Gets the value of the totalps property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalps() {
        return totalps;
    }

    /**
     * Sets the value of the totalps property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalps(String value) {
        this.totalps = value;
    }

    /**
     * Gets the value of the totalamountmedicine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalamountmedicine() {
        return totalamountmedicine;
    }

    /**
     * Sets the value of the totalamountmedicine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalamountmedicine(String value) {
        this.totalamountmedicine = value;
    }

    /**
     * Gets the value of the totalamountmedicalsupplies property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalamountmedicalsupplies() {
        return totalamountmedicalsupplies;
    }

    /**
     * Sets the value of the totalamountmedicalsupplies property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalamountmedicalsupplies(String value) {
        this.totalamountmedicalsupplies = value;
    }

    /**
     * Gets the value of the totalamountutilities property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalamountutilities() {
        return totalamountutilities;
    }

    /**
     * Sets the value of the totalamountutilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalamountutilities(String value) {
        this.totalamountutilities = value;
    }

    /**
     * Gets the value of the totalamountnonmedicalservice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalamountnonmedicalservice() {
        return totalamountnonmedicalservice;
    }

    /**
     * Sets the value of the totalamountnonmedicalservice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalamountnonmedicalservice(String value) {
        this.totalamountnonmedicalservice = value;
    }

    /**
     * Gets the value of the totalmooe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalmooe() {
        return totalmooe;
    }

    /**
     * Sets the value of the totalmooe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalmooe(String value) {
        this.totalmooe = value;
    }

    /**
     * Gets the value of the amountinfrastructure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountinfrastructure() {
        return amountinfrastructure;
    }

    /**
     * Sets the value of the amountinfrastructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountinfrastructure(String value) {
        this.amountinfrastructure = value;
    }

    /**
     * Gets the value of the amountequipment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountequipment() {
        return amountequipment;
    }

    /**
     * Sets the value of the amountequipment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountequipment(String value) {
        this.amountequipment = value;
    }

    /**
     * Gets the value of the totalco property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalco() {
        return totalco;
    }

    /**
     * Sets the value of the totalco property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalco(String value) {
        this.totalco = value;
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
