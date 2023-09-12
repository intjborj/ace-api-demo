CREATE TABLE doh.operation_hai (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	    "numhai" float,
        "numdischarges" float,
        "infectionrate" float,
        "patientnumvap" float,
        "totalventilatordays" float,
        "resultvap" float,
        "patientnumbsi" float,
        "totalnumcentralline" float,
        "resultbsi" float,
        "patientnumuti" float,
        "totalcatheterdays" float,
        "resultuti" float,
        "numssi" float,
        "totalproceduresdone" float,
        "resultssi" float,
        "reportingyear" int4,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);