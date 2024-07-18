package com.alkl1m.deal.web.controller;

import com.alkl1m.deal.TestBeans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
class DealControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/contractors.sql")
    void testSaveOrUpdateDeal_withValidPayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testSaveOrUpdateDeal_withUpdatePayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testFindById_withValidId_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", "139916c4-9caa-402d-a464-0a2e3a74e889"))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                            {
                              "id": "139916c4-9caa-402d-a464-0a2e3a74e889",
                              "description": "Test Deal 1",
                              "agreement_number": "AGREEMENT001",
                              "agreement_date": "2022-01-14T22:00:00.000+00:00",
                              "agreement_start_dt": "2022-02-01T06:00:00.000+00:00",
                              "availability_date": "2022-02-14T22:00:00.000+00:00",
                              "type": {
                                "id": "CREDIT",
                                "name": "Кредитная сделка"
                              },
                              "status": {
                                "id": "DRAFT",
                                "name": "Черновик"
                              },
                              "sum": 1000.00,
                              "close_dt": "2022-03-01T10:00:00.000+00:00",
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
    void testChangeStatus_withValidPayload_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change")
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testChangeStatus_withNotExistingDeal_returnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change")
                        .contentType(MediaType.APPLICATION_JSON)
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
        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", "fb651609-2075-453f-8b66-af3795315f26"))
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
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "id": "139916c4-9caa-402d-a464-0a2e3a74e889",
                                "description": "Test Deal 1",
                                "agreementDateRange": "2021-03-01 - 2025-03-01",
                                "availabilityDateRange": "2021-03-01 - 2026-03-01"
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
                                            "agreement_date": "2022-01-14T22:00:00.000+00:00",
                                            "agreement_start_dt": "2022-02-01T06:00:00.000+00:00",
                                            "availability_date": "2022-02-14T22:00:00.000+00:00",
                                            "type": {
                                              "id": "CREDIT",
                                              "name": "Кредитная сделка"
                                            },
                                            "status": {
                                              "id": "DRAFT",
                                              "name": "Черновик"
                                            },
                                            "sum": 1000.00,
                                            "close_dt": "2022-03-01T10:00:00.000+00:00",
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

}