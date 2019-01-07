# AppTg

Desenvolvimento de uma loja virtual na forma de aplicativo Android. Este app possui diversas funções de uma loja virtual tais como filtro de produtos, visualização das informações dos produtos, carrinho, edição do perfil do usuário entre outros.
---------> As opções de entrega e pagamento ainda não foram desenvolvidas.

O projeto foi desenvolvido na versão 3.2.1 do Android Studio

Para um bom entendimento do funcinamento do projeto é necessário criar um banco de dados utilizando o Firebase do Google. 
No Firebase é preciso:

1) importar o arquivo "first-ladys-kit-9349a-export (1)" dentro de Database > Realtime Database e utilizar as regras disponíveis no arquivos "Regras RealtimeDatabase";

2) criar duas pastas em Storage: a primeira para as fotos onde serão armazenadas as fotos de perfil dos usuários (eu criei uma pasta chamada imagem e uma chamada perfil dentro dela) e a segunda para você fazer o upload de todas as imagens dos produtos que estarão a venda na sua loja virtual (eu criei uma pasta chamada produtos). Após fazer o upload dos produtos deve-se colar a URL gerada pelo Firebase de cada imagem no Realtime Database;

3) em Authentication deve-se ativar o modo de login e-mail/senha.

Boa sorte!! ((:
