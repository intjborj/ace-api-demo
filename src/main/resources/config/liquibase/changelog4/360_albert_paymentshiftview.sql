CREATE OR REPLACE VIEW cashiering.payment_shift
AS select distinct ornumber,receipt_type, s.shiftno from cashiering.payment_tracker pt
                                                             left join cashiering.shifting s on s.id=pt.shiftid;
