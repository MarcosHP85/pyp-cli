const { Workbook } = require('exceljs')

async function run() {
  const workbook = new Workbook()
  const worksheet = workbook.addWorksheet('Ordenes de trabajo')
  worksheet.addRows([
    ['123','marcos','primera\nsegunda',20,new Date()],
    ['321','marcos','primera\nsegunda',30,new Date()]
  ])
  await workbook.xlsx.writeFile('out.xlsx')
}

run()
