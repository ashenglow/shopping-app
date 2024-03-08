package test.shop.web;

import lombok.Getter;
import lombok.Setter;

import javax.print.attribute.standard.PrinterURI;

@Getter
@Setter
public class MemberForm {

    private String name;
    private String city;
    private String street;
    private String zipcode;
}
