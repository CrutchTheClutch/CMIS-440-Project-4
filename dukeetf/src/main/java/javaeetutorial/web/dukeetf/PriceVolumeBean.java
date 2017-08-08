/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.web.dukeetf;


import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

/* Updates price and volume information every second */
@Startup
@Singleton
public class PriceVolumeBean {
    /* Use the container's timer service */
    @Resource
    private TimerService tservice;
    private DukeETFServlet servlet;
    private String path = Thread.currentThread().getContextClassLoader().getResource("../../WEB-INF/project4input.txt").getFile().replaceAll("%20"," ");    // Gets paths of txt file
    private File file = new File(path);
    private BufferedReader br;
    private static final Logger logger = Logger.getLogger("PriceVolumeBean");

    @PostConstruct
    public void init() {
        /* Initialize the EJB and create a timer */
        logger.log(Level.INFO, "Initializing EJB.");
        servlet = null;
        tservice.createIntervalTimer(0, 1000, new TimerConfig());
    }
    
    public void registerServlet(DukeETFServlet servlet) {
        /* Associate a servlet to send updates to */
        this.servlet = servlet;

        // Setup BufferedReader to read the project4input.txt file from the users Desktop
        try {
            br = new BufferedReader(new FileReader(file.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Timeout
    public void timeout() throws IOException {
        /* Adjust price and volume and send updates */
        String[] data = br.readLine().split(",");
        String date = data[0];
        String time = data[1];
        String price = data[2];
        String volume = data[3];
        String high = data[4];
        String low = data[5];
        if (servlet != null) {
            servlet.send(date, time, price, volume, high, low);
        }
    }
}

