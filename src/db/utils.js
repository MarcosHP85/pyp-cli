const tables = require('./tables.json')

module.exports = {
  sqlCols: (table, columns) => {
    const cols = tables
    .find(t => t.title == table.title)
    .columns.filter(c => columns.columns.find(col => col == c.title))
    .map(col => `${ col.name } AS "${ col.title }"`)

    let stringCol = ''
    for (let i=0; i < cols.length; i++) {
      stringCol += cols[i] + ','
    }
    return stringCol.slice(0,-1)
  }
}