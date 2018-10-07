package model

object ID {

  /**
  CONSISTENCY ALL;

  CREATE TABLE ids (id varchar, next_id counter, PRIMARY KEY (id));
  GRANT SELECT ON TABLE ids TO backend;
  GRANT MODIFY ON TABLE ids TO backend;

  UPDATE ids SET next_id = next_id + 1 WHERE id = $id USING CONSISTENCY ALL

  SELECT next_id FROM ids WHERE id = $id



    */
  


}
