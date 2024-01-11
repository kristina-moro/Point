CREATE TABLE IF NOT EXISTS appointment
(
    id              SERIAL                        NOT NULL,  -- или все-таки uuid ?
    customer_id     uuid                          NOT NULL,
    performer_id    uuid                          NOT NULL,
    datetime        timestamp with time zone      NOT NULL,        -- время, предложенное клиентом
    what            varchar(400)                  NOT NULL,        -- запрос клиента

        dt_from       timestamp with time zone     NOT NULL,       -- время, зарезервированное исполнителем при подтверждении записи
        dt_to         timestamp with time zone     NOT NULL,
        note          varchar(400),                                -- это примечание исполнителя

    --category_id     smallint,  -- это нужно ?
    status          varchar                       NOT NULL default 'REQUESTED', -- подумать над использованием  DECLINED, APPROVED, CANCELED, COMPLETED

    CONSTRAINT appointment__pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT appointment__customer_fk FOREIGN KEY (customer_id) REFERENCES users (id),
    CONSTRAINT appointment__performer_fk FOREIGN KEY (performer_id) REFERENCES users (id)
)
TABLESPACE point_ts;

-- добавить интервальный уникальный индекс
-- сделать log статусов и редактирования для appointment