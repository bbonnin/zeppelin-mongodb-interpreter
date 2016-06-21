var _ZEPPELIN_TABLE_LIMIT_ = __ZEPPELIN_TABLE_LIMIT__;

function flattenObject(obj, flattenArray) {
    var toReturn = {};
    
    for (var i in obj) {
        if (!obj.hasOwnProperty(i)) continue;
        
        //if ((typeof obj[i]) == 'object') {
        if (toString.call( obj[i] ) === '[object Object]' ||
            toString.call( obj[i] ) === '[object BSON]' ||
          (flattenArray && toString.call( obj[i] ) === '[object Array]')) {
            var flatObject = flattenObject(obj[i]);
            for (var x in flatObject) {
                if (!flatObject.hasOwnProperty(x)) continue;
                
                toReturn[i + '.' + x] = flatObject[x];
            }
        } else {
            toReturn[i] = tojson(obj[i], null, true);
        }
    }
    return toReturn;
}

function printTable(dbquery, fields, flattenArray) {

    // Flatten all the documents and get all the fields to build a table with all fields
    var docs = [];
    var createFieldSet = fields == null || fields.length == 0;
    var fieldSet = new Set(fields);
    
    while (dbquery.hasNext()) {
        var doc = dbquery.next();
        doc = flattenObject(doc, flattenArray);
        docs.push(doc);
        if (createFieldSet) {
            for (var i in doc) {
                if (doc.hasOwnProperty(i)) {
                    fieldSet.add(i);
                }
            }
        }
    }
    
    fields = [...fieldSet];

    var header = "%table ";
    fields.forEach(field => header += field + "\t")
    print(header.substring(0, header.length - 1));
    
    docs.forEach(doc => {
        var row = "";
        fields.forEach(field => row += doc[field] + "\t")
        print(row.substring(0, row.length - 1));
    });
}

DBQuery.prototype.table = function (fields, flattenArray) {
    if (this._limit > _ZEPPELIN_TABLE_LIMIT_) {
        this.limit(_ZEPPELIN_TABLE_LIMIT_);
    }
    printTable(this, fields, flattenArray);
}

