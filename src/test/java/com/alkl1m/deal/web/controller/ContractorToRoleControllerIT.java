package com.alkl1m.deal.web.controller;

import com.alkl1m.deal.TestBeans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
class ContractorToRoleControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateContractor_withValidPayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/contractor-to-role/add/{id}", "fb651609-2075-453f-8b66-af3795315f26")
                        .param("roleId", "BORROWER"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateContractor_withInvalidPayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/contractor-to-role/add/{id}", "fb651609-2075-453f-8b66-af3795315f26")
                        .param("roleId", "NOTEXISTINGROLE"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                    {
                                      "message": "Role not found",
                                      "errors": null
                                    }
                                """)
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testDeleteRoleForContractor_withValidPayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/contractor-to-role/add/{id}", "fb651609-2075-453f-8b66-af3795315f26")
                        .param("roleId", "DRAWER"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testDeleteRoleForContractor_withInvalidPayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/contractor-to-role/add/{id}", "fb651609-2075-453f-8b66-af3795315f26")
                        .param("roleId", "NOTEXISTINGROLE"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                    {
                                      "message": "Role not found",
                                      "errors": null
                                    }
                                """)
                );
    }

}