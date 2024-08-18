package com.alkl1m.deal.web.controller;

import com.alkl1m.deal.JwtUtil;
import com.alkl1m.deal.TestBeans;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
class ContractorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveContractor_withValidPayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": null,
                                  "dealId": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                  "contractorId": "contractor4",
                                  "name": "contractor4",
                                  "inn": "0123456789",
                                  "main": false
                                }
                                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveContractor_withInvalidUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("USER");
        String jwt = JwtUtil.generateJwt("user", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": null,
                                  "dealId": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                  "contractorId": "contractor4",
                                  "name": "contractor4",
                                  "inn": "0123456789",
                                  "main": false
                                }
                                """))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testUpdateContractor_withValidPayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": "fb651609-2075-453f-8b66-af3795315f26",
                                  "dealId": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                  "contractorId": "contractor4",
                                  "name": "contractor4",
                                  "inn": "0123456789",
                                  "main": false
                                }
                                """))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                    {
                                      "id": "fb651609-2075-453f-8b66-af3795315f26",
                                      "contractor_id": "contractor4",
                                      "name": "contractor4",
                                      "main": false,
                                      "roles": [
                                        {
                                          "id": "DRAWER",
                                          "name": "Векселедатель",
                                          "category": "BORROWER"
                                        }
                                      ]
                                    }
                                """)
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testDeleteContractor_withValidPayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deal-contractor/delete/{id}", "fb651609-2075-453f-8b66-af3795315f26")
                        .cookie(new Cookie("jwt", jwt))
                )
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testDeleteContractor_withInvalidPayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deal-contractor/delete/{id}", "79ebe316-4306-43ae-884b-a05f98dd16a6")
                        .cookie(new Cookie("jwt", jwt))
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                    {
                                      "message": "Contractor not found with id: 79ebe316-4306-43ae-884b-a05f98dd16a6",
                                      "errors": null
                                    }
                                """)
                );
    }

}