const inquirer = require('inquirer')
const tables = require('../db/tables.json')

module.exports = () => {
  const questions = [
    {
      name: 'title',
      type: 'list',
      message: 'De que vista desea descargar datos',
      choices: tables.map(table => table.title)
    }
  ]
  return inquirer.prompt(questions)
}
