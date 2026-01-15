package com.fpoly.config;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DatabaseStatusListener {
	private final DataSource dataSource;

    public DatabaseStatusListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkDatabaseConnection() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("=================================");
            System.out.println("KẾT NỐI DATABASE THÀNH CÔNG");
            System.out.println("Database: " + conn.getCatalog());
            System.out.println("=================================");
        } catch (Exception e) {
            System.out.println("KẾT NỐI DATABASE THẤT BẠI");
            e.printStackTrace();
        }
    }
}
