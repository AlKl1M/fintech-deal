INSERT INTO deal (id, description, agreement_number, agreement_date, agreement_start_dt, availability_date, deal_type, deal_status, sum, close_dt, create_date, modify_date, create_user_id, modify_user_id, is_active)
values ('139916c4-9caa-402d-a464-0a2e3a74e889', 'Test Deal 1', 'AGREEMENT001', '2022-01-15', '2022-02-01 08:00:00', '2022-02-15', 'CREDIT', 'DRAFT', 1000.00, '2022-03-01 12:00:00', '2022-03-01 12:00:00', null, 'user1', null, true);

INSERT INTO deal_contractor (id, deal_id, contractor_id, name, inn, main, create_date, modify_date, create_user_id, modify_user_id, is_active)
VALUES
    ('fb651609-2075-453f-8b66-af3795315f26', '139916c4-9caa-402d-a464-0a2e3a74e889', '123456789012', 'Contractor 1', '1234567890', true, CURRENT_TIMESTAMP, '2024-01-01 12:00:00', 'user1', null, true);

INSERT INTO contractor_to_role (deal_contractor, contractor_role, is_active)
VALUES ('fb651609-2075-453f-8b66-af3795315f26', 'DRAWER', true);