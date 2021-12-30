package com.eco.hrmecoservices;

public class ordersmodel {
    String Price, service_needed, serviceimage, tax, full_address, MoreInstructionsForUs, cartId, timeStamp;

    public ordersmodel() {
    }

    public ordersmodel(String price, String service_needed, String serviceimage, String tax, String full_address, String moreInstructionsForUs, String cartId, String timeStamp) {
        Price = price;
        this.service_needed = service_needed;
        this.serviceimage = serviceimage;
        this.tax = tax;
        this.full_address = full_address;
        MoreInstructionsForUs = moreInstructionsForUs;
        this.cartId = cartId;
        this.timeStamp = timeStamp;
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

    public String getTimeStamp() {
        return timeStamp;
    }
}
