INSERT INTO `manage_device` (
    `device_sn`,
    `device_name`,
    `user_id`,
    `nickname`,
    `workspace_id`,
    `device_type`,
    `sub_type`,
    `domain`,
    `firmware_version`,
    `compatible_status`,
    `version`,
    `device_index`,
    `child_sn`,
    `create_time`,
    `update_time`,
    `bound_time`,
    `bound_status`,
    `login_time`,
    `device_desc`,
    `url_normal`,
    `url_select`,
    `registration_number`
) VALUES (
             'dummygatewaysn',                          -- device_sn
             'Device Model 1',                          -- device_name
             'a1559e7c-8dd8-4780-b952-100cc4797da2',    -- user_id (adminPC)
             'My Device',                               -- nickname
             'e3dea0f5-37f2-4d79-ae58-490af3228069',    -- workspace_id
             1,                                         -- device_type
             1,                                         -- sub_type
             1,                                         -- domain
             '1.0.0',                                   -- firmware_version
             1,                                         -- compatible_status
             '1.0.0',                                   -- version
             'A',                                       -- device_index
             'dummydronesn',                            -- child_sn
             1634367932,                                -- create_time
             1634367932,                                -- update_time
             NULL,                                      -- bound_time
             0,                                         -- bound_status
             NULL,                                      -- login_time
             'This is a test device.',                  -- device_desc
             'http://example.com/normal.png',           -- url_normal
             'http://example.com/select.png',           -- url_select
             'REG123456'                                -- registration_number
         );
