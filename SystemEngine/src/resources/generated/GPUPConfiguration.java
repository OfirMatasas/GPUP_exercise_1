//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.11.08 at 09:22:10 AM IST 
//

package resources.generated;

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
 *       &lt;all>
 *         &lt;element ref="{}GPUP-Graph-Name"/>
 *         &lt;element ref="{}GPUP-Working-Directory"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "GPUP-Configuration")
public class GPUPConfiguration {

    @XmlElement(name = "GPUP-Graph-Name", required = true)
    protected String gpupGraphName;
    @XmlElement(name = "GPUP-Working-Directory", required = true)
    protected String gpupWorkingDirectory;

    /**
     * Gets the value of the gpupGraphName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGPUPGraphName() {
        return gpupGraphName;
    }

    /**
     * Sets the value of the gpupGraphName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGPUPGraphName(String value) {
        this.gpupGraphName = value;
    }

    /**
     * Gets the value of the gpupWorkingDirectory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGPUPWorkingDirectory() {
        return gpupWorkingDirectory;
    }

    /**
     * Sets the value of the gpupWorkingDirectory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGPUPWorkingDirectory(String value) {
        this.gpupWorkingDirectory = value;
    }

}
