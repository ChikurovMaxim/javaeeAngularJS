INSERT INTO USERS(ID, NAME ) VALUES (0, 'UNDEFINED');
INSERT INTO USERS(ID, NAME, LOGIN, PASSWORD ) VALUES (NULL, 'Uzumaki Naruto', 'naruto','1q2w3e' );
INSERT INTO USERS(ID, NAME, LOGIN, PASSWORD ) VALUES (NULL, 'Hatake Kakashi', 'dolan','1q2w3e');
INSERT INTO USERS(ID, NAME ) VALUES (NULL, 'Haruno Sakura' );
INSERT INTO ROLES(ID, NAME, DESCRIPTION) VALUES (NULL, 'ADMIN', 'Administrator');
INSERT INTO ROLES(ID, NAME, DESCRIPTION) VALUES (NULL, 'MODERATOR', 'Moderator');
INSERT INTO ROLES(ID, NAME, DESCRIPTION) VALUES (NULL, 'USER', 'User');
INSERT INTO user_roles(USER_ID, ROLE_ID) VALUES (2,1);
INSERT INTO user_roles(USER_ID, ROLE_ID) VALUES (3,2);
INSERT INTO article(ID,CONTEXT, CONTENT, CREATOR_ID) VALUES (NULL,'Sometopic','Somenews',2);
INSERT INTO article(ID,CONTEXT, CONTENT, CREATOR_ID) VALUES (NULL,'Sometopic2','Somenews2',3);
INSERT INTO article(ID,CONTEXT, CONTENT, CREATOR_ID) VALUES (NULL,'Sometopic2','Somenews3',4 );
INSERT INTO TOPIC (ID,NAME,DESCRIPTION) VALUES (1,'JAVA EE', 'JAVA EE');