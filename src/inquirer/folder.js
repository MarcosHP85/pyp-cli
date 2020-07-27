const inquirer = require('inquirer')


module.exports = () => {
  const questions = [
    {
      name: 'folder',
      type: 'file-tree-selection',
      onlyShowDir: true,
      onlyShowValid: true,
      validate: value => !value.startsWith('.')
    }
  ]
  return inquirer.prompt(questions)
}