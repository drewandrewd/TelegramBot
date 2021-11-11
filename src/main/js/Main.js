const axios = require('axios')
const cheerio = require('cheerio')

axios.get('https://urfu.ru/api/schedule/groups/lessons/985155/20211021/').then(html => {
  const $ = cheerio.load(html.data)

  const result = [] // результат

  let currentDayIndex = -1 // текущий день (при итерации становится 0)
  $('body > table > tbody > tr').each((i, elem) => { // перебираем каждую строку
    if ($(elem).hasClass('divide')) { // если эта строка divide (т.е новый день)
      if ($(elem).find('td > b').length) { // если divide содержит день
        currentDayIndex++ // итерация дня

        result[currentDayIndex] = { // инциализируем объект дня
          id: currentDayIndex,
          weekday: $(elem).find('td > b').text().trim(),
          weekdayName: null,
          lessons: []
        }
      }
    }

    if ($(elem).hasClass('shedule-weekday-row')) { // если эта строка с уроком
      if ($(elem).find('.shedule-weekday-name').length) { // есть ли в нем абревиатура
        result[currentDayIndex].weekdayName = $(elem).find('.shedule-weekday-name').text().trim() // пихаем в день
      }

      if ($(elem).find('.shedule-weekday-item').length) { // если эта строка содержит урок
        result[currentDayIndex].lessons.push({ // добавляем в список уроков
          time: $(elem).find('.shedule-weekday-time').text().trim(),
          name: $(elem).find('.shedule-weekday-item > dd').text().trim()
        })
      }
    }
  })

  console.log(result)
})