INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES ('Distribute Investment Earning', 'Distribute Investment Earning', '0 0 12 1/1 * ? *', '2015-08-17 19:01:31', 5, NULL, NULL, '2015-12-23 12:00:00', 'Distribute Investment EarningJobDetail1 _ DEFAULT', NULL, 1, 0, 1, 0, 0);





INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'CREATE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'DELETE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_LOANINVESTMENT', 'LOANINVESTMENT', 'DELETE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_LOANINVESTMENT', 'LOANINVESTMENT', 'CREATE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CLOSE_LOANINVESTMENT', 'LOANINVESTMENT', 'CLOSE', 0);



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CLOSE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'CLOSE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_LOANINVESTMENT', 'LOANINVESTMENT', 'UPDATE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'RUNBATCHJOB_INVESTMENT', 'INVESTMENT', 'RUNBATCHJOB', 0);
