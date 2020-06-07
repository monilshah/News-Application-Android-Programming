const port = process.env.PORT || 8080;
const express = require("express");
const app = express();
const cors = require("cors");
const bodyParser = require("body-parser");
const logger = require("morgan");
const googleTrends = require('google-trends-api');

const fetch = require('node-fetch');

app.use(logger('dev'));
app.use(cors());

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.get('/', (req, res) => {
    res.send('connection succesful!! hello node.js');
  });

  
  app.get('/guardianHomeTab', (req, res) => {
  
    fetch("https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04")
      .then(res => res.json())
      .then(data => {

        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });


  app.get('/guardianWorldTab', (req, res) => {

    fetch("https://content.guardianapis.com/world?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });


  app.get('/guardianSportTab', (req, res) => {

    fetch("https://content.guardianapis.com/sport?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });

  
  app.get('/guardianBusinessTab', (req, res) => {

    fetch("https://content.guardianapis.com/business?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });

  
  app.get('/guardianTechTab', (req, res) => {

    fetch("https://content.guardianapis.com/technology?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });

  app.get('/guardianPoliticsTab', (req, res) => {

    fetch("https://content.guardianapis.com/politics?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });

  app.get('/guardianScienceTab', (req, res) => {

    fetch("https://content.guardianapis.com/science?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });


  app.get('/detailedGuardian', (req, res) => {
  let idi=req.query.key

    fetch("https://content.guardianapis.com/" + idi + "?api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all")
      .then(res => res.json())
      .then(data => {

        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });

 

  app.get('/searchGuardian', (req, res) => {
    let idi=req.query.key
    console.log("id passed is in Guard:", idi)
    fetch('https://content.guardianapis.com/search?q="'+idi+'"&api-key=0289e527-879a-4bad-b1d7-d1505a1b2b04&show-blocks=all')
      .then(res => res.json())
      .then(data => {
        res.send({ data });
      })
      .catch(err => {
        res.redirect('/error');
      });
  });


  app.get('/googleTrends', (req, res)=>{
  	let keywordPassed = req.query.key

		googleTrends.interestOverTime({keyword: keywordPassed, startTime: new Date('2019-06-01')})
		.then(function(results){
			res.send(results);
		console.log('These results are awesome', results);
		})
		.catch(function(err){
		console.error('Oh no there was an error', err);
		});
  });




  app.listen(port, function() {
    console.log("Runnning on " + port);
  });