const { Workbook } = require('exceljs')

module.exports = async (rows) => {
  const workbook = new Workbook()
  const worksheet = workbook.addWorksheet('Ordenes de trabajo')
  worksheet.addRows(rows)
  await workbook.xlsx.writeFile('out.xlsx')
}
