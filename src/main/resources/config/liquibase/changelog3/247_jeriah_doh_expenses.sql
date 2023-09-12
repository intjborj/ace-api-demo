CREATE TABLE doh.expenses (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"salarieswages" decimal,
	"employeebenefits" decimal,
	"allowances" decimal,
	"totalps" decimal,
	"totalamountmedicine" decimal,
	"totalamountmedicalsupplies" decimal,
	"totalamountutilities" decimal,
	"totalamountnonmedicalservice" decimal,
	"totalmooe" decimal,
	"amountinfrastructure" decimal,
	"amountequipment" decimal,
	"totalco" decimal,
	"grandtotal" decimal,
	"reportingyear" int,


	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);