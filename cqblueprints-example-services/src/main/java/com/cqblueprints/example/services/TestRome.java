package com.cqblueprints.example.services;


import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * outputs an rss feed using a promo component that is embedded in the page at the current path being used
 */
@Component(metatype = false)
@Service({Servlet.class})
@Properties({@org.apache.felix.scr.annotations.Property(name = "sling.servlet.resourceTypes", value = {"sling/servlet/default"}), @org.apache.felix.scr.annotations.Property(name = "sling.servlet.extensions", value = {"rss"}), @org.apache.felix.scr.annotations.Property(name = "sling.servlet.methods", value = {"GET"})})
public class TestRome extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TestRome.class);

    private static final String CONTENT_TYPE = "application/rss+xml";

    private final String RSS_FEED_TYPE = "rss_2.0";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource resource = request.getResource();
        LOG.error("path is:" + resource.getPath());
        response.setContentType(CONTENT_TYPE);

        try {
            LOG.error("in get rss feed, will error on next line when rome is initialized and it tries to read properties");
            SyndFeed feed = new SyndFeedImpl();
            LOG.error("created synd feed");
            feed.setFeedType(RSS_FEED_TYPE);
            LOG.error("setting feed title");
            feed.setTitle("test");
            LOG.error("set feed title");
            //TODO: set to url of rss page using rss extension
            //feed.setLink("http://rome.dev.java.net");
            //TODO: add custom field for description
            //feed.setDescription("description");

            List entries = new ArrayList();
            Writer writer = new StringWriter();

            try {
                SyndEntry entry;
                SyndContent description;
                entry = new SyndEntryImpl();
                entry.setTitle("test");
                entry.setLink("http://www.cnn.com");
                description = new SyndContentImpl();
                description.setType("text/plain");
                description.setValue("test");
                entry.setDescription(description);
                entries.add(entry);
            } catch (Exception ex) {
                LOG.error("error adding item to rss feed at path {}", "test", ex);
            }
            feed.setEntries(entries);
            SyndFeedOutput output = new SyndFeedOutput();
            try {
                output.output(feed, writer);
                writer.close();
            } catch (Exception ex) {
                LOG.error("error generating overall rss feed", ex);
                throw new Exception("Could not generate rss feed");
            }

            response.getWriter().write(writer.toString());
            response.getWriter().flush();
            response.getWriter().close();
            LOG.error("returned feed content");
        } catch (Exception fe) {
            LOG.error("error generating feed", fe);
            response.setStatus(500);
        }
    }
}
