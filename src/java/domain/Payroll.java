package domain;

import database.PayrollDA;
import database.PayrollSystemDA;

import java.io.Serializable;
import java.text.*;
import java.util.*;
import javax.persistence.*;

@Entity
public class Payroll implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Payroll_ID")
    private int payrollID;
    @Column(name = "Payroll_Date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(name = "Employee_ID")
    private int employeeID;
    @Column(name = "Gross_Pay")
    private double grossPay;
    @Column(name = "Total_Deductions")
    private double totalDeductions;
    @Column(name = "Net_Pay")
    private double netPay;
    
    public Payroll(){}
    
    public void add(Payroll p) {
        EntityManager em = PayrollSystemDA.getEmFactory().createEntityManager();
        EntityTransaction transact = em.getTransaction();
        try {
            transact.begin();
            em.persist(p);
            System.out.println("Payroll record to add " + p);
            transact.commit();
        } finally {
            em.close();
        }
    }
    
    public static void calculatePayroll(Date date) {
        ArrayList<Employee> employees = Employee.getEmployees();
        ArrayList<WithholdingType>withholdingTypes = WithholdingType.getWithholdingTypes(); 
        
        Payroll payroll;
        Employee emp;
        WithholdingType withholding;
        double grossPay;
        double totalDeductions;
        double netPay;
        
        for(int i = 0; i < employees.size(); i++) {
            emp = employees.get(i);
            System.out.println(emp);
            grossPay = emp.calculateGrossPay(date);
            totalDeductions = 0;
            for (int n = 0; n < withholdingTypes.size(); n++) {
                withholding = withholdingTypes.get(n);
                totalDeductions += grossPay * withholding.getRate() / 100 + withholding.getAmount();
            }
            netPay = grossPay - totalDeductions;
                        
            payroll = new Payroll();
            payroll.setDate(date);
            payroll.setEmployeeID(emp.getEmployeeID());
            payroll.setGrossPay(grossPay);
            payroll.setTotalDeductions(totalDeductions);
            payroll.setNetPay(netPay);
            payroll.add(payroll);
        }
    }

    public Date getDate() {
        return date;
    }
    
    public String getDateFormatted(){
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        return dateFormat.format(date);
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public double getGrossPay() {
        return grossPay;
    }
    
    public String getGrossPayFormatted(){
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return currency.format(grossPay);
    }

    public double getNetPay() {
        return netPay;
    }
    
    public String getNetPayFormatted(){
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return currency.format(netPay);
    }
    
    public int getPayrollID() {
        return payrollID;
    }
    
    public static ArrayList<Payroll> getPayrollRecords(Date date) {
        return PayrollDA.getPayrollRecords(date);
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }
    
    public String getTotalDeductionsFormatted(){
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return currency.format(totalDeductions);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public void setPayrollID(int payrollID) {
        this.payrollID = payrollID;
    }

    public void setTotalDeductions(double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }
    
    public String toString(){
        Employee emp = Employee.find(employeeID);
        return getDateFormatted() + "  " + employeeID + "  " + emp.getLastName() + ",  "+ emp.getFirstName() + "  " + getGrossPayFormatted() + "  " + getTotalDeductionsFormatted() + "  " + getNetPayFormatted();
    }
}