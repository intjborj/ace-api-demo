
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
 *         &lt;element name="totaldeaths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldeaths48down" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldeaths48up" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalerdeaths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldoa" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalstillbirths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalneonataldeaths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalmaternaldeaths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldeathsnewborn" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldischargedeaths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="grossdeathrate" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="ndrnumerator" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="ndrdenominator" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="netdeathrate" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "totaldeaths",
    "totaldeaths48Down",
    "totaldeaths48Up",
    "totalerdeaths",
    "totaldoa",
    "totalstillbirths",
    "totalneonataldeaths",
    "totalmaternaldeaths",
    "totaldeathsnewborn",
    "totaldischargedeaths",
    "grossdeathrate",
    "ndrnumerator",
    "ndrdenominator",
    "netdeathrate",
    "reportingyear"
})
@XmlRootElement(name = "hospitalOperationsDeaths")
public class HospitalOperationsDeaths {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String totaldeaths;
    @XmlElement(name = "totaldeaths48down", required = true, nillable = true)
    protected String totaldeaths48Down;
    @XmlElement(name = "totaldeaths48up", required = true, nillable = true)
    protected String totaldeaths48Up;
    @XmlElement(required = true, nillable = true)
    protected String totalerdeaths;
    @XmlElement(required = true, nillable = true)
    protected String totaldoa;
    @XmlElement(required = true, nillable = true)
    protected String totalstillbirths;
    @XmlElement(required = true, nillable = true)
    protected String totalneonataldeaths;
    @XmlElement(required = true, nillable = true)
    protected String totalmaternaldeaths;
    @XmlElement(required = true, nillable = true)
    protected String totaldeathsnewborn;
    @XmlElement(required = true, nillable = true)
    protected String totaldischargedeaths;
    @XmlElement(required = true, nillable = true)
    protected String grossdeathrate;
    @XmlElement(required = true, nillable = true)
    protected String ndrnumerator;
    @XmlElement(required = true, nillable = true)
    protected String ndrdenominator;
    @XmlElement(required = true, nillable = true)
    protected String netdeathrate;
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
     * Gets the value of the totaldeaths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldeaths() {
        return totaldeaths;
    }

    /**
     * Sets the value of the totaldeaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldeaths(String value) {
        this.totaldeaths = value;
    }

    /**
     * Gets the value of the totaldeaths48Down property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldeaths48Down() {
        return totaldeaths48Down;
    }

    /**
     * Sets the value of the totaldeaths48Down property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldeaths48Down(String value) {
        this.totaldeaths48Down = value;
    }

    /**
     * Gets the value of the totaldeaths48Up property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldeaths48Up() {
        return totaldeaths48Up;
    }

    /**
     * Sets the value of the totaldeaths48Up property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldeaths48Up(String value) {
        this.totaldeaths48Up = value;
    }

    /**
     * Gets the value of the totalerdeaths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalerdeaths() {
        return totalerdeaths;
    }

    /**
     * Sets the value of the totalerdeaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalerdeaths(String value) {
        this.totalerdeaths = value;
    }

    /**
     * Gets the value of the totaldoa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldoa() {
        return totaldoa;
    }

    /**
     * Sets the value of the totaldoa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldoa(String value) {
        this.totaldoa = value;
    }

    /**
     * Gets the value of the totalstillbirths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalstillbirths() {
        return totalstillbirths;
    }

    /**
     * Sets the value of the totalstillbirths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalstillbirths(String value) {
        this.totalstillbirths = value;
    }

    /**
     * Gets the value of the totalneonataldeaths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalneonataldeaths() {
        return totalneonataldeaths;
    }

    /**
     * Sets the value of the totalneonataldeaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalneonataldeaths(String value) {
        this.totalneonataldeaths = value;
    }

    /**
     * Gets the value of the totalmaternaldeaths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalmaternaldeaths() {
        return totalmaternaldeaths;
    }

    /**
     * Sets the value of the totalmaternaldeaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalmaternaldeaths(String value) {
        this.totalmaternaldeaths = value;
    }

    /**
     * Gets the value of the totaldeathsnewborn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldeathsnewborn() {
        return totaldeathsnewborn;
    }

    /**
     * Sets the value of the totaldeathsnewborn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldeathsnewborn(String value) {
        this.totaldeathsnewborn = value;
    }

    /**
     * Gets the value of the totaldischargedeaths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldischargedeaths() {
        return totaldischargedeaths;
    }

    /**
     * Sets the value of the totaldischargedeaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldischargedeaths(String value) {
        this.totaldischargedeaths = value;
    }

    /**
     * Gets the value of the grossdeathrate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrossdeathrate() {
        return grossdeathrate;
    }

    /**
     * Sets the value of the grossdeathrate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrossdeathrate(String value) {
        this.grossdeathrate = value;
    }

    /**
     * Gets the value of the ndrnumerator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNdrnumerator() {
        return ndrnumerator;
    }

    /**
     * Sets the value of the ndrnumerator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNdrnumerator(String value) {
        this.ndrnumerator = value;
    }

    /**
     * Gets the value of the ndrdenominator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNdrdenominator() {
        return ndrdenominator;
    }

    /**
     * Sets the value of the ndrdenominator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNdrdenominator(String value) {
        this.ndrdenominator = value;
    }

    /**
     * Gets the value of the netdeathrate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetdeathrate() {
        return netdeathrate;
    }

    /**
     * Sets the value of the netdeathrate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetdeathrate(String value) {
        this.netdeathrate = value;
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
