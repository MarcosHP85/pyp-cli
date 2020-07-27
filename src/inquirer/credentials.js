const inquirer = require('inquirer')

module.exports = () => {
  const questions = [
    {
      name: 'user',
      type: 'input',
      message: 'Ingrese su usuario de IFS',
      validate: function(value) {
        if (value.length) return true
        else return 'Por favor ingrese su usuario de IFS'
      }
    },
    {
      name: 'password',
      type: 'password',
      mask: true,
      message: 'Ingrese su contraseña de IFS',
      validate: function(value) {
        if (value.length) return true
        else return 'Por favor ingrese su contraseña de IFS'
      }
    },
    {
      name: 'save',
      type: 'confirm',
      message: 'Desea guardar su usuario y contaseña?'
    }
  ]
  return inquirer.prompt(questions)
}
