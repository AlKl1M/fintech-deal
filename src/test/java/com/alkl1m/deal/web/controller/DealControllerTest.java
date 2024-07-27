package com.alkl1m.deal.web.controller;

import com.alkl1m.deal.JwtUtil;
import com.alkl1m.deal.TestBeans;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
class DealControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateDeal_withValidPayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": null,
                                  "description": "Сделка 000d3",
                                  "agreementNumber": "003-01",
                                  "agreementDate": "2023-03-01",
                                  "agreementStartDt": "2024-04-01",
                                  "availabilityDate": "2025-03-01",
                                  "typeId": "CREDIT",
                                  "sum": "3000.00",
                                  "closeDt": null
                                }
                                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateDeal_withValidDealSuperuser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("DEAL_SUPERUSER");
        String jwt = JwtUtil.generateJwt("dealsuperuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": null,
                                  "description": "Сделка 000d3",
                                  "agreementNumber": "003-01",
                                  "agreementDate": "2023-03-01",
                                  "agreementStartDt": "2024-04-01",
                                  "availabilityDate": "2025-03-01",
                                  "typeId": "CREDIT",
                                  "sum": "3000.00",
                                  "closeDt": null
                                }
                                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateDeal_withInvalidUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("USER");
        String jwt = JwtUtil.generateJwt("user", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": null,
                                  "description": "Сделка 000d3",
                                  "agreementNumber": "003-01",
                                  "agreementDate": "2023-03-01",
                                  "agreementStartDt": "2024-04-01",
                                  "availabilityDate": "2025-03-01",
                                  "typeId": "CREDIT",
                                  "sum": "3000.00",
                                  "closeDt": null
                                }
                                """))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateDeal_withUpdatePayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.put("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                  "description": "Сделка 000d3",
                                  "agreementNumber": "003-01",
                                  "agreementDate": "2023-03-01",
                                  "agreementStartDt": "2024-04-01",
                                  "availabilityDate": "2025-03-01",
                                  "typeId": "CREDIT",
                                  "sum": "3000.00",
                                  "closeDt": null
                                }
                                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testFindById_withValidIdAndUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("USER");
        String jwt = JwtUtil.generateJwt("user", roles);
        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", "139916c4-9caa-402d-a464-0a2e3a74e889")
                        .cookie(new Cookie("jwt", jwt))
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                    {
                                      "id": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                      "description": "Test Deal 1",
                                      "agreement_number": "AGREEMENT001",
                                      "agreement_date": "2022-01-15",
                                      "agreement_start_dt": "2022-02-01",
                                      "availability_date": "2022-02-15",
                                      "type": {
                                        "id": "CREDIT",
                                        "name": "Кредитная сделка"
                                      },
                                      "status": {
                                        "id": "DRAFT",
                                        "name": "Черновик"
                                      },
                                      "sum": 1000.00,
                                      "close_dt": "2022-03-01",
                                      "contractors": [
                                        {
                                          "id": "fb651609-2075-453f-8b66-af3795315f26",
                                          "contractor_id": "123456789012",
                                          "name": "Contractor 1",
                                          "main": true,
                                          "roles": [
                                            {
                                              "id": "DRAWER",
                                              "name": "Векселедатель",
                                              "category": "BORROWER"
                                            }
                                          ]
                                        }
                                      ]
                                    }
                                """)
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testChangeStatus_withValidPayloadAndDealSuperUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("DEAL_SUPERUSER");
        String jwt = JwtUtil.generateJwt("dealsuperuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                    "dealId": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                    "statusId": "DRAFT"
                                }
                                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testChangeStatus_withValidPayloadAndUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("USER");
        String jwt = JwtUtil.generateJwt("user", roles);
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                    "dealId": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                    "statusId": "DRAFT"
                                }
                                """))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testChangeStatus_withNotExistingDeal_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("dealsuperuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                    "dealId": "fb651609-2075-453f-8b66-af3795315f26",
                                    "statusId": "DRAFT"
                                }
                                """))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                {"message":"Deal not found","errors":null}
                                """)
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testFindById_withInvalidId_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("USER");
        String jwt = JwtUtil.generateJwt("user", roles);
        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", "fb651609-2075-453f-8b66-af3795315f26")
                        .cookie(new Cookie("jwt", jwt))
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                    {
                                      "message": "Contractor with id fb651609-2075-453f-8b66-af3795315f26 not found!",
                                      "errors": null
                                    }
                                """)
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSearchDeal_withValidPayload_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("SUPERUSER");
        String jwt = JwtUtil.generateJwt("superuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "id": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                  "description": "Test Deal 1",
                                  "agreementDateRange": "2021-03-01 - 2025-03-01",
                                  "availabilityDateRange": "2021-03-01 - 2026-03-01",
                                  "type": [
                                    {
                                      "id": "CREDIT",
                                      "name": "Кредитная сделка"
                                    }
                                  ],
                                  "status": [
                                    {
                                      "id": "DRAFT",
                                      "name": "Черновик"
                                    }
                                  ]
                                }
                                """))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                    {
                                      "deals": {
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "pageable": {
                                          "pageNumber": 0,
                                          "pageSize": 10,
                                          "sort": {
                                            "empty": true,
                                            "sorted": false,
                                            "unsorted": true
                                          },
                                          "offset": 0,
                                          "paged": true,
                                          "unpaged": false
                                        },
                                        "first": true,
                                        "last": true,
                                        "size": 10,
                                        "content": [
                                          {
                                            "id": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                            "description": "Test Deal 1",
                                            "agreement_number": "AGREEMENT001",
                                            "agreement_date": "2022-01-15",
                                            "agreement_start_dt": "2022-02-01",
                                            "availability_date": "2022-02-15",
                                            "type": {
                                              "id": "CREDIT",
                                              "name": "Кредитная сделка"
                                            },
                                            "status": {
                                              "id": "DRAFT",
                                              "name": "Черновик"
                                            },
                                            "sum": 1000.00,
                                            "close_dt": "2022-03-01",
                                            "contractors": [
                                              {
                                                "id": "fb651609-2075-453f-8b66-af3795315f26",
                                                "contractor_id": "123456789012",
                                                "name": "Contractor 1",
                                                "main": true,
                                                "roles": [
                                                  {
                                                    "id": "DRAWER",
                                                    "name": "Векселедатель",
                                                    "category": "BORROWER"
                                                  }
                                                ]
                                              }
                                            ]
                                          }
                                        ],
                                        "number": 0,
                                        "sort": {
                                          "empty": true,
                                          "sorted": false,
                                          "unsorted": true
                                        },
                                        "numberOfElements": 1,
                                        "empty": false
                                      }
                                    }
                                """)
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSearchDeal_withValidCreditUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("CREDIT_USER");
        String jwt = JwtUtil.generateJwt("credituser", roles);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "type": [
                                    {
                                      "id": "CREDIT",
                                      "name": "Кредитная сделка"
                                    }
                                  ]
                                }
                                """))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSearchDeal_withOverdraftTypeWithCreditUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("CREDIT_USER");
        String jwt = JwtUtil.generateJwt("credituser", roles);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "type": [
                                    {
                                      "id": "OVERDRAFT",
                                      "name": "Овердрафт"
                                    }
                                  ]
                                }
                                """))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testSearchDeal_withCreditTypeWithOverdraftUser_returnsValidData() throws Exception {
        List<String> roles = Arrays.asList("OVERDRAFT_USER");
        String jwt = JwtUtil.generateJwt("overdraftuser", roles);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwt", jwt))
                        .content("""
                                {
                                  "type": [
                                    {
                                      "id": "CREDIT",
                                      "name": "Кредитная сделка"
                                    }
                                  ]
                                }
                                """))
                .andExpectAll(
                        status().isForbidden()
                );
    }

}