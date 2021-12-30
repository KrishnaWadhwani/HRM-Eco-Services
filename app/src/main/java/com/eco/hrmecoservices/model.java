package com.eco.hrmecoservices;

public class model {
    /*String serviceName, serviceImage, price, tax, address;

    public model() {
    }

    public model(String serviceName, String serviceImage, String price, String tax, String address) {
        this.serviceName = serviceName;
        this.serviceImage = serviceImage;
        this.price = price;
        this.tax = tax;
        this.address = address;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public String getPrice() {
        return price;
    }

    public String getTax() {
        return tax;
    }

    public String getAddress() {
        return address;
    }*/
    String Price, service_needed, serviceimage, tax, full_address, MoreInstructionsForUs, cartId;

    public model() {
    }

    public model(String price, String service_needed, String serviceimage, String tax, String full_address, String moreInstructionsForUs, String cartId) {
        Price = price;
        this.service_needed = service_needed;
        this.serviceimage = serviceimage;
        this.tax = tax;
        this.full_address = full_address;
        MoreInstructionsForUs = moreInstructionsForUs;
        this.cartId = cartId;
    }

    public String getPrice() {
        return Price;
    }

    public String getService_needed() {
        return service_needed;
    }

    public String getServiceimage() {
        return serviceimage;
    }

    public String getTax() {
        return tax;
    }

    public String getFull_address() {
        return full_address;
    }

    public String getMoreInstructionsForUs() {
        return MoreInstructionsForUs;
    }

    public String getCartId() {
        return cartId;
    }
}
