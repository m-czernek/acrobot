CREATE TABLE acronym (
    id INT AUTO_INCREMENT PRIMARY KEY,
    acronym VARCHAR(10) not null
);
CREATE TABLE explanation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    explanation VARCHAR(80) not null,
    fid INT not null,
    KEY `fid` (`fid`),
    CONSTRAINT `items_ibfk_1` FOREIGN KEY (`fid`) REFERENCES `acronym` (`id`)
);