CREATE OR REPLACE VIEW cashiering.payment_tracker_shift
AS select pt.id, pt.receipt_type, pt.ornumber, s.shiftno, pt.ledger_header from cashiering.payment_tracker pt
 left join cashiering.shifting s on s.id = pt.shiftid;
