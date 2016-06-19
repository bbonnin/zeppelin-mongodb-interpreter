var _ZEPPELIN_TABLE_LIMIT_ = __ZEPPELIN_TABLE_LIMIT__;

function printTable(dbquery, fields) {
    var header = "%table ";
    fields.forEach(field => header += field + "\t")
    print(header.substring(0, header.length - 1));
    
    while (dbquery.hasNext()) {
        var doc = dbquery.next();
        var row = "";
        fields.forEach(field => row += doc[field] + "\t")
        print(row.substring(0, row.length - 1));
    }
}

DBQuery.prototype.table = function (fields) {
    this.limit(_ZEPPELIN_TABLE_LIMIT_);
    printTable(this, fields);
}

