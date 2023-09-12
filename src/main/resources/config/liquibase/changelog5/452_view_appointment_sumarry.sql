DROP VIEW IF EXISTS appointment.summary_appointment;
CREATE OR REPLACE VIEW appointment.summary_appointment
AS select
uuid_generate_v4() as id,
a.schedule,
a.schedule_time,
concat(s.schedule_date,' ',to_char(c.t_start + interval '8 hours', 'HH24:MI:SS')) as start_,
concat(s.schedule_date,' ',to_char(c.t_end + interval '8 hours', 'HH24:MI:SS')) as end_,
count(a.schedule_time) as person
from appointment.appointment a
left join appointment.schedule s on s.id = a.schedule
left join appointment.schedule_time st on st.id = a.schedule_time
left join appointment.config c on c.id = st.config
group by a.schedule, a.schedule_time, s.schedule_date, c.t_start, c.t_end
order by s.schedule_date;






