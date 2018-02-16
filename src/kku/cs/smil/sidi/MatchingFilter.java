/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

/**
 *
 * @author Administrator
 */
public class MatchingFilter {

    private String processURI = "";
    private String inputFilter = "";
    private String outputFilter = "";
    private double pScore;
    private double rScore;

    public MatchingFilter(String uri){
        this.processURI = uri;
    }

    public String getInputFilter() {
        return inputFilter;
    }

    public void setInputFilter(String inputFilter) {
        this.inputFilter = inputFilter;
    }

    public String getOutputFilter() {
        return outputFilter;
    }

    public void setOutputFilter(String outputFilter) {
        this.outputFilter = outputFilter;
    }

    public double getpScore() {
        return pScore;
    }

    public void setpScore(double pScore) {
        this.pScore = pScore;
    }

    public String getProcessURI() {
        return processURI;
    }

    public void setProcessURI(String processURI) {
        this.processURI = processURI;
    }

    public double getrScore() {
        return rScore;
    }

    public void setrScore(double rScore) {
        this.rScore = rScore;
    }
}
