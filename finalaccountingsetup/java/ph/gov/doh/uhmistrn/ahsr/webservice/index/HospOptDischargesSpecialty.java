
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
 *         &lt;element name="typeofservice" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="nopatients" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totallengthstay" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="nppay" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="nphservicecharity" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="nphtotal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="phpay" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="phservice" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="phtotal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="hmo" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="owwa" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="recoveredimproved" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="transferred" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="hama" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="absconded" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="unimproved" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="deathsbelow48" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="deathsover48" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldeaths" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totaldischarges" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "typeofservice",
    "nopatients",
    "totallengthstay",
    "nppay",
    "nphservicecharity",
    "nphtotal",
    "phpay",
    "phservice",
    "phtotal",
    "hmo",
    "owwa",
    "recoveredimproved",
    "transferred",
    "hama",
    "absconded",
    "unimproved",
    "deathsbelow48",
    "deathsover48",
    "totaldeaths",
    "totaldischarges",
    "remarks",
    "reportingyear"
})
@XmlRootElement(name = "hospOptDischargesSpecialty")
public class HospOptDischargesSpecialty {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String typeofservice;
    @XmlElement(required = true, nillable = true)
    protected String nopatients;
    @XmlElement(required = true, nillable = true)
    protected String totallengthstay;
    @XmlElement(required = true, nillable = true)
    protected String nppay;
    @XmlElement(required = true, nillable = true)
    protected String nphservicecharity;
    @XmlElement(required = true, nillable = true)
    protected String nphtotal;
    @XmlElement(required = true, nillable = true)
    protected String phpay;
    @XmlElement(required = true, nillable = true)
    protected String phservice;
    @XmlElement(required = true, nillable = true)
    protected String phtotal;
    @XmlElement(required = true, nillable = true)
    protected String hmo;
    @XmlElement(required = true, nillable = true)
    protected String owwa;
    @XmlElement(required = true, nillable = true)
    protected String recoveredimproved;
    @XmlElement(required = true, nillable = true)
    protected String transferred;
    @XmlElement(required = true, nillable = true)
    protected String hama;
    @XmlElement(required = true, nillable = true)
    protected String absconded;
    @XmlElement(required = true, nillable = true)
    protected String unimproved;
    @XmlElement(required = true, nillable = true)
    protected String deathsbelow48;
    @XmlElement(required = true, nillable = true)
    protected String deathsover48;
    @XmlElement(required = true, nillable = true)
    protected String totaldeaths;
    @XmlElement(required = true, nillable = true)
    protected String totaldischarges;
    @XmlElement(required = true, nillable = true)
    protected String remarks;
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
     * Gets the value of the typeofservice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeofservice() {
        return typeofservice;
    }

    /**
     * Sets the value of the typeofservice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeofservice(String value) {
        this.typeofservice = value;
    }

    /**
     * Gets the value of the nopatients property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNopatients() {
        return nopatients;
    }

    /**
     * Sets the value of the nopatients property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNopatients(String value) {
        this.nopatients = value;
    }

    /**
     * Gets the value of the totallengthstay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotallengthstay() {
        return totallengthstay;
    }

    /**
     * Sets the value of the totallengthstay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotallengthstay(String value) {
        this.totallengthstay = value;
    }

    /**
     * Gets the value of the nppay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNppay() {
        return nppay;
    }

    /**
     * Sets the value of the nppay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNppay(String value) {
        this.nppay = value;
    }

    /**
     * Gets the value of the nphservicecharity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNphservicecharity() {
        return nphservicecharity;
    }

    /**
     * Sets the value of the nphservicecharity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNphservicecharity(String value) {
        this.nphservicecharity = value;
    }

    /**
     * Gets the value of the nphtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNphtotal() {
        return nphtotal;
    }

    /**
     * Sets the value of the nphtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNphtotal(String value) {
        this.nphtotal = value;
    }

    /**
     * Gets the value of the phpay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhpay() {
        return phpay;
    }

    /**
     * Sets the value of the phpay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhpay(String value) {
        this.phpay = value;
    }

    /**
     * Gets the value of the phservice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhservice() {
        return phservice;
    }

    /**
     * Sets the value of the phservice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhservice(String value) {
        this.phservice = value;
    }

    /**
     * Gets the value of the phtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhtotal() {
        return phtotal;
    }

    /**
     * Sets the value of the phtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhtotal(String value) {
        this.phtotal = value;
    }

    /**
     * Gets the value of the hmo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHmo() {
        return hmo;
    }

    /**
     * Sets the value of the hmo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHmo(String value) {
        this.hmo = value;
    }

    /**
     * Gets the value of the owwa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwwa() {
        return owwa;
    }

    /**
     * Sets the value of the owwa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwwa(String value) {
        this.owwa = value;
    }

    /**
     * Gets the value of the recoveredimproved property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecoveredimproved() {
        return recoveredimproved;
    }

    /**
     * Sets the value of the recoveredimproved property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecoveredimproved(String value) {
        this.recoveredimproved = value;
    }

    /**
     * Gets the value of the transferred property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferred() {
        return transferred;
    }

    /**
     * Sets the value of the transferred property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferred(String value) {
        this.transferred = value;
    }

    /**
     * Gets the value of the hama property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHama() {
        return hama;
    }

    /**
     * Sets the value of the hama property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHama(String value) {
        this.hama = value;
    }

    /**
     * Gets the value of the absconded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbsconded() {
        return absconded;
    }

    /**
     * Sets the value of the absconded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbsconded(String value) {
        this.absconded = value;
    }

    /**
     * Gets the value of the unimproved property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnimproved() {
        return unimproved;
    }

    /**
     * Sets the value of the unimproved property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnimproved(String value) {
        this.unimproved = value;
    }

    /**
     * Gets the value of the deathsbelow48 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeathsbelow48() {
        return deathsbelow48;
    }

    /**
     * Sets the value of the deathsbelow48 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeathsbelow48(String value) {
        this.deathsbelow48 = value;
    }

    /**
     * Gets the value of the deathsover48 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeathsover48() {
        return deathsover48;
    }

    /**
     * Sets the value of the deathsover48 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeathsover48(String value) {
        this.deathsover48 = value;
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
     * Gets the value of the totaldischarges property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaldischarges() {
        return totaldischarges;
    }

    /**
     * Sets the value of the totaldischarges property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaldischarges(String value) {
        this.totaldischarges = value;
    }

    /**
     * Gets the value of the remarks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the value of the remarks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemarks(String value) {
        this.remarks = value;
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
