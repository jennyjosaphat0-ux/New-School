(function(){
  var btn = document.createElement('button');
  btn.className = 'ham-btn';
  btn.innerHTML = '☰ <span style="font-size:0.78rem;font-weight:700">MENU</span>';
  btn.style.display = 'none';
  document.body.appendChild(btn);

  var overlay = document.createElement('div');
  overlay.className = 'mob-overlay';
  document.body.appendChild(overlay);

  var sidebar = document.querySelector('.sidebar');

  function open(){ if(sidebar) sidebar.classList.add('open'); overlay.classList.add('show'); }
  function close(){ if(sidebar) sidebar.classList.remove('open'); overlay.classList.remove('show'); }

  btn.addEventListener('click', function(){
    sidebar && sidebar.classList.contains('open') ? close() : open();
  });
  overlay.addEventListener('click', close);
  if(sidebar) sidebar.querySelectorAll('a').forEach(function(l){ l.addEventListener('click', close); });

  function check(){
    btn.style.display = window.innerWidth <= 768 ? 'flex' : 'none';
    if(window.innerWidth > 768) close();
  }
  window.addEventListener('resize', check);
  check();
})();
