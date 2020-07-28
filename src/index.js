const ora = require('ora')
const figlet = require('figlet')
const oracle = require('oracledb')
const dbconfig = require('./db/config')
const output = require('./output')
const inquirer = require('inquirer')
const views = require('./inquirer/')
const chalk = require('chalk')
const { sqlCols } = require('./db/utils')
const Configstore = require('configstore')
const conf = new Configstore('pyp-cli')

inquirer.registerPrompt('checkbox-plus', require('inquirer-checkbox-plus-prompt'))
inquirer.registerPrompt('file-tree-selection', require('inquirer-file-tree-selection-prompt'))

console.log(
  chalk.yellowBright(
    figlet.textSync('PyP - cli', { horizontalLayout: 'full' })
  ) + '\n'
)

const run = async () => {
  // const folder = await views.askFolder()
  let credentials = conf.get('credentials')
  if (!credentials) {
    credentials = await views.askIfsCredentials()
    if (credentials.save) {
      conf.set('credentials', credentials)
    }
  }
  const table = await views.askIfsTable()
  const columns = await views.askTableColumns(table.title)
  const loading = ora({ spinner: 'line', text: 'Conectando...' }).start()
  // console.time('db')

  const stringCol = sqlCols(table, columns)
  // console.log(stringCol)
  const connection = await oracle.getConnection({ ...dbconfig, ...credentials })
  loading.text = 'Descargando datos...'
  // const result = await connection.execute(`SELECT ${ stringCol } FROM IFSATA.ACTIVE_SEPARATE_OVERVIEW`,[],{
  //   prefetchRows:   5000,
  //   fetchArraySize: 5000
  // })
  const result = await connection.execute("SELECT WO_NO,STATE,WO_NO_MADRE,REAL_F_DATE,CONTRACT,SUBESTADO_PLANIF,COM_INT_PLA,PLAN_F_DATE,PLAN_S_DATE,MCH_CODE,ORG_CODE,TIPO_PARADA,WORK_TYPE_ID,PRIORITY_ID,TAREA,CATEGORY_ID,REG_DATE,FIRMA_PAQ_TRAB,WORK_LEADER_SIGN,CONFIRMACION_1,PREPARED_BY,COM_INT_PRO,MCH_LOC,MCH_POS,COM_PLA_PRO,COM_IMP_PLA,COM_IMP_PRO,REQ_PAQ_TAB,PALABRA_CLAVE,NOTAS_MOTIVO,EQUIPOS_INDISPONIBLES,REQUIERE_QC,NOVEDAD_SEMANAL"
  + " FROM ifsata.ACTIVE_SEPARATE_OVERVIEW"
  + " where CONTRACT in (2000, 4000)"
  + " union "
  + "SELECT WO_NO,'Cancelado' as STATE,WO_NO_MADRE,REAL_F_DATE,CONTRACT,SUBESTADO_PLANIF,COM_INT_PLA,PLAN_F_DATE,PLAN_S_DATE,MCH_CODE,ORG_CODE,TIPO_PARADA,WORK_TYPE_ID,PRIORITY_ID,TAREA,CATEGORY_ID,REG_DATE,FIRMA_PAQ_TRAB,WORK_LEADER_SIGN,CONFIRMACION_1,PREPARED_BY,COM_INT_PRO,MCH_LOC,MCH_POS,COM_PLA_PRO,COM_IMP_PLA,COM_IMP_PRO,REQ_PAQ_TAB,PALABRA_CLAVE,NOTAS_MOTIVO,EQUIPOS_INDISPONIBLES,REQUIERE_QC,NOVEDAD_SEMANAL"
  + " FROM ifsata.HISTORICAL_SEPARATE_OVERVIEW"
  + " where CONTRACT in (2000, 4000) and WO_STATUS_ID = 'CANCELED'",[],{
    prefetchRows:   5000,
    fetchArraySize: 5000
  })
  loading.text = `Se descargaron ${ result.rows.length } OTs`
  loading.succeed()
  loading.clear()
  // console.timeEnd('db')

  const loadingExcel = ora({ spinner: 'line', text: 'Escribiendo archivo excel...' })
  loadingExcel.start()
  // console.time('excel')
  await output(result.rows)
  loadingExcel.succeed()
  // console.timeEnd('excel')
}

run()
