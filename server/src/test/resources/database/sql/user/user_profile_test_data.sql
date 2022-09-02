delete
from user_profile
where user_id in ('01G844Z28PDXFE3M4AXSBK0FFR', '01G844Z28PDXFE3M4AXSBK0FFZ', '01G844Z28PDXFE3M4AXSBK0FFA');

insert into user_profile (user_id, display_name)
values ('01G844Z28PDXFE3M4AXSBK0FFR', 'Test User A'),
       ('01G844Z28PDXFE3M4AXSBK0FFZ', 'Test User B'),
       ('01G844Z28PDXFE3M4AXSBK0FFA', 'Test User C');
