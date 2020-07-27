const inquirer = require('inquirer')
const fuzzy = require('fuzzy')
const tables = require('../db/tables.json')

module.exports = (tableTitle) => {
  const columns = tables.find(t => t.title == tableTitle).columns.map(c => c.title)
  const defaultColumns = tables.find(t => t.title == tableTitle).columns.filter(c => c.default).map(c => c.title)
  const questions = [
    {
      name: 'columns',
      type: 'checkbox-plus',
      message: 'Que columnas desea descargar',
      searchable: true,
      highlight: true,
      source: (_, input) => {
        input = input || ''
        return new Promise(resolve => {
          let fuzzyResult = fuzzy.filter(input, columns)
          let data = fuzzyResult.map(element => element.original)
          resolve(data)
        })
      },
      default: defaultColumns
    }
  ]
  return inquirer.prompt(questions)
}
