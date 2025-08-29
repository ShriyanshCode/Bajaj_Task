package com.bfhl.javaapp.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConfigurationProperties(prefix = "candidate")
public class AppProperties {
    private String name;
    private String regNo;
    private String email;
    private boolean authBearer = false;
    private boolean alsoPostToReturnedWebhook = true;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isAuthBearer() { return authBearer; }
    public void setAuthBearer(boolean authBearer) { this.authBearer = authBearer; }
    public boolean isAlsoPostToReturnedWebhook() { return alsoPostToReturnedWebhook; }
    public void setAlsoPostToReturnedWebhook(boolean alsoPostToReturnedWebhook) { this.alsoPostToReturnedWebhook = alsoPostToReturnedWebhook; }
}
