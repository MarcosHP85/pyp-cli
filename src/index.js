const ora = require('ora')
const figlet = require('figlet')
const oracle = require('oracledb')
const dbconfig = require('./db/config')
const output = require('./output')
const inquirer = require('inquirer')
const views = require('./inquirer/')
const tables = require('./db/tables.json')
const Configstore = require('configstore')
const conf = new Configstore('pyp-cli')
conf.set('test','OK')
inquirer.registerPrompt('checkbox-plus', require('inquirer-checkbox-plus-prompt'))
inquirer.registerPrompt('file-tree-selection', require('inquirer-file-tree-selection-prompt'))

console.log(
  figlet.textSync('PyP - cli', { horizontalLayout: 'full' })
)

const run = async () => {
  // const folder = await views.askFolder()
  const credentials = await views.askIfsCredentials()
  const table = await views.askIfsTable()
  const columns = await views.askTableColumns(table.title)
  const loading = ora({ spinner: 'line', text: 'Conectando...' }).start()
  console.time('db')
  const cols = tables
    .find(t => t.title == table.title)
    .columns.filter(c => columns.columns.find(col => col == c.title))
    .map(col => col.name)

  let stringCol = ''
  for (let i=0; i < cols.length; i++) {
    stringCol += cols[i] + ','
  }
  stringCol = stringCol.slice(0,-1)
  let connection = await oracle.getConnection({ ...dbconfig, ...credentials })
  const result = await connection.execute(`SELECT ${ stringCol } FROM IFSATA.ACTIVE_SEPARATE_OVERVIEW`)
  // console.log(result)
  await connection.close()
  console.timeEnd('db')
  console.time('excel')
  await output(result.rows)
  console.timeEnd('excel')

  loading.stop()
}

run()
